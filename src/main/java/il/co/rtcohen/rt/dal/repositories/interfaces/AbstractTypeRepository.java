package il.co.rtcohen.rt.dal.repositories.interfaces;

import il.co.rtcohen.rt.dal.repositories.exceptions.InsertException;
import il.co.rtcohen.rt.dal.repositories.exceptions.UpdateException;
import il.co.rtcohen.rt.dal.dao.interfaces.AbstractType;
import il.co.rtcohen.rt.utils.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public abstract class AbstractTypeRepository<T extends AbstractType> implements RepositoryInterface<T> {
    final static protected String DB_ID_COLUMN = "id";

    final protected Logger logger;
    final private DataSource dataSource;

    final protected String REPOSITORY_NAME;
    final protected String DB_TABLE_NAME;
    final private ArrayList<String> dbColumnsList;

    final private CacheManager<T> cacheManager;

    public AbstractTypeRepository(DataSource dataSource, String dbTableName, String repositoryName, String[]... additionalDbColumnsLists) {
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.logger.info(this.getRepositoryName() + " repository is being initiated");
        this.dataSource = dataSource;
        this.DB_TABLE_NAME = dbTableName;
        this.REPOSITORY_NAME = repositoryName;
        this.dbColumnsList = new ArrayList<>();
        for (String[] additionalDbColumnsList : additionalDbColumnsLists) {
            this.dbColumnsList.addAll(Arrays.asList(additionalDbColumnsList));
        }
        this.cacheManager = new CacheManager<>(AbstractType::getId);
    }

    abstract protected T getItemFromResultSet(ResultSet rs) throws SQLException;

    abstract protected int updateItemDetailsInStatement(PreparedStatement preparedStatement, T t) throws SQLException;

    public String getDbTableName() {
        return this.DB_TABLE_NAME;
    }

    public String getRepositoryName() {
        return this.REPOSITORY_NAME;
    }

    // The connection must be closed later on
    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    protected String getDbColumnsStringForInsertStatement() {
        return " (" + String.join(", ", this.dbColumnsList) + ") values ("
                + dbColumnsList.stream().map(dbColumn -> "?").collect(Collectors.joining(", ")) + ")";
    }

    protected String getDbColumnsStringForUpdateStatement() {
        return " " + String.join("=?, ", this.dbColumnsList) + "=? ";
    }

    private PreparedStatement generateInsertStatement(Connection connection, T t) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "insert into " + this.DB_TABLE_NAME + getDbColumnsStringForInsertStatement(),
                Statement.RETURN_GENERATED_KEYS
        );
        updateItemDetailsInStatement(preparedStatement, t);
        return preparedStatement;
    }

    private PreparedStatement generateUpdateStatement(Connection connection, T t) throws SQLException {
        String query = "update " + this.DB_TABLE_NAME + " set " + getDbColumnsStringForUpdateStatement() + " where " + DB_ID_COLUMN + "=?";
        PreparedStatement preparedStatement = connection.prepareStatement(
                query,
                Statement.RETURN_GENERATED_KEYS
        );
        updateItemDetailsInStatement(preparedStatement, t);
        preparedStatement.setInt(StringUtils.countOccurrencesOf(query, "?"), t.getId());
        return preparedStatement;
    }

    public List<T> getItems() {
        return getItems((String) null);
    }

    public List<T> getItems(String whereClause)  {
        String sqlQuery = "SELECT * FROM " + this.DB_TABLE_NAME;
        if (null != whereClause) {
            sqlQuery += " where " + whereClause;
        }
        this.logger.info(sqlQuery);
        List<T> list;
        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)
        ) {
            list = getItems(preparedStatement);
        } catch (SQLException e) {
            String error = getMessagesPrefix() + "error in getItems";
            this.logger.error(error, e);
            throw new DataRetrievalFailureException(error, e);
        }
        return list;
    }

    public List<T> getItems(PreparedStatement preparedStatement) {
        List<T> list = new ArrayList<>();
        try {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                T item = this.getItemFromResultSet(rs);
                list.add(item);
            }
            list = cacheManager.syncWithCache(list);
            logger.info(list.size() + " records have been retrieved");
            return list;
        } catch (SQLException e) {
            String error = getMessagesPrefix() + "error in getItems";
            this.logger.error(error, e);
            throw new DataRetrievalFailureException(error, e);
        }
    }

    public T getItem(Integer id) {
        if (null == id || 0 == id) {
            return null;
        }

        T fromCache = cacheManager.getFromCache(id);
        if (null != fromCache) {
            return fromCache;
        } else {
            return getItem(DB_ID_COLUMN + "=" + id);
        }
    }

    public T getItem(String whereClause) {
        List<T> list = new ArrayList<>();
        String sqlQuery = "SELECT * FROM " + this.DB_TABLE_NAME + " WHERE " + whereClause;
        this.logger.info(sqlQuery);
        try (Connection con = dataSource.getConnection(); PreparedStatement preparedStatement = con.prepareStatement(sqlQuery)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                list.add(this.getItemFromResultSet(rs));
            }
            oneRecordOnlyValidation(list.size(), "getItem");
            if (list.isEmpty()) {
                return null;
            } else {
                T t = list.get(0);
                t = cacheManager.syncWithCache(t);
                return t;
            }
        } catch (SQLException e) {
            String error = getMessagesPrefix() + "error in getItem (" + whereClause + ")";
            this.logger.error(error, e);
            throw new DataRetrievalFailureException(error, e);
        }
    }

    public long insertItem(T t) {
        try (
            Connection connection = this.dataSource.getConnection();
            PreparedStatement preparedStatement = generateInsertStatement(connection, t)
        ){
            this.logger.info(preparedStatement.toString());
            int n = preparedStatement.executeUpdate();
            oneRecordOnlyValidation(n, "created");
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long id = generatedKeys.getLong(1);
                    t.setId(id);
                    this.logger.info(getMessagesPrefix()
                            + "New record has been inserted to the table \"" + this.DB_TABLE_NAME +"\" (id=" + id + ")");
                    t.setBindRepository(this);
                    t.postSave();
                    t = cacheManager.syncWithCache(t);
                    return id;
                }
                throw new SQLException("error getting the new key");
            }
        }
        catch (SQLException e) {
            String error = getMessagesPrefix() + "error in insertItem";
            this.logger.error(error, e);
            throw new InsertException(error ,e);
        }
    }

    public void updateItem(T t) {
        if (null == t.getId() || 0 == t.getId()) {
            insertItem(t);
            return;
        }
        try (
                Connection connection = this.dataSource.getConnection();
                PreparedStatement preparedStatement = generateUpdateStatement(connection, t)
        ) {
            this.logger.info(preparedStatement.toString());
            int n = preparedStatement.executeUpdate();
            oneRecordOnlyValidation(n, "updated");
            this.logger.info(getMessagesPrefix() + "data was updated (" + t + ")");
            t.setBindRepository(this);
            t.postSave();
            t = cacheManager.syncWithCache(t);
        }
        catch (SQLException e) {
            String error = getMessagesPrefix() + "error in updateItem";
            this.logger.error(error, e);
            throw new UpdateException(error, e);
        }
    }

    public void deleteItem(T t) {
        try (
            Connection connection = this.dataSource.getConnection();
            PreparedStatement preparedStatement = generateDeleteStatement(connection, t)
        ) {
            int n = preparedStatement.executeUpdate();
            oneRecordOnlyValidation(n, "updated");
            this.logger.info(getMessagesPrefix() + " data was deleted (" + t + ")");
            cacheManager.deleteFromCache(t);
        }
        catch (SQLException e) {
            String error = getMessagesPrefix() + "error in deleteItem";
            this.logger.error(error, e);
            throw new UpdateException(error, e);
        }
    }

    private PreparedStatement generateDeleteStatement(Connection connection, T t) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "delete from " + this.DB_TABLE_NAME + " where " + DB_ID_COLUMN + "=?",
                Statement.RETURN_GENERATED_KEYS
        );
        preparedStatement.setInt(1, t.getId());
        return preparedStatement;
    }

    private String getMessagesPrefix() {
        return this.REPOSITORY_NAME + " repository - ";
    }

    private void oneRecordOnlyValidation(int n, String verb) throws SQLException {
        if (n == 0) {
            logger.warn(getMessagesPrefix() + "No record has been found (" + verb + ")");
        }
        else if (n > 1) {
            throw new SQLException(getMessagesPrefix() + "More than one record has been " + verb);
        }
    }
}
