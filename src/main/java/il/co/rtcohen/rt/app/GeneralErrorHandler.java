package il.co.rtcohen.rt.app;

import com.vaadin.server.*;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.AbstractComponent;

import java.net.SocketException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

@SpringComponent
public class GeneralErrorHandler implements ErrorHandler {

    public void error(ErrorEvent event) {
        doDefault(event);
    }

    private static void doDefault(ErrorEvent event) {
        Throwable t = event.getThrowable();
        if (t instanceof SocketException) {
            getLogger().info("SocketException in CommunicationManager. Most likely client (browser) closed socket.");
        } else {
            t = DefaultErrorHandler.findRelevantThrowable(t);
            AbstractComponent component = DefaultErrorHandler.findAbstractComponent(event);
            if (component != null) {
                ErrorMessage errorMessage = new UserError(LanguageSettings.getLocaleString("error"));
                component.setComponentError(errorMessage);
            }
            getLogger().log(Level.SEVERE, "", t);
//            getLogger().log(Level.SEVERE, "", Arrays.copyOfRange(t.getStackTrace(), 0, 5)); // TODO: Make it works
        }
    }

    private static Logger getLogger() {
        return Logger.getLogger(GeneralErrorHandler.class.getName());
    }
}
