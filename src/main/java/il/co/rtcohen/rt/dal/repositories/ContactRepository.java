package il.co.rtcohen.rt.dal.repositories;

import il.co.rtcohen.rt.dal.dao.Contact;
import il.co.rtcohen.rt.dal.dao.Site;
import il.co.rtcohen.rt.dal.repositories.interfaces.AbstractTypeWithNameAndActiveFieldsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

@Repository
public class ContactRepository extends AbstractTypeWithNameAndActiveFieldsRepository<Contact> implements RepositoryInterface<Contact> {
    static protected final String DB_SITE_ID_COLUMN = "siteid";
    static protected final String DB_PHONE_COLUMN = "phone";
    static protected final String DB_NOTES_COLUMN = "notes";

    private final SiteRepository siteRepository;

    @Autowired
    public ContactRepository(DataSource dataSource, SiteRepository siteRepository) {
        super(dataSource, "CONTACT", "Contacts",
                new String[] {
                        DB_SITE_ID_COLUMN,
                        DB_PHONE_COLUMN,
                        DB_NOTES_COLUMN
                });
        this.siteRepository = siteRepository;
    }

    protected Contact getItemFromResultSet(ResultSet rs) throws SQLException {
        return new Contact(
                rs.getInt(DB_ID_COLUMN),
                rs.getString(DB_NAME_COLUMN),
                rs.getBoolean(DB_ACTIVE_COLUMN),
                this.siteRepository.getItem(rs.getInt(DB_SITE_ID_COLUMN)),
                rs.getString(DB_PHONE_COLUMN),
                rs.getString(DB_NOTES_COLUMN)
        );
    }

    @Override
    protected int updateItemDetailsInStatement(PreparedStatement preparedStatement, Contact contact) throws SQLException {
        int fieldsCounter = super.updateItemDetailsInStatement(preparedStatement, contact);
        fieldsCounter++;
        preparedStatement.setInt(fieldsCounter, contact.getSite().getId());
        fieldsCounter++;
        preparedStatement.setString( fieldsCounter, contact.getPhone());
        fieldsCounter++;
        preparedStatement.setString( fieldsCounter, contact.getNotes());
        return fieldsCounter;
    }

    public List<Contact> getItems(Site site) throws SQLException {
        return getItems(DB_SITE_ID_COLUMN + "=" + site.getId());
    }

    @Deprecated
    public List<Contact> getContactsBySite(Integer siteId) throws SQLException {
        return getContactsBySite(siteId, false);
    }

    @Deprecated
    public List<Contact> getContactsBySite(Integer siteId, boolean onlyActive) throws SQLException {
        List<Contact> list = this.getItems();
        list.removeIf(contact -> contact.getSite().getId() != siteId || (onlyActive && !contact.isActive()));
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
        insertItem(new Contact(null, name, true, this.siteRepository.getItem(siteId), phone, notes));
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
