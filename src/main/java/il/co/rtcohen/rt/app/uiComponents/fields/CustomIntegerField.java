package il.co.rtcohen.rt.app.uiComponents.fields;

import il.co.rtcohen.rt.app.grids.AbstractTypeFilterGrid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.vaadin.ui.TextField;
import com.vaadin.shared.Registration;

import static il.co.rtcohen.rt.app.uiComponents.StyleSettings.FORMS_FIELD_HEIGHT;

public class CustomIntegerField extends TextField {
    private static final Logger logger = LoggerFactory.getLogger(AbstractTypeFilterGrid.class);

    private final Integer minValue;
    private final Integer maxValue;
    private final boolean allowEmptyValue;
    private Integer lastValue = 0;

    public CustomIntegerField(
            String caption,
            Integer currentValue,
            Integer minValue,
            Integer maxValue,
            boolean allowEmptyValue,
            ValueChangeListener<String> valueChangeListener,
            String width) {
        super(caption);
        if (null != currentValue) {
            this.setValue(currentValue.toString());
        }
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.allowEmptyValue = allowEmptyValue;

        this.addValueChangeListener(valueChangeListener);

        this.setHeight(FORMS_FIELD_HEIGHT);
        if (null != width) {
            this.setWidth(width);
        }
    }

    @Override
    public Registration addValueChangeListener(ValueChangeListener<String> valueChangeListener) {
        return super.addValueChangeListener(valueChangeEvent -> {
            try {
                String stringValue = this.getValue();
                if (this.allowEmptyValue && (null == stringValue || stringValue.isEmpty())) {
                    // do nothing
                } else {
                    int integerValue = Integer.parseInt(stringValue);
                    if ((null != this.minValue && integerValue < this.minValue)
                            || (null != this.maxValue && this.maxValue < integerValue)) {
                        throw new IllegalArgumentException();
                    }
                    lastValue = integerValue;
                }
                if (null != valueChangeListener) {
                    valueChangeListener.valueChange(valueChangeEvent);
                }
            } catch (Exception ignored) {
                this.setValue(lastValue.toString());
            }
        });
    }
}
