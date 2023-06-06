package il.co.rtcohen.rt.app.grids;

import il.co.rtcohen.rt.dal.dao.AbstractType;

@Deprecated
public class CustomGrid<T extends AbstractType> extends AbstractFilterGrid<T> {

    public CustomGrid() {
        super();
        this.initGrid();
    }

    protected void addColumns() {
    }

    protected void sort() {
    }

    protected void setStyle() {
    }

    protected void initGrid() {
    }
}
