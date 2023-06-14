package il.co.rtcohen.rt.app.grids;

import il.co.rtcohen.rt.app.uiComponents.columns.CustomCheckBoxColumn;
import il.co.rtcohen.rt.app.uiComponents.columns.CustomNumericColumn;
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
        this.initGrid(true);
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
                Area::getHere,
                Area::setHere,
                "hereColumn",
                "here",
                null,
                this
        );
    }

    private void addDisplayOrderColumn() {
        CustomNumericColumn.addToGrid(
                Area::getDisplayOrder,
                Area::setDisplayOrder,
                80,
                DISPLAY_ORDER_COLUMN_ID,
                "order",
                false,
                false,
                true,
                this
        );
    }
}
