package il.co.rtcohen.rt.app.views;

import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import il.co.rtcohen.rt.app.UIComponents;
import il.co.rtcohen.rt.dal.repositories.GeneralRepository;
import org.vaadin.addons.filteringgrid.FilterGrid;

public abstract class AbstractDataView<T> extends AbstractView implements View {

    String title;
    FilterGrid<T> grid;
    GeneralRepository generalRepository;
    Button addButton;

    AbstractDataView(ErrorHandler errorHandler,GeneralRepository generalRepository) {
        super(errorHandler);
        this.generalRepository=generalRepository;
        this.generalRepository=generalRepository;
        setAddButton();
    }

    abstract void createView(ViewChangeListener.ViewChangeEvent event);

    abstract void addColumns();

    abstract void addGrid();

    abstract void setTabIndexes();

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        setDefaultComponentAlignment(Alignment.TOP_CENTER);
        setHeight(getUI().getHeight(),getUI().getHeightUnits());
        createView(event);
    }

    void initGrid(String style) {
        grid = new FilterGrid<>();
        grid.getEditor().setSaveCaption("שמור");
        grid.getEditor().setCancelCaption("בטל");
        if (!style.equals(""))
            grid.setStyleGenerator(item -> style);
    }

    void addHeader() {
        addComponent(UIComponents.header(title));
    }

    private void setAddButton() {
        addButton = UIComponents.addButton();
        addButton.setEnabled(false);
    }

    TextField addNewNameField() {
        TextField newName = new TextField();
        newName.focus();
        newName.addFocusListener(focusEvent ->
                addButton.setClickShortcut(ShortcutAction.KeyCode.ENTER));
        newName.addBlurListener(event -> addButton.removeClickShortcut());
        newName.addValueChangeListener(valueChangeEvent -> {
            if (newName.getValue().isEmpty())
                addButton.setEnabled(false);
            else
                addButton.setEnabled(true);
        });
        return newName;
    }


}
