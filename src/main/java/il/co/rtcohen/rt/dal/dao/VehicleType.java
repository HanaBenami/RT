package il.co.rtcohen.rt.dal.dao;

import il.co.rtcohen.rt.dal.dao.interfaces.AbstractTypeWithNameAndActiveFields;
import il.co.rtcohen.rt.dal.dao.interfaces.BindRepository;

public class VehicleType extends AbstractTypeWithNameAndActiveFields implements BindRepository<VehicleType> {
    public VehicleType() {
        setObjectName("vehicleType");
    }

    public VehicleType(int id, String name, boolean active) {
        super(id, name, active);
    }
}
