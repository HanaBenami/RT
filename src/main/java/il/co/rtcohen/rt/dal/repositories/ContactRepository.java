package il.co.rtcohen.rt.dal.repositories;

import il.co.rtcohen.rt.dal.dao.Contact;
import il.co.rtcohen.rt.dal.dao.Site;
import il.co.rtcohen.rt.dal.repositories.interfaces.AbstractTypeWithNameAndActiveFieldsRepository;
import il.co.rtcohen.rt.dal.repositories.interfaces.RepositoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

@Repository
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
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
}
