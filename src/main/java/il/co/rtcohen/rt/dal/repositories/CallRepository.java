package il.co.rtcohen.rt.dal.repositories;

import il.co.rtcohen.rt.dal.repositories.interfaces.AbstractTypeRepository;
import il.co.rtcohen.rt.utils.NullPointerExceptionWrapper;
import il.co.rtcohen.rt.dal.dao.*;
import il.co.rtcohen.rt.dal.repositories.exceptions.UpdateException;
import il.co.rtcohen.rt.utils.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

@Repository
public class CallRepository extends AbstractTypeRepository<Call> implements RepositoryInterface<Call> {
    static protected final String DB_CUSTOMER_ID_COLUMN = "custID";
    static protected final String DB_SITE_ID_COLUMN = "siteID";
    static protected final String DB_VEHICLE_ID_COLUMN = "vehicleId";
    static protected final String DB_CALL_TYPE_ID_COLUMN = "calltypeid";
    static protected final String DB_DESCRIPTION_COLUMN = "descr";
    static protected final String DB_NOTES_COLUMN = "notes";
    static protected final String DB_START_DATE_COLUMN = "startdate";
    static protected final String DB_PLANNING_DATE_COLUMN = "date1";
    static protected final String DB_SCHEDULED_DATE_COLUMN = "date2";
    static protected final String DB_END_DATE_COLUMN = "enddate";
    static protected final String DB_SCHEDULED_ORDER_COLUMN = "workorder";
    static protected final String DB_DRIVER_ID_COLUMN = "driverid";
    static protected final String DB_IS_MEETING_COLUMN = "meeting";
    static protected final String DB_IS_DONE_COLUMN = "done";
    static protected final String DB_IS_HERE_COLUMN = "here";
    static protected final String DB_IS_DELETED_COLUMN = "deleted";
    static protected final String DB_USER_ID_COLUMN = "userid";

    private final CustomerRepository customerRepository;
    private final SiteRepository siteRepository;
    private final VehicleRepository vehicleRepository;
    private final CallTypeRepository callTypeRepository;
    private final DriverRepository driverRepository;
    private final UsersRepository usersRepository;

    @Autowired
    public CallRepository(DataSource dataSource,
                          CustomerRepository customerRepository,
                          SiteRepository siteRepository,
                          VehicleTypeRepository vehicleTypeRepository,
                          VehicleRepository vehicleRepository,
                          CallTypeRepository callTypeRepository,
                          DriverRepository driverRepository,
                          UsersRepository usersRepository
    ) {
        super(
                dataSource, "call", "Calls",
                new String[]{
                        DB_CUSTOMER_ID_COLUMN,
                        DB_SITE_ID_COLUMN,
                        DB_VEHICLE_ID_COLUMN,
                        DB_CALL_TYPE_ID_COLUMN,
                        DB_DESCRIPTION_COLUMN,
                        DB_NOTES_COLUMN,
                        DB_START_DATE_COLUMN,
                        DB_PLANNING_DATE_COLUMN,
                        DB_SCHEDULED_DATE_COLUMN,
                        DB_END_DATE_COLUMN,
                        DB_SCHEDULED_ORDER_COLUMN,
                        DB_DRIVER_ID_COLUMN,
                        DB_IS_MEETING_COLUMN,
                        DB_IS_DONE_COLUMN,
                        DB_IS_HERE_COLUMN,
                        DB_IS_DELETED_COLUMN,
                        DB_USER_ID_COLUMN
                }
        );
        this.customerRepository = customerRepository;
        this.siteRepository = siteRepository;
        this.vehicleRepository = vehicleRepository;
        this.callTypeRepository = callTypeRepository;
        this.driverRepository = driverRepository;
        this.usersRepository = usersRepository;
    }

