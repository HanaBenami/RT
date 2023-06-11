package il.co.rtcohen.rt.app.grids;

import java.util.function.Predicate;
import java.util.function.Supplier;
import com.vaadin.ui.TextField;

import il.co.rtcohen.rt.app.uiComponents.CustomCheckBoxColumn;
import il.co.rtcohen.rt.app.uiComponents.CustomTextColumn;
import il.co.rtcohen.rt.dal.dao.interfaces.AbstractTypeWithNameAndActiveFields;
import il.co.rtcohen.rt.dal.repositories.interfaces.AbstractTypeWithNameAndActiveFieldsRepository;

public class AbstractTypeWithNameAndActiveFieldsGrid<T extends AbstractTypeWithNameAndActiveFields> extends AbstractTypeFilterGrid<T> {
    private final String NAME_COLUMN_ID = "nameColumn";
    private TextField nameFilterTextField;

    public AbstractTypeWithNameAndActiveFieldsGrid(
            AbstractTypeWithNameAndActiveFieldsRepository<T> abstractTypeWithNameAndActiveFieldsRepository,
            Supplier<T> newItemSupplier,
            String titleKey,
            Predicate<T> itemsFilterPredicate
    ) {
        super(
                abstractTypeWithNameAndActiveFieldsRepository,
                newItemSupplier,
                titleKey,
                itemsFilterPredicate
        );
    }

    @Override
    public void initGrid() {
        this.setCustomSortColumnId(NAME_COLUMN_ID);
        super.initGrid();
    }

    @Override
    protected T getNewItem() {
        T t = super.getNewItem();
        assert null != nameFilterTextField;
        t.setName(nameFilterTextField.getValue());
        return t;
    }

    @Override
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

    protected void addNameColumn() {
        CustomTextColumn<T> column = CustomTextColumn.addToGrid(
                T::getName,
                T::setName,
                230,
                NAME_COLUMN_ID,
                "name",
                false,
                true,
                false,
                this
        );
        nameFilterTextField = column.getFilterField();
    }
}
