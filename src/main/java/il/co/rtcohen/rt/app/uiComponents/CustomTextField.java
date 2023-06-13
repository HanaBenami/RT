package il.co.rtcohen.rt.app.uiComponents;

import com.vaadin.ui.TextField;

import il.co.rtcohen.rt.app.LanguageSettings;

import static il.co.rtcohen.rt.app.uiComponents.StyleSettings.FORMS_FIELD_HEIGHT;

public class CustomTextField extends TextField {
    public CustomTextField(
            String captionKey,
            String currentValue,
            ValueChangeListener<String> valueChangeListener
    ) {
        super();
        if (null != captionKey) {
            this.setCaption(LanguageSettings.getLocaleString(captionKey));
        }
        if (null != currentValue) {
            this.setValue(currentValue);
        }
        if (null != valueChangeListener) {
            this.addValueChangeListener(valueChangeListener);
        }
        this.setHeight(FORMS_FIELD_HEIGHT);
    }
}
