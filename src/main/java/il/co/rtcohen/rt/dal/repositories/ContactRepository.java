package il.co.rtcohen.rt.dal.repositories;

import il.co.rtcohen.rt.dal.dao.Contact;
import il.co.rtcohen.rt.dal.dao.Site;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

@Repository
public class ContactRepository extends AbstractRepository<Contact> {
    @Autowired
    public ContactRepository(DataSource dataSource) {
        super(dataSource, "CONTACT", "Contacts");
    }

    protected Contact getItemFromResultSet(ResultSet rs) throws SQLException {
        return new Contact(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getBoolean("active"),
                rs.getInt("siteid"),
                rs.getString("phone"),
                rs.getString("notes")
        );
    }

    protected PreparedStatement generateInsertStatement(Connection connection, Contact contact) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "insert into " + this.DB_TABLE_NAME
                        + " (name,siteId,phone,notes)"
                        + " values (?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.setString(1, contact.getName());
        stmt.setInt(2, contact.getSiteId());
        stmt.setString(3, contact.getPhone());
        stmt.setString(4, contact.getNotes());
        return stmt;
    }

    protected PreparedStatement generateUpdateStatement(Connection connection, Contact contact) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "update " + this.DB_TABLE_NAME + " set name=?, siteId=?, phone=?, notes=?, active=? where id=?",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.setString(1, contact.getName());
        stmt.setInt(2, contact.getSiteId());
        stmt.setString(3, contact.getPhone());
        stmt.setString(4, contact.getNotes());
        stmt.setBoolean(5, contact.isActive());
        stmt.setInt(6, contact.getId());
        return stmt;
    }

    public List<Contact> getItems(Site site) {
        List<Contact> list = this.getItems();
        list.removeIf(contact -> !contact.getSiteId().equals(site.getId()));
        return list;
    }

    @Deprecated
    public List<Contact> getContactsBySite(Integer siteId) {
        return getContactsBySite(siteId, false);
    }

    @Deprecated
    public List<Contact> getContactsBySite(Integer siteId, boolean onlyActive) {
        List<Contact> list = this.getItems();
        list.removeIf(contact -> contact.getSiteId() != siteId || (onlyActive && !contact.isActive()));
        return list;
    }

    @Deprecated
    public Contact getContactById (int id) {
        return getItem(id);
    }

    @Deprecated
    private Contact getContactFromRS(ResultSet rs) throws SQLException {
        return getItemFromResultSet(rs);
    }

    @Deprecated
    public long insertContact (String name, int siteId, String phone, String notes) {
        insertItem(new Contact(null, name, true, siteId, phone, notes));
        return 1; // TODO: change to real value, if needed
    }

    @Deprecated
    public int deleteContact(int id) {
        deleteItem(getItem(id));
        return 1; // TODO: change to real value, if needed
    }

    @Deprecated
    public void updateContact (Contact contact) {
        updateItem(contact);
    }
}