    protected Call getItemFromResultSet(ResultSet rs) throws SQLException {
        return new Call(
                rs.getInt(DB_ID_COLUMN),
                customerRepository.getItem(rs.getInt(DB_CUSTOMER_ID_COLUMN)),
                siteRepository.getItem(rs.getInt(DB_SITE_ID_COLUMN)),
                vehicleRepository.getItem(rs.getInt(DB_VEHICLE_ID_COLUMN)),
                callTypeRepository.getItem(rs.getInt(DB_CALL_TYPE_ID_COLUMN)),
                rs.getString(DB_DESCRIPTION_COLUMN),
                rs.getString(DB_NOTES_COLUMN),
                new Date(rs.getString(DB_START_DATE_COLUMN)),
                new Date(rs.getString(DB_PLANNING_DATE_COLUMN)),
                new Date(rs.getString(DB_SCHEDULED_DATE_COLUMN)),
                new Date(rs.getString(DB_END_DATE_COLUMN)),
                rs.getInt(DB_SCHEDULED_ORDER_COLUMN),
                driverRepository.getItem(rs.getInt(DB_DRIVER_ID_COLUMN)),
                rs.getBoolean(DB_IS_MEETING_COLUMN),
                rs.getBoolean(DB_IS_DONE_COLUMN),
                rs.getBoolean(DB_IS_HERE_COLUMN),
                rs.getBoolean(DB_IS_DELETED_COLUMN),
                usersRepository.getItem(rs.getInt(DB_USER_ID_COLUMN))
        );
    }

    @Override
    protected int updateItemDetailsInStatement(PreparedStatement preparedStatement, Call call) throws SQLException {
        int fieldsCounter = 1;
        preparedStatement.setInt(fieldsCounter, call.getId());
        fieldsCounter++;
        preparedStatement.setInt(fieldsCounter, NullPointerExceptionWrapper.getWrapper(call, c -> c.getSite().getId(), 0));
        fieldsCounter++;
        preparedStatement.setInt(fieldsCounter, NullPointerExceptionWrapper.getWrapper(call, c -> c.getVehicle().getId(), 0));
        fieldsCounter++;
        preparedStatement.setInt(fieldsCounter, NullPointerExceptionWrapper.getWrapper(call, c -> c.getCallType().getId(), 0));
        fieldsCounter++;
        preparedStatement.setString(fieldsCounter, call.getDescription());
        fieldsCounter++;
        preparedStatement.setString(fieldsCounter, call.getNotes());
        fieldsCounter++;
        preparedStatement.setString(fieldsCounter, NullPointerExceptionWrapper.getWrapper(call, c -> c.getStartDate().toString(), ""));
        fieldsCounter++;
        preparedStatement.setString(fieldsCounter, NullPointerExceptionWrapper.getWrapper(call, c -> c.getPlanningDate().toString(), ""));
        fieldsCounter++;
        preparedStatement.setString(fieldsCounter, NullPointerExceptionWrapper.getWrapper(call, c -> c.getCurrentScheduledDate().toString(), ""));
        fieldsCounter++;
        preparedStatement.setString(fieldsCounter, NullPointerExceptionWrapper.getWrapper(call, c -> c.getEndDate().toString(), ""));
        fieldsCounter++;
        preparedStatement.setInt(fieldsCounter, call.getCurrentScheduledOrder());
        fieldsCounter++;
        preparedStatement.setInt(fieldsCounter, NullPointerExceptionWrapper.getWrapper(call, c -> c.getCurrentDriver().getId(), 0));
        fieldsCounter++;
        preparedStatement.setBoolean(fieldsCounter, call.isMeeting());
        fieldsCounter++;
        preparedStatement.setBoolean(fieldsCounter, call.isDone());
        fieldsCounter++;
        preparedStatement.setBoolean(fieldsCounter, call.isHere());
        fieldsCounter++;
        preparedStatement.setBoolean(fieldsCounter, call.isDeleted());
        fieldsCounter++;
        preparedStatement.setInt(fieldsCounter, NullPointerExceptionWrapper.getWrapper(call, c -> c.getUser().getId(), 0));
        return fieldsCounter;
    }

