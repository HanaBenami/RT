package il.co.rtcohen.rt.app.grids;

import java.util.function.Supplier;

import il.co.rtcohen.rt.app.uiComponents.CustomCheckBoxColumn;
import il.co.rtcohen.rt.dal.dao.interfaces.AbstractTypeWithNameAndActiveFields;
import il.co.rtcohen.rt.dal.repositories.interfaces.AbstractTypeWithNameAndActiveFieldsRepository;

public class AbstractTypeWithNameAndActiveFieldsGrid<T extends AbstractTypeWithNameAndActiveFields> extends AbstractTypeFilterGrid<T> {
    private final String NAME_COLUMN_ID = "nameColumn";

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
        this.setCustomSortColumnId(NAME_COLUMN_ID);
    }

    protected void addColumns() {
        addActiveColumn();
        addNameColumn();
        addIdColumn();
    }

    protected void addActiveColumn() {
        CustomCheckBoxColumn.addToGrid(
                T::isActive,
                T::setActive,
                "activeColumn",
                "active",
                Boolean.TRUE,
                this
        );
    }

    private void addNameColumn() {
        this.addTextColumn(
                T::getName,
                T::setName,
                230,
                NAME_COLUMN_ID,
                "name"
        );
    }
}
