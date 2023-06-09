package il.co.rtcohen.rt.app.uiComponents;

import com.vaadin.ui.TextArea;
import il.co.rtcohen.rt.app.LanguageSettings;

public class CustomTextArea extends TextArea {
    public CustomTextArea(String captionKey, String width, String height) {
        super(LanguageSettings.getLocaleString(captionKey));
        this.setStyleName("v-textarea");
        this.setWidth(width);
        this.setHeight(height);
    }
}
