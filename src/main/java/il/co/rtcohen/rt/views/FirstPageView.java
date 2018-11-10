package il.co.rtcohen.rt.views;

import com.vaadin.server.ErrorHandler;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Image;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@SpringView(name = FirstPageView.VIEW_NAME)
public class FirstPageView extends AbstractView {

    static final String VIEW_NAME = "";

    @Autowired
    private FirstPageView(ErrorHandler errorHandler) {
        super(errorHandler);
    }

    @PostConstruct
    protected void enter() {
        setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        setSizeFull();
        Image logo = new Image(null,new ThemeResource("rtlogo.png"));
        logo.setHeight("300");
        addComponents(logo);
    }

}