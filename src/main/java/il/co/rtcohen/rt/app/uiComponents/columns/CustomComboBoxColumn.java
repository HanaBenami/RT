package il.co.rtcohen.rt.app.uiComponents.columns;

import com.vaadin.data.Binder;
import com.vaadin.data.ValueProvider;
import com.vaadin.server.Setter;
import il.co.rtcohen.rt.app.GeneralErrorHandler;
import il.co.rtcohen.rt.app.LanguageSettings;
import il.co.rtcohen.rt.app.grids.AbstractTypeFilterGrid;
import il.co.rtcohen.rt.app.uiComponents.fields.CustomCheckBox;
import il.co.rtcohen.rt.app.uiComponents.fields.CustomComboBox;
import il.co.rtcohen.rt.dal.dao.interfaces.Cloneable;
import il.co.rtcohen.rt.utils.NullPointerExceptionWrapper;
import il.co.rtcohen.rt.dal.dao.interfaces.AbstractType;
import il.co.rtcohen.rt.dal.dao.interfaces.BindRepository;
import il.co.rtcohen.rt.dal.dao.interfaces.Nameable;
import org.vaadin.addons.filteringgrid.FilterGrid;

import java.util.logging.Logger;

// C - Type of object represented by the combobox
// T - Type of object represented by the grid
public class CustomComboBoxColumn<C extends Nameable & BindRepository<C>, T extends AbstractType & Cloneable<T>>
        extends AbstractCustomColumn<T, String, CustomComboBox<C>> {

    private CustomComboBoxColumn(
            AbstractTypeFilterGrid<T> grid,
            FilterGrid.Column<T, String> column,
            String columnId,
            String labelKey,
            int width
    ) {
        super(grid, column, null, columnId, labelKey, width);
    }

    public static <C extends Nameable & BindRepository<C>, T extends AbstractType & Cloneable<T>> CustomComboBoxColumn<C, T> addToGrid(
            CustomComboBox<C> selectionComboBox,
            CustomComboBox<C> filterComboBox,
            ValueProvider<T, C> valueProvider,
            Setter<T, C> setter,
            boolean required,
            int width,
            String columnId,
            String labelKey,
            AbstractTypeFilterGrid<T> grid) {
        // Basic column
        FilterGrid.Column<T, String> column = grid.addColumn(
                T -> NullPointerExceptionWrapper.getWrapper(T, t -> valueProvider.apply(t).getName(), "")
        );

        // Setter
        if (null != setter) {
            assert null != selectionComboBox && null != valueProvider;
            Binder.BindingBuilder<T, C> binder = grid.getEditor().getBinder().forField(selectionComboBox);
            if (required) {
                binder = binder.asRequired();
                selectionComboBox.setEmptySelectionAllowed(false);
            }
            column.setEditorBinding(binder.bind(
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

        return new CustomComboBoxColumn<>(grid, column, columnId, labelKey, width);
    }

    private static Logger getLogger() {
        return Logger.getLogger(GeneralErrorHandler.class.getName());
    }
}
