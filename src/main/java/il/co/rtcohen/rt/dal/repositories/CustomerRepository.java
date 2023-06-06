package il.co.rtcohen.rt.dal.repositories;
import il.co.rtcohen.rt.dal.dao.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

import javax.sql.DataSource;
import java.sql.*;

@Repository
public class CustomerRepository extends AbstractRepository<Customer> {
    @Autowired
    public CustomerRepository(DataSource dataSource) {
        super(dataSource, "CUST", "Customers", null);
    }

    protected Customer getItemFromResultSet(ResultSet rs) throws SQLException {
        return new Customer(rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("custtype"),
                rs.getBoolean("active"));
    }

    protected PreparedStatement generateInsertStatement(Connection connection, Customer customer) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "insert into " + this.DB_TABLE_NAME + " (name, custtype, active) values (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.setString(1, customer.getName());
        stmt.setInt(2, customer.getCustomerTypeID());
        stmt.setBoolean(3, customer.isActive());
        return stmt;
    }

    protected PreparedStatement generateUpdateStatement(Connection connection, Customer customer) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "update " + this.DB_TABLE_NAME + " set name=?, custtype=?, active=? where id=?",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.setString(1, customer.getName());
        stmt.setInt(2, customer.getCustomerTypeID());
        stmt.setBoolean(3, customer.isActive());
        stmt.setInt(4, customer.getId());
        return stmt;
    }

    // TODO: Delete
    @Deprecated
    public List<Customer> getCustomers() {
        return getItems();
    }
    @Deprecated
    public long insertCustomer(String name, Integer custTupeId) {
        return insertItem(new Customer(null, name, custTupeId, true));
    }
    @Deprecated
    public void updateCustomerType(Customer customer) {
        this.updateItem(customer);
    }
}

