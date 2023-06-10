package il.co.rtcohen.rt.app;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Image;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageSettings {

    public static Locale locale;
    private static ResourceBundle language;

    static {
        locale = new Locale("iw", "ISR");
        language = ResourceBundle.getBundle("language", locale);
    }

    static void changeLocale() {
        if (locale==Locale.ENGLISH)
            locale = new Locale("iw", "ISR");
        else
            locale = Locale.ENGLISH;
        language = ResourceBundle.getBundle("language", locale);
    }

    static Image getFlag() {
        Image flag;
        if (locale!=Locale.ENGLISH)
            flag = new Image(null, new ThemeResource("EN.png"));
        else
            flag = new Image(null, new ThemeResource("HE.png"));
        return flag;
    }

    public static boolean isHebrew() {
        return locale != Locale.ENGLISH;
    }

    public static boolean containsLocaleString(String key) {
        return language.containsKey(key);
    }

    public static String getLocaleString(String key) {
        return new String(language.getString(key).getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }

}
