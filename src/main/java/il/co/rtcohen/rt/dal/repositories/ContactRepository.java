package il.co.rtcohen.rt.dal.repositories;

import il.co.rtcohen.rt.dal.DeleteException;
import il.co.rtcohen.rt.dal.InsertException;
import il.co.rtcohen.rt.dal.UpdateException;
import il.co.rtcohen.rt.dal.dao.Contact;
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
public class ContactRepository {

    static final private Logger log = LoggerFactory.getLogger(ContactRepository.class);

    private final DataSource dataSource;

    @Autowired
    public ContactRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Contact> getContactsBySite(Integer siteId) {
        return getContactsBySite(siteId, false);
    }

    public List<Contact> getContactsBySite(Integer siteId, boolean onlyActive) {
        List<Contact> list = new ArrayList<>();
        List<Integer> id = getContactsIdBySite(siteId, onlyActive);
        for (Integer i : id)
            list.add(getContactById(i));
        return list;
    }

    private List<Integer> getContactsIdBySite(Integer siteId, boolean onlyActive) {
        List<Integer> list = new ArrayList<>();
        try (Connection con = dataSource.getConnection(); PreparedStatement stmt = con.prepareStatement
                ("SELECT id FROM contact where siteid=?" + (onlyActive ? " and active=?" : ""))) {
            stmt.setInt(1, siteId);
            if (onlyActive) {
                stmt.setBoolean(2,true);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
                list.add(rs.getInt("id"));
            return list;
        }
        catch (SQLException e) {
            String msg = "error in getContactsBySite for siteid=" + siteId + (onlyActive ? " and active=true" : "") + ": ";
            log.error(msg, e);
            throw new DataRetrievalFailureException(msg, e);
        }
    }

    public Contact getContactById (int id) {
        try (Connection con = dataSource.getConnection(); PreparedStatement stmt = con.prepareStatement
                ("SELECT * FROM contact WHERE id=?")) {
            stmt.setInt(1,id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return getContactFromRS(rs);
            }
            log.info("no contact with id="+id);
            return null;
        }
        catch (SQLException e) {
            String msg = "error in getContactById for id=" + id;
            log.error(msg, e);
            throw new DataRetrievalFailureException(msg, e);
        }
    }

    private Contact getContactFromRS(ResultSet rs) throws SQLException {
        return new Contact(
                rs.getInt("id"), rs.getString("name"), rs.getBoolean("active"),
                rs.getInt("siteid"), rs.getString("phone"), rs.getString("notes")
        );
    }

    public long insertContact (String name, int siteId, String phone, String notes) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement stmt = con.prepareStatement("insert into contact" +
                             " (name,siteId,phone,notes)" +
                             " values (?,?,?,?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1,name);
            stmt.setInt(2,siteId);
            stmt.setString(3,phone);
            stmt.setString(4,notes);
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
                        log.info("contact id=" + id + " has been created");
                        return id;
                    }
                    throw new SQLException("error getting the new key");
                }
                catch (SQLException e) {
                    log.error("error getting the new key",e);
                    throw new InsertException("error getting the new key",e);
                }
            }
        }
        catch (SQLException e) {
            String msg = "error in insertContact (\"" + name + "\"): ";
            log.error(msg, e);
            throw new InsertException(msg, e);
        }
    }

    public int deleteContact(int id) {
        int n;
        try (Connection con = dataSource.getConnection();
             PreparedStatement stmt = con.prepareStatement("delete from contact where id=?",
                     Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1,id);
            n=stmt.executeUpdate();
            if (n == 1) {
                log.info("contact id=" + id + " has been deleted");
            }
            else {
                throw new SQLException(n + " contacts have been deleted for id=" + id);
            }
            return n;
        }
        catch (SQLException e) {
            String msg = "error in deleteContact with id=" + id + ": ";
            log.error(msg,e);
            throw new DeleteException(msg,e);
        }
    }

    public void updateContact (Contact contact) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement stmt = con.prepareStatement("update contact" +
                     " set name=?,active=?,phone=?,notes=?"+
                     " where id=?")) {
            stmt.setString(1, contact.getName());
            stmt.setBoolean(2, contact.getActive());
            stmt.setString(3, contact.getPhone());
            stmt.setString(4, contact.getNotes());
            stmt.setInt(5, contact.getId());
            int n=stmt.executeUpdate();
            if (n == 0) {
                throw new SQLException("no record has been updated");
            }
        }
        catch (SQLException e) {
            String msg = "error in updateContact (id=" + contact.getId() + "): ";
            log.error(msg, e);
            throw new UpdateException(msg, e);
        }
    }
}
