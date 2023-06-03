package il.co.rtcohen.rt.dal.repositories;

import il.co.rtcohen.rt.dal.dao.GeneralObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;

@Repository
public class GeneralObjectRepository extends AbstractRepository<GeneralObject> {

    protected final String ID_COLUMN = "id";
    protected final String NAME_COLUMN = "id";
    protected final String ACTIVE_COLUMN = "id";

    @Autowired
    public GeneralObjectRepository(DataSource dataSource, String DB_TABLE_NAME, String REPOSITORY_NAME) {
        super(dataSource, DB_TABLE_NAME, REPOSITORY_NAME);
    }

    protected GeneralObject getItemFromResultSet(ResultSet rs) throws SQLException {
        return new GeneralObject(rs.getInt(ID_COLUMN),
                rs.getString(NAME_COLUMN),
                rs.getBoolean(ACTIVE_COLUMN),
                DB_TABLE_NAME);
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

    @Override
    public boolean isItemValid(GeneralObject generalObject) {
        return !generalObject.getName().isEmpty();
    }
}

