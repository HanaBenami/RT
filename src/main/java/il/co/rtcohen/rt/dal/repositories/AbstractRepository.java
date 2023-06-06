package il.co.rtcohen.rt.dal.repositories;

import il.co.rtcohen.rt.dal.InsertException;
import il.co.rtcohen.rt.dal.UpdateException;
import il.co.rtcohen.rt.dal.dao.AbstractType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public abstract class AbstractRepository<T extends AbstractType> implements RepositoryInterface<T> {

    protected final Logger logger ;
    private final DataSource dataSource;

    final static private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    final static private String nullDateString = "1901-01-01";

    protected String DB_TABLE_NAME; // TODO: final?
    protected String REPOSITORY_NAME; // TODO: final?
    protected String[] DB_COLUMNS;

    public AbstractRepository(DataSource dataSource, String dbTableName, String repositoryName, String[] dbColumns ) {
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.dataSource = dataSource;
        this.DB_TABLE_NAME = dbTableName;
        this.REPOSITORY_NAME = repositoryName;
        this.DB_COLUMNS = dbColumns;
    }

    public String getDbTableName() {
        return this.DB_TABLE_NAME;
    }

    public void setDbTableName(String dbTableName) {
        this.DB_TABLE_NAME = dbTableName;
    }

    public void setRepositoryName(String repositoryName) {
        this.REPOSITORY_NAME = repositoryName;
    }

    public String getRepositoryName() {
        return this.REPOSITORY_NAME;
    }

    protected String getDbColumnsStringForInsertStatement() {
        return " (" + String.join(", ", this.DB_COLUMNS) + ") values ("
                + Arrays.stream(DB_COLUMNS).map(dbColumn -> "?").collect(Collectors.joining(", ")) + ")";
    }

    protected String getDbColumnsStringForUpdateStatement() {
        return " " + String.join("=?, ", this.DB_COLUMNS) + "=? ";
    }

    abstract protected T getItemFromResultSet(ResultSet rs) throws SQLException;

    // TODO: use in all the interfaces and change to abstract
    protected void updateItemDetailsInStatement(PreparedStatement stmt, T t) throws SQLException {}

    protected PreparedStatement generateInsertStatement(Connection connection, T t) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "insert into " + this.DB_TABLE_NAME + getDbColumnsStringForInsertStatement(),
                Statement.RETURN_GENERATED_KEYS
        );
        updateItemDetailsInStatement(stmt, t);
        return stmt;
    }

    protected PreparedStatement generateUpdateStatement(Connection connection, T t) throws SQLException {
        String query = "update " + this.DB_TABLE_NAME + " set " + getDbColumnsStringForUpdateStatement() + " where id=?";
        PreparedStatement stmt = connection.prepareStatement(
                query,
                Statement.RETURN_GENERATED_KEYS
        );
        updateItemDetailsInStatement(stmt, t);
        stmt.setInt(StringUtils.countOccurrencesOf(query, "?"), t.getId());
        return stmt;
    }

    public List<T> getItems() {
        List<T> list = new ArrayList<>();
        String sql = "SELECT * FROM " + this.DB_TABLE_NAME;
        this.logger.info(sql);
        try (Connection con = dataSource.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(this.getItemFromResultSet(rs));
            }
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
        return getItem("id=" + id);
    }

    public T getItem(String whereClause) {
        List<T> list = new ArrayList<>();
        String sqlQuery = "SELECT * FROM " + this.DB_TABLE_NAME + " WHERE " + whereClause;
        this.logger.info(sqlQuery);
        try (Connection con = dataSource.getConnection(); PreparedStatement stmt = con.prepareStatement(sqlQuery)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(this.getItemFromResultSet(rs));
            }
            oneRecordOnlyValidation(list.size(), "getItem");
            return list.get(0);
        } catch (SQLException e) {
            String error = getMessagesPrefix() + "error in getItem (" + whereClause + ")";
            this.logger.error(error, e);
            throw new DataRetrievalFailureException(error, e);
        }
    }

    public long insertItem(T t) {
        try {
            Connection connection = this.dataSource.getConnection();
            PreparedStatement stmt = generateInsertStatement(connection, t);
            this.logger.info(stmt.toString());
            int n = stmt.executeUpdate();
            oneRecordOnlyValidation(n, "created");
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long id = generatedKeys.getLong(1);
                    t.setId(id);
                    this.logger.info(getMessagesPrefix()
                            + "New record has been inserted to the table \"" + this.DB_TABLE_NAME +"\" (id=" + id + ")");
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
        if (null == t.getId()) {
            insertItem(t);
            return;
        }

        try {
            Connection connection = this.dataSource.getConnection();
            PreparedStatement stmt = generateUpdateStatement(connection, t);
            this.logger.info(stmt.toString());
            int n = stmt.executeUpdate();
            oneRecordOnlyValidation(n, "updated");
            this.logger.info(getMessagesPrefix() + "data was updated (" + t + ")");
        }
        catch (SQLException e) {
            String error = getMessagesPrefix() + "error in updateItem";
            this.logger.error(error, e);
            throw new UpdateException(error, e);
        }
    }

    public void deleteItem(T t) {
        try {
            Connection connection = this.dataSource.getConnection();
            PreparedStatement stmt = generateDeleteStatement(connection, t);
            int n = stmt.executeUpdate();
            oneRecordOnlyValidation(n, "updated");
            this.logger.info(getMessagesPrefix() + " data was deleted (" + t + ")");
        }
        catch (SQLException e) {
            String error = getMessagesPrefix() + "error in deleteItem";
            this.logger.error(error, e);
            throw new UpdateException(error, e);
        }
    }

    private PreparedStatement generateDeleteStatement(Connection connection, T t) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "delete from " + this.DB_TABLE_NAME + " where id=?",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.setInt(1, t.getId());
        return stmt;
    }

    private String getMessagesPrefix() {
        return this.REPOSITORY_NAME + " repository - ";
    }

    private void oneRecordOnlyValidation(int n, String verb) throws SQLException {
        if (n == 0) {
            throw new SQLException(getMessagesPrefix() + "No record has been " + verb);
        }
        else if (n > 1) {
            throw new SQLException(getMessagesPrefix() + "More than one record has been " + verb);
        }
    }

    static public String dateToString(LocalDate date) {
        return (null == date ? nullDateString : date.format(dateFormatter));
    }

    static public LocalDate stringToDate(String s) {
        return LocalDate.parse(s, dateFormatter);
    }
}
