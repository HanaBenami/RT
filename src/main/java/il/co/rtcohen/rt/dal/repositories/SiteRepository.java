package il.co.rtcohen.rt.dal.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

import il.co.rtcohen.rt.dal.dao.Customer;
import il.co.rtcohen.rt.dal.dao.Site;
import il.co.rtcohen.rt.dal.repositories.interfaces.AbstractTypeWithNameAndActiveFieldsRepository;
import il.co.rtcohen.rt.dal.repositories.interfaces.RepositoryInterface;

@Repository
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SiteRepository extends AbstractTypeWithNameAndActiveFieldsRepository<Site> implements RepositoryInterface<Site> {
    static protected final String DB_CUSTOMER_ID_COLUMN = "custid";
    static protected final String DB_AREA_ID_COLUMN = "areaid";
    static protected final String DB_ADDRESS_COLUMN = "address";
    static protected final String DB_NOTES_COLUMN = "notes";

    final private AreasRepository areasRepository;
    final private CustomerRepository customerRepository;

    @Autowired
    public SiteRepository(DataSource dataSource, AreasRepository areasRepository, CustomerRepository customerRepository) {
        super(
                dataSource, "SITE", "Sites",
                new String[]{
                        DB_CUSTOMER_ID_COLUMN,
                        DB_AREA_ID_COLUMN,
                        DB_ADDRESS_COLUMN,
                        DB_NOTES_COLUMN
                }
        );
        this.areasRepository = areasRepository;
        this.customerRepository = customerRepository;
    }

    protected Site getItemFromResultSet(ResultSet rs) throws SQLException {
        return new Site(
                this.customerRepository.getItem(rs.getInt(DB_CUSTOMER_ID_COLUMN)),
                rs.getInt(DB_ID_COLUMN),
                rs.getString(DB_NAME_COLUMN),
                this.areasRepository.getItem(rs.getInt(DB_AREA_ID_COLUMN)),
                rs.getString(DB_ADDRESS_COLUMN),
                rs.getBoolean(DB_ACTIVE_COLUMN),
                rs.getString(DB_NOTES_COLUMN));
    }

    @Override
    protected int updateItemDetailsInStatement(PreparedStatement preparedStatement, Site site) throws SQLException {
        int fieldsCounter = super.updateItemDetailsInStatement(preparedStatement, site);
        fieldsCounter++;
        preparedStatement.setInt(fieldsCounter, site.getCustomer().getId());
        fieldsCounter++;
        preparedStatement.setInt(fieldsCounter, site.getArea().getId());
        fieldsCounter++;
        preparedStatement.setString(fieldsCounter, site.getAddress());
        fieldsCounter++;
        preparedStatement.setString(fieldsCounter, site.getNotes());
        return fieldsCounter;
    }

    public List<Site> getItems(boolean onlyActiveItems) throws SQLException {
        List<Site> list = this.getItems();
        list.removeIf(generalObject -> !generalObject.isActive());
        return list;
    }

    public List<Site> getItems(Customer customer) throws SQLException {
        return this.getItems(DB_CUSTOMER_ID_COLUMN + "=" + customer.getId() + " and " + DB_ACTIVE_COLUMN + "=1");
    }
}

