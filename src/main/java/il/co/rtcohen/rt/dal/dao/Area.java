package il.co.rtcohen.rt.dal.dao;

public class Area extends GeneralObject {

    private boolean here;
    private Integer displayOrder;

    public Area() {
        super();
    }

    public Area(String name) {
        super(name);
    }

    public Area(int id, String name, boolean here, boolean active, int displayOrder) {
        super(id, name, active);
        this.here = here;
        this.displayOrder = displayOrder;
    }

    public boolean getHere () {
        return here;
    }

    public void setHere (boolean here) {
        this.here=here;
    }

    public Integer getDisplayOrder () {
        return displayOrder;
    }

    public void setDisplayOrder (Integer displayOrder) {
        this.displayOrder=displayOrder;
    }

}
