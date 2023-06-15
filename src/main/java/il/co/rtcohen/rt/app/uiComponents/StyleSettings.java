package il.co.rtcohen.rt.app.uiComponents;

import il.co.rtcohen.rt.dal.dao.Call;
import il.co.rtcohen.rt.utils.Date;

import java.time.LocalDate;

public class StyleSettings {
    private StyleSettings() {}

    public static final String FORMS_FIELD_HEIGHT = "30px";
    public static final String FILTER_FIELD_HEIGHT = FORMS_FIELD_HEIGHT;
    public static final String COMBO_BOX_HEIGHT = FORMS_FIELD_HEIGHT;
    public static final String FILTER_FIELD_WIDTH = "95%";

    static public String getBoldStyle(String value) {
        return (null == value ? "null" : "bold");
    }

    static public String getBoldStyle(Integer value) {
        return (null == value || 0 == value ? "null" : "bold");
    }

    public static String callStyle(Call call) {
        LocalDate scheduledDate = (
                (null == call.getCurrentScheduledDate() || Date.nullDate().equals(call.getCurrentScheduledDate()))
                ? null
                : call.getCurrentScheduledDate().getLocalDate()
        );
        if (LocalDate.now().equals(scheduledDate)) {
            return "green";
        }
        if (LocalDate.now().plusDays(1).equals(scheduledDate)) {
            return "yellow";
        }
        if (null == scheduledDate && call.getStartDate().getLocalDate().isBefore(LocalDate.now().minusDays(6))) {
            return "darkred";
        }
        if (null == scheduledDate && call.getStartDate().getLocalDate().isBefore(LocalDate.now().minusDays(2))) {
            return "red";
        }
        return null;
    }
}
