package il.co.rtcohen.rt.dal.dao;

public class CustomerType extends AbstractTypeWithNameAndActiveFields implements BindRepository<CustomerType> {
    public CustomerType() {
    }

    public CustomerType(int id, String name, boolean active) {
        super(id, name, active);
    }
}
