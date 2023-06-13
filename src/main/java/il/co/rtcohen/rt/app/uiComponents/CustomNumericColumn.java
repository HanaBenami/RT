package il.co.rtcohen.rt.app.uiComponents;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.Setter;
import com.vaadin.ui.TextField;
import il.co.rtcohen.rt.app.LanguageSettings;
import il.co.rtcohen.rt.app.grids.AbstractTypeFilterGrid;
import il.co.rtcohen.rt.dal.dao.interfaces.AbstractType;
import il.co.rtcohen.rt.dal.dao.interfaces.Cloneable;
import org.vaadin.addons.filteringgrid.FilterGrid;
import org.vaadin.addons.filteringgrid.filters.InMemoryFilter;

// T - Type of object represented by the grid
// TODO: extend AbstractCustomColumn
public class CustomNumericColumn<T extends AbstractType> {
    private CustomNumericColumn() {};

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
    public static <T extends AbstractType & Cloneable<T>> FilterGrid.Column<T, Integer> addToGrid(
            ValueProvider<T, Integer> valueProvider,
            Setter<T, Integer> setter,
            int width,
            String id,
            String label,
            boolean isBoldText,
            boolean allowFilter,
            boolean filterByExactMatch,
            AbstractTypeFilterGrid<T> abstractTypeFilterGrid) {
        // Basic column
        FilterGrid.Column<T, Integer> column = abstractTypeFilterGrid.addColumn(valueProvider);
        column.setId(id).setExpandRatio(1).setResizable(true).setWidth(width).setHidable(true);
        if (isBoldText) {
            column.setStyleGenerator(T -> getBoldNumberStyle(valueProvider.apply(T))
            );
        }
        abstractTypeFilterGrid.getDefaultHeaderRow().getCell(id).setText(LanguageSettings.getLocaleString(label));

        // Setter
        if (null != setter) {
            column.setEditorBinding(abstractTypeFilterGrid.getEditor().getBinder().forField(new TextField()).bind(
                    T -> {
                        Integer value = valueProvider.apply(T);
                        return (null == value ? "0" : value.toString());
                    },
                    (T, value) -> setter.accept(T, (null == value || value.isEmpty() ? 0 : Integer.parseInt(value)))
            ));
        }

        // Filter
        if (allowFilter) {
            TextField filterField = new TextField();
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
            abstractTypeFilterGrid.setFilterField(id, filterField);
        }

        return column;
    }

    static private String getBoldNumberStyle(Integer value) {
        return (null == value || 0 == value ? "null" : "bold");
    }
}
