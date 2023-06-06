package il.co.rtcohen.rt.app.UiComponents;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.Setter;
import il.co.rtcohen.rt.app.GeneralErrorHandler;
import il.co.rtcohen.rt.app.LanguageSettings;
import il.co.rtcohen.rt.app.grids.AbstractTypeFilterGrid;
import il.co.rtcohen.rt.dal.dao.AbstractType;
import il.co.rtcohen.rt.dal.dao.BindRepository;
import il.co.rtcohen.rt.dal.dao.Nameable;
import org.vaadin.addons.filteringgrid.FilterGrid;

import java.util.logging.Logger;

// C - Type of object represented by the combobox
// T - Type of object represented by the grid
public class CustomComboBoxColumn<C extends Nameable & BindRepository<C>, T extends AbstractType> {
    private CustomComboBoxColumn() {}

    public static <C extends Nameable & BindRepository<C>, T extends AbstractType> FilterGrid.Column<T, String> addToGrid(
            CustomComboBox<C> selectionComboBox,
            CustomComboBox<C> filterComboBox,
            ValueProvider<T, String> stringValueProvider,
            ValueProvider<T, C> valueProvider,
            Setter<T, C> setter,
            int width,
            String id,
            String label,
            AbstractTypeFilterGrid<T> abstractTypeFilterGrid) {
        FilterGrid.Column<T, String> column = abstractTypeFilterGrid.addColumn(stringValueProvider).setId(id);
        column.setEditorBinding(abstractTypeFilterGrid.getEditor().getBinder().forField(selectionComboBox).bind(valueProvider, setter));
        column.setWidth(width).setExpandRatio(1).setResizable(true).setHidable(true);

        filterComboBox.setWidth("95%");
        column.setFilter((filterComboBox), (currentValueString, filterValueObject) ->
                (null == filterValueObject || null == currentValueString || filterValueObject.getName().equals(currentValueString)));

        abstractTypeFilterGrid.getDefaultHeaderRow().getCell(id).setText(LanguageSettings.getLocaleString(label));

        return column;
    }

    private static Logger getLogger() {
        return Logger.getLogger(GeneralErrorHandler.class.getName());
    }
}
