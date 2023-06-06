package il.co.rtcohen.rt.dal.repositories;

import il.co.rtcohen.rt.dal.dao.VehicleType;
import il.co.rtcohen.rt.dal.dao.VehicleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@Qualifier("VehicleTypeRepository")
public class VehicleTypeRepository extends AbstractRepository<VehicleType> implements RepositoryInterface<VehicleType> {
    static private  final String DB_ID_COLUMN = "id";
    static private  final String DB_NAME_COLUMN = "name";
    static private  final String DB_ACTIVE_COLUMN = "active";
    
    @Autowired
    public VehicleTypeRepository(DataSource dataSource) {
        super(dataSource, "carType", "Vehicles types",
                new String[] {
                        DB_NAME_COLUMN, DB_ACTIVE_COLUMN
                }
        );
    }

    public List<VehicleType> getItems(boolean onlyActiveItems) {
        List<VehicleType> list = this.getItems();
        list.removeIf(VehicleType -> !VehicleType.isActive());
        return list;
    }

    protected VehicleType getItemFromResultSet(ResultSet rs) throws SQLException {
        return new VehicleType(
                rs.getInt(DB_ID_COLUMN),
                rs.getString(DB_NAME_COLUMN),
                rs.getBoolean(DB_ACTIVE_COLUMN)
        );
    }

    protected void updateItemDetailsInStatement(PreparedStatement stmt, VehicleType VehicleType) throws SQLException {
        int fieldsCounter = 1;
        stmt.setString(fieldsCounter, VehicleType.getName());
        fieldsCounter++;
        stmt.setBoolean(fieldsCounter, VehicleType.isActive());
    }
}

