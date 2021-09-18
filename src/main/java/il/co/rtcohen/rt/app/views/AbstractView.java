package il.co.rtcohen.rt.app.views;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.View;
import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.VerticalLayout;

@Theme("myTheme")
class AbstractView extends VerticalLayout implements View {

    AbstractView(ErrorHandler errorHandler) {
        setErrorHandler(errorHandler);
    }

    int getSessionUsernameId() {
        return (int) getSession().getAttribute("userid");
    }
}