    public List<Call> getItems(Site site) throws SQLException {
        return getItems(DB_SITE_ID_COLUMN + "=" + site.getId());
    }

    public List<Call> getItems(Vehicle vehicle) throws SQLException {
        return getItems(DB_VEHICLE_ID_COLUMN + "=" + vehicle.getId());
    }

    public List<Call> getItems(Date scheduledDate) {
        List<Call> list = null;
        try {
            Connection connection = getConnection();
            String sqlQuery = "select * from " + DB_TABLE_NAME;
            if (null != scheduledDate) {
                sqlQuery += " where " + DB_SCHEDULED_DATE_COLUMN + "=?";
            }
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            int fieldsCounter = 1;
            if (null != scheduledDate) {
                preparedStatement.setString(fieldsCounter, scheduledDate.toString());
                fieldsCounter++;
            }
            list = getItems(connection, preparedStatement);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return list;
    }

    public List<Call> getItems(Boolean isDone, Boolean isDeleted, Date maximalEndDate) {
        List<Call> list = null;
        try {
            Connection connection = getConnection();
            String sqlQuery = "select * from " + DB_TABLE_NAME;
            if (null != isDone) {
                sqlQuery += " and " + DB_IS_DONE_COLUMN + "=?";
            }
            if (null != isDeleted) {
                sqlQuery += " and " + DB_IS_DELETED_COLUMN + "=?";
            }
            if (null != maximalEndDate) {
                sqlQuery += " and ?<=" + DB_END_DATE_COLUMN + "";
            }
            assert sqlQuery.contains("and");
            sqlQuery = sqlQuery.replaceFirst("and", "where");
            logger.info(sqlQuery);
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            int fieldsCounter = 1;
            if (null != isDone) {
                preparedStatement.setBoolean(fieldsCounter, isDone);
                fieldsCounter++;
            }
            if (null != isDeleted) {
                preparedStatement.setBoolean(fieldsCounter, isDeleted);
                fieldsCounter++;
            }
            if (null != maximalEndDate) {
                preparedStatement.setString(fieldsCounter, maximalEndDate.toString());
                fieldsCounter++;
            }
            list = getItems(connection, preparedStatement);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return list;
    }

    @Deprecated
    public List<Call> getCalls() throws SQLException {
        return getItems();
    }

    @Deprecated
    public List<Call> getCallsBySite(Integer siteId) throws SQLException {
        return getItems(siteRepository.getItem(siteId));
    }

    @Deprecated
    public List<Call> getCalls(LocalDate date) throws SQLException {
        return getItems(DB_SCHEDULED_DATE_COLUMN + "='" + Date.localDateToString(date) + "'");
    }

    @Deprecated
    public List<Call> getCalls(LocalDate date, int driver) throws SQLException {

        return getItems(DB_SCHEDULED_DATE_COLUMN + "='" + Date.localDateToString(date) + "' and " + DB_DRIVER_ID_COLUMN + "=" + driver);
    }

    @Deprecated
    public List<Call> getLocalCalls() throws SQLException {
        List<Call> calls = getItems();
        calls.removeIf(call -> !call.isHere());
        return calls;
    }

    @Deprecated
    public List<Call> getCalls(Boolean isDone, Boolean isDeleted) throws SQLException {
        List<Call> calls = getItems();
        calls.removeIf(call -> (call.isDone() != isDone || call.isDeleted() != isDeleted));
        return calls;
    }

    @Deprecated
    public List<Call> getCalls(LocalDate date, Boolean isDone, Boolean isDeleted) throws SQLException {
        List<Call> calls = getCalls(date);
        calls.removeIf(call -> (call.isDone() != isDone || call.isDeleted() != isDeleted));
        return calls;
    }

    @Deprecated
    public List<Call> getOpenCallsPerArea(int area) throws SQLException {
        List<Call> calls = getItems();
        calls.removeIf(call -> (null == call.getSite() || null == call.getSite().getArea() || call.getSite().getArea().getId() != area));
        return calls;
    }

    @Deprecated
    public int countActiveCallsByCustomer (Integer customerId) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("select * from call where done=? and custid=?");
        preparedStatement.setBoolean(1,false);
        preparedStatement.setInt(2,customerId);
        ResultSet rs = preparedStatement.executeQuery();
        return getItems(connection, preparedStatement).size();
    }

    @Deprecated
    public Call getCallById(Integer id) {
        return getItem(id);
    }

    @Deprecated // TODO replace all usage!!
    public long insertCall(int customerId, LocalDate startDate, int userId) {
        Call call = new Call();
        call.setStartDate(new Date(startDate));
        call.setUser(usersRepository.getItem(userId));
        return insertItem(call);
    }

    @Deprecated
    public long insertCall(int customerId, LocalDate startDate, int userId, int siteId) {
        long id = insertCall(customerId,startDate, userId);
        Call call = getCallById((int)id);
        call.setSite(siteRepository.getItem(siteId));
        updateCall(call);
        return id;
    }

    @Deprecated
    public void updateCall(Call call) {
        updateItem(call);
    }

    @Override
    public void updateItem(Call call) {
        super.updateItem(call);
//        callService.updateCall(call); // TODO rewrite it
    }

    @Deprecated
    public int newOrder(Call call) {
        int newOrder = 1;
        if (call.getCurrentDriver().getId() == 0)
            return 0;
        else if (null == call.getCurrentScheduledDate())
            return 0;
        else {
            try (Connection con = getConnection(); PreparedStatement preparedStatement = con.prepareStatement
                    ("select max(workorder) workorder from call where driverid=? and date2=?")) {
                preparedStatement.setInt(1, call.getCurrentDriver().getId());
                preparedStatement.setString(2, call.getCurrentScheduledDate().toString());
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    newOrder = (rs.getInt("workorder")) + 1;
                }
                return newOrder;
            } catch (SQLException e) {
                logger.error("error in newOrder (call id="+call.getId()+": ",e);
                throw new UpdateException("error in newOrder (call id="+call.getId()+": ",e);
            }
        }
    }

