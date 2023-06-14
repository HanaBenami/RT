package il.co.rtcohen.rt.app.uiComponents.columns;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.Setter;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import il.co.rtcohen.rt.app.GeneralErrorHandler;
import il.co.rtcohen.rt.app.LanguageSettings;
import il.co.rtcohen.rt.app.grids.AbstractTypeFilterGrid;
import il.co.rtcohen.rt.app.uiComponents.fields.CustomCheckBox;
import il.co.rtcohen.rt.app.uiComponents.StyleSettings;
import il.co.rtcohen.rt.dal.dao.interfaces.AbstractType;
import il.co.rtcohen.rt.dal.dao.interfaces.Cloneable;
import org.vaadin.addons.filteringgrid.FilterGrid;

import java.util.logging.Logger;

// T - Type of object represented by the grid
public class CustomCheckBoxColumn<T extends AbstractType & Cloneable<T>> {
    private CustomCheckBoxColumn() {}

    // Usage:
    //    FilterGrid.Column<Call, Component> column = CustomCheckBoxColumn.addToGrid((
    //                    Call::isHere,
    //            (Setter<Call, Boolean>) Call::setHere,
    //            "activeColumn",
    //            "active",
    //            Boolean.TRUE,
    //            this
    //    );
    // TODO: extend AbstractCustomColumn
    public static <T extends AbstractType & Cloneable<T>> FilterGrid.Column<T, Component> addToGrid(ValueProvider<T, Boolean> valueProvider,
                                                                                                    Setter<T, Boolean> setter,
                                                                                                    String id,
                                                                                                    String label,
                                                                                                    Boolean defaultFilter,
                                                                                                    AbstractTypeFilterGrid<T> abstractTypeFilterGrid) {
        // Basic column
        FilterGrid.Column<T, Component> column = abstractTypeFilterGrid.addComponentColumn(
                T -> new CustomCheckBox(null, valueProvider.apply(T), true)
        );
        column.setId(id).setExpandRatio(1).setWidth(50).setResizable(true).setSortable(false).setHidable(true);
        abstractTypeFilterGrid.getDefaultHeaderRow().getCell(id).setText(LanguageSettings.getLocaleString(label));

        // Setter
        if (null != setter) {
            column.setEditorBinding(abstractTypeFilterGrid.getEditor().getBinder().forField(new CheckBox()).bind(valueProvider, setter));
        }

        // Filter
        ComboBox<Boolean> filterComboBox = new ComboBox<>();
        filterComboBox.setItems(Boolean.TRUE, Boolean.FALSE);
        filterComboBox.setItemCaptionGenerator(
                bool -> (null == bool ? "" : (Boolean.TRUE == bool ? "V" : "X"))
        );
        filterComboBox.setEmptySelectionAllowed(true);
        if (null != defaultFilter) {
            filterComboBox.setValue(defaultFilter);
        }
        filterComboBox.setHeight(StyleSettings.FILTER_FIELD_HEIGHT);
        filterComboBox.setWidth(StyleSettings.FILTER_FIELD_WIDTH);
        column.setFilter(checkBox -> (
                (CheckBox)checkBox).getValue(),
                filterComboBox,
                (currentValue, filterValue) -> (null == filterValue || filterValue.equals(currentValue))
        );
        column.setWidth(80);

        return column;
    }

    private static Logger getLogger() {
        return Logger.getLogger(GeneralErrorHandler.class.getName());
    }
}
