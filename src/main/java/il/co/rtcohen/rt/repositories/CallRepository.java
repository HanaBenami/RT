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
        return getCallsList("");
    }

    public List<Call> getCallsBySite(Integer siteId) {
        return getCallsList(" where siteid="+siteId);
    }

    private List<Call> getCallsList(String where) {
        String sql="";
        try (Connection con = dataSource.getConnection(); Statement stmt = con.createStatement()) {
            sql="select * from call"+where;
            ResultSet rs = stmt.executeQuery(sql);
            return getListFromRS(rs);
        }
        catch (SQLException e) {
            log.info(sql);
            log.error("error in getCallsList: ",e);
            throw new DataRetrievalFailureException("error in getCallsList: ",e);
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

    public List<Call> getCalls(String date) {
        String where=" where date2='"+date+"'";
        return getCallsList(where);
    }

    public List<Call> getCalls(LocalDate date) {
        return getCalls(date.format(Call.dateFormatter));
    }

    public List<Call> getCalls(LocalDate date, int driver) {
        return getCalls(date.format(Call.dateFormatter),driver);
    }

    public List<Call> getCalls(String date, int driver) {
        String where=" where date2='"+date+"' and driverid='"+driver+"'";
        return getCallsList(where);
    }

    public List<Call> getLocalCalls() {
        String where=" where here='true'";
        return getCallsList(where);
    }

    public List<Call> getCalls(Boolean isDone) {
        String where=" where done='"+isDone+"'";
        return getCallsList(where);
    }

    public List<Call> getOpenCallsPerArea(int area) {
        String where;
        if(areaRepository.getAreaById(area).getName().equals("מוסך"))
        {
            where = " where done='false' and ((siteid in (select id from site where areaid=" + area + ")) " +
                    " or (here='true' and date2='" + Call.nullDateString + "')) order by date2";
        }
        else {
            where = " where done='false' and siteid in (select id from site where areaid=" + area + ") " +
                    "and (here='false' or (here='true' and date2!='" + Call.nullDateString + "')) order by date2";
        }
        return getCallsList(where);
    }

    public int countActiveCallsByCustomer (Integer customerId) {
        String where=" where done='false' and custid='"+customerId+"'";
        return getCallsList(where).size();
    }

    public Call getCallById(Integer id) {
        String where=" where id='"+id+"'";
        List<Call> calls = getCallsList(where);
        if (calls.isEmpty())
            return new Call();
        else
            return calls.get(0);
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
        int n=0;
        try (Connection con = dataSource.getConnection();
             PreparedStatement stmt = con.prepareStatement("delete from call where id=?",
                     Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1,call.getId());
            n=stmt.executeUpdate();
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
            if ((call.getDate2()!=call.getPreDate2()) || (call.getOrder()!=call.getPreOrder())
                    || (call.getDriverId()!=call.getPreDriverId()) )
                n += updateCallPlan(call);
            return n;
        } catch (SQLException e) {
            log.error("error in updateCall (id="+call.getId()+"): ",e);
            throw new UpdateException("error in updateCall (id="+call.getId()+"): ",e);
        }
    }

    private int newOrder(Call call) {
        String sql = "";
        int newOrder = 1;
        if (call.getDriverId() == 0)
            return 0;
        else if ((call.getDate2().format(Call.dateFormatter)).equals(Call.nullDateString))
            return 0;
        else {
            sql = "select max(workorder) workorder from call where driverid=" + call.getDriverId() + " and date2='" + (call.getDate2().format(call.dateFormatter)) + "'";
            try (Connection con = dataSource.getConnection(); Statement stmt = con.createStatement()) {
                ResultSet rs = stmt.executeQuery(sql);
                log.info("SQL statement: "+sql);
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

    private int updatequery(String sql) {
        try (Connection con = dataSource.getConnection(); Statement stmt = con.createStatement()) {
            int n = stmt.executeUpdate(sql);
            log.info("SQL statement: "+sql);
            return n;
        } catch (SQLException e) {
            log.error("error in updatequery: ",e);
            throw new UpdateException("error in updatequery: ",e);
        }
    }

    private int updateCallPlan(Call call) {
        String sql;
        int n = 0;
        sql = "update call set workorder=0 where id=" + call.getId();
        n += updatequery(sql);

        //fix newOrder in case of null values in other fields or too big new value in newOrder
        if ((call.getDriverId() == 0)
                || ((call.getDate2().format(Call.dateFormatter)).equals(Call.nullDateString))
                || (call.getOrder() == 0) || (call.getOrder() > newOrder(call)) )
            call.setOrder(newOrder(call));

        // if there is no change and date and driver and there are valid values
        if ((call.getPreDriverId() == call.getDriverId())
                && ((call.getPreDate2().format(Call.dateFormatter)).equals(call.getDate2().format(Call.dateFormatter)))
                && (call.getDriverId() != 0)
                && !((call.getDate2().format(Call.dateFormatter)).equals(Call.nullDateString)))
            n+=updateCallPlanNoChange(call);

        // if there is change and date and driver
        else
            n+=updateCallPlanChange(call);

        // update the call with its new values
        sql = "update call set date2='" + call.getDate2().format(Call.dateFormatter)
                + "', driverid=" + call.getDriverId()
                + ", workorder=" + call.getOrder()
                + " where id=" + call.getId();
        n += updatequery(sql);
        call.setPreOrder(call.getOrder());
        call.setPreDriverId(call.getDriverId());
        call.setPreDate2(call.getDate2());
        return n;

    }


    private int updateCallPlanNoChange(Call call) {
        String sql;
        int n = 0;

            //validate new order value not too big
            if (call.getOrder() > newOrder(call)) {
                call.setOrder(newOrder(call) - 1);
            };

            // if the call had no order value before the change
            // fix others call with its new driver and date
            if ((call.getPreOrder() == 0) && (call.getOrder() != 0)) {
                sql = query("+1",call.getDriverId(),call.getDate2()
                        ,">=",call.getOrder());
                n += updatequery(sql);
            }

            // if the call had order value before
            else {
                // if order value is smaller than before or from next valid value
                // fix order value in other calls with the same date and driver
                if (call.getPreOrder() > call.getOrder()) {
                    sql = query("+1",call.getDriverId(),call.getDate2()
                            ,">=",call.getOrder(),"<",call.getPreOrder());
                    n += updatequery(sql);
                }

                //if order value is bigger than before
                // fix order value in other calls with the same date and driver
                if (call.getPreOrder() < call.getOrder()) {
                    sql = query("-1",call.getDriverId(),call.getDate2()
                            ,"<=",call.getOrder(),">",call.getPreOrder());
                    n += updatequery(sql);
                }
            }

        return n;
    }

    private int updateCallPlanChange(Call call) {
        String sql;
        int n = 0;

        // if valid driver and date
        // fix order value in other calls with the same date and driver
        // (according to new values)
        if ((call.getDriverId() != 0)
                && !((call.getDate2().format(Call.dateFormatter)).equals(Call.nullDateString))) {
            sql = query("+1", call.getDriverId(), call.getDate2()
                    , ">=", call.getOrder());
            n += updatequery(sql);
        }

        // if the call had previous order value with valid driver and date
        // fix order value in other calls with the same date and driver
        // (according to previous values)
        if ((call.getPreOrder() != 0) && (call.getPreDriverId() != 0)
                && !((call.getPreDate2().format(Call.dateFormatter)).equals(Call.nullDateString))) {
            sql = query("-1", call.getPreDriverId(), call.getPreDate2()
                    , ">", call.getPreOrder());
            n += updatequery(sql);
        }

        return n;
    }

    private String query (String plus, int driver, LocalDate date,
                          String orderOperator, int order, String orderOperator2, int order2) {
        String sql =query(plus, driver, date, orderOperator, order);
        sql += " and workorder" + orderOperator2 + order2;
        return sql;
    }

    private String query (String plus, int driver, LocalDate date, String orderOperator, int order) {
        String sql ="";
        sql = "update call set workorder=workorder"+plus+" where "
                + "driverid=" + driver
                + " and date2='" + (date.format(Call.dateFormatter))
                + "' and workorder" + orderOperator + order;
        return sql;
    }

}

