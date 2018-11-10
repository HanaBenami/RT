package il.co.rtcohen.rt.repositories;

import il.co.rtcohen.rt.dao.Call;
import il.co.rtcohen.rt.exceptions.DeleteException;
import il.co.rtcohen.rt.exceptions.UpdateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CallRepository {

    final static private Logger log = LoggerFactory.getLogger(CallRepository.class);

    private DataSource dataSource;

    private AreaRepository areaRepository;

    @Autowired
    public CallRepository(DataSource dataSource,AreaRepository areaRepository) {
        this.areaRepository = areaRepository;
        this.dataSource = dataSource;
    }

    public List<Call> getCalls() {
        try (Connection con = dataSource.getConnection(); PreparedStatement stmt =
                con.prepareStatement("select * from call")) {
            ResultSet rs = stmt.executeQuery();
            return getListFromRS(rs);
        }
        catch (SQLException e) {
            log.error("error in getCalls: ",e);
            throw new DataRetrievalFailureException("error in getCalls: ",e);
        }
    }

    public List<Call> getCallsBySite(Integer siteId) {
        try (Connection con = dataSource.getConnection(); PreparedStatement stmt =
                con.prepareStatement("select * from call where siteid=?")) {
            stmt.setInt(1,siteId);
            ResultSet rs = stmt.executeQuery();
            return getListFromRS(rs);
        }
        catch (SQLException e) {
            log.error("error in getCallsBySite for siteId="+siteId+": ",e);
            throw new DataRetrievalFailureException("error in getCallsBySite for siteId="+siteId+": ",e);
        }
    }

    private List<Call> getListFromRS(ResultSet rs) throws SQLException {
        List<Call> list = new ArrayList<>();
        while (rs.next()) {
            list.add(new Call(rs.getInt("id"),rs.getInt("custid"),
                    rs.getInt("siteid"),rs.getString("descr"),rs.getInt("cartypeid"),
                    rs.getInt("calltypeid"),rs.getString("notes"),rs.getString("startdate"),
                    rs.getString("date1"),rs.getString("date2"),rs.getString("enddate"),
                    rs.getBoolean("meeting"),rs.getBoolean("done"),rs.getBoolean("here"),
                    rs.getInt("driverid"),rs.getInt("workorder")));
        }
        return list;
    }

    private List<Call> getCalls(String date) {
        try (Connection con = dataSource.getConnection(); PreparedStatement stmt =
                con.prepareStatement("select * from call where date2=?")) {
            stmt.setString(1,date);
            ResultSet rs = stmt.executeQuery();
            return getListFromRS(rs);
        }
        catch (SQLException e) {
            log.error("error in getCalls where date2="+date+": ",e);
            throw new DataRetrievalFailureException("error in getCalls where date2="+date+": ",e);
        }
    }

    public List<Call> getCalls(LocalDate date) {
        return getCalls(date.format(Call.dateFormatter));
    }

    public List<Call> getCalls(LocalDate date, int driver) {
        return getCalls(date.format(Call.dateFormatter),driver);
    }

    private List<Call> getCalls(String date, int driver) {
        try (Connection con = dataSource.getConnection(); PreparedStatement stmt =
                con.prepareStatement("select * from call where date2=? and driverid=?")) {
            stmt.setString(1,date);
            stmt.setInt(2,driver);
            ResultSet rs = stmt.executeQuery();
            return getListFromRS(rs);
        }
        catch (SQLException e) {
            log.error("error in getCalls where date2="+date+" and driver="+driver+": ",e);
            throw new DataRetrievalFailureException("error in getCalls where date2="+date+" and driver="+driver+": ",e);
        }
    }

    public List<Call> getLocalCalls() {
        try (Connection con = dataSource.getConnection(); PreparedStatement stmt =
                con.prepareStatement("select * from call where here=?")) {
            stmt.setBoolean(1,true);
            ResultSet rs = stmt.executeQuery();
            return getListFromRS(rs);
        }
        catch (SQLException e) {
            log.error("error in getLocalCalls: ",e);
            throw new DataRetrievalFailureException("error in getLocalCalls: ",e);
        }
    }

    public List<Call> getCalls(Boolean isDone) {
        try (Connection con = dataSource.getConnection(); PreparedStatement stmt =
                con.prepareStatement("select * from call where done=?")) {
            stmt.setBoolean(1,isDone);
            ResultSet rs = stmt.executeQuery();
            return getListFromRS(rs);
        }
        catch (SQLException e) {
            log.error("error in getCalls where done="+isDone+": ",e);
            throw new DataRetrievalFailureException("error in getCalls where done="+isDone+": ",e);
        }
    }

    public List<Call> getOpenCallsPerArea(int area) {
        if(areaRepository.getAreaById(area).getName().equals("מוסך"))
            return getGarageCalls(area);
        else
            return getAreaCalls(area);
    }

    private List<Call> getGarageCalls(int area) {
        try (Connection con = dataSource.getConnection(); PreparedStatement stmt =
                con.prepareStatement("select * from call where done=? and ((siteid in " +
                        "(select id from site where areaid=?)) " +
                        "or (here=? and date2=?)) order by date2")) {
            stmt.setBoolean(1,false);
            stmt.setInt(2,area);
            stmt.setBoolean(3,true);
            stmt.setString(4,Call.nullDateString);
            ResultSet rs = stmt.executeQuery();
            return getListFromRS(rs);
        }
        catch (SQLException e) {
            log.error("error in getGarageCalls: ",e);
            throw new DataRetrievalFailureException("error getGarageCalls: ",e);
        }
    }

    private List<Call> getAreaCalls(int area) {
        try (Connection con = dataSource.getConnection(); PreparedStatement stmt =
                con.prepareStatement("select * from call where done=? and ((siteid in " +
                        "(select id from site where areaid=?)) " +
                        "and (here=? or (here=? and date2!=?))) order by date2")) {
            stmt.setBoolean(1,false);
            stmt.setInt(2,area);
            stmt.setBoolean(3,false);
            stmt.setBoolean(4,true);
            stmt.setString(5,Call.nullDateString);
            ResultSet rs = stmt.executeQuery();
            return getListFromRS(rs);
        }
        catch (SQLException e) {
            log.error("error in getAreaCalls for area="+area+": ",e);
            throw new DataRetrievalFailureException("error in getAreaCalls for area="+area+": ",e);
        }
    }

    public int countActiveCallsByCustomer (Integer customerId) {
        try (Connection con = dataSource.getConnection(); PreparedStatement stmt =
                con.prepareStatement("select * from call where done=? and custid=?")) {
            stmt.setBoolean(1,false);
            stmt.setInt(2,customerId);
            ResultSet rs = stmt.executeQuery();
            return getListFromRS(rs).size();
        }
        catch (SQLException e) {
            log.error("error in countActiveCallsByCustomer where customer="+customerId+": ",e);
            throw new DataRetrievalFailureException("error in countActiveCallsByCustomer where customer="+customerId+": ",e);
        }
    }

    public Call getCallById(Integer id) {
        try (Connection con = dataSource.getConnection(); PreparedStatement stmt =
                con.prepareStatement("select * from call where id=?")) {
            stmt.setInt(1,id);
            ResultSet rs = stmt.executeQuery();
            List<Call> list = getListFromRS(rs);
            if (list.isEmpty())
                return new Call();
            else
                return list.get(0);
        }
        catch (SQLException e) {
            log.error("error in getCallById where id="+id+": ",e);
            throw new DataRetrievalFailureException("error in getCallById where id="+id+": ",e);
        }
    }

    public long insertCall(int customerId, LocalDate startDate) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement stmt = con.prepareStatement("insert into call (custID,startdate) values (?,?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1,customerId);
            stmt.setString(2,startDate.format(Call.dateFormatter));
            int n=stmt.executeUpdate();
            if (n == 0) {
                throw new SQLException("no call has been created");
            }
            else if (n > 1) {
                throw new SQLException("more than one record has been created");
            }
            else {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long id = generatedKeys.getLong(1);
                        log.info("call id=" + id + " has been created");
                        return id;
                    }
                    throw new SQLException("error getting the new key");
                }
            }
        }
        catch (SQLException e) {
            log.error("error in insertCall: ",e);
            throw new UpdateException("error in insertCall: ",e);
        }
    }

    public long insertCall(int customerId, LocalDate startDate, int siteId) {
        long id = insertCall(customerId,startDate);
        Call call = getCallById((int)id);
        call.setSiteId(siteId);
        updateCall(call);
        return id;
    }

    public int deleteCall(int id) {
        Call call = getCallById(id);
        call.setDate2(Call.nullDate);
        try (Connection con = dataSource.getConnection();
             PreparedStatement stmt = con.prepareStatement("delete from call where id=?",
                     Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1,call.getId());
            int n=stmt.executeUpdate();
            if (n == 1) {
                log.info("call id="+call.getId()+" has been deleted");
            }
            else {
                throw new SQLException(n+" records have been deleted for id="+call.getId());
            }
            return n;
        }
        catch (SQLException e) {
            log.error("error in deleteCall: ",e);
            throw new DeleteException("error in deleteCall: ",e);
        }
    }

    public int updateCall(Call call) {
        try (Connection con = dataSource.getConnection(); PreparedStatement stmt = con.prepareStatement(
                "update call set custid=?, siteid=?, descr=?, cartypeid=?, calltypeid=?, notes=?, startdate=?, " +
                        "date1=?, enddate=?, meeting=?, done=?, here=? where id=?")) {
            stmt.setInt(1, call.getCustomerId());
            stmt.setInt(2, call.getSiteId());
            stmt.setString(3, call.getDescription());
            stmt.setInt(4, call.getCarTypeId());
            stmt.setInt(5, call.getCallTypeId());
            stmt.setString(6, call.getNotes());
            stmt.setString(7, call.getStartDate().format(Call.dateFormatter));
            stmt.setString(8, call.getDate1().format(Call.dateFormatter));
            stmt.setString(9, call.getEndDate().format(Call.dateFormatter));
            stmt.setBoolean(10, call.isMeeting());
            stmt.setBoolean(11, call.isDone());
            stmt.setBoolean(12, call.isHere());
            stmt.setInt(13, call.getId());
            int n = stmt.executeUpdate();
            log.info("updateCall with id="+call.getId()+": "+stmt);
            return n;
        } catch (SQLException e) {
            log.error("error in updateCall (id="+call.getId()+"): ",e);
            throw new UpdateException("error in updateCall (id="+call.getId()+"): ",e);
        }
    }

    public int newOrder(Call call) {
        int newOrder = 1;
        if (call.getDriverId() == 0)
            return 0;
        else if ((call.getDate2().format(Call.dateFormatter)).equals(Call.nullDateString))
            return 0;
        else {
            try (Connection con = dataSource.getConnection(); PreparedStatement stmt = con.prepareStatement
                    ("select max(workorder) workorder from call where driverid=? and date2=?")) {
                stmt.setInt(1,call.getDriverId());
                stmt.setString(2,call.getDate2().format(Call.dateFormatter));
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    newOrder = (rs.getInt("workorder")) + 1;
                }
                return newOrder;
            } catch (SQLException e) {
                log.error("error in newOrder (call id="+call.getId()+": ",e);
                throw new UpdateException("error in newOrder (call id="+call.getId()+": ",e);
            }
        }
    }

    public int updateQuery(String sql) {
        try (Connection con = dataSource.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            int n = stmt.executeUpdate();
            log.info("SQL statement: "+sql);
            return n;
        } catch (SQLException e) {
            log.error("error in updateQuery: ",e);
            throw new UpdateException("error in updateQuery: ",e);
        }
    }

    public int updateQuery(String plus, int driver, LocalDate date,
                            String orderOperator, int order, String orderOperator2, int order2) {
        String sql = stringUpdateQuery(plus, driver, date, orderOperator, order)
                + " and workorder" + orderOperator2 + order2;
        return updateQuery(sql);
    }

    public int updateQuery(String plus, int driver, LocalDate date, String orderOperator, int order) {
        String sql = stringUpdateQuery(plus,driver,date,orderOperator,order);
        return updateQuery(sql);
    }

    public String stringUpdateQuery(String plus, int driver, LocalDate date, String orderOperator, int order) {
        return "update call set workorder=workorder"+plus+" where "
                + "driverid=" + driver
                + " and date2='" + (date.format(Call.dateFormatter))
                + "' and workorder" + orderOperator + order;
    }

    public int resetOrderQuery(Call call) {
        String sql = "update call set workorder=0 where id=" + call.getId();
        return updateQuery(sql);
    }

    public int updateOrderQuery(Call call) {
        String sql = "update call set date2='" + call.getDate2().format(Call.dateFormatter)
                + "', driverid=" + call.getDriverId()
                + ", workorder=" + call.getOrder()
                + " where id=" + call.getId();
        return updateQuery(sql);
    }

}

