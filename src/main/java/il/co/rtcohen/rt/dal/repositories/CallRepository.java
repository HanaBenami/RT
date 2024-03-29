package il.co.rtcohen.rt.dal.repositories;

import il.co.rtcohen.rt.utils.Logger;
import il.co.rtcohen.rt.dal.dao.Driver;
import il.co.rtcohen.rt.dal.repositories.interfaces.AbstractTypeRepository;
import il.co.rtcohen.rt.dal.repositories.interfaces.RepositoryInterface;
import il.co.rtcohen.rt.utils.NullPointerExceptionWrapper;
import il.co.rtcohen.rt.dal.dao.*;
import il.co.rtcohen.rt.utils.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
    static protected final String DB_GARAGE_STATUS_ID_COLUMN = "garageStatusID";
    static protected final String DB_WAREHOUSE_STATUS_ID_COLUMN = "warehouseStatusID";
    static protected final String DB_INVOICE_NUM_COLUMN = "invoiceNum";
    static protected final String DB_INVOICE_DOC_ID_COLUMN = "invoiceDocumentId";

    private final CustomerRepository customerRepository;
    private final SiteRepository siteRepository;
    private final VehicleRepository vehicleRepository;
    private final CallTypeRepository callTypeRepository;
    private final DriverRepository driverRepository;
    private final UsersRepository usersRepository;
    private final GarageStatusRepository garageStatusRepository;
    private final WarehouseStatusRepository warehouseStatusRepository;

    @Autowired
    public CallRepository(DataSource dataSource,
            CustomerRepository customerRepository,
            SiteRepository siteRepository,
            VehicleRepository vehicleRepository,
            CallTypeRepository callTypeRepository,
            DriverRepository driverRepository,
            UsersRepository usersRepository,
            GarageStatusRepository garageStatusRepository,
            WarehouseStatusRepository warehouseStatusRepository) {
        super(
                dataSource, "call", "Calls",
                new String[] {
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
                        DB_USER_ID_COLUMN,
                        DB_GARAGE_STATUS_ID_COLUMN,
                        DB_WAREHOUSE_STATUS_ID_COLUMN,
                        DB_INVOICE_NUM_COLUMN,
                        DB_INVOICE_DOC_ID_COLUMN
                });
        this.customerRepository = customerRepository;
        this.siteRepository = siteRepository;
        this.vehicleRepository = vehicleRepository;
        this.callTypeRepository = callTypeRepository;
        this.driverRepository = driverRepository;
        this.usersRepository = usersRepository;
        this.garageStatusRepository = garageStatusRepository;
        this.warehouseStatusRepository = warehouseStatusRepository;
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
                usersRepository.getItem(rs.getInt(DB_USER_ID_COLUMN)),
                garageStatusRepository.getItem(rs.getInt(DB_GARAGE_STATUS_ID_COLUMN)),
                warehouseStatusRepository.getItem(rs.getInt(DB_WAREHOUSE_STATUS_ID_COLUMN)),
                rs.getInt(DB_INVOICE_NUM_COLUMN),
                rs.getInt(DB_INVOICE_DOC_ID_COLUMN));
    }

    @Override
    protected int updateItemDetailsInStatement(PreparedStatement preparedStatement, Call call) throws SQLException {
        int fieldsCounter = 1;
        preparedStatement.setInt(fieldsCounter,
                NullPointerExceptionWrapper.getWrapper(call, c -> c.getCustomer().getId(), 0));
        fieldsCounter++;
        preparedStatement.setInt(fieldsCounter,
                NullPointerExceptionWrapper.getWrapper(call, c -> c.getSite().getId(), 0));
        fieldsCounter++;
        preparedStatement.setInt(fieldsCounter,
                NullPointerExceptionWrapper.getWrapper(call, c -> c.getVehicle().getId(), 0));
        fieldsCounter++;
        preparedStatement.setInt(fieldsCounter,
                NullPointerExceptionWrapper.getWrapper(call, c -> c.getCallType().getId(), 0));
        fieldsCounter++;
        preparedStatement.setString(fieldsCounter, call.getDescription());
        fieldsCounter++;
        preparedStatement.setString(fieldsCounter, call.getNotes());
        fieldsCounter++;
        preparedStatement.setString(fieldsCounter, NullPointerExceptionWrapper.getWrapper(call,
                c -> c.getStartDate().toString(), Date.nullDate().toString()));
        fieldsCounter++;
        preparedStatement.setString(fieldsCounter, NullPointerExceptionWrapper.getWrapper(call,
                c -> c.getPlanningDate().toString(), Date.nullDate().toString()));
        fieldsCounter++;
        preparedStatement.setString(fieldsCounter, NullPointerExceptionWrapper.getWrapper(call,
                c -> c.getCurrentScheduledDate().toString(), Date.nullDate().toString()));
        fieldsCounter++;
        preparedStatement.setString(fieldsCounter, NullPointerExceptionWrapper.getWrapper(call,
                c -> c.getEndDate().toString(), Date.nullDate().toString()));
        fieldsCounter++;
        preparedStatement.setInt(fieldsCounter, call.getCurrentScheduledOrder());
        fieldsCounter++;
        preparedStatement.setInt(fieldsCounter,
                NullPointerExceptionWrapper.getWrapper(call, c -> c.getCurrentDriver().getId(), 0));
        fieldsCounter++;
        preparedStatement.setBoolean(fieldsCounter, call.isMeeting());
        fieldsCounter++;
        preparedStatement.setBoolean(fieldsCounter, call.isDone());
        fieldsCounter++;
        preparedStatement.setBoolean(fieldsCounter, call.isHere());
        fieldsCounter++;
        preparedStatement.setBoolean(fieldsCounter, call.isDeleted());
        fieldsCounter++;
        preparedStatement.setInt(fieldsCounter,
                NullPointerExceptionWrapper.getWrapper(call, c -> c.getOpenedByUser().getId(), 0));
        fieldsCounter++;
        preparedStatement.setInt(fieldsCounter,
                NullPointerExceptionWrapper.getWrapper(call, c -> c.getGarageStatus().getId(), 0));
        fieldsCounter++;
        preparedStatement.setInt(fieldsCounter,
                NullPointerExceptionWrapper.getWrapper(call, c -> c.getWarehouseStatus().getId(), 0));
        fieldsCounter++;
        preparedStatement.setInt(fieldsCounter,
                NullPointerExceptionWrapper.getWrapper(call, c -> c.getInvoiceNum(), 0));
        fieldsCounter++;
        preparedStatement.setInt(fieldsCounter,
                NullPointerExceptionWrapper.getWrapper(call, c -> c.getInvoiceDocumentId(), 0));
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
            Logger.getLogger(this).debug(sqlQuery);
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

    public List<Call> getScheduledCalls(Date scheduledDate) {
        return getScheduledCalls(scheduledDate, null);
    }

    public List<Call> getScheduledCalls(@NotNull Date scheduledDate, Driver driver) {
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

    // TODO: move logic to appropriate place
    public List<Call> getCallsCurrentlyInTheGarage() {
        return getItems(false, false, true, null, null, Date.nullDate(), null, null);
    }

    // TODO: move logic to appropriate place
    public List<Call> getOpenCallsInArea(Area area) {
        List<Call> callsNotHere = getItems(false, false, false, null, null, null, null, area);
        List<Call> callsHereReadyToLeave = getItems(false, false, true, null, null, null, Date.nullDate(), area);
        List<Call> fullList = new ArrayList<>(callsNotHere);
        fullList.addAll(callsHereReadyToLeave);
        if (null == area) {
            fullList.removeIf(call -> (null != call.getSite() && null != call.getSite().getArea()));
        }
        return fullList;
    }

    public List<Call> getItems(Boolean isDone, Boolean isDeleted, Boolean isHere,
            Date maximalEndDate, Date maximalScheduleDate, Date scheduledDateEqualTo, Date scheduleDateOtherThan,
            Area area) {
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
            if (null != maximalScheduleDate) {
                sqlQuery += " and ?>=" + DB_SCHEDULED_DATE_COLUMN + "";
            }
            if (null != scheduledDateEqualTo) {
                sqlQuery += " and " + DB_SCHEDULED_DATE_COLUMN + "=?";
            }
            if (null != scheduleDateOtherThan) {
                sqlQuery += " and " + DB_SCHEDULED_DATE_COLUMN + "!=?";
            }
            assert sqlQuery.contains("and");
            sqlQuery = sqlQuery.replaceFirst("and", "where");
            Logger.getLogger(this).debug(sqlQuery);
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
            if (null != maximalScheduleDate) {
                preparedStatement.setString(fieldsCounter, maximalScheduleDate.toString());
                fieldsCounter++;
            }
            if (null != scheduledDateEqualTo) {
                preparedStatement.setString(fieldsCounter, scheduledDateEqualTo.toString());
                fieldsCounter++;
            }
            if (null != scheduleDateOtherThan) {
                preparedStatement.setString(fieldsCounter, scheduleDateOtherThan.toString());
                fieldsCounter++;
            }
            list = getItems(preparedStatement);
            if (null != area) {
                list.removeIf(call -> (null == call.getSite() || null == call.getSite().getArea()
                        || !call.getSite().getArea().equals(area)));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return list;
    }

    public List<Call> getItems(Integer invoiceNum, Integer invoiceDocumentId) {
        List<Call> list = null;
        try (Connection connection = getConnection()) {
            String sqlQuery = "select * from " + DB_TABLE_NAME;
            if (null != invoiceNum) {
                sqlQuery += " and " + DB_INVOICE_NUM_COLUMN + "=?";
            }
            if (null != invoiceDocumentId) {
                sqlQuery += " and " + DB_INVOICE_DOC_ID_COLUMN + "=?";
            }
            if (sqlQuery.contains("and")) {
                sqlQuery = sqlQuery.replaceFirst("and", "where");
            }
            Logger.getLogger(this).debug(sqlQuery);
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            int fieldsCounter = 1;
            if (null != invoiceNum) {
                preparedStatement.setInt(fieldsCounter, invoiceNum);
                fieldsCounter++;
            }
            if (null != invoiceDocumentId) {
                preparedStatement.setInt(fieldsCounter, invoiceDocumentId);
                fieldsCounter++;
            }
            list = getItems(preparedStatement);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return list;
    }
}