package il.co.rtcohen.rt.dal.dao;

import il.co.rtcohen.rt.dal.dao.interfaces.AbstractTypeWithNameAndActiveFields;
import il.co.rtcohen.rt.dal.dao.interfaces.BindRepository;

public class CallType extends AbstractTypeWithNameAndActiveFields implements BindRepository<CallType> {
    public CallType() {
        setObjectName("callType");
    }

    public CallType(int id, String name, boolean active) {
        super(id, name, active);
    }
}
