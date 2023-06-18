package il.co.rtcohen.rt.app.views;

import com.vaadin.server.ErrorHandler;
import com.vaadin.spring.annotation.SpringView;
import il.co.rtcohen.rt.app.grids.WarehouseStatusesGrid;
import il.co.rtcohen.rt.dal.dao.WarehouseStatus;
import il.co.rtcohen.rt.dal.repositories.WarehouseStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;

@SpringView(name = WarehouseStatusView.VIEW_NAME)
public class WarehouseStatusView extends AbstractDataView<WarehouseStatus> {
    static final String VIEW_NAME = "warehouseStatuses";

    // Repositories
    private final WarehouseStatusRepository warehouseStatusRepository;

    // Grids
    private WarehouseStatusesGrid warehouseStatusGrid;

    @Autowired
    private WarehouseStatusView(ErrorHandler errorHandler, WarehouseStatusRepository warehouseStatusRepository) {
        super(errorHandler, "warehouseStatus");
        this.warehouseStatusRepository = warehouseStatusRepository;
    }

    @Override
    void addGrids() {
        addGrid();
    }

    void addGrid() {
        removeGrid();
        warehouseStatusGrid = new WarehouseStatusesGrid(warehouseStatusRepository);
        addComponentsAndExpand(warehouseStatusGrid.getVerticalLayout(true, false));
    }

    @Override
    void removeGrids() {
        removeGrid();
    }

    void removeGrid() {
        if (null != warehouseStatusGrid) {
            removeComponent(warehouseStatusGrid.getVerticalLayout());
            warehouseStatusGrid = null;
        }
    }

    @Override
    void setTabIndexesAndFocus() {
        warehouseStatusGrid.focus();
    }
}
