package il.co.rtcohen.rt.app.uiComponents.columns;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.Setter;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import il.co.rtcohen.rt.app.GeneralErrorHandler;
import il.co.rtcohen.rt.app.grids.AbstractTypeFilterGrid;
import il.co.rtcohen.rt.app.uiComponents.fields.CustomCheckBox;
import il.co.rtcohen.rt.app.uiComponents.StyleSettings;
import il.co.rtcohen.rt.dal.dao.interfaces.AbstractType;
import il.co.rtcohen.rt.dal.dao.interfaces.Cloneable;
import org.vaadin.addons.filteringgrid.FilterGrid;

import java.util.logging.Logger;

public class CustomCheckBoxColumn<T extends AbstractType & Cloneable<T>>
        extends AbstractCustomColumn<T, CustomCheckBox, CustomCheckBox> {
    private CustomCheckBoxColumn(
            AbstractTypeFilterGrid<T> grid,
            FilterGrid.Column<T, CustomCheckBox> column,
            String columnId,
            String labelKey) {
        super(grid, column, null, columnId, labelKey, 80);
        this.column.setSortable(false);
    }

    // Usage:
    // FilterGrid.Column<Call, Component> column = CustomCheckBoxColumn.addToGrid((
    // Call::isHere,
    // (Setter<Call, Boolean>) Call::setHere,
    // "activeColumn",
    // "active",
    // Boolean.TRUE,
    // this
    // );
    public static <T extends AbstractType & Cloneable<T>> CustomCheckBoxColumn<T> addToGrid(
            ValueProvider<T, Boolean> valueProvider,
            Setter<T, Boolean> setter,
            String columnId,
            String labelKey,
            Boolean defaultFilter,
            AbstractTypeFilterGrid<T> grid) {
        // Basic column
        FilterGrid.Column<T, CustomCheckBox> column = grid.addComponentColumn(
                T -> new CustomCheckBox(null, valueProvider.apply(T), true));

        // Setter
        if (null != setter) {
            column.setEditorBinding(grid.getEditor().getBinder().forField(new CheckBox()).bind(valueProvider, setter));
        }

        // Filter
        ComboBox<Boolean> filterComboBox = new ComboBox<>();
        filterComboBox.setItems(Boolean.TRUE, Boolean.FALSE);
        filterComboBox.setItemCaptionGenerator(
                bool -> (null == bool ? "" : (Boolean.TRUE == bool ? "V" : "X")));
        filterComboBox.setEmptySelectionAllowed(true);
        if (grid.applyDefaultFilters && null != defaultFilter) {
            filterComboBox.setValue(defaultFilter);
        }
        filterComboBox.setHeight(StyleSettings.FILTER_FIELD_HEIGHT);
        filterComboBox.setWidth(StyleSettings.FILTER_FIELD_WIDTH);
        column.setFilter(
                CheckBox::getValue,
                filterComboBox,
                (currentValue, filterValue) -> (null == filterValue || filterValue.equals(currentValue)));

        return new CustomCheckBoxColumn<>(grid, column, columnId, labelKey);
    }

    private static Logger getLogger() {
        return Logger.getLogger(GeneralErrorHandler.class.getName());
    }
}
