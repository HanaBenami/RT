package il.co.rtcohen.rt.dal.dao;

import il.co.rtcohen.rt.dal.dao.interfaces.AbstractTypeWithNameAndActiveFields;
import il.co.rtcohen.rt.dal.dao.interfaces.BindRepository;
import il.co.rtcohen.rt.dal.dao.interfaces.Cloneable;

public class Area extends AbstractTypeWithNameAndActiveFields implements BindRepository<Area>, Cloneable<Area> {
    private boolean here = false;
    private int displayOrder = 0;

    public Area() {
        super();
    }

    public Area(int id, String name, boolean here, boolean active, int displayOrder) {
        super(id, name, active);
        this.here = here;
        this.displayOrder = displayOrder;
    }

    public Area(Area other) {
        super(other);
        this.here = other.here;
        this.displayOrder = other.displayOrder;
    }

    @Override
    public Area cloneObject() {
        return new Area(this);
    }

    public String getObjectName() {
        return "area";
    }

    public boolean getHere() {
        return here;
    }

    public void setHere(boolean here) {
        this.here = here;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}
