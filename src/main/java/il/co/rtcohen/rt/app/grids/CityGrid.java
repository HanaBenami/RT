package il.co.rtcohen.rt.app.grids;

import il.co.rtcohen.rt.app.uiComponents.columns.CustomComboBoxColumn;
import il.co.rtcohen.rt.app.uiComponents.fields.CustomComboBox;
import il.co.rtcohen.rt.dal.dao.*;
import il.co.rtcohen.rt.dal.repositories.AreaRepository;
import il.co.rtcohen.rt.dal.repositories.CityRepository;

public class CityGrid extends AbstractTypeWithNameAndActiveFieldsGrid<City> {
    private final AreaRepository areaRepository;

    public CityGrid(CityRepository cityRepository, AreaRepository areaRepository) {
        super(
                cityRepository,
                City::new,
                "cities",
                null, true);
        this.areaRepository = areaRepository;
        this.initGrid(true, 0);
    }

    protected void addColumns() {
        addActiveColumn();
        addAreaColumn();
        addNameColumn();
        addIdColumn();
    }

    private void addAreaColumn() {
        CustomComboBoxColumn<Area, City> column = CustomComboBoxColumn.addToGrid(
                CustomComboBox.getComboBox(areaRepository),
                CustomComboBox.getComboBox(areaRepository),
                City::getArea,
                City::setArea,
                true,
                250,
                "areaColumn",
                "area",
                this);
    }
}
