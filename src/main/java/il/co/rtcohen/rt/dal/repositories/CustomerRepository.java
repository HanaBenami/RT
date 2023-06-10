package il.co.rtcohen.rt.dal.repositories;
import il.co.rtcohen.rt.dal.dao.Customer;
import il.co.rtcohen.rt.dal.repositories.interfaces.AbstractTypeWithNameAndActiveFieldsRepository;
import il.co.rtcohen.rt.dal.repositories.interfaces.RepositoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

import javax.sql.DataSource;
import java.sql.*;

@Repository
public class CustomerRepository extends AbstractTypeWithNameAndActiveFieldsRepository<Customer> implements RepositoryInterface<Customer> {
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
                this.customerTypeRepository.getItem(rs.getInt(DB_CUST_TYPE_ID_COLUMN)),
                rs.getInt(DB_HASK_KEY_COLUMN),
                rs.getBoolean(DB_ACTIVE_COLUMN)
        );
    }

    @Override
    protected int updateItemDetailsInStatement(PreparedStatement preparedStatement, Customer customer) throws SQLException {
        int fieldsCounter = super.updateItemDetailsInStatement(preparedStatement, customer);
        fieldsCounter++;
        preparedStatement.setInt(fieldsCounter, customer.getCustomerType().getId());
        fieldsCounter++;
        preparedStatement.setInt(fieldsCounter, customer.getHashavshevetId());
        return fieldsCounter;
    }
}

