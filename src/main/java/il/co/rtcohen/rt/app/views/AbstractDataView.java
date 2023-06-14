package il.co.rtcohen.rt.app.views;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import il.co.rtcohen.rt.app.LanguageSettings;
import il.co.rtcohen.rt.app.uiComponents.fields.CustomButton;
import il.co.rtcohen.rt.app.uiComponents.UIComponents;
import il.co.rtcohen.rt.dal.dao.interfaces.AbstractType;

abstract class AbstractDataView<T extends AbstractType> extends AbstractView implements View {
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

    protected void addTitle() {
        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setDefaultComponentAlignment(Alignment.TOP_CENTER);
        titleLayout.addComponent(getRefreshButton());
        titleLayout.addComponent(UIComponents.header(this.title));
        titleLayout.setWidth("70%");
        this.addComponent(titleLayout);
    }

    protected Button getRefreshButton() {
        return getButton(VaadinIcons.REFRESH, clickEvent -> refreshData());
    }

    protected CustomButton getButton(VaadinIcons vaadinIcons, Button.ClickListener clickListener) {
        return new CustomButton(vaadinIcons, true, clickListener);
    }

    abstract void addGrids();

    abstract void removeGrids();

    abstract void setTabIndexes();

    protected void refreshData() {
        removeGrids();
        addGrids();
        setTabIndexes();
    }

    protected void setScrollable(boolean scrollable) {
        String style = "scrollable";
        if (scrollable) {
            this.addStyleName(style);
        } else {
            this.removeStyleName(style);
        }
    }
}
