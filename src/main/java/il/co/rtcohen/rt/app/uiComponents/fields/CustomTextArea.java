package il.co.rtcohen.rt.app.uiComponents.fields;

import com.vaadin.ui.TextArea;
import il.co.rtcohen.rt.app.LanguageSettings;

public class CustomTextArea extends TextArea {
    public CustomTextArea(String captionKey, String currentValue, String width, String height) {
        super(null == captionKey ? null : LanguageSettings.getLocaleString(captionKey));
        this.setStyleName("v-textarea");
        if (null != currentValue) {
            this.setValue(currentValue);
        }
        if (null != width) {
            this.setWidth(width);
        }
        if (null != height) {
            this.setHeight(height);
        }
    }
}
