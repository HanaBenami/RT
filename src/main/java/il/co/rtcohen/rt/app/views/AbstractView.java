package il.co.rtcohen.rt.app.views;

import com.vaadin.annotations.Theme;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.*;
import il.co.rtcohen.rt.app.LanguageSettings;
import il.co.rtcohen.rt.app.uiComponents.CustomLabel;
import il.co.rtcohen.rt.app.uiComponents.fields.CustomButton;

@Theme("myTheme")
abstract public class AbstractView extends VerticalLayout implements View {
    String title;

    AbstractView(ErrorHandler errorHandler, String titleKey) {
        setErrorHandler(errorHandler);
        setTitle(titleKey);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        setDefaultComponentAlignment(Alignment.TOP_CENTER);
        addTitleLayout();
        if (null != getUI()) {
            setHeight(getUI().getHeight(), getUI().getHeightUnits());
            getUI().setLocale(LanguageSettings.locale);
        }
    }

    protected void setTitle(String titleKey) {
        if (null != titleKey) {
            this.title = LanguageSettings.getLocaleString(titleKey);
        } else {
            this.title = "";
        }
    }

    protected void addTitleLayout(Component... additionalComponents) {
        if (title.isEmpty() && 0 == additionalComponents.length) {
            return;
        }

        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setDefaultComponentAlignment(Alignment.TOP_CENTER);
        for (Component component : additionalComponents) {
            titleLayout.addComponent(component);
        }
        titleLayout.addComponent(new CustomLabel(this.title, null, true, CustomLabel.LabelStyle.SMALL_TITLE));
        titleLayout.setWidth("70%");
        this.addComponent(titleLayout);
    }

    protected CustomButton getButton(VaadinIcons vaadinIcons, Button.ClickListener clickListener) {
        return new CustomButton(vaadinIcons, true, clickListener);
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
