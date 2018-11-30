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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;

@SpringUI()
@SpringViewDisplay
@Theme("myTheme")
public class MainUI extends UI implements ViewDisplay {

    private Panel springViewDisplay;
    private MenuBar menu;
    private HorizontalLayout topLayout;
    private Component navigationBar;
    Image language;

    @Autowired
    @Value("${settings.multiLanguage}") Boolean multiLanguage;

    @Override
    protected void init(VaadinRequest request) {

        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        setContent(mainLayout);
        topLayout = new HorizontalLayout();
        addLogo();
        addPrintButton();
        if (multiLanguage)
            addLanguageButton();
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

    private void addLanguageButton() {
        language = LanguageSettings.getFlag();
        language.setStyleName("noBorderButton");
        language.setHeight("40");
        language.setWidth("62");
        language.addClickListener(clickEvent -> changeLocale());
        topLayout.addComponent(language);
    }

    private void changeLocale() {
        LanguageSettings.changeLocale();
        topLayout.removeComponent(navigationBar);
        topLayout.removeComponent(language);
        addLanguageButton();
        addNavigationBar();
        getUI().getNavigator().navigateTo("");
    }

    private void addNavigationBar() {
        navigationBar = createNavigationBar();
        if(LanguageSettings.isHebrew())
            navigationBar.addStyleName("menu_right");
        else
            navigationBar.addStyleName("menu_left");
        topLayout.addComponentsAndExpand(navigationBar);
    }

    @Override
    public void showView(View view) {
        if(springViewDisplay!=null)
            springViewDisplay.setContent((Component) view);
    }

    private void setupMenu() {
        MenuBar.MenuItem setup = menu.addItem(LanguageSettings.getLocaleString("setupMenu"));
        setup.setIcon(VaadinIcons.COG_O);
        MenuBar.MenuItem driver = setup.addItem(LanguageSettings.getLocaleString("drivers"));
        driver.setCommand((MenuBar.Command) selectedItem ->
                getUI().getNavigator().navigateTo("update/table=driver"));
        setup.addSeparator();

        MenuBar.MenuItem area = setup.addItem(LanguageSettings.getLocaleString("areaMenu"));
        area.setCommand((MenuBar.Command) selectedItem ->
                getUI().getNavigator().navigateTo("area"));
        setup.addSeparator();

        MenuBar.MenuItem custType = setup.addItem(LanguageSettings.getLocaleString("customerTypeTitle"));
        custType.setCommand((MenuBar.Command) selectedItem ->
                getUI().getNavigator().navigateTo("update/table=custtype"));

        MenuBar.MenuItem carType = setup.addItem(LanguageSettings.getLocaleString("carTypeTitle"));
        carType.setCommand((MenuBar.Command) selectedItem ->
                getUI().getNavigator().navigateTo("update/table=cartype"));

        MenuBar.MenuItem callType = setup.addItem(LanguageSettings.getLocaleString("callTypeTitle"));
        callType.setCommand((MenuBar.Command) selectedItem ->
                getUI().getNavigator().navigateTo("update/table=calltype"));
    }

    private void addCustomerMenu() {
        MenuBar.MenuItem customerMenu = menu.addItem(LanguageSettings.getLocaleString("customers"));
        customerMenu.setIcon(VaadinIcons.GROUP);
        MenuBar.MenuItem customer = customerMenu.addItem(LanguageSettings.getLocaleString("customers"));
        customer.setCommand((MenuBar.Command) selectedItem ->
                getUI().getNavigator().navigateTo("customer"));
        customerMenu.addSeparator();
        MenuBar.MenuItem site = customerMenu.addItem(LanguageSettings.getLocaleString("sites"));
        site.setCommand((MenuBar.Command) selectedItem ->
                getUI().getNavigator().navigateTo("site"));
        customerMenu.addItem(LanguageSettings.getLocaleString("addSite"), (MenuBar.Command) selectedItem -> Page.getCurrent()
                .open(UIPaths.EDITSITE.getPath(), "_new2",700,400, BorderStyle.NONE));
    }

    private void addReportsMenu() {
        MenuBar.MenuItem reports = menu.addItem (LanguageSettings.getLocaleString("reportsMenu"));
        reports.setIcon(VaadinIcons.PRINT);
        reports.addItem(LanguageSettings.getLocaleString("workSchedule"), (MenuBar.Command) selectedItem -> Page.getCurrent()
                .open(UIPaths.PRINT.getPath()+ LocalDate.now().format(UIComponents.dateFormatter), "_blank",
                        getPage().getBrowserWindowWidth(),
                        getPage().getBrowserWindowHeight(), BorderStyle.MINIMAL));
        reports.addItem(LanguageSettings.getLocaleString("openCalls"), (MenuBar.Command) selectedItem -> Page.getCurrent()
                .open(UIPaths.PRINT.getPath()+"open", "_blank",
                        getPage().getBrowserWindowWidth(),
                        getPage().getBrowserWindowHeight(), BorderStyle.MINIMAL));
        reports.addItem(LanguageSettings.getLocaleString("currentlyHereReport"), (MenuBar.Command) selectedItem -> Page.getCurrent()
                .open(UIPaths.PRINT.getPath()+"here", "_blank",
                        getPage().getBrowserWindowWidth(),
                        getPage().getBrowserWindowHeight(), BorderStyle.MINIMAL));
    }

    private void addBigScreenMenu() {
        MenuBar.MenuItem bigScreen = menu.addItem(LanguageSettings.getLocaleString("bigScreenMenu"));
        bigScreen.setIcon(VaadinIcons.EYE);
        bigScreen.addItem(LanguageSettings.getLocaleString("outsideBigScreen"), (MenuBar.Command) selectedItem -> Page.getCurrent()
                .open(UIPaths.BIGSCREEN.getPath()+"out", "_blank",getPage().getBrowserWindowWidth(),
                        getPage().getBrowserWindowHeight()-10, BorderStyle.NONE));
        bigScreen.addItem(LanguageSettings.getLocaleString("garageBigScreen"), (MenuBar.Command) selectedItem -> Page.getCurrent()
                .open(UIPaths.BIGSCREEN.getPath()+"here", "_blank",
                        getPage().getBrowserWindowWidth(),
                        getPage().getBrowserWindowHeight()-10, BorderStyle.NONE));
        bigScreen.addItem(LanguageSettings.getLocaleString("allCalls"), (MenuBar.Command) selectedItem -> Page.getCurrent()
                .open(UIPaths.BIGSCREEN.getPath()+"all", "_blank",
                        getPage().getBrowserWindowWidth(),
                        getPage().getBrowserWindowHeight()-10, BorderStyle.NONE));
    }

    private void addCallsMenu() {
        MenuBar.MenuItem call = menu.addItem(LanguageSettings.getLocaleString("calls"));
        call.setIcon(VaadinIcons.BELL_O);
        MenuBar.MenuItem calls = call.addItem(LanguageSettings.getLocaleString("callsTable"));
        calls.setCommand((MenuBar.Command) selectedItem ->
                getUI().getNavigator().navigateTo("call"));
        call.addItem(LanguageSettings.getLocaleString("addCall"), (MenuBar.Command) selectedItem -> Page.getCurrent()
                .open(UIPaths.EDITCALL.getPath(), "_new3",700,650, BorderStyle.NONE));
    }

    private Component createNavigationBar() {
        menu = new MenuBar();
        MenuBar.MenuItem welcome = menu.addItem(LanguageSettings.getLocaleString("main"));
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