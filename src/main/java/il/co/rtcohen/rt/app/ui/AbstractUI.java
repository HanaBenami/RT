package il.co.rtcohen.rt.app.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;
import il.co.rtcohen.rt.dal.repositories.CallRepository;
import il.co.rtcohen.rt.dal.repositories.GeneralRepository;

@Theme("myTheme")
public abstract class AbstractUI<T extends Layout> extends UI {

    T layout;
    CallRepository callRepository;
    GeneralRepository generalRepository;

    AbstractUI(ErrorHandler errorHandler, CallRepository callRepository, GeneralRepository generalRepository) {
        setErrorHandler(errorHandler);
        this.callRepository=callRepository;
        this.generalRepository=generalRepository;
    }

    protected abstract void setupLayout();

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        setupLayout();
    }

}
