package il.co.rtcohen.rt.app.uiComponents;

import com.vaadin.ui.Label;
import il.co.rtcohen.rt.app.LanguageSettings;

import static il.co.rtcohen.rt.app.uiComponents.StyleSettings.FORMS_FIELD_HEIGHT;

public class CustomLabel extends Label {
    public enum LabelStyle {
        TITLE("LABEL-SMALL-HEADER"),
        ERROR("LABEL-RIGHT-RED");

        public final String styleName;

        LabelStyle(String styleName) {
            this.styleName = styleName;
        }
    }

    public CustomLabel(String textKey, String width) {
        this(textKey, width, null);
    }

    public CustomLabel(String textKey, String width, LabelStyle labelStyle) {
        super(LanguageSettings.containsLocaleString(textKey) ? LanguageSettings.getLocaleString(textKey) : textKey);
        this.setHeight(FORMS_FIELD_HEIGHT);
        if (null != width) {
            this.setWidth(width);
        }
        if (null != labelStyle) {
            this.setStyleName(labelStyle.styleName);
        }
    }
}
