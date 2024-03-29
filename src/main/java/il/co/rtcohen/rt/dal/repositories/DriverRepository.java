package il.co.rtcohen.rt.dal.repositories;

import il.co.rtcohen.rt.dal.dao.Driver;
import il.co.rtcohen.rt.dal.repositories.interfaces.AbstractTypeWithNameAndActiveFieldsRepository;
import il.co.rtcohen.rt.dal.repositories.interfaces.RepositoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class DriverRepository extends AbstractTypeWithNameAndActiveFieldsRepository<Driver> implements RepositoryInterface<Driver> {
    @Autowired
    public DriverRepository(DataSource dataSource) {
        super(dataSource, "driver", "drivers",
                new String[] {

                }
        );
    }

    protected Driver getItemFromResultSet(ResultSet rs) throws SQLException {
        return new Driver(
                rs.getInt(DB_ID_COLUMN),
                rs.getString(DB_NAME_COLUMN),
                rs.getBoolean(DB_ACTIVE_COLUMN)
        );
    }
}
