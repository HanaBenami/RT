package il.co.rtcohen.rt.app.uiComponents;

import com.vaadin.ui.CheckBox;
import il.co.rtcohen.rt.app.LanguageSettings;

public class CustomCheckBox extends CheckBox {
    public CustomCheckBox(String captionKey, boolean value, boolean isReadOnly) {
        super(null == captionKey ? null : LanguageSettings.getLocaleString(captionKey));
        this.setReadOnly(isReadOnly);
        this.setValue(value);
    }
}
