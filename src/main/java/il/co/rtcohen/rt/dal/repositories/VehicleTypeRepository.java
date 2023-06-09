package il.co.rtcohen.rt.dal.repositories;

import il.co.rtcohen.rt.dal.dao.VehicleType;
import il.co.rtcohen.rt.dal.repositories.interfaces.AbstractTypeWithNameAndActiveFieldsRepository;
import il.co.rtcohen.rt.dal.repositories.interfaces.RepositoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class VehicleTypeRepository extends AbstractTypeWithNameAndActiveFieldsRepository<VehicleType> implements RepositoryInterface<VehicleType> {
    @Autowired
    public VehicleTypeRepository(DataSource dataSource) {
        super(dataSource, "carType", "Vehicles types",
                new String[] {

                }
        );
    }

    protected VehicleType getItemFromResultSet(ResultSet rs) throws SQLException {
        return new VehicleType(
                rs.getInt(DB_ID_COLUMN),
                rs.getString(DB_NAME_COLUMN),
                rs.getBoolean(DB_ACTIVE_COLUMN)
        );
    }
}
