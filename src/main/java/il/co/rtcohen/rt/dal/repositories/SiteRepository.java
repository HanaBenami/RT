package il.co.rtcohen.rt.dal.repositories;
import il.co.rtcohen.rt.dal.dao.Customer;
import il.co.rtcohen.rt.dal.dao.interfaces.AbstractTypeWithNameAndActiveFields;
import il.co.rtcohen.rt.dal.dao.Site;
import il.co.rtcohen.rt.dal.repositories.interfaces.AbstractTypeWithNameAndActiveFieldsRepository;
import il.co.rtcohen.rt.dal.repositories.interfaces.RepositoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.stream.Collectors;

@Repository
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
        List<Site> list = this.getItems();
        list.removeIf(site -> (null == site.getCustomer() || !site.getCustomer().getId().equals(customer.getId())));
        return list;
    }



    @Deprecated
    public List<Integer> getActiveIdByCustomer(Integer customerId) throws SQLException {
        return getIdByCustomer(customerId,true);
    }

    @Deprecated
    private List<Integer> getIdByCustomer(Integer customerId, boolean active) throws SQLException {
        if (customerId>0) {
            if (active)
                return getActiveByCustomer(customerId);
            else
                return getByCustomer(customerId);
        }
        else
            return getActive();
    }

    @Deprecated
    private List<Integer> getByCustomer(Integer customerId) throws SQLException {
        List<Site> list = this.getItems();
        list.removeIf(site -> null == site.getCustomer() || !site.getCustomer().getId().equals(customerId));
        return list.stream().map(AbstractTypeWithNameAndActiveFields::getId).collect(Collectors.toList());
    }

    @Deprecated
    private List<Integer> getActiveByCustomer(Integer customerId) throws SQLException {
        List<Site> list = this.getItems();
        list.removeIf(site -> null == site.getCustomer() || !site.getCustomer().getId().equals(customerId) && site.isActive());
        return list.stream().map(AbstractTypeWithNameAndActiveFields::getId).collect(Collectors.toList());
    }

    @Deprecated
    private List<Integer> getActive() throws SQLException {
        return getItems(true).stream().map(AbstractTypeWithNameAndActiveFields::getId).collect(Collectors.toList());
    }

    @Deprecated
    public Site getSiteById (int id) {
        return getItem(id);
    }

}

