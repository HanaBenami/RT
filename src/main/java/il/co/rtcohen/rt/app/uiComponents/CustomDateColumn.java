package il.co.rtcohen.rt.app.uiComponents;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.Setter;
import il.co.rtcohen.rt.app.LanguageSettings;
import il.co.rtcohen.rt.dal.dao.interfaces.Cloneable;
import org.vaadin.addons.filteringgrid.FilterGrid;

import il.co.rtcohen.rt.app.grids.AbstractTypeFilterGrid;
import il.co.rtcohen.rt.dal.dao.interfaces.AbstractType;
import il.co.rtcohen.rt.utils.Date;

import java.time.LocalDate;
import java.util.function.Supplier;

// T - Type of object represented by the grid
public class CustomDateColumn<T extends AbstractType & Cloneable<T>> {
    private CustomDateColumn() {};

    public static <T extends AbstractType & Cloneable<T>> FilterGrid.Column<T, LocalDate> addToGrid(
            ValueProvider<T, Date> valueProvider,
            Setter<T, Date> setter,
            Supplier<LocalDate> dateSupplierToSetOnFocusEvent,
            String id,
            String label,
            boolean isBoldText,
            AbstractTypeFilterGrid<T> abstractTypeFilterGrid) {
        // Basic column
        FilterGrid.Column<T, LocalDate> column = abstractTypeFilterGrid.addColumn(
                T -> {
                    Date date = valueProvider.apply(T);
                    return (null == date ? null : date.getLocalDate());
                },
                CustomDateField.dateRenderer()
        );
        column.setId(id).setExpandRatio(1).setWidth(130).setResizable(true).setSortable(true).setHidable(true);
        column.setStyleGenerator(T -> CustomDateField.getDateStyle(valueProvider.apply(T), isBoldText));
        abstractTypeFilterGrid.getDefaultHeaderRow().getCell(id).setText(LanguageSettings.getLocaleString(label));

        // Setter
        if (null != setter) {
            CustomDateField dateField = new CustomDateField();
            column.setEditorBinding(abstractTypeFilterGrid.getEditor().getBinder().forField(dateField).bind(
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

        return column;
    }
}
