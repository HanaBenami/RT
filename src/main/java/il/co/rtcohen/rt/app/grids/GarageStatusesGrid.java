package il.co.rtcohen.rt.app.grids;

import il.co.rtcohen.rt.app.uiComponents.CustomCheckBoxColumn;
import il.co.rtcohen.rt.app.uiComponents.CustomNumericColumn;
import il.co.rtcohen.rt.dal.dao.GarageStatus;
import il.co.rtcohen.rt.dal.repositories.GarageStatusRepository;

public class GarageStatusesGrid extends AbstractTypeWithNameAndActiveFieldsGrid<GarageStatus> {
    private final String DISPLAY_ORDER_COLUMN_ID = "displayOrderColumn";

    public GarageStatusesGrid(GarageStatusRepository garageStatusRepository) {
        super(garageStatusRepository,
                GarageStatus::new,
                "area",
                null
        );
        this.initGrid();
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
                GarageStatus::isPendingGarage,
                GarageStatus::setPendingGarage,
                "pendingGarageColumn",
                "pendingGarage",
                null,
                this
        );
    }

    private void addDisplayOrderColumn() {
        CustomNumericColumn.addToGrid(
                GarageStatus::getDisplayOrder,
                GarageStatus::setDisplayOrder,
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
