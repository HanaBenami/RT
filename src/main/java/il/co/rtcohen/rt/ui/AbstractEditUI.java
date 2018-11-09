package il.co.rtcohen.rt.ui;

import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.*;
import il.co.rtcohen.rt.UIcomponents;
import il.co.rtcohen.rt.repositories.CallRepository;
import il.co.rtcohen.rt.repositories.GeneralRepository;
import il.co.rtcohen.rt.repositories.SiteRepository;

import java.util.Optional;

public abstract class AbstractEditUI extends AbstractUI<GridLayout> {
    protected com.vaadin.ui.GridLayout layout;
    SiteRepository siteRepository;
    TextField siteNotes;
    TextField phone;
    TextField contact;
    TextField address;
    ComboBox<Integer> customerCombo;

    AbstractEditUI(SiteRepository siteRepository,ErrorHandler errorHandler, CallRepository callRepository, GeneralRepository generalRepository) {
        super(errorHandler,callRepository,generalRepository);
        this.siteRepository=siteRepository;
    }

    void setPrintButton() {
        Button print = UIcomponents.printButton();
        print.addClickListener(clickEvent ->
                JavaScript.getCurrent().execute("print();"));
        layout.addComponent(print,0,0,0,0);
        layout.setComponentAlignment(print, Alignment.TOP_LEFT);
    }

    void setDeleteButton() {
        Button delete = UIcomponents.trashButton();
        delete.addClickListener(clickEvent -> deleteId());
        layout.addComponent(delete,0,1,0,1);
        layout.setComponentAlignment(delete,Alignment.TOP_LEFT);
    }

    void startLayout(String title) {
        layout = new com.vaadin.ui.GridLayout(4, 11);
        layout.addComponent(UIcomponents.smallHeader(title),3,0);
    }

    void continueLayout() {
        addData();
        setPrintButton();
        setDeleteButton();
        tabIndexes();
        layout.setSpacing(true);
        layout.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
    }

    abstract void tabIndexes();
    abstract void deleteId();
    abstract void addData();
    abstract void getSelected();

    Optional<Integer> selectedId() {
        if (getPage().getUriFragment().matches("\\d+"))
            return Optional.of((Integer.parseInt(getPage().getUriFragment())));
        else
            return Optional.empty();
    }

    boolean noParameter() {
        return
                ((getPage().getUriFragment()==null)||(getPage().getUriFragment().isEmpty())
                        ||getPage().getUriFragment().equals("0"));
    }

    void closeWindow() {
        JavaScript.getCurrent().execute(
                "setTimeout(function() {self.close();},500);");
    }

    VerticalLayout mainLayout() {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        mainLayout.addComponentsAndExpand(layout);
        return mainLayout;
    }

}
