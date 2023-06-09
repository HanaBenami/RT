package il.co.rtcohen.rt.app.grids;

import il.co.rtcohen.rt.app.uiComponents.CustomCheckBoxColumn;
import il.co.rtcohen.rt.app.uiComponents.CustomNumericColumn;
import il.co.rtcohen.rt.dal.dao.Area;
import il.co.rtcohen.rt.dal.repositories.AreasRepository;

public class AreasGrid extends AbstractTypeFilterGrid<Area> {
    private final String NAME_COLUMN_ID = "nameColumn";

    public AreasGrid(AreasRepository areasRepository) {
        super(areasRepository,
                Area::new,
                "area",
                null
        );
        this.initGrid();
        this.setCustomSortColumnId(NAME_COLUMN_ID);
    }

    protected void addColumns() {
        addActiveColumn();
        addHereColumn();
        addDisplayOrderColumn();
        addNameColumn();
        addIdColumn();
    }

    private void addActiveColumn() {
        CustomCheckBoxColumn.addToGrid(
                Area::isActive,
                Area::setActive,
                "activeColumn",
                "active",
                Boolean.TRUE,
                this
        );
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
                "displayOrderColumn",
                "order",
                false,
                false,
                true,
                this
        );
    }

    private void addNameColumn() {
        this.addTextColumn(
                Area::getName,
                Area::setName,
                230,
                NAME_COLUMN_ID,
                "name"
        );
    }
}
