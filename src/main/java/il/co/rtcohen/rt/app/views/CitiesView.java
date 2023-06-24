package il.co.rtcohen.rt.app.views;

import com.vaadin.server.ErrorHandler;
import com.vaadin.spring.annotation.SpringView;
import il.co.rtcohen.rt.app.grids.CityGrid;
import il.co.rtcohen.rt.dal.dao.Area;
import il.co.rtcohen.rt.dal.repositories.AreaRepository;
import il.co.rtcohen.rt.dal.repositories.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;

@SpringView(name = CitiesView.VIEW_NAME)
public class CitiesView extends AbstractDataView<Area> {
    static final String VIEW_NAME = "cities";

    // Repositories
    private final CityRepository cityRepository;
    private final AreaRepository areaRepository;

    // Grids
    private CityGrid cityGrid;

    @Autowired
    private CitiesView(ErrorHandler errorHandler, CityRepository cityRepository, AreaRepository areaRepository) {
        super(errorHandler, "citiesTitle");
        this.cityRepository = cityRepository;
        this.areaRepository = areaRepository;
    }

    @Override
    void addGrids() {
        addGrid();
    }

    void addGrid() {
        removeGrid();
        cityGrid = new CityGrid(cityRepository, areaRepository);
        addComponentsAndExpand(cityGrid.getVerticalLayout(true, false));
    }

    @Override
    void removeGrids() {
        removeGrid();
    }

    void removeGrid() {
        if (null != cityGrid) {
            removeComponent(cityGrid.getVerticalLayout());
            cityGrid = null;
        }
    }

    @Override
    void setTabIndexesAndFocus() {
        cityGrid.focus();
    }
}
