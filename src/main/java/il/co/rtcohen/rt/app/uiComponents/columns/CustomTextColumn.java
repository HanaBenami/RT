package il.co.rtcohen.rt.app.uiComponents.columns;

import com.vaadin.data.Binder;
import com.vaadin.data.ValueProvider;
import com.vaadin.server.Setter;
import com.vaadin.ui.TextField;
import il.co.rtcohen.rt.app.grids.AbstractTypeFilterGrid;
import il.co.rtcohen.rt.app.uiComponents.fields.CustomTextField;
import il.co.rtcohen.rt.app.uiComponents.StyleSettings;
import il.co.rtcohen.rt.dal.dao.interfaces.AbstractType;

import il.co.rtcohen.rt.dal.dao.interfaces.Cloneable;
import org.vaadin.addons.filteringgrid.FilterGrid;
import org.vaadin.addons.filteringgrid.filters.InMemoryFilter;

import static il.co.rtcohen.rt.app.uiComponents.StyleSettings.getBoldStyle;

// T - Type of object represented by the grid
public class CustomTextColumn<T extends AbstractType & Cloneable<T>> extends AbstractCustomColumn<T, String, TextField> {

    private CustomTextColumn(
            AbstractTypeFilterGrid<T> grid,
            FilterGrid.Column<T, String> column,
            TextField filterField,
            String columnId,
            String labelKey,
            Integer width
    ) {
        // TODO: Decide if to use the width attribute (currently it looks better without it) --> Change signature
        super(grid, column, filterField, columnId, labelKey, null);
    }

    // Usage:
    //    CustomTextColumn<Call>  column = CustomTextColumn.addToGrid(
    //            call -> NullPointerExceptionWrapper.getWrapper(call, c -> c.getSite().getAddress(), ""),
    //            null,
    //            false,
    //            180,
    //            "addressColumn",
    //            "address",
    //            false,
    //            true,
    //            false,
    //            this
    //    );
    //    column.getColumn().setHidable(true);
    //    column.getColumn().setHidden(true);
    public static <T extends AbstractType & Cloneable<T>> CustomTextColumn<T> addToGrid(
            ValueProvider<T, String> valueProvider,
            Setter<T, String> setter,
            boolean required,
            Integer width,
            String columnId,
            String labelKey,
            boolean isBoldText,
            boolean allowFilter,
            boolean filterByExactMatch,
            AbstractTypeFilterGrid<T> grid) {
        // Basic column
        FilterGrid.Column<T, String> column = grid.addColumn(valueProvider);
        if (isBoldText) {
            column.setStyleGenerator(T -> getBoldStyle(valueProvider.apply(T))
            );
        }

        // Setter
        CustomTextField inputField = new CustomTextField(null, null, null);
        if (null != setter) {
            Binder.BindingBuilder<T, String> binder = grid.getEditor().getBinder().forField(inputField);
            if (required) {
                binder = binder.asRequired();
            }
            column.setEditorBinding(binder.bind(
                    T -> {
                        String value = valueProvider.apply(T);
                        return (null == value ? "" : value);
                    },
                    (T, value) -> setter.accept(T, (null == value ? "" : value))
            ));
        }

        // Filter
        TextField filterField = null;
        if (allowFilter) {
            filterField = new TextField();
            filterField.setWidth(StyleSettings.FILTER_FIELD_WIDTH);
            filterField.setHeight(StyleSettings.FILTER_FIELD_HEIGHT);
            column.setFilter(
                    filterField,
                    (filterByExactMatch
                            ? (currentValue, filterValue) -> filterValue.isEmpty() || filterValue.equals(currentValue)
                            : InMemoryFilter.StringComparator.containsIgnoreCase()
                    )
            );
        }

        return new CustomTextColumn<T>(grid, column, filterField, columnId, labelKey, width);
    }
}
