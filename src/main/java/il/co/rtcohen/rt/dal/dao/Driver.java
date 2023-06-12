package il.co.rtcohen.rt.dal.dao;

import il.co.rtcohen.rt.dal.dao.interfaces.AbstractTypeWithNameAndActiveFields;
import il.co.rtcohen.rt.dal.dao.interfaces.BindRepository;

public class Driver extends AbstractTypeWithNameAndActiveFields implements BindRepository<Driver> {
    public Driver() {
        this.setObjectName("driver");
    }

    public Driver(int id, String name, boolean active) {
        super(id, name, active);
    }
}
