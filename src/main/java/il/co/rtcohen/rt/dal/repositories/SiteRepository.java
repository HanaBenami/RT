package il.co.rtcohen.rt.dal.repositories;
import il.co.rtcohen.rt.dal.dao.Customer;
import il.co.rtcohen.rt.dal.dao.AbstractTypeWithNameAndActiveFields;
import il.co.rtcohen.rt.dal.dao.Site;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
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
    protected int updateItemDetailsInStatement(PreparedStatement stmt, Site site) throws SQLException {
        int fieldsCounter = super.updateItemDetailsInStatement(stmt, site);
        fieldsCounter++;
        stmt.setInt(fieldsCounter, site.getCustomer().getId());
        fieldsCounter++;
        stmt.setInt(fieldsCounter, site.getArea().getId());
        fieldsCounter++;
        stmt.setString(fieldsCounter, site.getAddress());
        fieldsCounter++;
        stmt.setString(fieldsCounter, site.getNotes());
        return fieldsCounter;
    }

    public List<Site> getItems(boolean onlyActiveItems) {
        List<Site> list = this.getItems();
        list.removeIf(generalObject -> !generalObject.isActive());
        return list;
    }

    public List<Site> getItems(Customer customer) {
        List<Site> list = this.getItems();
        list.removeIf(site -> (null == site.getCustomer() || !site.getCustomer().getId().equals(customer.getId())));
        return list;
    }







    @Deprecated
    public List<Site> getSitesByCustomer(Integer customerId) {
        List<Site> list = new ArrayList<>();
        List<Integer> id = getIdByCustomer(customerId,false);
        for (Integer i : id)
            list.add(getSiteById(i));
        return list;
    }

    @Deprecated
    public List<Integer> getActiveIdByCustomer(Integer customerId) {
        return getIdByCustomer(customerId,true);
    }

    @Deprecated
    private List<Integer> getIdByCustomer(Integer customerId, boolean active) {
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
    private List<Integer> getByCustomer(Integer customerId) {
        List<Site> list = this.getItems();
        list.removeIf(site -> null == site.getCustomer() || !site.getCustomer().getId().equals(customerId));
        return list.stream().map(AbstractTypeWithNameAndActiveFields::getId).collect(Collectors.toList());
    }

    @Deprecated
    private List<Integer> getActiveByCustomer(Integer customerId) {
        List<Site> list = this.getItems();
        list.removeIf(site -> null == site.getCustomer() || !site.getCustomer().getId().equals(customerId) && site.isActive());
        return list.stream().map(AbstractTypeWithNameAndActiveFields::getId).collect(Collectors.toList());
    }

    @Deprecated
    private List<Integer> getActive() {
        return getItems(true).stream().map(AbstractTypeWithNameAndActiveFields::getId).collect(Collectors.toList());
    }

    @Deprecated
    public Site getSiteById (int id) {
        return getItem(id);
    }

    @Deprecated
    public long insertSite (String name,Integer areaId,String address, Integer customerId, String contact, String phone, String notes) {
        return insertItem(new Site(this.customerRepository.getItem(customerId), 0, name, this.areasRepository.getItem(areaId), address, true, notes));
    }

    @Deprecated
    public int deleteSite(int id) {
        deleteItem(getItem(id));
        return 1; // TODO: change to real value, if needed
    }

    @Deprecated
    public void updateSite (Site site) {
        updateItem(site);
    }
}

