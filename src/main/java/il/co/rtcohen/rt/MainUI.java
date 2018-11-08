//TODO:
//H2 hebrew issue (JAR)
//prepared statement
//TEST
//maven profile without h2
//debug mode warning         V

package il.co.rtcohen.rt;

import com.vaadin.annotations.Theme;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.BorderStyle;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.ui.*;

import java.time.LocalDate;

@SpringUI()
@SpringViewDisplay
@Theme("myTheme")
public class MainUI extends UI implements ViewDisplay {

    private Panel springViewDisplay;

    @Override
    protected void init(VaadinRequest request) {
        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        setContent(mainLayout);

        final HorizontalLayout topLayout = new HorizontalLayout();

        Image logo = new Image(null, new ThemeResource("rtlogo.png"));
        logo.setHeight("40");
        topLayout.addComponent(logo);

        Button print = UIcomponents.printButton();
        print.addClickListener(clickEvent ->
                JavaScript.getCurrent().execute("print();"));
        topLayout.addComponent(print);

        Component navigationBar = createNavigationBar();
        navigationBar.addStyleName("menu");
        topLayout.addComponentsAndExpand(navigationBar);

        mainLayout.addComponent(topLayout);
        springViewDisplay = new Panel();
        springViewDisplay.setSizeFull();
        mainLayout.addComponent(springViewDisplay);
        mainLayout.setExpandRatio(springViewDisplay, 1.0f);

        setErrorHandler(new MyErrorHandler());

    }

    @Override
    public void showView(View view) {
        if(springViewDisplay!=null)
            springViewDisplay.setContent((Component) view);
    }

    private Component createNavigationBar() {
        MenuBar menu = new MenuBar();


        MenuBar.MenuItem welcome = menu.addItem("ראשי");
        welcome.setIcon(VaadinIcons.HOME_O);
        welcome.setCommand((MenuBar.Command) selectedItem ->
                getUI().getNavigator().navigateTo(""));

        MenuBar.MenuItem setup = menu.addItem("תחזוקה");
        setup.setIcon(VaadinIcons.COG_O);

        MenuBar.MenuItem driver = setup.addItem("נהגים");
        driver.setCommand((MenuBar.Command) selectedItem ->
                getUI().getNavigator().navigateTo("update/table=driver"));
        setup.addSeparator();

        MenuBar.MenuItem area = setup.addItem("אזורים בארץ");
        area.setCommand((MenuBar.Command) selectedItem ->
                getUI().getNavigator().navigateTo("area"));
        setup.addSeparator();

        MenuBar.MenuItem custtype = setup.addItem("סוגי לקוחות");
        custtype.setCommand((MenuBar.Command) selectedItem ->
                getUI().getNavigator().navigateTo("update/table=custtype"));

        MenuBar.MenuItem cartype = setup.addItem("סוגי כלים");
        cartype.setCommand((MenuBar.Command) selectedItem ->
                getUI().getNavigator().navigateTo("update/table=cartype"));

        MenuBar.MenuItem calltype = setup.addItem("סוגי קריאות");
        calltype.setCommand((MenuBar.Command) selectedItem ->
                getUI().getNavigator().navigateTo("update/table=calltype"));


        MenuBar.MenuItem cust = menu.addItem("לקוחות");
        cust.setIcon(VaadinIcons.GROUP);
        MenuBar.MenuItem customer = cust.addItem("לקוחות");
        customer.setCommand((MenuBar.Command) selectedItem ->
                getUI().getNavigator().navigateTo("customer"));
        cust.addSeparator();
        MenuBar.MenuItem site = cust.addItem("אתרים");
        site.setCommand((MenuBar.Command) selectedItem ->
                getUI().getNavigator().navigateTo("site"));
        MenuBar.MenuItem addSite = cust.addItem("הוספת אתר", (MenuBar.Command) selectedItem -> Page.getCurrent()
                .open("/editsite#0", "_new2",750,400, BorderStyle.NONE));

        MenuBar.MenuItem reports = menu.addItem ("דוחות");
        reports.setIcon(VaadinIcons.PRINT);
        MenuBar.MenuItem reportOrder = reports.addItem("סידור עבודה", (MenuBar.Command) selectedItem -> Page.getCurrent()
                .open("/print#"+ LocalDate.now().format(UIcomponents.dateFormatter), "_blank",
                        getPage().getBrowserWindowWidth(),
                        getPage().getBrowserWindowHeight(), BorderStyle.MINIMAL));
        MenuBar.MenuItem reportOpenCalls = reports.addItem("קריאות פתוחות", (MenuBar.Command) selectedItem -> Page.getCurrent()
                .open("/print#open", "_blank",
                        getPage().getBrowserWindowWidth(),
                        getPage().getBrowserWindowHeight(), BorderStyle.MINIMAL));
        MenuBar.MenuItem reportHere = reports.addItem("כלים שנמצאים כאן", (MenuBar.Command) selectedItem -> Page.getCurrent()
                .open("/print#here", "_blank",
                        getPage().getBrowserWindowWidth(),
                        getPage().getBrowserWindowHeight(), BorderStyle.MINIMAL));

        MenuBar.MenuItem bigScreen = menu.addItem("מסך גדול");
        bigScreen.setIcon(VaadinIcons.EYE);
        MenuBar.MenuItem out = bigScreen.addItem("בחוץ", (MenuBar.Command) selectedItem -> Page.getCurrent()
                .open("/bigscreen#out", "_blank",getPage().getBrowserWindowWidth(),
                        getPage().getBrowserWindowHeight()-10, BorderStyle.NONE));
        MenuBar.MenuItem here = bigScreen.addItem("כאן", (MenuBar.Command) selectedItem -> Page.getCurrent()
                .open("/bigscreen#here", "_blank",
                        getPage().getBrowserWindowWidth(),
                        getPage().getBrowserWindowHeight()-10, BorderStyle.NONE));
        MenuBar.MenuItem all = bigScreen.addItem("הכל", (MenuBar.Command) selectedItem -> Page.getCurrent()
                .open("/bigscreen#all", "_blank",
                        getPage().getBrowserWindowWidth(),
                        getPage().getBrowserWindowHeight()-10, BorderStyle.NONE));

        MenuBar.MenuItem call = menu.addItem("קריאות");
        call.setIcon(VaadinIcons.BELL_O);
        MenuBar.MenuItem calls = call.addItem("טבלת קריאות");
        calls.setCommand((MenuBar.Command) selectedItem ->
                getUI().getNavigator().navigateTo("call"));
        MenuBar.MenuItem add = call.addItem("הוספה", (MenuBar.Command) selectedItem -> Page.getCurrent()
                .open("/editcall#0", "_blank",750,700, BorderStyle.NONE));

        return menu;

    }

}