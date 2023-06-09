package il.co.rtcohen.rt.app.views;

import com.vaadin.server.ErrorHandler;
import com.vaadin.spring.annotation.SpringView;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.function.Supplier;

import il.co.rtcohen.rt.app.grids.AbstractTypeWithNameAndActiveFieldsGrid;
import il.co.rtcohen.rt.dal.dao.interfaces.AbstractTypeWithNameAndActiveFields;
import il.co.rtcohen.rt.dal.repositories.interfaces.AbstractTypeWithNameAndActiveFieldsRepository;

@SpringView(name = AbstractTypeWithNameAndActiveFieldsView.VIEW_NAME)
abstract class AbstractTypeWithNameAndActiveFieldsView<T extends AbstractTypeWithNameAndActiveFields>
        extends AbstractDataView<AbstractTypeWithNameAndActiveFields> {

    static final String VIEW_NAME = "update";

    private final AbstractTypeWithNameAndActiveFieldsRepository<T> abstractTypeWithNameAndActiveFieldsRepository;
    private final Supplier<T> newItemSupplier;
    private final String titleKey;
    private AbstractTypeWithNameAndActiveFieldsGrid<T> abstractTypeWithNameAndActiveFieldsGrid;

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
        abstractTypeWithNameAndActiveFieldsGrid = new AbstractTypeWithNameAndActiveFieldsGrid<T>(
                this.abstractTypeWithNameAndActiveFieldsRepository,
                this.newItemSupplier,
                this.titleKey
        );
        addComponentsAndExpand(abstractTypeWithNameAndActiveFieldsGrid.getVerticalLayout(true, false));
    }

    @Override
    void removeGrids() {
        removeGrid();
    }

    void removeGrid() {
        if (null != abstractTypeWithNameAndActiveFieldsGrid) {
            removeComponent(abstractTypeWithNameAndActiveFieldsGrid.getVerticalLayout(false, false));
            abstractTypeWithNameAndActiveFieldsGrid = null;
        }
    }

    // TODO
    @Override
    void setTabIndexes() {
        abstractTypeWithNameAndActiveFieldsGrid.setTabIndex(1);
    }
}
