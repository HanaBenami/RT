package il.co.rtcohen.rt.dal.dao;

import il.co.rtcohen.rt.dal.dao.interfaces.AbstractTypeWithNameAndActiveFields;
import il.co.rtcohen.rt.dal.dao.interfaces.BindRepository;
import il.co.rtcohen.rt.dal.dao.interfaces.Cloneable;

public class GarageStatus extends AbstractTypeWithNameAndActiveFields implements BindRepository<GarageStatus>, Cloneable<GarageStatus> {
    private boolean isPendingGarage = false;
    private int displayOrder = 0;

    public GarageStatus() {
        super();
    }

    public GarageStatus(int id, String name, boolean isPendingGarage, boolean active, int displayOrder) {
        super(id, name, active);
        this.isPendingGarage = isPendingGarage;
        this.displayOrder = displayOrder;
    }

    public GarageStatus(GarageStatus other) {
        super(other);
        this.isPendingGarage = other.isPendingGarage;
        this.displayOrder = other.displayOrder;
    }

    @Override
    public GarageStatus cloneObject() {
        return new GarageStatus(this);
    }

    public String getObjectName() {
        return "garageStatus";
    }

    public boolean isPendingGarage() {
        return this.isPendingGarage;
    }

    public void setPendingGarage(boolean isWaitingForGarage) {
        this.isPendingGarage = isWaitingForGarage;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}
