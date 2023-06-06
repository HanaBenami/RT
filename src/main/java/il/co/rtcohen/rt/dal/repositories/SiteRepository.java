package il.co.rtcohen.rt.dal.repositories;
import il.co.rtcohen.rt.dal.dao.Customer;
import il.co.rtcohen.rt.dal.dao.GeneralObject;
import il.co.rtcohen.rt.dal.dao.Site;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class SiteRepository extends AbstractRepository<Site> {
    @Autowired
    public SiteRepository(DataSource dataSource) {
        super(dataSource, "SITE", "Sites", null);
    }

    protected Site getItemFromResultSet(ResultSet rs) throws SQLException {
        return new Site(rs.getInt("custid"),
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("areaID"),
                rs.getString("address"),
                rs.getBoolean("active"),
                rs.getString("contact"),
                rs.getString("phone"),
                rs.getString("notes"));
    }

    protected PreparedStatement generateInsertStatement(Connection connection, Site site) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "insert into " + this.DB_TABLE_NAME
                        + " (name,areaID,address,custid,contact,phone,notes)"
                        + " values (?,?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.setString(1, site.getName());
        stmt.setInt(2, site.getAreaId());
        stmt.setString(3, site.getAddress());
        stmt.setInt(4, site.getCustomerId());
        stmt.setString(5, site.getContact()); // TODO: delete
        stmt.setString(6, site.getPhone()); // TODO: delete
        stmt.setString(7, site.getNotes());
        return stmt;
    }

    protected PreparedStatement generateUpdateStatement(Connection connection, Site site) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "update " + this.DB_TABLE_NAME + " set name=?, areaID=?, address=?, custid=?, contact=?, phone=?, notes=?, active=? where id=?",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.setString(1, site.getName());
        stmt.setInt(2, site.getAreaId());
        stmt.setString(3, site.getAddress());
        stmt.setInt(4, site.getCustomerId());
        stmt.setString(5, site.getContact()); // TODO: delete
        stmt.setString(6, site.getPhone()); // TODO: delete
        stmt.setString(7, site.getNotes());
        stmt.setBoolean(8, site.isActive());
        stmt.setInt(9, site.getId());
        return stmt;
    }

    public List<Site> getItems(boolean onlyActiveItems) {
        List<Site> list = this.getItems();
        list.removeIf(generalObject -> !generalObject.isActive());
        return list;
    }

    public List<Site> getItems(Customer customer) {
        List<Site> list = this.getItems();
        list.removeIf(site -> site.getCustomerId() != customer.getId());
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
        list.removeIf(site -> !site.getCustomerId().equals(customerId));
        return list.stream().map(GeneralObject::getId).collect(Collectors.toList());
    }

    @Deprecated
    private List<Integer> getActiveByCustomer(Integer customerId) {
        List<Site> list = this.getItems();
        list.removeIf(site -> site.getCustomerId() != customerId && site.isActive());
        return list.stream().map(GeneralObject::getId).collect(Collectors.toList());
    }

    @Deprecated
    private List<Integer> getActive() {
        return getItems(true).stream().map(GeneralObject::getId).collect(Collectors.toList());
    }

    @Deprecated
    public Site getSiteById (int id) {
        return getItem(id);
    }

    @Deprecated
    public long insertSite (String name,Integer areaId,String address, Integer customerId, String contact, String phone, String notes) {
        return insertItem(new Site(customerId, 0, name, areaId, address, true, contact, phone, notes));
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

