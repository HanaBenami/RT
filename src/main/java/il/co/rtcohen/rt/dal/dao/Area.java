package il.co.rtcohen.rt.dal.dao;

import il.co.rtcohen.rt.dal.dao.interfaces.AbstractTypeWithNameAndActiveFields;
import il.co.rtcohen.rt.dal.dao.interfaces.BindRepository;

public class Area extends AbstractTypeWithNameAndActiveFields implements BindRepository<Area> {
    private boolean here = false;
    private int displayOrder = 0;

    public Area() {
        super();
        setObjectName("area");
    }

    public Area(String name) {
        super(name);
    }

    public Area(int id, String name, boolean here, boolean active, int displayOrder) {
        super(id, name, active);
        this.here = here;
        this.displayOrder = displayOrder;
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
