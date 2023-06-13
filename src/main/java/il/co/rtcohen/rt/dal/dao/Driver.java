package il.co.rtcohen.rt.dal.dao;

import il.co.rtcohen.rt.dal.dao.interfaces.AbstractTypeWithNameAndActiveFields;
import il.co.rtcohen.rt.dal.dao.interfaces.BindRepository;
import il.co.rtcohen.rt.dal.dao.interfaces.Cloneable;

public class Driver extends AbstractTypeWithNameAndActiveFields implements BindRepository<Driver>, Cloneable<Driver> {
    public Driver() {}

    public Driver(int id, String name, boolean active) {
        super(id, name, active);
    }

    public Driver(Driver other) {
        super(other);
    }

    @Override
    public Driver cloneObject() {
        return new Driver(this);
    }

    public String getObjectName() {
        return "driver";
    }
}
