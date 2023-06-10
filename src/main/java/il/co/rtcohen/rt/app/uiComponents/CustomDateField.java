package il.co.rtcohen.rt.app.uiComponents;

import com.vaadin.ui.DateField;
import com.vaadin.ui.renderers.LocalDateRenderer;
import il.co.rtcohen.rt.utils.Date;

import static il.co.rtcohen.rt.app.uiComponents.StyleSettings.FORMS_FIELD_HEIGHT;

public class CustomDateField extends DateField {
    static final private String DATE_FORMAT = "dd/MM/yy";

    public CustomDateField() {
        this(null);
    }

    public CustomDateField(Date currentValue) {
        super();
        this.setDateFormat(DATE_FORMAT);
        this.setHeight(FORMS_FIELD_HEIGHT);
        if (null != currentValue && !Date.nullDate().equals(currentValue)) {
            this.setValue(currentValue.getLocalDate());
        }
    }

    static public LocalDateRenderer dateRenderer() {
        return new LocalDateRenderer(DATE_FORMAT);
    }

    static public String getDateStyle(Date date, boolean bold) {
        if (null == date || Date.nullDate().equals(date) || null == date.getLocalDate()) {
            return "null";
        } else if (bold) {
            return "bold";
        } else {
            return null;
        }
    }
}
