package il.co.rtcohen.rt.dal.repositories;

import il.co.rtcohen.rt.dal.dao.Vehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;

@Repository
public class VehicleRepository extends AbstractTypeWithNameAndActiveFieldsRepository<Vehicle> implements RepositoryInterface<Vehicle> {
    static private final String DB_SITE_ID_COLUMN ="siteId";
    static private final String DB_VEHICLE_TYPE_ID_COLUMN ="typeId";
    static private final String DB_MODEL_COLUMN ="model";
    static private final String DB_SERIES_COLUMN = "series";
    static private final String DB_ZAMA_COLUMN = "zama";
    static private final String DB_LICENSE_COLUMN = "license";
    static private final String DB_ENGINE_HOURS_COLUMN = "engineHours";
    static private final String DB_LAST_UPDATE_COLUMN = "lastUpdate";

    private final VehicleTypeRepository vehicleTypeRepository;

    @Autowired
    public VehicleRepository(DataSource dataSource, VehicleTypeRepository vehicleTypeRepository) {
        super(dataSource, "vehicle", "vehicle",
                new String[] {
                        DB_SITE_ID_COLUMN,
                        DB_VEHICLE_TYPE_ID_COLUMN,
                        DB_MODEL_COLUMN,
                        DB_SERIES_COLUMN,
                        DB_ZAMA_COLUMN, DB_LICENSE_COLUMN,
                        DB_ENGINE_HOURS_COLUMN,
                        DB_LAST_UPDATE_COLUMN
                });
        this.vehicleTypeRepository = vehicleTypeRepository;
    }

    protected Vehicle getItemFromResultSet(ResultSet rs) throws SQLException {
        return new Vehicle(
                rs.getInt(DB_ID_COLUMN),
                rs.getString(DB_NAME_COLUMN),
                rs.getBoolean(DB_ACTIVE_COLUMN),
                rs.getInt(DB_SITE_ID_COLUMN),
                this.vehicleTypeRepository.getItem(rs.getInt(DB_VEHICLE_TYPE_ID_COLUMN)),
                rs.getString(DB_MODEL_COLUMN),
                rs.getString(DB_SERIES_COLUMN),
                rs.getInt(DB_ZAMA_COLUMN),
                rs.getInt(DB_LICENSE_COLUMN),
                rs.getInt(DB_ENGINE_HOURS_COLUMN),
                stringToDate(rs.getString(DB_LAST_UPDATE_COLUMN))
        );
    }

    protected int updateItemDetailsInStatement(PreparedStatement stmt, Vehicle vehicle) throws SQLException {
        vehicle.setLastUpdate();
        int fieldsCounter = super.updateItemDetailsInStatement(stmt, vehicle);
        fieldsCounter++;
        stmt.setInt(fieldsCounter, vehicle.getSiteId());
        fieldsCounter++;
        stmt.setInt(fieldsCounter, vehicle.getVehicleType().getId());
        fieldsCounter++;
        stmt.setString(fieldsCounter, vehicle.getModel());
        fieldsCounter++;
        stmt.setString(fieldsCounter, vehicle.getSeries());
        fieldsCounter++;
        stmt.setInt(fieldsCounter, vehicle.getZama());
        fieldsCounter++;
        stmt.setInt(fieldsCounter, vehicle.getLicense());
        fieldsCounter++;
        stmt.setInt(fieldsCounter, vehicle.getEngineHours());
        fieldsCounter++;
        stmt.setString(fieldsCounter, dateToString(vehicle.getLastUpdate()));
        return fieldsCounter;
    }
}
