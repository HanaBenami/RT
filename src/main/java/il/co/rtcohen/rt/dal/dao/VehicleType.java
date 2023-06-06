package il.co.rtcohen.rt.dal.dao;

import il.co.rtcohen.rt.dal.repositories.VehicleTypeRepository;

public class VehicleType extends GeneralObject implements BindRepository<VehicleType> {
    public VehicleType() {
    }

    public VehicleType(int id, String name, boolean active) {
        super(id, name, active);
    }
}
