package il.co.rtcohen.rt.app.grids;

import java.util.function.Predicate;
import java.util.function.Supplier;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.*;

import il.co.rtcohen.rt.app.uiComponents.*;
import il.co.rtcohen.rt.app.uiComponents.columns.CustomCheckBoxColumn;
import il.co.rtcohen.rt.app.uiComponents.columns.CustomTextColumn;
import il.co.rtcohen.rt.app.uiComponents.fields.CustomButton;
import il.co.rtcohen.rt.app.uiComponents.fields.CustomTextField;
import il.co.rtcohen.rt.dal.dao.interfaces.AbstractTypeWithNameAndActiveFields;
import il.co.rtcohen.rt.dal.dao.interfaces.Cloneable;
import il.co.rtcohen.rt.dal.repositories.interfaces.AbstractTypeWithNameAndActiveFieldsRepository;

public class AbstractTypeWithNameAndActiveFieldsGrid<T extends AbstractTypeWithNameAndActiveFields & Cloneable<T>>
        extends AbstractTypeFilterGrid<T> {
    private final String NAME_COLUMN_ID = "nameColumn";
    private TextField nameFilterTextField;
    private boolean showNewNameField = false;

    public AbstractTypeWithNameAndActiveFieldsGrid(
            AbstractTypeWithNameAndActiveFieldsRepository<T> abstractTypeWithNameAndActiveFieldsRepository,
            Supplier<T> newItemSupplier,
            String titleKey,
            Predicate<T> itemsFilterPredicate,
            boolean applyDefaultFilters) {
        super(
                abstractTypeWithNameAndActiveFieldsRepository,
                newItemSupplier,
                titleKey,
                itemsFilterPredicate,
                applyDefaultFilters);
        this.setCustomSortColumnId(NAME_COLUMN_ID);
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

    protected CustomCheckBoxColumn<T> addActiveColumn() {
        return CustomCheckBoxColumn.addToGrid(
                T::isActive,
                T::setActive,
                "activeColumn",
                "active",
                Boolean.TRUE,
                this);
    }

    protected void addNameColumn() {
        CustomTextColumn<T> column = CustomTextColumn.addToGrid(
                T::getName,
                T::setName,
                true,
                230,
                NAME_COLUMN_ID,
                "name",
                false,
                true,
                false,
                this);
        nameFilterTextField = column.getFilterField();
    }

    public void setShowNewNameField(boolean showNewNameField) {
        this.showNewNameField = showNewNameField;
    }

    @Override
    protected RtlHorizontalLayout customAdditionalLayout() {
        if (this.showNewNameField) {
            return this.newNameLayout();
        } else {
            return null;
        }
    }

    private RtlHorizontalLayout newNameLayout() {
        RtlHorizontalLayout newNameLayout = new RtlHorizontalLayout();

        Label before = new CustomLabel("add", null, true, CustomLabel.LabelStyle.VERY_SMALL_TEXT);

        Button addButton = new CustomButton(VaadinIcons.PLUS, true, clickEvent -> addEmptyLines(1));
        addButton.setEnabled(false);

        CustomTextField newNameField = new CustomTextField(
                null,
                null,
                event -> {
                    nameFilterTextField.setValue(event.getValue());
                    addButton.setEnabled(!event.getValue().isEmpty());
                });
        newNameField.setWidth("100%");

        newNameLayout.addComponents(before);
        newNameLayout.addComponentsAndExpand(newNameField);
        newNameLayout.addComponents(addButton);

        return newNameLayout;
    }
}
