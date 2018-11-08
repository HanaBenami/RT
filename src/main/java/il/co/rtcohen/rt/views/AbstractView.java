package il.co.rtcohen.rt.views;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.View;
import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.VerticalLayout;

@Theme("myTheme")
public class AbstractView extends VerticalLayout implements View {

    AbstractView(ErrorHandler errorHandler) {
        setErrorHandler(errorHandler);
    }

}
