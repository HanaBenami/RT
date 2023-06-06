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
    static private  final String DB_ID_COLUMN = "id";
    static private  final String DB_NAME_COLUMN = "name";
    static private  final String DB_ACTIVE_COLUMN = "active";

    @Autowired
    public GeneralObjectRepository(DataSource dataSource) {
        super(dataSource, null, null,
                new String[] {
                        DB_NAME_COLUMN, DB_ACTIVE_COLUMN
                });
    }

    public GeneralObjectRepository(DataSource dataSource, String dbTableName, String repositoryName) {
        super(dataSource, dbTableName, repositoryName,
                new String[] {
                        DB_NAME_COLUMN, DB_ACTIVE_COLUMN
                });
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
                rs.getInt(DB_ID_COLUMN),
                rs.getString(DB_NAME_COLUMN),
                rs.getBoolean(DB_ACTIVE_COLUMN)
        );
    }

    protected void updateItemDetailsInStatement(PreparedStatement stmt, GeneralObject generalObject) throws SQLException {
        int fieldsCounter = 1;
        stmt.setString(fieldsCounter, generalObject.getName());
        fieldsCounter++;
        stmt.setBoolean(fieldsCounter, generalObject.isActive());
    }
}

