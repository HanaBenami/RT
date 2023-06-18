package il.co.rtcohen.rt.dal.dao;

import il.co.rtcohen.rt.dal.dao.interfaces.AbstractTypeWithNameAndActiveFields;
import il.co.rtcohen.rt.dal.dao.interfaces.BindRepository;
import il.co.rtcohen.rt.dal.dao.interfaces.Cloneable;

public class WarehouseStatus extends AbstractTypeWithNameAndActiveFields implements BindRepository<WarehouseStatus>, Cloneable<WarehouseStatus> {
    private boolean isPendingWarehouse = false;
    private int displayOrder = 0;

    public WarehouseStatus() {
        super();
    }

    public WarehouseStatus(int id, String name, boolean isPendingWarehouse, boolean active, int displayOrder) {
        super(id, name, active);
        this.isPendingWarehouse = isPendingWarehouse;
        this.displayOrder = displayOrder;
    }

    public WarehouseStatus(WarehouseStatus other) {
        super(other);
        this.isPendingWarehouse = other.isPendingWarehouse;
        this.displayOrder = other.displayOrder;
    }

    @Override
    public WarehouseStatus cloneObject() {
        return new WarehouseStatus(this);
    }

    public String getObjectName() {
        return "warehouseStatus";
    }

    public boolean isPendingWarehouse() {
        return this.isPendingWarehouse;
    }

    public void setPendingWarehouse(boolean isWaitingForWarehouse) {
        this.isPendingWarehouse = isWaitingForWarehouse;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}
