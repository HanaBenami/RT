package il.co.rtcohen.rt.app.grids;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.Setter;
import com.vaadin.ui.Component;
import il.co.rtcohen.rt.app.UIComponents;
import il.co.rtcohen.rt.dal.dao.GeneralObject;
import il.co.rtcohen.rt.dal.repositories.*;

public class GeneralObjectGrid extends AbstractFilterGrid<GeneralObject> {

    public GeneralObjectGrid(GeneralObjectRepository generalObjectRepository, String titleKey) {
        super(generalObjectRepository, GeneralObject::new, titleKey, null);
        this.initGrid();
    }

    protected void addColumns() {
        addActiveColumn();
        addNameColumn();
        addIdColumn();
    }

    private void addActiveColumn() {
        this.addBooleanColumn(
                (ValueProvider<GeneralObject, Component>) GeneralObject -> UIComponents.checkBox(GeneralObject.isActive(),true),
                (ValueProvider<GeneralObject, Boolean>) GeneralObject::isActive,
                (Setter<GeneralObject, Boolean>) GeneralObject::setActive,
                "activeColumn",
                "active",
                Boolean.TRUE
        );
    }

    private void addNameColumn() {
        this.addTextColumn(
                GeneralObject::getName,
                GeneralObject::setName,
                230,
                "nameColumn",
                "name"
        );
    }
}
