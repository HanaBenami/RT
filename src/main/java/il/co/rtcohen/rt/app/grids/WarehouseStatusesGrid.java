package il.co.rtcohen.rt.app.grids;

import il.co.rtcohen.rt.app.uiComponents.columns.CustomCheckBoxColumn;
import il.co.rtcohen.rt.app.uiComponents.columns.CustomIntegerColumn;
import il.co.rtcohen.rt.dal.dao.WarehouseStatus;
import il.co.rtcohen.rt.dal.repositories.WarehouseStatusRepository;

public class WarehouseStatusesGrid extends AbstractTypeWithNameAndActiveFieldsGrid<WarehouseStatus> {
    private final String DISPLAY_ORDER_COLUMN_ID = "displayOrderColumn";

    public WarehouseStatusesGrid(WarehouseStatusRepository warehouseStatusRepository) {
        super(warehouseStatusRepository,
                WarehouseStatus::new,
                "area",
                null);
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
                WarehouseStatus::isPendingWarehouse,
                WarehouseStatus::setPendingWarehouse,
                "pendingWarehouseColumn",
                "pendingWarehouse",
                null,
                this);
    }

    private void addDisplayOrderColumn() {
        CustomIntegerColumn.addToGrid(
                WarehouseStatus::getDisplayOrder,
                WarehouseStatus::setDisplayOrder,
                null, null, 80,
                DISPLAY_ORDER_COLUMN_ID,
                "order",
                false,
                false,
                true,
                this);
    }
}
