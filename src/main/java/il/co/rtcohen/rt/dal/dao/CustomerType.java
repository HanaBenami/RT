package il.co.rtcohen.rt.dal.dao;

import il.co.rtcohen.rt.dal.dao.interfaces.AbstractTypeWithNameAndActiveFields;
import il.co.rtcohen.rt.dal.dao.interfaces.BindRepository;

public class CustomerType extends AbstractTypeWithNameAndActiveFields implements BindRepository<CustomerType> {
    public CustomerType() {
    }

    public CustomerType(int id, String name, boolean active) {
        super(id, name, active);
    }
}
