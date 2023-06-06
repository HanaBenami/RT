package il.co.rtcohen.rt.dal.dao;

public class CallType extends AbstractTypeWithNameAndActiveFields implements BindRepository<CallType> {
    public CallType() {
    }

    public CallType(int id, String name, boolean active) {
        super(id, name, active);
    }
}
