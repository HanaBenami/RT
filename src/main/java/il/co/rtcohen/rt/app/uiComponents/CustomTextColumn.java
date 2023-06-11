package il.co.rtcohen.rt.app.uiComponents;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.Setter;
import com.vaadin.ui.TextField;
import il.co.rtcohen.rt.app.grids.AbstractTypeFilterGrid;
import il.co.rtcohen.rt.dal.dao.interfaces.AbstractType;
import org.vaadin.addons.filteringgrid.FilterGrid;
import org.vaadin.addons.filteringgrid.filters.InMemoryFilter;

import static il.co.rtcohen.rt.app.uiComponents.StyleSettings.getBoldStyle;

// T - Type of object represented by the grid
public class CustomTextColumn<T extends AbstractType> extends AbstractCustomColumn<T, String, TextField> {
    private CustomTextColumn(
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
    public static <T extends AbstractType> CustomTextColumn<T> addToGrid(
            ValueProvider<T, String> valueProvider,
            Setter<T, String> setter,
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
        if (null != setter) {
            column.setEditorBinding(grid.getEditor().getBinder().forField(new TextField()).bind(
                    T -> {
                        String value = valueProvider.apply(T);
                        return (null == value ? "0" : value);
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
            grid.setFilterField(columnId, filterField);
        }

        return new CustomTextColumn<T>(grid, column, filterField, columnId, labelKey, width);
    }
}
