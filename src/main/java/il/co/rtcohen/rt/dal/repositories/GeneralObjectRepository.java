package il.co.rtcohen.rt.dal.repositories;

import il.co.rtcohen.rt.dal.dao.AbstractTypeWithNameAndState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;

@Repository
abstract public class GeneralTypeRepository<T extends AbstractTypeWithNameAndState> extends AbstractRepository<T> {

    protected final String ID_COLUMN = "id";
    protected final String NAME_COLUMN = "id";
    protected final String ACTIVE_COLUMN = "id";

    @Autowired
    public GeneralTypeRepository(DataSource dataSource, String DB_TABLE_NAME, String REPOSITORY_NAME) {
        super(dataSource, DB_TABLE_NAME, REPOSITORY_NAME);
    }

    protected PreparedStatement generateInsertStatement(Connection connection, T t) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "insert into " + this.DB_TABLE_NAME + " (" + NAME_COLUMN + ", " + ACTIVE_COLUMN + ") values (?, ?)",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.setString(1, t.getName());
        stmt.setBoolean(2, t.isActive());
        return stmt;
    }

    protected PreparedStatement generateUpdateStatement(Connection connection, T t) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "update " + this.DB_TABLE_NAME + " set " + NAME_COLUMN + "=?, " + ACTIVE_COLUMN + "=? where " + ID_COLUMN + "=?",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.setString(1, t.getName());
        stmt.setBoolean(2, t.isActive());
        stmt.setInt(3, t.getId());
        return stmt;
    }

    @Override
    public boolean isItemValid(T t) {
        return true;
        // TODO
    }
}

