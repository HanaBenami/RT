package il.co.rtcohen.rt.app.uiComponents;

import com.vaadin.ui.DateField;
import com.vaadin.ui.renderers.LocalDateRenderer;
import il.co.rtcohen.rt.utils.Date;

public class CustomDateField extends DateField {
    static final private String DATE_FORMAT = "dd/MM/yy";

    public CustomDateField() {
        super();
        this.setDateFormat(DATE_FORMAT);
    }

    static public LocalDateRenderer dateRenderer() {
        return new LocalDateRenderer(DATE_FORMAT);
    }

    static public String getDateStyle(Date date, boolean bold) {
        if (Date.nullDate().equals(date) || null == date.getLocalDate()) {
            return "null";
        } else if (bold) {
            return "bold";
        } else {
            return null;
        }
    }
}
