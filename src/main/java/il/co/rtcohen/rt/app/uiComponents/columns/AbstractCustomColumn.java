package il.co.rtcohen.rt.app.uiComponents.columns;

import com.vaadin.ui.Component;
import il.co.rtcohen.rt.app.LanguageSettings;
import il.co.rtcohen.rt.app.grids.AbstractTypeFilterGrid;
import il.co.rtcohen.rt.dal.dao.interfaces.AbstractType;
import il.co.rtcohen.rt.dal.dao.interfaces.Cloneable;
import org.vaadin.addons.filteringgrid.FilterGrid;

// T - Object
// D - Data/field type (e.g. String)
// F - Editing component (e.g. TextField)
class AbstractCustomColumn<T extends AbstractType & Cloneable<T>, D, F extends Component> {
    AbstractTypeFilterGrid<T> grid;
    FilterGrid.Column<T, D> column;
    F filterField;
    String columnId;
    String labelKey;
    Integer width;

    public AbstractCustomColumn(
            AbstractTypeFilterGrid<T> grid,
            FilterGrid.Column<T, D> column,
            F filterField,
            String columnId,
            String labelKey,
            Integer width
    ) {
        this.grid = grid;
        this.column = column;
        this.filterField = filterField;
        this.columnId = columnId;
        this.labelKey = labelKey;
        this.width = width;

        this.column.setId(columnId).setExpandRatio(1).setResizable(true).setHidable(true);
        if (null != width) {
            this.column.setWidth(width);
        }
        if (null != labelKey) {
            grid.getDefaultHeaderRow().getCell(columnId).setText(LanguageSettings.getLocaleString(this.labelKey));
        }
    }

    public FilterGrid.Column<T, D> getColumn() {
        return column;
    }

    public F getFilterField() {
        return filterField;
    }
}
