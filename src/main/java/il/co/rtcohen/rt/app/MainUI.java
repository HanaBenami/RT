package il.co.rtcohen.rt.app;

import com.vaadin.annotations.Theme;
import com.vaadin.event.ShortcutAction;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.*;
import com.vaadin.shared.ui.BorderStyle;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.ui.*;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Image;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Panel;
import il.co.rtcohen.rt.app.UiComponents.UIComponents;
import il.co.rtcohen.rt.app.ui.UIPaths;
import il.co.rtcohen.rt.dal.dao.User;
import il.co.rtcohen.rt.dal.repositories.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.util.stream.Collectors;

@SpringUI()
@SpringViewDisplay
@Theme("myTheme")
public class MainUI extends UI implements ViewDisplay {
    private final UsersRepository usersRepository;

    private Panel springViewDisplay;
    private MenuBar menu;
    private HorizontalLayout topLayout;
    private HorizontalLayout loginLayout;
    private HorizontalLayout navigationLayout;
    Image language;

    @Value("${settings.multiLanguage}") Boolean multiLanguage;

    @Value("${settings.workOrderWidth}") int workOrderWidth;

    final static private Logger log = LoggerFactory.getLogger(MainUI.class);

    @Autowired
    private MainUI(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    protected void init(VaadinRequest request) {
        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        setContent(mainLayout);
        topLayout = new HorizontalLayout();
        addLogo();
        addPrintButton();
        if (multiLanguage) {
            addLanguageButton();
        }
        addOrRefreshUsernameLayout();
        addOrRefreshNavigationLayout();
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
        try {
            topLayout.removeComponent(language);
        } catch (Exception ignored) {}
        language = LanguageSettings.getFlag();
        language.setStyleName("noBorderButton");
        language.setHeight("40");
        language.setWidth("62");
        language.addClickListener(clickEvent -> changeLocale());
        topLayout.addComponent(language);
    }

    private void changeLocale() {
        LanguageSettings.changeLocale();
        addLanguageButton();
        addOrRefreshUsernameLayout();
        addOrRefreshNavigationLayout();
        getUI().getNavigator().navigateTo("");
    }

    private void addOrRefreshNavigationLayout() {
        try {
            topLayout.removeComponent(navigationLayout);
        } catch (Exception ignored) {}
        Component navigationBar = createNavigationBar();
        navigationBar.addStyleName(LanguageSettings.isHebrew() ? "menu_right" : "menu_left");
        navigationLayout = new HorizontalLayout();
        navigationLayout.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
        navigationLayout.addComponentsAndExpand(navigationBar);
        if (null == getSessionUsername()) {
            menu.setEnabled(false);
        }
        topLayout.addComponentsAndExpand(navigationLayout);
    }

    @Override
    public void showView(View view) {
        if(springViewDisplay!=null)
            springViewDisplay.setContent((Component) view);
    }

    private MenuBar.Command generateMenuBarCommand(String url) {
        return (MenuBar.Command) selectedItem -> getUI().getNavigator().navigateTo(url);
    }

    private void addHomeMenu() {
        MenuBar.MenuItem welcome = menu.addItem(LanguageSettings.getLocaleString("main"));
        welcome.setIcon(VaadinIcons.HOME_O);
        welcome.setCommand(generateMenuBarCommand(""));
    }

    private void addSetupMenu() {
        MenuBar.MenuItem setup = menu.addItem(LanguageSettings.getLocaleString("setupMenu"));
        setup.setIcon(VaadinIcons.COG_O);
        MenuBar.MenuItem driver = setup.addItem(LanguageSettings.getLocaleString("drivers"));
        driver.setCommand(generateMenuBarCommand("drivers"));
        setup.addSeparator();
        MenuBar.MenuItem area = setup.addItem(LanguageSettings.getLocaleString("areaMenu"));
        area.setCommand(generateMenuBarCommand("areas"));
        setup.addSeparator();
        MenuBar.MenuItem custType = setup.addItem(LanguageSettings.getLocaleString("custtypeTitle"));
        custType.setCommand(generateMenuBarCommand("customerType"));
        MenuBar.MenuItem carType = setup.addItem(LanguageSettings.getLocaleString("cartypeTitle"));
        carType.setCommand(generateMenuBarCommand("vehicleType"));
        MenuBar.MenuItem callType = setup.addItem(LanguageSettings.getLocaleString("calltypeTitle"));
        callType.setCommand(generateMenuBarCommand("callType"));
        setup.addSeparator();
        MenuBar.MenuItem user = setup.addItem(LanguageSettings.getLocaleString("usersMenu"));
        user.setCommand(generateMenuBarCommand("users"));
    }

    private void addCustomerMenu() {
        MenuBar.MenuItem customerMenu = menu.addItem(LanguageSettings.getLocaleString("customers"));
        customerMenu.setIcon(VaadinIcons.GROUP);
        customerMenu.setCommand(generateMenuBarCommand("customers"));
    }

    @Deprecated
    private void addCustomerMenuOld() {
        MenuBar.MenuItem customerMenu = menu.addItem(LanguageSettings.getLocaleString("customers-old"));
        customerMenu.setIcon(VaadinIcons.GROUP);
        MenuBar.MenuItem customer = customerMenu.addItem(LanguageSettings.getLocaleString("customers"));
        customer.setCommand(generateMenuBarCommand("customer"));
        customerMenu.addSeparator();
        MenuBar.MenuItem site = customerMenu.addItem(LanguageSettings.getLocaleString("sites"));
        site.setCommand(generateMenuBarCommand("site"));
        customerMenu.addItem(LanguageSettings.getLocaleString("addSite"), (MenuBar.Command) selectedItem -> Page.getCurrent()
                .open(UIPaths.EDITSITE.getPath(), "_new2",700,500, BorderStyle.NONE));
    }

    private void addReportsMenu() {
        MenuBar.MenuItem reports = menu.addItem (LanguageSettings.getLocaleString("reportsMenu"));
        reports.setIcon(VaadinIcons.PRINT);
        reports.addItem(LanguageSettings.getLocaleString("workSchedule"), (MenuBar.Command) selectedItem -> Page.getCurrent()
                .open(UIPaths.PRINT.getPath()+ LocalDate.now().format(UIComponents.dateFormatter), "_blank",
                        workOrderWidth,
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
        calls.setCommand(generateMenuBarCommand("call"));
        call.addItem(LanguageSettings.getLocaleString("addCall"), (MenuBar.Command) selectedItem -> Page.getCurrent()
                .open(UIPaths.EDITCALL.getPath(), "_new3",750,770, BorderStyle.NONE));
    }

    private Component createNavigationBar() {
        menu = new MenuBar();
        addHomeMenu();
        addSetupMenu();
        addCustomerMenuOld();
        addCustomerMenu();
        addReportsMenu();
        addBigScreenMenu();
        addCallsMenu();
        return menu;
    }

    // Login

    TextField usernameTextbox;

    private void addOrRefreshUsernameLayout() {
        try {
            topLayout.removeComponent(loginLayout);
        } catch (Exception ignored) {}
        loginLayout = new HorizontalLayout();
        loginLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        if (null == getSessionUsername()) {
            addUsernameTextbox();
        } else {
            addUsernameLabel();
        }
        topLayout.addComponents(loginLayout);
        addOrRefreshNavigationLayout();
    }

    protected void addUsernameTextbox() {
        // Login button
        Button loginBtn = UIComponents.bigButton(VaadinIcons.KEY);
        loginBtn.setEnabled(false);
        loginBtn.addClickListener(click -> login());
        loginLayout.addComponents(loginBtn);
        // Username textbox
        usernameTextbox = new TextField();
        usernameTextbox.setWidth("200");
        usernameTextbox.focus();
        usernameTextbox.addFocusListener(
            focusEvent -> loginBtn.setClickShortcut(ShortcutAction.KeyCode.ENTER)
        );
        usernameTextbox.addBlurListener(event -> loginBtn.removeClickShortcut());
        usernameTextbox.addValueChangeListener(valueChangeEvent -> {
            loginBtn.setEnabled(!usernameTextbox.getValue().isEmpty());
        });
        loginLayout.addComponents(usernameTextbox);
    }

    void addUsernameLabel() {
        // exit button
        Button exitBtn = UIComponents.bigButton(VaadinIcons.EXIT);
        exitBtn.setEnabled(true);
        exitBtn.addClickListener(click -> logout());
        loginLayout.addComponents(exitBtn);
        // Username label
        log.info("Username: " + getSessionUsername());
        Label usernameLabel = UIComponents.label("");
        usernameLabel.setValue(LanguageSettings.getLocaleString("hello") + " " + getSessionUsername());
        usernameLabel.setStyleName("LABEL-LEFT-BOLD");
        usernameLabel.setHeight("40");
        usernameLabel.setWidth("300");
        loginLayout.addComponent(usernameLabel);
    }

    static boolean emptyToString(Object obj) {
        return (null == obj || obj.toString().replace(" ", "").equals(""));
    }

    String getSessionUsername() {
        Object username = getSession().getAttribute("username");
        return (emptyToString(username) ? null : username.toString());
    }

    void setSessionUsername(String username) {
        getSession().setAttribute("username", username);
        log.info("username: " + username);
        int userid = usersRepository.getItemByName(username).getId();
        getSession().setAttribute("userid", userid);
        log.info("userid: " + userid);
    }

    void login() {
        String newUsername = usernameTextbox.getValue();
        if (emptyToString(newUsername)) {
            usernameTextbox.setComponentError(new UserError(LanguageSettings.getLocaleString("emptyUsername")));
        } else if (!usersRepository.getItems(true).stream()
                .map(User::getName).collect(Collectors.toList()).contains(newUsername)) {
            usernameTextbox.setComponentError(new UserError(LanguageSettings.getLocaleString("invalidUsername")));
        } else {
            setSessionUsername(newUsername);
            addOrRefreshUsernameLayout();
        }
    }

    void logout() {
        setSessionUsername(null);
        addOrRefreshUsernameLayout();
    }
}