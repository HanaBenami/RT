package il.co.rtcohen.rt.app.views;

import com.vaadin.server.ErrorHandler;
import com.vaadin.spring.annotation.SpringView;
import il.co.rtcohen.rt.app.grids.AreasGrid;
import il.co.rtcohen.rt.dal.dao.Area;
import il.co.rtcohen.rt.dal.repositories.AreaRepository;
import org.springframework.beans.factory.annotation.Autowired;

@SpringView(name = AreasView.VIEW_NAME)
public class AreasView extends AbstractDataView<Area> {
    static final String VIEW_NAME = "areas";

    // Repositories
    private final AreaRepository areaRepository;

    // Grids
    private AreasGrid areasGrid;

    @Autowired
    private AreasView(ErrorHandler errorHandler, AreaRepository areaRepository) {
        super(errorHandler, "areaTitle");
        this.areaRepository = areaRepository;
    }

    @Override
    void addGrids() {
        addGrid();
    }

    void addGrid() {
        removeGrid();
        areasGrid = new AreasGrid(areaRepository);
        addComponentsAndExpand(areasGrid.getVerticalLayout(true, false));
    }

    @Override
    void removeGrids() {
        removeGrid();
    }

    void removeGrid() {
        if (null != areasGrid) {
            removeComponent(areasGrid.getVerticalLayout());
            areasGrid = null;
        }
    }

    @Override
    void setTabIndexesAndFocus() {
        areasGrid.focus();
    }
}
