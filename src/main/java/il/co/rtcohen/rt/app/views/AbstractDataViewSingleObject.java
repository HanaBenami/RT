package il.co.rtcohen.rt.app.views;

import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import il.co.rtcohen.rt.app.LanguageSettings;
import il.co.rtcohen.rt.app.UiComponents.UIComponents;
import il.co.rtcohen.rt.app.grids.AbstractFilterGrid;
import il.co.rtcohen.rt.app.grids.CustomGrid;
import il.co.rtcohen.rt.dal.dao.AbstractType;
import il.co.rtcohen.rt.dal.repositories.GeneralRepository;
import org.vaadin.addons.filteringgrid.FilterGrid;

@Deprecated
public abstract class AbstractDataViewSingleObject<T extends AbstractType> extends AbstractView implements View {
    String title;
    AbstractFilterGrid<T> grid;
    GeneralRepository generalRepository;
    Button addButton;

    AbstractDataViewSingleObject(ErrorHandler errorHandler, GeneralRepository generalRepository) {
        super(errorHandler);
        this.generalRepository=generalRepository;
//      TODO:  UI.getCurrent().setDirection(Direction.RIGHT_TO_LEFT);
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
        getUI().setLocale(LanguageSettings.locale);
    }

    static <C> void initGrid(String style, FilterGrid<C> filterGrid) {
        filterGrid.getEditor().setSaveCaption(LanguageSettings.getLocaleString("save"));
        filterGrid.getEditor().setCancelCaption(LanguageSettings.getLocaleString("cancel"));
        if (!style.equals("")) {
            filterGrid.setStyleGenerator(item -> style);
        }
    }

    void initGrid(String style) {
        grid = new CustomGrid<>();
        initGrid(style, grid);
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
            addButton.setEnabled(!newName.getValue().isEmpty());
        });
        return newName;
    }
}
