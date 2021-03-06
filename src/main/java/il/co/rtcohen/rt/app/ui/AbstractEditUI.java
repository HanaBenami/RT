package il.co.rtcohen.rt.app.ui;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.*;
import il.co.rtcohen.rt.app.LanguageSettings;
import il.co.rtcohen.rt.app.UIComponents;
import il.co.rtcohen.rt.dal.repositories.CallRepository;
import il.co.rtcohen.rt.dal.repositories.GeneralRepository;
import il.co.rtcohen.rt.dal.repositories.SiteRepository;

import java.util.Optional;

abstract class AbstractEditUI extends AbstractUI<GridLayout> {

    SiteRepository siteRepository;
    TextField siteNotes;
    TextField phone;
    TextField contact;
    TextField address;
    ComboBox<Integer> customerCombo;
    int selectedId;

    AbstractEditUI(SiteRepository siteRepository,ErrorHandler errorHandler, CallRepository callRepository, GeneralRepository generalRepository) {
        super(errorHandler,callRepository,generalRepository);
        this.siteRepository=siteRepository;
    }

    abstract void setTabIndexes();
    abstract void deleteCurrentId();
    abstract void addFields();
    abstract void getSelectedId();
    abstract void createNew();
    abstract void reloadNew();

    private void setPrintButton() {
        Button print = UIComponents.printButton();
        print.addClickListener(clickEvent ->
                JavaScript.getCurrent().execute("print();"));
        layout.addComponent(print,0,0,0,0);
        //layout.setComponentAlignment(print, Alignment.TOP_LEFT);
    }

    void initLayout(String title) {
        layout = new GridLayout(4, 11);
        layout.addComponent(UIComponents.smallHeader(title),3,0);
        layout.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        setContent(layout);
    }

    void addLayoutComponents() {
        addFields();
        setPrintButton();
        setDeleteButton();
        setEscapeButton();
        setTabIndexes();
        layout.setSpacing(true);
        layout.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        mainLayout.addComponentsAndExpand(layout);
        setContent(mainLayout);
    }

    private void setDeleteButton() {
        Button delete = UIComponents.trashButton();
        delete.addClickListener(clickEvent -> deleteCurrentId());
        layout.addComponent(delete,0,1,0,1);
        //layout.setComponentAlignment(delete,Alignment.TOP_LEFT);
    }

    private void setEscapeButton() {
        Button escapeButton = UIComponents.closeButton();
        escapeButton.addClickListener(clickEvent -> closeWindow());
        escapeButton.setClickShortcut(ShortcutAction.KeyCode.ESCAPE);
        layout.addComponent(escapeButton,0,2,0,2);
        //layout.setComponentAlignment(escapeButton,Alignment.TOP_LEFT);
    }

    Boolean hasParameter() {
        if ((getPage().getUriFragment()==null)||(getPage().getUriFragment().isEmpty())
                ||getPage().getUriFragment().equals("0")) {
            createNew();
            return false;
        }
        return true;
    }

    Optional<Integer> selectedId() {
        if (getPage().getUriFragment().matches("\\d+"))
            return Optional.of((Integer.parseInt(getPage().getUriFragment())));
        else
            return Optional.empty();
    }

    void closeWindow() {
        JavaScript.getCurrent().execute(
                "setTimeout(function() {self.close();},500);");
    }

}
