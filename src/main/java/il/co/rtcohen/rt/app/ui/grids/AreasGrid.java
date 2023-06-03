package il.co.rtcohen.rt.app.ui.grids;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.Setter;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Component;
import il.co.rtcohen.rt.app.UIComponents;
import il.co.rtcohen.rt.dal.dao.Area;
import il.co.rtcohen.rt.dal.repositories.AreasRepository;

public class AreasGrid extends AbstractFilterGrid<Area> {
    public AreasGrid(AreasRepository areasRepository) {
        super(areasRepository,
                Area::new,
                "area",
                null
        );
        this.initGrid();
    }

    protected void addColumns() {
        addActiveColumn();
        addHereColumn();
        addDisplayOrderColumn();
        addNameColumn();
        addIdColumn();
    }

    protected void sort() {
        this.sort("nameColumn", SortDirection.ASCENDING);
    }

    private void addActiveColumn() {
        this.addBooleanColumn(
                (ValueProvider<Area, Component>) area -> UIComponents.checkBox(area.isActive(),true),
                (ValueProvider<Area, Boolean>) Area::isActive,
                (Setter<Area, Boolean>) Area::setActive,
                "activeColumn",
                "active",
                Boolean.TRUE
        );
    }

    private void addHereColumn() {
        this.addBooleanColumn(
                (ValueProvider<Area, Component>) area -> UIComponents.checkBox(area.getHere(),true),
                (ValueProvider<Area, Boolean>) Area::getHere,
                (Setter<Area, Boolean>) Area::setHere,
                "hereColumn",
                "here",
                null
        );
    }

    private void addDisplayOrderColumn() {
        this.addNumericColumn(
                Area::getDisplayOrder,
                Area::setDisplayOrder,
                80,
                "displayOrderColumn",
                "order"
        );
    }

    private void addNameColumn() {
        this.addTextColumn(
                Area::getName,
                Area::setName,
                230,
                "nameColumn",
                "name"
        );
    }

    private void addIdColumn() {
        this.addNumericColumn(
                Area::getId,
                null,
                80,
                "idColumn",
                "id"
        );
    }
}
