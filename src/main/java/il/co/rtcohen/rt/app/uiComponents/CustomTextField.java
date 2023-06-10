package il.co.rtcohen.rt.app.uiComponents;

import com.vaadin.ui.TextField;

import static il.co.rtcohen.rt.app.uiComponents.StyleSettings.FORMS_FIELD_HEIGHT;

public class CustomTextField extends TextField {
    public CustomTextField(
            String caption,
            String currentValue,
            ValueChangeListener<String> valueChangeListener
    ) {
        super(caption, currentValue, valueChangeListener);
        this.setHeight(FORMS_FIELD_HEIGHT);
    }
}
