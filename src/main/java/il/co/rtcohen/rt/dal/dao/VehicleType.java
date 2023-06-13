package il.co.rtcohen.rt.dal.dao;

import il.co.rtcohen.rt.dal.dao.interfaces.AbstractTypeWithNameAndActiveFields;
import il.co.rtcohen.rt.dal.dao.interfaces.BindRepository;
import il.co.rtcohen.rt.dal.dao.interfaces.Cloneable;

public class VehicleType extends AbstractTypeWithNameAndActiveFields implements BindRepository<VehicleType>, Cloneable<VehicleType> {

    public VehicleType() {

    }

    public VehicleType(int id, String name, boolean active) {
        super(id, name, active);
    }

    public VehicleType(VehicleType other) {
        super(other);
    }

    @Override
    public VehicleType cloneObject() {
        return new VehicleType(this);
    }

    @Override
    public String getObjectName() {
        return "vehicleType";
    }
}
