package il.co.rtcohen.rt.dal.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.sql.*;
import java.util.List;

import il.co.rtcohen.rt.dal.dao.Driver;
import il.co.rtcohen.rt.dal.repositories.interfaces.AbstractTypeRepository;
import il.co.rtcohen.rt.dal.repositories.interfaces.RepositoryInterface;
import il.co.rtcohen.rt.utils.NullPointerExceptionWrapper;
import il.co.rtcohen.rt.dal.dao.*;
import il.co.rtcohen.rt.utils.Date;

@Repository
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
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
        preparedStatement.setInt(fieldsCounter, NullPointerExceptionWrapper.getWrapper(call, c -> c.getOpenedByUser().getId(), 0));
        return fieldsCounter;
    }

    public List<Call> getItems(Customer customer, Site site, Vehicle vehicle, Boolean isDone) {
        List<Call> list = null;
        try (Connection connection = getConnection()) {
            String sqlQuery = "select * from " + DB_TABLE_NAME;
            if (null != isDone) {
                sqlQuery += " and " + DB_IS_DONE_COLUMN + "=?";
            }
            if (null != customer) {
                sqlQuery += " and " + DB_CUSTOMER_ID_COLUMN + "=?";
            }
            if (null != site) {
                sqlQuery += " and " + DB_SITE_ID_COLUMN + "=?";
            }
            if (null != vehicle) {
                sqlQuery += " and " + DB_VEHICLE_ID_COLUMN + "=?";
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
            if (null != customer) {
                preparedStatement.setInt(fieldsCounter, customer.getId());
                fieldsCounter++;
            }
            if (null != site) {
                preparedStatement.setInt(fieldsCounter, site.getId());
                fieldsCounter++;
            }
            if (null != vehicle) {
                preparedStatement.setInt(fieldsCounter, vehicle.getId());
                fieldsCounter++;
            }
            list = getItems(preparedStatement);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return list;
    }

    public List<Call> getItems(Date scheduledDate) {
        return getItems(scheduledDate, null);
    }

    public List<Call> getItems(@NotNull Date scheduledDate, Driver driver) {
        List<Call> list = null;
        try (Connection connection = getConnection()) {
            String sqlQuery = "select * from " + DB_TABLE_NAME;
            sqlQuery += " where " + DB_SCHEDULED_DATE_COLUMN + "=?";
            if (null != driver) {
                sqlQuery += " and " + DB_DRIVER_ID_COLUMN + "=?";
            }
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            int fieldsCounter = 1;
            preparedStatement.setString(fieldsCounter, scheduledDate.toString());
            fieldsCounter++;
            if (null != driver) {
                preparedStatement.setInt(fieldsCounter, driver.getId());
            }
            list = getItems(preparedStatement);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return list;
    }

    public List<Call> getItems(Boolean isDone, Boolean isDeleted, Boolean isHere, Date maximalEndDate, Area area) {
        List<Call> list = null;
        try (Connection connection = getConnection()) {
            String sqlQuery = "select * from " + DB_TABLE_NAME;
            if (null != isDone) {
                sqlQuery += " and " + DB_IS_DONE_COLUMN + "=?";
            }
            if (null != isDeleted) {
                sqlQuery += " and " + DB_IS_DELETED_COLUMN + "=?";
            }
            if (null != isHere) {
                sqlQuery += " and " + DB_IS_HERE_COLUMN + "=?";
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
            if (null != isHere) {
                preparedStatement.setBoolean(fieldsCounter, isHere);
                fieldsCounter++;
            }
            if (null != maximalEndDate) {
                preparedStatement.setString(fieldsCounter, maximalEndDate.toString());
                fieldsCounter++;
            }
            list = getItems(preparedStatement);
            if (null != area) {
                list.removeIf(call -> (null == call.getSite() || null == call.getSite().getArea() || !call.getSite().getArea().equals(area)));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return list;
    }
}