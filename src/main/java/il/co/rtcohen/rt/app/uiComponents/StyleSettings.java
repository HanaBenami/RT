package il.co.rtcohen.rt.app.uiComponents;

import il.co.rtcohen.rt.dal.dao.Call;

import java.time.LocalDate;

public class StyleSettings {
    private StyleSettings() {}

    public static final String FILTER_FIELD_HEIGHT = "30px";
    public static final String FILTER_FIELD_WIDTH = "95%";

    public static String callStyle(Call call) {
        if (call.getCurrentScheduledDate().getLocalDate().equals(LocalDate.now())) {
            return "green";
        }
        if (call.getCurrentScheduledDate().getLocalDate().equals(LocalDate.now().plusDays(1))) {
            return "yellow";
        }
        if ((call.getCurrentScheduledDate()==null)&&(call.getStartDate().getLocalDate().isBefore(LocalDate.now().minusDays(6)))) {
            return "darkred";
        }
        if ((call.getCurrentScheduledDate()==null)&&(call.getStartDate().getLocalDate().isBefore(LocalDate.now().minusDays(2)))) {
            return "red";
        }
        return null;
    }
}
