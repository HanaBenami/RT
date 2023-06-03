package il.co.rtcohen.rt.dal.repositories;

import il.co.rtcohen.rt.dal.dao.GeneralObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

@Repository
@Qualifier("GeneralObjectRepository")
public class GeneralObjectRepository extends AbstractRepository<GeneralObject> {
    protected final String ID_COLUMN = "id";
    protected final String NAME_COLUMN = "name";
    protected final String ACTIVE_COLUMN = "active";

    @Autowired
    public GeneralObjectRepository(DataSource dataSource) {
        super(dataSource, null, null);
    }

    public GeneralObjectRepository(DataSource dataSource, String dbTableName, String repositoryName) {
        super(dataSource, dbTableName, repositoryName);
    }

    public void setDbTableName(String dbTableName) {
        this.DB_TABLE_NAME = dbTableName;
    }

    public String getDbTableName() {
        return this.DB_TABLE_NAME;
    }

    public void setRepositoryName(String repositoryName) {
        this.REPOSITORY_NAME = repositoryName;
    }

    public String getRepositoryName() {
        return this.REPOSITORY_NAME;
    }

    public List<GeneralObject> getItems(boolean onlyActiveItems) {
        List<GeneralObject> list = this.getItems();
        list.removeIf(generalObject -> !generalObject.isActive());
        return list;
    }

    protected GeneralObject getItemFromResultSet(ResultSet rs) throws SQLException {
        return new GeneralObject(
                rs.getInt(ID_COLUMN),
                rs.getString(NAME_COLUMN),
                rs.getBoolean(ACTIVE_COLUMN)
        );
    }

    protected PreparedStatement generateInsertStatement(Connection connection, GeneralObject generalObject) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "insert into " + this.DB_TABLE_NAME + " (" + NAME_COLUMN + ", " + ACTIVE_COLUMN + ") values (?, ?)",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.setString(1, generalObject.getName());
        stmt.setBoolean(2, generalObject.isActive());
        return stmt;
    }

    protected PreparedStatement generateUpdateStatement(Connection connection, GeneralObject generalObject) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "update " + this.DB_TABLE_NAME + " set " + NAME_COLUMN + "=?, " + ACTIVE_COLUMN + "=? where " + ID_COLUMN + "=?",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.setString(1, generalObject.getName());
        stmt.setBoolean(2, generalObject.isActive());
        stmt.setInt(3, generalObject.getId());
        return stmt;
    }

    @Deprecated
    public String getName(int id, String dbTableName) {
        setDbTableName(dbTableName);
        return getItem(id).getName();
    }
}

