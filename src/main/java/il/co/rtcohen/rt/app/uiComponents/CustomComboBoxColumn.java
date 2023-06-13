package il.co.rtcohen.rt.app.uiComponents;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.Setter;
import il.co.rtcohen.rt.app.GeneralErrorHandler;
import il.co.rtcohen.rt.app.LanguageSettings;
import il.co.rtcohen.rt.app.grids.AbstractTypeFilterGrid;
import il.co.rtcohen.rt.dal.dao.interfaces.Cloneable;
import il.co.rtcohen.rt.utils.NullPointerExceptionWrapper;
import il.co.rtcohen.rt.dal.dao.interfaces.AbstractType;
import il.co.rtcohen.rt.dal.dao.interfaces.BindRepository;
import il.co.rtcohen.rt.dal.dao.interfaces.Nameable;
import org.vaadin.addons.filteringgrid.FilterGrid;

import java.util.logging.Logger;

// C - Type of object represented by the combobox
// T - Type of object represented by the grid
public class CustomComboBoxColumn<C extends Nameable & BindRepository<C>, T extends AbstractType> {
    private CustomComboBoxColumn() {}

    // TODO: extend AbstractCustomColumn
    public static <C extends Nameable & BindRepository<C>, T extends AbstractType & Cloneable<T>> FilterGrid.Column<T, String> addToGrid(
            CustomComboBox<C> selectionComboBox,
            CustomComboBox<C> filterComboBox,
            ValueProvider<T, C> valueProvider,
            Setter<T, C> setter,
            int width,
            String id,
            String label,
            AbstractTypeFilterGrid<T> abstractTypeFilterGrid) {
        // Basic column
        FilterGrid.Column<T, String> column = abstractTypeFilterGrid.addColumn(
                T -> NullPointerExceptionWrapper.getWrapper(T, t -> valueProvider.apply(t).getName(), "")
        );
        column.setId(id).setWidth(width).setExpandRatio(1).setResizable(true).setHidable(true);
        abstractTypeFilterGrid.getDefaultHeaderRow().getCell(id).setText(LanguageSettings.getLocaleString(label));

        // Setter
        if (null != setter) {
            assert null != selectionComboBox && null != valueProvider;
            column.setEditorBinding(abstractTypeFilterGrid.getEditor().getBinder().forField(selectionComboBox).bind(
                    t -> NullPointerExceptionWrapper.getWrapper(t, valueProvider, null),
                    setter
            ));
        }

        // Filter
        filterComboBox.setWidth("95%");
        filterComboBox.setEmptySelectionAllowed(true);
        filterComboBox.setNewItemProvider(null);
        filterComboBox.addContextClickListener(contextClickEvent -> filterComboBox.setValue(null));
        column.setFilter((filterComboBox), (currentValueString, filterValueObject) ->
                (null == filterValueObject || null == currentValueString || filterValueObject.getName().equals(currentValueString)));

        return column;
    }

    private static Logger getLogger() {
        return Logger.getLogger(GeneralErrorHandler.class.getName());
    }
}
