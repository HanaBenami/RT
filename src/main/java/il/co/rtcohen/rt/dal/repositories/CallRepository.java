package il.co.rtcohen.rt.dal.repositories;

import il.co.rtcohen.rt.dal.dao.Call;
import il.co.rtcohen.rt.dal.UpdateException;
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
public class CallRepository {   // TODO: extends AbstractRepository

    final static private Logger log = LoggerFactory.getLogger(CallRepository.class);

    private DataSource dataSource;

    private AreasRepository areasRepository;

    @Autowired
    public CallRepository(DataSource dataSource, AreasRepository areasRepository) {
        this.areasRepository = areasRepository;
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
                    rs.getBoolean("meeting"),rs.getBoolean("done"),rs.getBoolean("deleted"),
                    rs.getBoolean("here"),rs.getInt("driverid"),rs.getInt("workorder"),
                    rs.getInt("userid")));
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

    public List<Call> getCalls(Boolean isDone, Boolean isDeleted) {
        try (Connection con = dataSource.getConnection(); PreparedStatement stmt =
                con.prepareStatement("select * from call where done=? and deleted=?")) {
            stmt.setBoolean(1, isDone);
            stmt.setBoolean(2, isDeleted);
            ResultSet rs = stmt.executeQuery();
            return getListFromRS(rs);
        }
        catch (SQLException e) {
            String msg = "error in getCalls where done=" + isDone + " and deleted=" + isDeleted + ": ";
            log.error(msg, e);
            throw new DataRetrievalFailureException(msg,e);
        }
    }

    public List<Call> getCalls(LocalDate date, Boolean isDone, Boolean isDeleted) {
        return getCalls(date.format(Call.dateFormatter), isDone, isDeleted);
    }

    public List<Call> getCalls(String date, Boolean isDone, Boolean isDeleted) {
        try (Connection con = dataSource.getConnection(); PreparedStatement stmt =
                con.prepareStatement("select * from call where done=? and deleted=? and ?<endDate")) {
            stmt.setBoolean(1, isDone);
            stmt.setBoolean(2, isDeleted);
            stmt.setString(3, date);
            ResultSet rs = stmt.executeQuery();
            return getListFromRS(rs);
        }
        catch (SQLException e) {
            String msg = "error in getCalls where done=" + isDone + " and deleted=" + isDeleted +
                    "and : " + date + "<date2: ";
            log.error(msg,e);
            throw new DataRetrievalFailureException(msg,e);
        }
    }

    public List<Call> getOpenCallsPerArea(int area) {
        if(areasRepository.getAreaById(area).getName().equals("מוסך"))
            return getGarageCalls(area);
        else
            return getAreaCalls(area);
    }

    private List<Call> getGarageCalls(int area) {
        try (Connection con = dataSource.getConnection(); PreparedStatement stmt =
                con.prepareStatement("select * from v_opencall where done=? " +
                        "and ((areaid=?) or (here=? and date2=?)) " +
                        "order by date2")) {
            stmt.setBoolean(1,false);
            stmt.setInt(2,area);           // garage area
            stmt.setBoolean(3,true);    // or here=true + date2=empty
            stmt.setString(4, Call.nullDateString);
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
                con.prepareStatement("select * from v_opencall where done=? " +
                        "and (areaid=?) " +
                        "and (here=? or (here=? and date2!=?)) order by date2")) {
            stmt.setBoolean(1,false);
            stmt.setInt(2,area);           // selected area
            // and...
            stmt.setBoolean(3,false);   // (here=false)
            stmt.setBoolean(4,true);    // or (here=true + date2=not-empty)
            stmt.setString(5, Call.nullDateString);
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

    public long insertCall(int customerId, LocalDate startDate, int userId) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement stmt = con.prepareStatement("insert into call (custID,startdate,userID) values (?,?,?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1,customerId);
            stmt.setString(2,startDate.format(Call.dateFormatter));
            stmt.setInt(3,userId);
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

    public long insertCall(int customerId, LocalDate startDate, int userId, int siteId) {
        long id = insertCall(customerId,startDate, userId);
        Call call = getCallById((int)id);
        call.setSiteId(siteId);
        updateCall(call);
        return id;
    }

//    public int deleteCall(int id) {
//        Call call = getCallById(id);
//        call.setDate2(Call.nullDate);
//        try (Connection con = dataSource.getConnection();
//             PreparedStatement stmt = con.prepareStatement("delete from call where id=?",
//                     Statement.RETURN_GENERATED_KEYS)) {
//            stmt.setInt(1,call.getId());
//            int n=stmt.executeUpdate();
//            if (n == 1) {
//                log.info("call id="+call.getId()+" has been deleted");
//            }
//            else {
//                throw new SQLException(n+" records have been deleted for id="+call.getId());
//            }
//            return n;
//        }
//        catch (SQLException e) {
//            log.error("error in deleteCall: ",e);
//            throw new DeleteException("error in deleteCall: ",e);
//        }
//    }

    public void updateCall(Call call) {
        String msg = "updateCall with id=" + call.getId() + ": ";
        try (Connection con = dataSource.getConnection(); PreparedStatement stmt = con.prepareStatement(
                "update call set custid=?, siteid=?, descr=?, cartypeid=?, calltypeid=?, notes=?, startdate=?, " +
                        "date1=?, enddate=?, meeting=?, done=?, here=?, deleted=?, userId=? where id=?")) {
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
            stmt.setBoolean(13, call.isDeleted());
            stmt.setInt(14, call.getUserId());
            stmt.setInt(15, call.getId());
            msg += stmt.toString();
            stmt.executeUpdate();
            log.info(msg);
        } catch (SQLException e) {
            log.error("error in " + msg, e);
            throw new UpdateException("error in " + msg,e);
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

    private void updateQuery(String sql) {
        try (Connection con = dataSource.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.executeUpdate();
            log.info("SQL statement: "+sql);
        } catch (SQLException e) {
            log.error("error in updateQuery: ",e);
            throw new UpdateException("error in updateQuery: ",e);
        }
    }

    public void updateQuery(String plus, int driver, LocalDate date,
                            String orderOperator, int order, String orderOperator2, int order2) {
        String sql = stringUpdateQuery(plus, driver, date, orderOperator, order)
                + " and workorder" + orderOperator2 + order2;
        updateQuery(sql);
    }

    public void updateQuery(String plus, int driver, LocalDate date, String orderOperator, int order) {
        String sql = stringUpdateQuery(plus,driver,date,orderOperator,order);
        updateQuery(sql);
    }

    private String stringUpdateQuery(String plus, int driver, LocalDate date, String orderOperator, int order) {
        return "update call set workorder=workorder"+plus+" where "
                + "driverid=" + driver
                + " and date2='" + (date.format(Call.dateFormatter))
                + "' and workorder" + orderOperator + order;
    }

    public void resetOrderQuery(Call call) {
        String sql = "update call set workorder=0 where id=" + call.getId();
        updateQuery(sql);
    }

    public void updateOrderQuery(Call call) {
        String sql = "update call set date2='" + call.getDate2().format(Call.dateFormatter)
                + "', driverid=" + call.getDriverId()
                + ", workorder=" + call.getOrder()
                + " where id=" + call.getId();
        updateQuery(sql);
    }
}
