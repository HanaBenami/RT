package il.co.rtcohen.rt.app.uiComponents.columns;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.Setter;
import com.vaadin.ui.TextField;
import il.co.rtcohen.rt.app.grids.AbstractTypeFilterGrid;
import il.co.rtcohen.rt.app.uiComponents.StyleSettings;
import il.co.rtcohen.rt.app.uiComponents.fields.CustomIntegerField;
import il.co.rtcohen.rt.dal.dao.interfaces.AbstractType;
import il.co.rtcohen.rt.dal.dao.interfaces.Cloneable;
import org.vaadin.addons.filteringgrid.FilterGrid;
import org.vaadin.addons.filteringgrid.filters.InMemoryFilter;

// T - Type of object represented by the grid
public class CustomIntegerColumnWithStringValue<T extends AbstractType & Cloneable<T>> extends AbstractCustomColumn<T, String, TextField> {
    private CustomIntegerColumnWithStringValue(
            AbstractTypeFilterGrid<T> grid,
            FilterGrid.Column<T, String> column,
            TextField filterField,
            String columnId,
            String labelKey,
            Integer width
    ) {
        super(grid, column, filterField, columnId, labelKey, width);
    }

    // Usage:
    //        CustomNumericColumn.addToGrid(
    //            T::getId,
    //            null,
    //            70,
    //            idFieldId,
    //            "id",
    //            false,
    //            true,
    //            true,
    //            this
    //        );
    public static <T extends AbstractType & Cloneable<T>> CustomIntegerColumnWithStringValue<T> addToGrid(
            ValueProvider<T, String> valueProvider,
            Setter<T, Integer> setter,
            Integer minValue,
            Integer maxValue,
            int width,
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
        if (null != setter) {
            column.setEditorBinding(grid.getEditor().getBinder().forField(
                    new CustomIntegerField(null, null, minValue, maxValue, false, null, null)
            ).bind(
                    T -> {
                        String value = valueProvider.apply(T);
                        return (null == value ? "" : value.replaceAll("\\D", ""));
                    },
                    (T, value) -> setter.accept(T, (null == value || value.isEmpty() ? 0 : Integer.parseInt(value)))
            ));
        }

        // Filter
        TextField filterField = null;
        if (allowFilter) {
            filterField = new CustomIntegerField(null, null, minValue, maxValue, true, null, null);
            filterField.setWidth(StyleSettings.FILTER_FIELD_WIDTH);
            filterField.setHeight(StyleSettings.FILTER_FIELD_HEIGHT);
            column.setFilter(
                    filterField,
                    (filterByExactMatch
                            ? (currentValue, filterValue) ->
                                    null == filterValue
                                            || filterValue.isEmpty()
                                            || ((null != currentValue) && filterValue.equals(currentValue.toString()))
                            : InMemoryFilter.StringComparator.containsIgnoreCase()
                    )
            );
        }

        return new CustomIntegerColumnWithStringValue<T>(grid, column, filterField, columnId, labelKey, width);
    }

    static private String getBoldStyle(String value) {
        return (null == value ? "null" : "bold");
    }
}
