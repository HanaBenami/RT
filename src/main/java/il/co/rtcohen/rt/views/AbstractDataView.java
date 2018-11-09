package il.co.rtcohen.rt.views;

import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;
import il.co.rtcohen.rt.UIcomponents;
import il.co.rtcohen.rt.repositories.GeneralRepository;

public abstract class AbstractDataView extends AbstractView implements View {

    String title;
    Component dataGrid;
    public GeneralRepository generalRepository;
    Button addButton;

    AbstractDataView(ErrorHandler errorHandler,GeneralRepository generalRepository) {
        super(errorHandler);
        this.generalRepository=generalRepository;
        addButton();
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

    private void addButton() {
        addButton = UIcomponents.addButton();
        addButton.setEnabled(false);
    }

    TextField newName() {
        TextField newName = new TextField();
        newName.focus();
        newName.addFocusListener(focusEvent ->
                addButton.setClickShortcut(ShortcutAction.KeyCode.ENTER));
        newName.addBlurListener(event -> {
            addButton.removeClickShortcut();
        });
        newName.addValueChangeListener(valueChangeEvent -> {
            if (newName.getValue().isEmpty())
                addButton.setEnabled(false);
            else
                addButton.setEnabled(true);
        });
        return newName;
    }
}
