package il.co.rtcohen.rt.app.uiComponents.fields;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;
import il.co.rtcohen.rt.app.LanguageSettings;

public class CustomButton extends Button {
    public CustomButton(VaadinIcons vaadinIcons, boolean withBorder, ClickListener clickListener) {
        super();
        this.setIcon(vaadinIcons);
        this.setStyleName(withBorder ? ValoTheme.BUTTON_PRIMARY : "noBorderButton");
        if (null != clickListener) {
            this.addClickListener(clickListener);
        }
    }

    public CustomButton(VaadinIcons vaadinIcons, boolean withBorder, String sourceUrl, int windowHeight, int windowWidth) {
        super();
        this.setIcon(vaadinIcons);
        if (!withBorder) {
            this.setStyleName("noBorderButton");
        }
        final BrowserWindowOpener opener = new BrowserWindowOpener(new ExternalResource(sourceUrl));
        opener.setFeatures("height=" + windowHeight + ",width=" + windowWidth + ",resizable");
        opener.extend(this);
    }

    public static Button countingIcon(VaadinIcons zeroIcon, VaadinIcons oneIcon, VaadinIcons multipleIcon, int n) {
        CustomButton button = new CustomButton(zeroIcon, false, null);
        if (1 == n) {
            button.setCaption(String.valueOf((n)));
            button.setIcon(oneIcon);
        } else if (1 < n) {
            button.setCaption(String.valueOf((n)));
            button.setIcon(multipleIcon);
        }
        return button;
    }

    @Override
    public void setCaption(String caption) {
        super.setCaption(LanguageSettings.containsLocaleString(caption) ? LanguageSettings.getLocaleString(caption) : caption);
    }
}
