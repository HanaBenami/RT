package il.co.rtcohen.rt.app.views;

import il.co.rtcohen.rt.dal.dao.interfaces.AbstractType;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;

abstract class AbstractDataView<T extends AbstractType> extends AbstractView implements View {
        AbstractDataView(ErrorHandler errorHandler, String titleKey) {
        super(errorHandler, titleKey);
    }

    abstract void addGrids();

    abstract void removeGrids();

    abstract void setTabIndexesAndFocus();

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        super.enter(event);
        addGrids();
        setTabIndexesAndFocus();
    }

    @Override
    protected void addTitleLayout(Component... additionalComponents) {
        super.addTitleLayout(getRefreshButton());
    }

    protected Button getRefreshButton() {
        return getButton(VaadinIcons.REFRESH, clickEvent -> refreshData());
    }

    protected void refreshData() {
        removeGrids();
        addGrids();
        setTabIndexesAndFocus();
    }
}
