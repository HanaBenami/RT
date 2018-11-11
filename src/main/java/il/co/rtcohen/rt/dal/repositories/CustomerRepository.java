package il.co.rtcohen.rt.dal.repositories;
import il.co.rtcohen.rt.dal.dao.Customer;
import il.co.rtcohen.rt.dal.exceptions.InsertException;
import il.co.rtcohen.rt.dal.exceptions.UpdateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomerRepository {

    final static private Logger log = LoggerFactory.getLogger(CustomerRepository.class);

    private DataSource dataSource;

    @Autowired
    public CustomerRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Customer> getCustomers() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM CUST";
        try (Connection con = dataSource.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Customer(rs.getInt("id"),rs.getString("name"),rs.getInt("custtype"),rs.getBoolean("active")));
            }
            return list;
        }
        catch (SQLException e) {
            log.error(sql);
            log.error("error in getCustomers: ",e);
            throw new DataRetrievalFailureException("error in getCustomers: ",e);
        }
    }

    public long insertCustomer(String name,Integer custTupeId) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement stmt = con.prepareStatement("insert into cust (name,custtype) values (?,?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1,name);
            stmt.setInt(2,custTupeId);
            int n=stmt.executeUpdate();
            if (n == 0) {
                throw new SQLException("no record has been created");
            }
            else if (n > 1) {
                throw new SQLException("more than one record has been created");
            }
            else {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long id = generatedKeys.getLong(1);
                        log.info("customer id=" + id + " has been created");
                        return id;
                    }
                    throw new SQLException("error getting the new key");
                }
            }
        }
        catch (SQLException e) {
            log.error("error in insertCustomer (\""+name+"\"): ",e);
            throw new InsertException("error in insertCustomer (\""+name+"\"): ",e);
        }
    }

    public void updateCustomerType(Customer customer) {
        try (Connection con = dataSource.getConnection(); PreparedStatement stmt = con.prepareStatement
                ("update cust set custtype=? where id=?")) {
            stmt.setInt(1,customer.getCustomerTypeID());
            stmt.setInt(2,customer.getId());
            int n=stmt.executeUpdate();
            log.info("Updating customer where id="+customer.getId()+" > "+n+" records has been updated");
        }
        catch (SQLException e) {
            log.error("error in updateCustomerType (id="+customer.getId()+"): ",e);
            throw new UpdateException("error in updateCustomerType (id="+customer.getId()+"): ",e);
        }
    }

}

