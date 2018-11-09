package il.co.rtcohen.rt.ui;

import com.vaadin.server.ErrorHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;
import il.co.rtcohen.rt.repositories.CallRepository;
import il.co.rtcohen.rt.repositories.GeneralRepository;

public abstract class AbstractUI<T extends Layout> extends UI {

    CallRepository callRepository;
    protected GeneralRepository generalRepository;

    AbstractUI(ErrorHandler errorHandler, CallRepository callRepository, GeneralRepository generalRepository) {
        setErrorHandler(errorHandler);
        this.callRepository=callRepository;
        this.generalRepository=generalRepository;
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        setupLayout();
    }

    protected abstract void setupLayout();

}
