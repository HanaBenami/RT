package il.co.rtcohen.rt.app.grids;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.Setter;
import com.vaadin.ui.Component;
import il.co.rtcohen.rt.app.UiComponents.UIComponents;
import il.co.rtcohen.rt.dal.dao.AbstractTypeWithNameAndActiveFields;
import il.co.rtcohen.rt.dal.repositories.*;

import java.util.function.Supplier;

public class AbstractTypeWithNameAndActiveFieldsGrid<T extends AbstractTypeWithNameAndActiveFields> extends AbstractTypeFilterGrid<T> {

    public AbstractTypeWithNameAndActiveFieldsGrid(
            AbstractTypeWithNameAndActiveFieldsRepository<T> abstractTypeWithNameAndActiveFieldsRepository,
            Supplier<T> newItemSupplier,
            String titleKey
    ) {
        super(
                abstractTypeWithNameAndActiveFieldsRepository,
                newItemSupplier,
                titleKey,
                null
        );
        this.initGrid();
    }

    protected void addColumns() {
        addActiveColumn();
        addNameColumn();
        addIdColumn();
    }

    private void addActiveColumn() {
        this.addBooleanColumn(
                (ValueProvider<T, Component>) GeneralObject -> UIComponents.checkBox(GeneralObject.isActive(),true),
                (ValueProvider<T, Boolean>) T::isActive,
                (Setter<T, Boolean>) T::setActive,
                "activeColumn",
                "active",
                Boolean.TRUE
        );
    }

    private void addNameColumn() {
        this.addTextColumn(
                T::getName,
                T::setName,
                230,
                "nameColumn",
                "name"
        );
    }
}
