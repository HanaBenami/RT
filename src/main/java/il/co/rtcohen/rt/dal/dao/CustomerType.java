package il.co.rtcohen.rt.dal.dao;

import il.co.rtcohen.rt.dal.dao.interfaces.AbstractTypeWithNameAndActiveFields;
import il.co.rtcohen.rt.dal.dao.interfaces.BindRepository;
import il.co.rtcohen.rt.dal.dao.interfaces.Cloneable;

public class CustomerType extends AbstractTypeWithNameAndActiveFields implements BindRepository<CustomerType>, Cloneable<CustomerType> {
    public CustomerType() {

    }

    public CustomerType(CustomerType other) {
        super(other);
    }

    @Override
    public CustomerType cloneObject() {
        return new CustomerType(this);
    }

    public CustomerType(int id, String name, boolean active) {
        super(id, name, active);
    }

    public String getObjectName() {
        return "custType";
    }
}
