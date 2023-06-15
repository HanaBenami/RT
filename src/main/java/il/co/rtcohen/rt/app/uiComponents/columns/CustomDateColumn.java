package il.co.rtcohen.rt.app.uiComponents.columns;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.Setter;
import il.co.rtcohen.rt.app.uiComponents.fields.CustomDateField;
import il.co.rtcohen.rt.app.uiComponents.StyleSettings;
import il.co.rtcohen.rt.dal.dao.interfaces.Cloneable;
import org.vaadin.addons.filteringgrid.FilterGrid;

import il.co.rtcohen.rt.app.grids.AbstractTypeFilterGrid;
import il.co.rtcohen.rt.dal.dao.interfaces.AbstractType;
import il.co.rtcohen.rt.utils.Date;

import java.time.LocalDate;
import java.util.function.Supplier;

// T - Type of object represented by the grid
public class CustomDateColumn<T extends AbstractType & Cloneable<T>> extends AbstractCustomColumn<T, LocalDate, CustomDateField> {
    private CustomDateColumn(
                             AbstractTypeFilterGrid<T> grid,
                             FilterGrid.Column<T, LocalDate> column,
                             CustomDateField filterField,
                             String columnId,
                             String labelKey
    ) {
        super(grid, column, filterField, columnId, labelKey, 130);
    }

    public static <T extends AbstractType & Cloneable<T>> CustomDateColumn<T> addToGrid(
            ValueProvider<T, Date> valueProvider,
            Setter<T, Date> setter,
            Supplier<LocalDate> dateSupplierToSetOnFocusEvent,
            String columnId,
            String labelKey,
            boolean isBoldText,
            AbstractTypeFilterGrid<T> grid) {
        // Basic column
        FilterGrid.Column<T, LocalDate> column = grid.addColumn(
                T -> {
                    Date date = valueProvider.apply(T);
                    return (null == date ? null : date.getLocalDate());
                },
                CustomDateField.dateRenderer()
        );
        column.setStyleGenerator(T -> CustomDateField.getDateStyle(valueProvider.apply(T), isBoldText));

        // Setter
        if (null != setter) {
            CustomDateField dateField = new CustomDateField();
            column.setEditorBinding(grid.getEditor().getBinder().forField(dateField).bind(
                    T -> valueProvider.apply(T).getLocalDate(),
                    (T, value) -> setter.accept(T, new Date(value))
            ));
            column.setEditable(true);
            if (null != dateSupplierToSetOnFocusEvent) {
                dateField.addFocusListener(focusEvent -> dateField.setValue(dateSupplierToSetOnFocusEvent.get()));
            }
        }

        // Filter
        CustomDateField filterField = new CustomDateField();
        filterField.setWidth(StyleSettings.FILTER_FIELD_WIDTH);
        filterField.setHeight(StyleSettings.FILTER_FIELD_HEIGHT);
        filterField.addContextClickListener(contextClickEvent -> filterField.setValue(null));
        column.setFilter(
                filterField,
                (currentValue, filterValue) -> filterValue == null || currentValue.isEqual(filterValue)
        );

        return new CustomDateColumn<T>(grid, column, filterField, columnId, labelKey);
    }
}
