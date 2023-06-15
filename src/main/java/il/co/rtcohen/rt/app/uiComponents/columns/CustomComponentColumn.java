package il.co.rtcohen.rt.app.uiComponents.columns;

import com.vaadin.data.ValueProvider;
import com.vaadin.ui.Component;
import il.co.rtcohen.rt.dal.dao.interfaces.Cloneable;
import org.vaadin.addons.filteringgrid.FilterGrid;

import il.co.rtcohen.rt.app.grids.AbstractTypeFilterGrid;
import il.co.rtcohen.rt.dal.dao.interfaces.AbstractType;

// F - Type of component
// T - Type of object represented by the grid
public class CustomComponentColumn<T extends AbstractType & Cloneable<T>, F extends Component> extends AbstractCustomColumn<T, Component, F> {
    private CustomComponentColumn(
            AbstractTypeFilterGrid<T> grid,
            FilterGrid.Column<T, Component> column,
            String columnId,
            String labelKey,
            Integer width
    ) {
        super(grid, column, null, columnId, labelKey, width);
        column.setSortable(false);
    }

    public static <T extends AbstractType & Cloneable<T>, F extends Component> CustomComponentColumn<T, F> addToGrid(
            ValueProvider<T, Component> componentProvider,
            int width,
            String columnId,
            String labelKey,
            AbstractTypeFilterGrid<T> grid
    ) {
        FilterGrid.Column<T, Component> column = grid.addComponentColumn(componentProvider);
        return new CustomComponentColumn<>(grid, column, columnId, labelKey, width);
    }
}
