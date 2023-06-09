package il.co.rtcohen.rt.app.uiComponents;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.Setter;
import il.co.rtcohen.rt.app.LanguageSettings;
import org.vaadin.addons.filteringgrid.FilterGrid;

import il.co.rtcohen.rt.app.grids.AbstractTypeFilterGrid;
import il.co.rtcohen.rt.dal.dao.interfaces.AbstractType;
import il.co.rtcohen.rt.utils.Date;

import java.time.LocalDate;

// T - Type of object represented by the grid
public class CustomDateColumn<T extends AbstractType> {
    private CustomDateColumn() {};

    public static <T extends AbstractType> FilterGrid.Column<T, LocalDate> addToGrid(
            ValueProvider<T, Date> valueProvider,
            Setter<T, Date> setter,
            String id,
            String label,
            boolean isBoldText,
            AbstractTypeFilterGrid<T> abstractTypeFilterGrid) {
        // Basic column
        FilterGrid.Column<T, LocalDate> column = abstractTypeFilterGrid.addColumn(
                T -> valueProvider.apply(T).getLocalDate(),
                CustomDateField.dateRenderer()
        );
        column.setId(id).setExpandRatio(1).setWidth(130).setResizable(true).setSortable(true).setHidable(true);
        column.setStyleGenerator(T -> CustomDateField.getDateStyle(valueProvider.apply(T), isBoldText));
        abstractTypeFilterGrid.getDefaultHeaderRow().getCell(id).setText(LanguageSettings.getLocaleString(label));

        // Setter
        if (null != setter) {
            column.setEditorBinding(abstractTypeFilterGrid.getEditor().getBinder().forField(new CustomDateField()).bind(
                    T -> valueProvider.apply(T).getLocalDate(),
                    (T, value) -> setter.accept(T, new Date(value))
            ));
            column.setEditable(true);
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
