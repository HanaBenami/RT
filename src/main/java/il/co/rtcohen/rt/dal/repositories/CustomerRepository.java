package il.co.rtcohen.rt.dal.repositories;

import il.co.rtcohen.rt.dal.dao.Customer;
import il.co.rtcohen.rt.dal.repositories.interfaces.AbstractTypeSyncedWithHashavshevetRepository;
import il.co.rtcohen.rt.dal.repositories.interfaces.RepositoryInterface;

import il.co.rtcohen.rt.utils.NullPointerExceptionWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;

@Repository
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CustomerRepository extends AbstractTypeSyncedWithHashavshevetRepository<Customer> implements RepositoryInterface<Customer> {
    static protected final String DB_CUST_TYPE_ID_COLUMN = "custtype";
    static protected final String DB_HASK_KEY_COLUMN = "hashkey";

    private final CustomerTypeRepository customerTypeRepository;

    @Autowired
    public CustomerRepository(DataSource dataSource, CustomerTypeRepository customerTypeRepository) {
        super(
            dataSource, "CUST", "Customers",
            new String[] {
                DB_CUST_TYPE_ID_COLUMN,
                DB_HASK_KEY_COLUMN
            }
        );
        this.customerTypeRepository = customerTypeRepository;
    }

    protected Customer getItemFromResultSet(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getInt(DB_ID_COLUMN),
                rs.getString(DB_NAME_COLUMN),
                rs.getBoolean(DB_ACTIVE_COLUMN),
                rs.getInt(DB_HASH_FIRST_DOC_ID),
                rs.getInt(DB_HASK_KEY_COLUMN),
                this.customerTypeRepository.getItem(rs.getInt(DB_CUST_TYPE_ID_COLUMN))
        );
    }

    @Override
    protected int updateItemDetailsInStatement(PreparedStatement preparedStatement, Customer customer) throws SQLException {
        int fieldsCounter = super.updateItemDetailsInStatement(preparedStatement, customer);
        fieldsCounter++;
        preparedStatement.setInt(fieldsCounter, NullPointerExceptionWrapper.getWrapper(customer, c -> c.getCustomerType().getId(), 0));
        fieldsCounter++;
        preparedStatement.setInt(fieldsCounter, customer.getHashavshevetCustomerId());
        return fieldsCounter;
    }

    public Customer getItemByHashKey(Integer hashKey) {
        return super.getItem(DB_HASK_KEY_COLUMN + "=" + hashKey);
    }
}

