package il.co.rtcohen.rt.app;

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
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Image;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Panel;
import il.co.rtcohen.rt.app.ui.UIPaths;

import java.time.LocalDate;

@SpringUI()
@SpringViewDisplay
@Theme("myTheme")
public class MainUI extends UI implements ViewDisplay {

    private Panel springViewDisplay;
    private MenuBar menu;
    private HorizontalLayout topLayout;

    @Override
    protected void init(VaadinRequest request) {
        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        setContent(mainLayout);
        topLayout = new HorizontalLayout();
        addLogo();
        addPrintButton();
        addNavigationBar();
        mainLayout.addComponent(topLayout);
        springViewDisplay = new Panel();
        springViewDisplay.setSizeFull();
        mainLayout.addComponent(springViewDisplay);
        mainLayout.setExpandRatio(springViewDisplay, 1.0f);
    }

    private void addLogo() {
        Image logo = new Image(null, new ThemeResource("rtlogo.png"));
        logo.setHeight("40");
        topLayout.addComponent(logo);
    }

    private void addPrintButton() {
        Button print = UIComponents.printButton();
        print.addClickListener(clickEvent ->
                JavaScript.getCurrent().execute("print();"));
        topLayout.addComponent(print);
    }

    private void addNavigationBar() {
        Component navigationBar = createNavigationBar();
        navigationBar.addStyleName("menu");
        topLayout.addComponentsAndExpand(navigationBar);
    }

    @Override
    public void showView(View view) {
        if(springViewDisplay!=null)
            springViewDisplay.setContent((Component) view);
    }

    private void setupMenu() {
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

        MenuBar.MenuItem custType = setup.addItem("סוגי לקוחות");
        custType.setCommand((MenuBar.Command) selectedItem ->
                getUI().getNavigator().navigateTo("update/table=custtype"));

        MenuBar.MenuItem carType = setup.addItem("סוגי כלים");
        carType.setCommand((MenuBar.Command) selectedItem ->
                getUI().getNavigator().navigateTo("update/table=cartype"));

        MenuBar.MenuItem callType = setup.addItem("סוגי קריאות");
        callType.setCommand((MenuBar.Command) selectedItem ->
                getUI().getNavigator().navigateTo("update/table=calltype"));
    }
    private void addCustomerMenu() {
        MenuBar.MenuItem customerMenu = menu.addItem("לקוחות");
        customerMenu.setIcon(VaadinIcons.GROUP);
        MenuBar.MenuItem customer = customerMenu.addItem("לקוחות");
        customer.setCommand((MenuBar.Command) selectedItem ->
                getUI().getNavigator().navigateTo("customer"));
        customerMenu.addSeparator();
        MenuBar.MenuItem site = customerMenu.addItem("אתרים");
        site.setCommand((MenuBar.Command) selectedItem ->
                getUI().getNavigator().navigateTo("site"));
        customerMenu.addItem("הוספת אתר", (MenuBar.Command) selectedItem -> Page.getCurrent()
                .open(UIPaths.EDITSITE.getPath(), "_new2",750,400, BorderStyle.NONE));
    }
    private void addReportsMenu() {
        MenuBar.MenuItem reports = menu.addItem ("דוחות");
        reports.setIcon(VaadinIcons.PRINT);
        reports.addItem("סידור עבודה", (MenuBar.Command) selectedItem -> Page.getCurrent()
                .open(UIPaths.PRINT.getPath()+ LocalDate.now().format(UIComponents.dateFormatter), "_blank",
                        getPage().getBrowserWindowWidth(),
                        getPage().getBrowserWindowHeight(), BorderStyle.MINIMAL));
        reports.addItem("קריאות פתוחות", (MenuBar.Command) selectedItem -> Page.getCurrent()
                .open(UIPaths.PRINT.getPath()+"open", "_blank",
                        getPage().getBrowserWindowWidth(),
                        getPage().getBrowserWindowHeight(), BorderStyle.MINIMAL));
        reports.addItem("כלים שנמצאים כאן", (MenuBar.Command) selectedItem -> Page.getCurrent()
                .open(UIPaths.PRINT.getPath()+"here", "_blank",
                        getPage().getBrowserWindowWidth(),
                        getPage().getBrowserWindowHeight(), BorderStyle.MINIMAL));
    }
    private void addBigScreenMenu() {
        MenuBar.MenuItem bigScreen = menu.addItem("מסך גדול");
        bigScreen.setIcon(VaadinIcons.EYE);
        bigScreen.addItem("בחוץ", (MenuBar.Command) selectedItem -> Page.getCurrent()
                .open(UIPaths.BIGSCREEN.getPath()+"out", "_blank",getPage().getBrowserWindowWidth(),
                        getPage().getBrowserWindowHeight()-10, BorderStyle.NONE));
        bigScreen.addItem("כאן", (MenuBar.Command) selectedItem -> Page.getCurrent()
                .open(UIPaths.BIGSCREEN.getPath()+"here", "_blank",
                        getPage().getBrowserWindowWidth(),
                        getPage().getBrowserWindowHeight()-10, BorderStyle.NONE));
        bigScreen.addItem("הכל", (MenuBar.Command) selectedItem -> Page.getCurrent()
                .open(UIPaths.BIGSCREEN.getPath()+"all", "_blank",
                        getPage().getBrowserWindowWidth(),
                        getPage().getBrowserWindowHeight()-10, BorderStyle.NONE));
    }
    private void addCallsMenu() {
        MenuBar.MenuItem call = menu.addItem("קריאות");
        call.setIcon(VaadinIcons.BELL_O);
        MenuBar.MenuItem calls = call.addItem("טבלת קריאות");
        calls.setCommand((MenuBar.Command) selectedItem ->
                getUI().getNavigator().navigateTo("call"));
        call.addItem("הוספה", (MenuBar.Command) selectedItem -> Page.getCurrent()
                .open(UIPaths.EDITCALL.getPath(), "_new3",700,700, BorderStyle.NONE));
    }
    private Component createNavigationBar() {
        menu = new MenuBar();
        MenuBar.MenuItem welcome = menu.addItem("ראשי");
        welcome.setIcon(VaadinIcons.HOME_O);
        welcome.setCommand((MenuBar.Command) selectedItem ->
                getUI().getNavigator().navigateTo(""));
        setupMenu();
        addCustomerMenu();
        addReportsMenu();
        addBigScreenMenu();
        addCallsMenu();
        return menu;
    }

}