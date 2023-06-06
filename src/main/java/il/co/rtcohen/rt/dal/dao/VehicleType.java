package il.co.rtcohen.rt.dal.dao;

public class VehicleType extends AbstractTypeWithNameAndActiveFields implements BindRepository<VehicleType> {
    public VehicleType() {
    }

    public VehicleType(int id, String name, boolean active) {
        super(id, name, active);
    }
}
