package il.co.rtcohen.rt.app.uiComponents;

import org.vaadin.ui.NumberField;

import static il.co.rtcohen.rt.app.uiComponents.StyleSettings.FORMS_FIELD_HEIGHT;

public class CustomNumericField extends NumberField {
    public CustomNumericField(
            String caption,
            Integer currentValue,
            Integer minValue,
            Integer maxValue,
            ValueChangeListener<String> valueChangeListener
    ) {
        super(caption);
        if (null != currentValue) {
            this.setValue(currentValue.toString());
        }
        if (null != minValue) {
            this.setMinValue(minValue);
        }
        if (null != maxValue) {
            this.setMaxValue(maxValue);
        }
        if (null != valueChangeListener) {
            this.addValueChangeListener(valueChangeListener);
        }
        this.setHeight(FORMS_FIELD_HEIGHT);
    }
}
