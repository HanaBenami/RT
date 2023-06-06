package il.co.rtcohen.rt.dal.repositories;

import il.co.rtcohen.rt.dal.dao.GeneralObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
@Qualifier("CarTypeRepository")
public class VehicleTypeRepository extends GeneralObjectRepository implements RepositoryInterface<GeneralObject> {
    @Autowired
    public VehicleTypeRepository(DataSource dataSource) {
        super(dataSource, "carType", "Vehicles types");
    }
}

