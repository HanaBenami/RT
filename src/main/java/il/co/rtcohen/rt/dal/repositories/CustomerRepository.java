package il.co.rtcohen.rt.dal.repositories;
import il.co.rtcohen.rt.dal.dao.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

import javax.sql.DataSource;
import java.sql.*;

@Repository
public class CustomerRepository extends AbstractTypeWithNameAndActiveFieldsRepository<Customer> implements RepositoryInterface<Customer> {
    static protected final String DB_CUST_TYPE_ID_COLUMN = "custtype";

    private final CustomerTypeRepository customerTypeRepository;

    @Autowired
    public CustomerRepository(DataSource dataSource, CustomerTypeRepository customerTypeRepository) {
        super(
            dataSource, "CUST", "Customers",
            new String[] {
                DB_CUST_TYPE_ID_COLUMN
            }
        );
        this.customerTypeRepository = customerTypeRepository;
    }

    protected Customer getItemFromResultSet(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getInt(DB_ID_COLUMN),
                rs.getString(DB_NAME_COLUMN),
                this.customerTypeRepository.getItem(rs.getInt(DB_CUST_TYPE_ID_COLUMN)),
                rs.getBoolean(DB_ACTIVE_COLUMN)
        );
    }

    @Override
    protected int updateItemDetailsInStatement(PreparedStatement stmt, Customer customer) throws SQLException {
        int fieldsCounter = super.updateItemDetailsInStatement(stmt, customer);
        fieldsCounter++;
        stmt.setInt( fieldsCounter, customer.getCustomerType().getId());
        return fieldsCounter;
    }

    @Deprecated
    public List<Customer> getCustomers() {
        return getItems();
    }
    @Deprecated
    public long insertCustomer(String name, Integer custTupeId) {
        return insertItem(new Customer(null, name, this.customerTypeRepository.getItem(custTupeId), true));
    }
    @Deprecated
    public void updateCustomerType(Customer customer) {
        this.updateItem(customer);
    }
}

