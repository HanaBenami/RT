package il.co.rtcohen.rt.dal.repositories;

import il.co.rtcohen.rt.dal.dao.User;
import il.co.rtcohen.rt.dal.repositories.interfaces.AbstractTypeWithNameAndActiveFieldsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class UsersRepository extends AbstractTypeWithNameAndActiveFieldsRepository<User> implements RepositoryInterface<User> {
    @Autowired
    public UsersRepository(DataSource dataSource) {
        super(dataSource, "users", "users types",
                new String[] {

                }
        );
    }

    protected User getItemFromResultSet(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt(DB_ID_COLUMN),
                rs.getString(DB_NAME_COLUMN),
                rs.getBoolean(DB_ACTIVE_COLUMN)
        );
    }
}
