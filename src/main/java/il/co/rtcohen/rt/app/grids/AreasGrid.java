package il.co.rtcohen.rt.app.grids;

import il.co.rtcohen.rt.app.uiComponents.columns.CustomCheckBoxColumn;
import il.co.rtcohen.rt.app.uiComponents.columns.CustomIntegerColumn;
import il.co.rtcohen.rt.dal.dao.Area;
import il.co.rtcohen.rt.dal.repositories.AreasRepository;

public class AreasGrid extends AbstractTypeWithNameAndActiveFieldsGrid<Area> {
    private final String DISPLAY_ORDER_COLUMN_ID = "displayOrderColumn";

    public AreasGrid(AreasRepository areasRepository) {
        super(areasRepository,
                Area::new,
                "area",
                null
        );
        this.initGrid(true, 0);
        this.setCustomSortColumnId(DISPLAY_ORDER_COLUMN_ID);
    }

    protected void addColumns() {
        addActiveColumn();
        addHereColumn();
        addDisplayOrderColumn();
        addNameColumn();
        addIdColumn();
    }

    private void addHereColumn() {
        CustomCheckBoxColumn.addToGrid(
                Area::isHere,
                Area::setHere,
                "hereColumn",
                "here",
                null,
                this
        );
    }

    private void addDisplayOrderColumn() {
        CustomIntegerColumn.addToGrid(
                Area::getDisplayOrder,
                Area::setDisplayOrder,
                null, null, 80,
                DISPLAY_ORDER_COLUMN_ID,
                "order",
                false,
                false,
                true,
                this
        );
    }
}
