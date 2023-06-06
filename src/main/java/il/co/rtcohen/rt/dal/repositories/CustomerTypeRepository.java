package il.co.rtcohen.rt.dal.repositories;

import il.co.rtcohen.rt.dal.dao.CustomerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class CustomerTypeRepository extends AbstractTypeWithNameAndActiveFieldsRepository<CustomerType> implements RepositoryInterface<CustomerType> {
    @Autowired
    public CustomerTypeRepository(DataSource dataSource) {
        super(dataSource, "custType", "Customer types",
                new String[] {

                }
        );
    }

    protected CustomerType getItemFromResultSet(ResultSet rs) throws SQLException {
        return new CustomerType(
                rs.getInt(DB_ID_COLUMN),
                rs.getString(DB_NAME_COLUMN),
                rs.getBoolean(DB_ACTIVE_COLUMN)
        );
    }
}
