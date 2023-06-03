package il.co.rtcohen.rt.app.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import il.co.rtcohen.rt.app.LanguageSettings;
import il.co.rtcohen.rt.app.UIComponents;
import il.co.rtcohen.rt.dal.dao.AbstractType;

public abstract class AbstractDataView<T extends AbstractType> extends AbstractView implements View {
    String title;

    @Deprecated
    AbstractDataView(ErrorHandler errorHandler) {
        super(errorHandler);
    }

    AbstractDataView(ErrorHandler errorHandler, String titleKey) {
        super(errorHandler);
        this.setTitle(titleKey);
    }

    protected void setTitle(String titleKey) {
        if (null != titleKey) {
            this.title = LanguageSettings.getLocaleString(titleKey);
        } else {
            this.title = "";
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        setDefaultComponentAlignment(Alignment.TOP_CENTER);
        setHeight(getUI().getHeight(), getUI().getHeightUnits());
        addTitle();
        addGrids();
        setTabIndexes();
        getUI().setLocale(LanguageSettings.locale);
    }

    private void addTitle() {
        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setDefaultComponentAlignment(Alignment.TOP_CENTER);
        titleLayout.addComponent(getRefreshButton());
        titleLayout.addComponents(UIComponents.header(this.title));
        titleLayout.setWidth("80%");
        this.addComponent(titleLayout);
    }

    private Button getRefreshButton() {
        Button refreshButton = UIComponents.refreshButton();
        refreshButton.addClickListener(clickEvent -> refreshData());
        return refreshButton;
    }

    abstract void addGrids();

    abstract void removeGrids();

    abstract void setTabIndexes();

    protected void refreshData() {
        removeGrids();
        addGrids();
        setTabIndexes();
    }
}
