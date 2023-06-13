package il.co.rtcohen.rt.dal.dao;

import il.co.rtcohen.rt.dal.dao.interfaces.AbstractTypeWithNameAndActiveFields;
import il.co.rtcohen.rt.dal.dao.interfaces.BindRepository;
import il.co.rtcohen.rt.dal.dao.interfaces.Cloneable;

public class CallType extends AbstractTypeWithNameAndActiveFields implements BindRepository<CallType>, Cloneable<CallType> {
    public CallType() {
    }

    public CallType(int id, String name, boolean active) {
        super(id, name, active);
    }

    public CallType(CallType other) {
        super(other);
    }

    @Override
    public CallType cloneObject() {
        return new CallType(this);
    }

    @Override
    public String getObjectName() {
        return "callType";
    }
}
