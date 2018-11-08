package il.co.rtcohen.rt.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import il.co.rtcohen.rt.UIcomponents;
import il.co.rtcohen.rt.repositories.GeneralRepository;

public abstract class AbstractDataView extends AbstractView implements View {

    String title;
    Component dataGrid;
    protected GeneralRepository generalRepository;

    AbstractDataView(ErrorHandler errorHandler,GeneralRepository generalRepository) {
        super(errorHandler);
        this.generalRepository=generalRepository;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        setDefaultComponentAlignment(Alignment.TOP_CENTER);
        setHeight(getUI().getHeight(),getUI().getHeightUnits());
        createView(event);
    }

    void addHeader() {
        addComponent(UIcomponents.header(title));
    }

    abstract void createView(ViewChangeListener.ViewChangeEvent event);

}