    @Deprecated
    private void updateQuery(String sql) {
        try (Connection con = getConnection(); PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
            logger.info("SQL statement: "+sql);
        } catch (SQLException e) {
            logger.error("error in updateQuery: ",e);
            throw new UpdateException("error in updateQuery: ",e);
        }
    }

    @Deprecated
    public void updateQuery(String plus, int driver, LocalDate date,
                            String orderOperator, int order, String orderOperator2, int order2) {
        String sql = stringUpdateQuery(plus, driver, date, orderOperator, order)
                + " and workorder" + orderOperator2 + order2;
        updateQuery(sql);
    }

    @Deprecated
    public void updateQuery(String plus, int driver, LocalDate date, String orderOperator, int order) {
        String sql = stringUpdateQuery(plus,driver,date,orderOperator,order);
        updateQuery(sql);
    }

    @Deprecated
    private String stringUpdateQuery(String plus, int driver, LocalDate date, String orderOperator, int order) {
        return "update call set workorder=workorder"+plus+" where "
                + "driverid=" + driver
                + " and date2='" + Date.localDateToString(date)
                + "' and workorder" + orderOperator + order;
    }

    @Deprecated
    public void resetOrderQuery(Call call) {
        String sql = "update call set workorder=0 where id=" + call.getId();
        updateQuery(sql);
    }

    @Deprecated
    public void updateOrderQuery(Call call) {
        String sql = "update call set date2='" + call.getCurrentScheduledDate().toString()
                + "', driverid=" + call.getCurrentDriver().getId()
                + ", workorder=" + call.getCurrentScheduledOrder()
                + " where id=" + call.getId();
        updateQuery(sql);
    }

    }