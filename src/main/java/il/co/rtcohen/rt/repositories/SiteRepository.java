package il.co.rtcohen.rt.repositories;
import il.co.rtcohen.rt.dao.Site;
import il.co.rtcohen.rt.exceptions.DeleteException;
import il.co.rtcohen.rt.exceptions.InsertException;
import il.co.rtcohen.rt.exceptions.UpdateException;
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
public class SiteRepository {

    static final private Logger log = LoggerFactory.getLogger(SiteRepository.class);

    private DataSource dataSource;

    @Autowired
    public SiteRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Site> getSitesByCustomer(Integer customerId) {
        List<Site> list = new ArrayList<>();
        List<Integer> id = getIdByCustomer(customerId,false);
        for (Integer i : id)
            list.add(getSiteById(i));
        return list;
    }

    public List<Integer> getActiveIdByCustomer(Integer customerId) {
        return getIdByCustomer(customerId,true);
    }

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

    private List<Integer> getByCustomer(Integer customerId) {
        List<Integer> list = new ArrayList<>();
        try (Connection con = dataSource.getConnection(); PreparedStatement stmt = con.prepareStatement
                ("SELECT id FROM site  where custid=?")) {
            stmt.setInt(1,customerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
                list.add(rs.getInt("id"));
            return list;
        }
        catch (SQLException e) {
            log.error("error in getByCustomer for customer="+customerId+": ",e);
            throw new DataRetrievalFailureException("error in getByCustomer for customer="+customerId+": ",e);
        }
    }

    private List<Integer> getActiveByCustomer(Integer customerId) {
        List<Integer> list = new ArrayList<>();
        try (Connection con = dataSource.getConnection(); PreparedStatement stmt = con.prepareStatement
                ("SELECT id FROM site  where custid=? and active=?")) {
            stmt.setInt(1,customerId);
            stmt.setBoolean(2,true);
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
                list.add(rs.getInt("id"));
            return list;
        }
        catch (SQLException e) {
            log.error("error in getActiveByCustomer for customer="+customerId+": ",e);
            throw new DataRetrievalFailureException("error in getActiveByCustomer for customer="+customerId+": ",e);
        }
    }

    private List<Integer> getActive() {
        List<Integer> list = new ArrayList<>();
        try (Connection con = dataSource.getConnection(); PreparedStatement stmt = con.prepareStatement
                ("SELECT id FROM site  where active=?")) {
            stmt.setBoolean(1,true);
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
                list.add(rs.getInt("id"));
            return list;
        }
        catch (SQLException e) {
            log.error("error in getActive: ",e);
            throw new DataRetrievalFailureException("error in getActive: ",e);
        }
    }

    private List<Integer> getByCustomer(String sql) {
        List<Integer> list = new ArrayList<>();
        try (Connection con = dataSource.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
                list.add(rs.getInt("id"));
            return list;
        }
        catch (SQLException e) {
            log.error("SQL statement: "+sql);
            log.error("error in getByCustomer: ",e);
            throw new DataRetrievalFailureException("error in getByCustomer: ",e);
        }
    }

    public Site getSiteById (int id) {
        try (Connection con = dataSource.getConnection(); PreparedStatement stmt = con.prepareStatement
                ("SELECT * FROM site WHERE id=?")) {
            stmt.setInt(1,id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return getSiteFromRS(rs);
            }
            log.info("no site with id="+id);
            return (new Site());
        }
        catch (SQLException e) {
            log.error("error in getSiteById where id="+id+": ",e);
            throw new DataRetrievalFailureException("error in getSiteById where id="+id+": ",e);
        }
    }

    private Site getSiteFromRS(ResultSet rs) throws SQLException {
        return new Site(rs.getInt("custid"),rs.getInt("id"),rs.getString("name"),rs.getInt("areaID"),
            rs.getString("address"),rs.getBoolean("active"),rs.getString("contact"),
            rs.getString("phone"),rs.getString("notes"));
    }

    public long     insertSite (String name,Integer areaId,String address, Integer customerId, String contact, String phone, String notes) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement stmt = con.prepareStatement("insert into site"+
                             " (name,areaID,address,custid,contact,phone,notes)"+
                             " values (?,?,?,?,?,?,?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1,name);
            stmt.setInt(2,areaId);
            stmt.setString(3,address);
            stmt.setInt(4,customerId);
            stmt.setString(5,contact);
            stmt.setString(6,phone);
            stmt.setString(7,notes);
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
                        log.info("site id=" + id + " has been created");
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
            log.error("error in insertSite (\""+name+"\"): ",e);
            throw new InsertException("error in insertSite (\""+name+"\"): ",e);
        }
    }

    public int deleteSite(int id) {
        int n;
        try (Connection con = dataSource.getConnection();
             PreparedStatement stmt = con.prepareStatement("delete from site where id=?",
                     Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1,id);
            n=stmt.executeUpdate();
            if (n == 1) {
                log.info("site id="+id+" has been deleted");
            }
            else {
                throw new SQLException(n+" sites have been deleted for id="+id);
            }
            return n;
        }
        catch (SQLException e) {
            log.error("error in deleteSite: ",e);
            throw new DeleteException("error in deleteSite: ",e);
        }
    }

    public long updateSite (Site site) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement stmt = con.prepareStatement("update site"+
                     " set name=?,areaID=?,address=?,custid=?,contact=?,phone=?,notes=?"+
                     " where id=?")) {
            stmt.setString(1,site.getName());
            stmt.setInt(2,site.getAreaId());
            stmt.setString(3,site.getAddress());
            stmt.setInt(4,site.getCustomerId());
            stmt.setString(5,site.getContact());
            stmt.setString(6,site.getPhone());
            stmt.setString(7,site.getNotes());
            stmt.setInt(8,site.getId());
            int n=stmt.executeUpdate();
            if (n == 0) {
                throw new SQLException("no record has been updated");
            }
            else
                return n;
        }
        catch (SQLException e) {
            log.error("error in updateSite (id="+site.getId()+"):",e);
            throw new UpdateException("error in updateSite (id="+site.getId()+"):",e);
        }
    }

}

