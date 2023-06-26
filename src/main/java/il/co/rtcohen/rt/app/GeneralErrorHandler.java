package il.co.rtcohen.rt.app;

import il.co.rtcohen.rt.utils.Logger;

import com.vaadin.server.*;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.AbstractComponent;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.SocketException;

@SpringComponent
public class GeneralErrorHandler implements ErrorHandler {

    @Override
    public void error(ErrorEvent event) {
        doDefault(event);
    }

    private static void doDefault(ErrorEvent event) {
        Throwable t = event.getThrowable();
        if (t instanceof SocketException) {
            Logger.getLogger(GeneralErrorHandler.class).info("SocketException in CommunicationManager. Most likely client (browser) closed socket.");
        } else {
            t = DefaultErrorHandler.findRelevantThrowable(t);
            AbstractComponent component = DefaultErrorHandler.findAbstractComponent(event);
            if (component != null) {
                ErrorMessage errorMessage = new UserError(LanguageSettings.getLocaleString("error"));
                component.setComponentError(errorMessage);
            }
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            t.printStackTrace(printWriter);
            Logger.getLogger(GeneralErrorHandler.class).error(stringWriter.toString());
        }
    }
}
