package il.co.rtcohen.rt.app.uiComponents.columns;

import com.vaadin.data.ValueProvider;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.server.Setter;
import com.vaadin.ui.TextField;

import org.apache.poi.ss.formula.functions.T;
import org.vaadin.addons.filteringgrid.FilterGrid;
import org.vaadin.addons.filteringgrid.filters.InMemoryFilter;

import il.co.rtcohen.rt.app.grids.AbstractTypeFilterGrid;
import il.co.rtcohen.rt.app.uiComponents.StyleSettings;
import il.co.rtcohen.rt.app.uiComponents.fields.CustomIntegerField;
import il.co.rtcohen.rt.dal.dao.interfaces.AbstractType;
import il.co.rtcohen.rt.dal.dao.interfaces.Cloneable;

// T - Type of object represented by the grid
public class CustomIntegerColumn<T extends AbstractType & Cloneable<T>>
        extends AbstractCustomColumn<T, Integer, TextField> {
    private CustomIntegerColumn(
            AbstractTypeFilterGrid<T> grid,
            FilterGrid.Column<T, Integer> column,
            TextField filterField,
            String columnId,
            String labelKey,
            Integer width) {
        super(grid, column, filterField, columnId, labelKey, width);
    }

    // Usage:
    // CustomNumericColumn.addToGrid(
    // T::getId,
    // null,
    // 70,
    // idFieldId,
    // "id",
    // false,
    // true,
    // true,
    // this
    // );
    public static <T extends AbstractType & Cloneable<T>> CustomIntegerColumn<T> addToGrid(
            ValueProvider<T, Integer> valueProvider,
            Setter<T, Integer> setter,
            Integer minValue,
            Integer maxValue,
            int width,
            String columnId,
            String labelKey,
            boolean isBoldText,
            boolean allowFilter, boolean filterByExactMatch, AbstractTypeFilterGrid<T> grid) {
        return addToGrid(valueProvider, setter, minValue, maxValue,
                value -> true, "",
                width, columnId, labelKey, isBoldText, allowFilter, filterByExactMatch, grid);
    }

    public static <T extends AbstractType & Cloneable<T>> CustomIntegerColumn<T> addToGrid(
            ValueProvider<T, Integer> valueProvider,
            Setter<T, Integer> setter,
            Integer minValue,
            Integer maxValue,
            SerializablePredicate<String> validatorFunction,
            String validatorMessage,
            int width,
            String columnId,
            String labelKey,
            boolean isBoldText,
            boolean allowFilter, boolean filterByExactMatch, AbstractTypeFilterGrid<T> grid) {
        // Basic column
        FilterGrid.Column<T, Integer> column = grid.addColumn(valueProvider);
        if (isBoldText) {
            column.setStyleGenerator(T -> getBoldNumberStyle(valueProvider.apply(T)));
        }

        // Setter
        if (null != setter) {
            column.setEditorBinding(grid.getEditor().getBinder().forField(
                    new CustomIntegerField(null, null, minValue, maxValue, false, null, null))
                    .withValidator(validatorFunction, validatorMessage)
                    .bind(
                            T -> {
                                Integer value = valueProvider.apply((T) T);
                                return (null == value ? "0" : value.toString());
                            },
                            (T, value) -> setter.accept((T) T,
                                    (null == value || value.isEmpty() ? 0 : Integer.parseInt(value)))));
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
                            ? (currentValue, filterValue) -> null == filterValue
                                    || filterValue.isEmpty()
                                    || ((null != currentValue) && filterValue.equals(currentValue.toString()))
                            : InMemoryFilter.StringComparator.containsIgnoreCase()));
        }

        return new CustomIntegerColumn<T>(grid, column, filterField, columnId, labelKey, width);
    }

    static private String getBoldNumberStyle(Integer value) {
        return (null == value || 0 == value ? "null" : "bold");
    }
}
