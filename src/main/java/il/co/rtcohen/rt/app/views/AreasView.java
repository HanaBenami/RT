package il.co.rtcohen.rt.app.views;

import com.vaadin.server.ErrorHandler;
import com.vaadin.spring.annotation.SpringView;
import il.co.rtcohen.rt.app.grids.AreasGrid;
import il.co.rtcohen.rt.dal.dao.GeneralObject;
import il.co.rtcohen.rt.dal.repositories.AreasRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@SpringView(name = AreasView.VIEW_NAME)
public class AreasView extends AbstractDataView<GeneralObject> {
    static final String VIEW_NAME = "areas";
    private static final Logger logger = LoggerFactory.getLogger(AreasView.class);
    private String dbTableName;

    // Repositories
    private final AreasRepository areasRepository;

    // Grids
    private AreasGrid areasGrid;

    @Autowired
    private AreasView(ErrorHandler errorHandler, AreasRepository areasRepository) {
        super(errorHandler, "areaTitle");
        this.areasRepository = areasRepository;
    }
    @Override
    void addGrids() {
        addGrid();
    }

    void addGrid() {
        removeGrid();
        areasGrid = new AreasGrid(areasRepository);
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

    // TODO
    @Override
    void setTabIndexes() {
        areasGrid.setTabIndex(1);
    }
}
