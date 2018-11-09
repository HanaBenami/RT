package il.co.rtcohen.rt.ui;

import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.*;
import il.co.rtcohen.rt.UIcomponents;
import il.co.rtcohen.rt.repositories.CallRepository;
import il.co.rtcohen.rt.repositories.GeneralRepository;
import il.co.rtcohen.rt.repositories.SiteRepository;

public abstract class AbstractEditUI<GridLayout> extends AbstractUI {
    protected com.vaadin.ui.GridLayout layout;
    SiteRepository siteRepository;
    protected TextField siteNotes;
    protected TextField phone;
    protected TextField contact;
    protected TextField address;

    AbstractEditUI(SiteRepository siteRepository,ErrorHandler errorHandler, CallRepository callRepository, GeneralRepository generalRepository) {
        super(errorHandler,callRepository,generalRepository);
        this.siteRepository=siteRepository;
    }

    void printButton() {
        Button print = UIcomponents.printButton();
        print.addClickListener(clickEvent ->
                JavaScript.getCurrent().execute("print();"));
        layout.addComponent(print,0,0,0,0);
        layout.setComponentAlignment(print, Alignment.TOP_LEFT);
    }
}
