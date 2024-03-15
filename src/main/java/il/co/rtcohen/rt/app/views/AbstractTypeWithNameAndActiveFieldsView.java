package il.co.rtcohen.rt.app.views;

import com.vaadin.server.ErrorHandler;
import com.vaadin.spring.annotation.SpringView;
import il.co.rtcohen.rt.dal.dao.interfaces.Cloneable;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.function.Supplier;

import il.co.rtcohen.rt.app.grids.AbstractTypeWithNameAndActiveFieldsGrid;
import il.co.rtcohen.rt.dal.dao.interfaces.AbstractTypeWithNameAndActiveFields;
import il.co.rtcohen.rt.dal.repositories.interfaces.AbstractTypeWithNameAndActiveFieldsRepository;

@SpringView(name = AbstractTypeWithNameAndActiveFieldsView.VIEW_NAME)
abstract class AbstractTypeWithNameAndActiveFieldsView<T extends AbstractTypeWithNameAndActiveFields & Cloneable<T>>
        extends AbstractDataView<AbstractTypeWithNameAndActiveFields> {

    static final String VIEW_NAME = "update";

    private final AbstractTypeWithNameAndActiveFieldsRepository<T> abstractTypeWithNameAndActiveFieldsRepository;
    private final Supplier<T> newItemSupplier;
    private final String titleKey;
    private AbstractTypeWithNameAndActiveFieldsGrid<T> grid;

    @Autowired
    protected AbstractTypeWithNameAndActiveFieldsView(ErrorHandler errorHandler,
            AbstractTypeWithNameAndActiveFieldsRepository<T> abstractTypeWithNameAndActiveFieldsRepository,
            Supplier<T> newItemSupplier,
            String titleKey) {
        super(errorHandler, titleKey);
        this.abstractTypeWithNameAndActiveFieldsRepository = abstractTypeWithNameAndActiveFieldsRepository;
        this.newItemSupplier = newItemSupplier;
        this.titleKey = titleKey;
    }

    @Override
    void addGrids() {
        addGrid();
    }

    void addGrid() {
        removeGrid();
        grid = new AbstractTypeWithNameAndActiveFieldsGrid<T>(
                this.abstractTypeWithNameAndActiveFieldsRepository,
                this.newItemSupplier,
                this.titleKey,
                null, true);
        grid.initGrid(true, 0);
        addComponentsAndExpand(grid.getVerticalLayout(true, false));
    }

    @Override
    void removeGrids() {
        removeGrid();
    }

    void removeGrid() {
        if (null != grid) {
            removeComponent(grid.getVerticalLayout(false, false));
            grid = null;
        }
    }

    @Override
    void setTabIndexesAndFocus() {
        grid.focus();
    }
}
