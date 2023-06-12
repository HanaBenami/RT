package il.co.rtcohen.rt.app.views;

import com.vaadin.server.ErrorHandler;
import com.vaadin.spring.annotation.SpringView;
import org.springframework.beans.factory.annotation.Autowired;

import il.co.rtcohen.rt.app.grids.GarageStatusesGrid;
import il.co.rtcohen.rt.dal.dao.GarageStatus;
import il.co.rtcohen.rt.dal.repositories.GarageStatusRepository;

@SpringView(name = GarageStatusView.VIEW_NAME)
public class GarageStatusView extends AbstractDataView<GarageStatus> {
    static final String VIEW_NAME = "garageStatuses";

    // Repositories
    private final GarageStatusRepository garageStatusRepository;

    // Grids
    private GarageStatusesGrid garageStatusGrid;

    @Autowired
    private GarageStatusView(ErrorHandler errorHandler, GarageStatusRepository garageStatusRepository) {
        super(errorHandler, "garageStatus");
        this.garageStatusRepository = garageStatusRepository;
    }

    @Override
    void addGrids() {
        addGrid();
    }

    void addGrid() {
        removeGrid();
        garageStatusGrid = new GarageStatusesGrid(garageStatusRepository);
        addComponentsAndExpand(garageStatusGrid.getVerticalLayout(true, false));
    }

    @Override
    void removeGrids() {
        removeGrid();
    }

    void removeGrid() {
        if (null != garageStatusGrid) {
            removeComponent(garageStatusGrid.getVerticalLayout());
            garageStatusGrid = null;
        }
    }

    // TODO
    @Override
    void setTabIndexes() {
        garageStatusGrid.setTabIndex(1);
    }
}
