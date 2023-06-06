package il.co.rtcohen.rt.dal.dao;

public class Driver extends AbstractTypeWithNameAndActiveFields implements BindRepository<Driver> {
    public Driver() {
    }

    public Driver(int id, String name, boolean active) {
        super(id, name, active);
    }
}
