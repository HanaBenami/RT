package il.co.rtcohen.rt.app.uiComponents;

import com.vaadin.ui.CheckBox;

public class CustomCheckBox extends CheckBox {
    public CustomCheckBox(String caption, boolean value, boolean isReadOnly) {
        super(caption);
        this.setReadOnly(isReadOnly);
        this.setValue(value);
    }
}
