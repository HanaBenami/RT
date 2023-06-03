package il.co.rtcohen.rt.app.views;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ErrorHandler;
import com.vaadin.spring.annotation.SpringView;
import il.co.rtcohen.rt.app.ui.grids.GeneralObjectGrid;
import il.co.rtcohen.rt.dal.dao.GeneralObject;
import il.co.rtcohen.rt.dal.repositories.GeneralObjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Map;

@SpringView(name = GeneralObjectView.VIEW_NAME)
public class GeneralObjectView extends AbstractDataView<GeneralObject> {
    static final String VIEW_NAME = "update";
    private static final Logger logger = LoggerFactory.getLogger(GeneralObjectView.class);
    private String dbTableName;

    // Repositories
    private final GeneralObjectRepository generalObjectRepository;

    // Grids
    private GeneralObjectGrid generalObjectGrid;

    @Autowired
    private GeneralObjectView(ErrorHandler errorHandler, GeneralObjectRepository generalObjectRepository) {
        super(errorHandler, null);
        this.generalObjectRepository = generalObjectRepository;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Map<String, String> parametersMap = event.getParameterMap();
        logger.info("Parameters map " + Arrays.toString(parametersMap.entrySet().toArray()));
        this.dbTableName = parametersMap.get("table");
        this.generalObjectRepository.setDbTableName(this.dbTableName);
        this.generalObjectRepository.setRepositoryName(this.dbTableName);
        this.setTitle(getTitleKey());
        super.enter(event);
    }

    private String getTitleKey() {
        return (null == this.dbTableName ? null : this.dbTableName + "Title");
    }

    @Override
    void addGrids() {
        addGrid();
    }

    void addGrid() {
        removeGrid();
        generalObjectGrid = new GeneralObjectGrid(generalObjectRepository, getTitleKey());
        addComponentsAndExpand(generalObjectGrid.getVerticalLayout(true, false));
    }

    @Override
    void removeGrids() {
        removeGrid();
    }

    void removeGrid() {
        if (null != generalObjectGrid) {
            removeComponent(generalObjectGrid.getVerticalLayout(false, false));
            generalObjectGrid = null;
        }
    }

    // TODO
    @Override
    void setTabIndexes() {
        generalObjectGrid.setTabIndex(1);
    }
}
