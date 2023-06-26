package il.co.rtcohen.rt.app.uiComponents;

import com.vaadin.ui.Label;

import il.co.rtcohen.rt.app.LanguageSettings;

import static il.co.rtcohen.rt.app.uiComponents.StyleSettings.FORMS_FIELD_HEIGHT;

public class CustomLabel extends Label {
    public enum LabelStyle {
        MEDIUM_TITLE("LABEL-MEDIUM-HEADER"),
        SMALL_TITLE("LABEL-SMALL-HEADER"),
        MEDIUM_TEXT("LABEL-MEDIUM-TEXT"),
        SMALL_TEXT("LABEL-SMALL-TEXT"),
        VERY_SMALL_TEXT("VERY-SMALL-TEXT"),
        ERROR("LABEL-ERROR");

        public final String styleName;

        LabelStyle(String styleName) {
            this.styleName = styleName;
        }
    }

    public CustomLabel(String textKey, String width) {
        this(textKey, width, true, null);
    }

    public CustomLabel(String textKey, String width, boolean setStandardHeight, LabelStyle labelStyle) {
        super((null != textKey && LanguageSettings.containsLocaleString(textKey)) ? LanguageSettings.getLocaleString(textKey) : textKey);
        if (null != width) {
            this.setWidth(width);
        }
        if (setStandardHeight) {
            this.setHeight(FORMS_FIELD_HEIGHT);
        }
        if (null != labelStyle) {
            this.setStyleName(labelStyle.styleName);
        }
    }
}
