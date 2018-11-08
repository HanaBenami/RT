package il.co.rtcohen.rt;

import com.vaadin.event.ListenerMethod;
import com.vaadin.server.*;
import com.vaadin.shared.Connector;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import java.lang.reflect.InvocationTargetException;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

@SpringComponent
public class MyErrorHandler implements ErrorHandler {
    public MyErrorHandler() {
    }

    public void error(ErrorEvent event) {
        doDefault(event);
    }

    public static void doDefault(ErrorEvent event) {
        Throwable t = event.getThrowable();
        if (t instanceof SocketException) {
            getLogger().info("SocketException in CommunicationManager. Most likely client (browser) closed socket.");
        } else {
            t = findRelevantThrowable(t);
            AbstractComponent component = findAbstractComponent(event);
            if (component != null) {
                ErrorMessage errorMessage = new UserError("שגיאה");
                component.setComponentError(errorMessage);
            }

            getLogger().log(Level.SEVERE, "", t);
        }
    }

    public static Throwable findRelevantThrowable(Throwable t) {
        try {
            if (t instanceof ServerRpcManager.RpcInvocationException && t.getCause() instanceof InvocationTargetException) {
                return findRelevantThrowable(t.getCause().getCause());
            }

            if (t instanceof ListenerMethod.MethodException) {
                return t.getCause();
            }
        } catch (Exception var2) {
            ;
        }

        return t;
    }

    private static Logger getLogger() {
        return Logger.getLogger(com.vaadin.server.DefaultErrorHandler.class.getName());
    }

    public static AbstractComponent findAbstractComponent(ErrorEvent event) {
        if (event instanceof ClientConnector.ConnectorErrorEvent) {
            Component c = findComponent(((ClientConnector.ConnectorErrorEvent)event).getConnector());
            if (c instanceof AbstractComponent) {
                return (AbstractComponent)c;
            }
        }

        return null;
    }

    public static Component findComponent(Connector connector) {
        if (connector instanceof Component) {
            return (Component)connector;
        } else {
            return connector.getParent() != null ? findComponent(connector.getParent()) : null;
        }
    }
}
