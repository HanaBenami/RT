package il.co.rtcohen.rt.app.views;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;

import com.vaadin.icons.VaadinIcons;
import il.co.rtcohen.rt.app.uiComponents.CustomButton;
import il.co.rtcohen.rt.app.uiComponents.CustomComboBox;
import il.co.rtcohen.rt.app.uiComponents.CustomDateField;
import il.co.rtcohen.rt.dal.dao.Customer;
import il.co.rtcohen.rt.dal.dao.Driver;
import il.co.rtcohen.rt.utils.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.*;
import com.vaadin.shared.ui.BorderStyle;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;

import il.co.rtcohen.rt.app.LanguageSettings;
import il.co.rtcohen.rt.app.grids.CallsGrid;
import il.co.rtcohen.rt.app.ui.UIPaths;
import il.co.rtcohen.rt.dal.dao.Call;
import il.co.rtcohen.rt.dal.repositories.*;


@SpringView(name = CallsView.VIEW_NAME)
public class CallsView extends AbstractDataView<Call> {
    static final String VIEW_NAME = "calls";
    private static final Logger logger = LoggerFactory.getLogger(CallsView.class);

    // Parameters
    private Map<String, String> parametersMap;

    // Repositories
    private final CallRepository callRepository;
    private final CustomerRepository customerRepository;
    private final CustomerTypeRepository customerTypeRepository;
    private final SiteRepository siteRepository;
    private final AreasRepository areasRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final UsersRepository usersRepository;
    private final DriverRepository driverRepository;
    private final CallTypeRepository callTypeRepository;

    // Grids & layouts
    private GridLayout headerLayout;
    private CallsGrid callsGrid;

    // Form fields
    private final String COMBOBOX_WIDTH = "500";
    private ComboBox<CallsGrid.CallsFilterOptions> callsFilterComboBox;
    private ComboBox<Customer> customerFilterComboBox;
    private Customer selectedCustomer;
    private final String SCHEDULE_FIELDS_WIDTH = "150";
    private final String SCHEDULE_FIELDS_HEIGHT = "30";
    private CustomDateField nextScheduleDateField;
    private ComboBox<Driver> nextScheduleDriverComboBox;

    // Buttons
    private Button refreshButton;
    private Button printButton;

    @Value("${settings.workOrderWidth}")
    int workOrderWidth;

    @Autowired
    private CallsView(ErrorHandler errorHandler,
                      CallRepository callRepository,
                      CustomerRepository customerRepository,
                      CustomerTypeRepository customerTypeRepository,
                      SiteRepository siteRepository,
                      AreasRepository areasRepository,
                      VehicleRepository vehicleRepository,
                      VehicleTypeRepository vehicleTypeRepository,
                      UsersRepository usersRepository,
                      DriverRepository driverRepository,
                      CallTypeRepository callTypeRepository) {
        super(errorHandler, "calls");
        this.callRepository = callRepository;
        this.customerRepository = customerRepository;
        this.customerTypeRepository = customerTypeRepository;
        this.siteRepository = siteRepository;
        this.areasRepository = areasRepository;
        this.vehicleRepository = vehicleRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
        this.usersRepository = usersRepository;
        this.driverRepository = driverRepository;
        this.callTypeRepository = callTypeRepository;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Map<String, String> parametersMap = event.getParameterMap();
        logger.info("Parameters map " + Arrays.toString(parametersMap.entrySet().toArray()));
        int selectedCustomerId = Integer.parseInt(parametersMap.getOrDefault("customer", "0"));
        if (0 != selectedCustomerId) {
            this.selectedCustomer = customerRepository.getItem(selectedCustomerId);
        }
        super.enter(event);
    }

    @Override
    protected void addTitle() {
        addHeaderLayout();
    }

    @Override
    void addGrids() {
        addCallsGrid();
    }

    private void addHeaderLayout() {
        headerLayout = new GridLayout(4, 2);
        headerLayout.setWidth("95%");
        headerLayout.setSpacing(true);
        addCallsFilter();
        addCustomerFilter();
        addScheduleFields();
        addPrintButton();
        addRefreshButton();
        addComponent(headerLayout);
    }

    private void addCallsFilter() {
        // Button
        CustomButton selectButton = this.getButton(VaadinIcons.SEARCH, null);
        headerLayout.addComponent(selectButton, 2, 0);
        headerLayout.setComponentAlignment(selectButton, Alignment.MIDDLE_RIGHT);
        selectButton.setEnabled(false);
        // ComboBox
        callsFilterComboBox = new ComboBox<>();
        callsFilterComboBox.setEmptySelectionAllowed(false);
        callsFilterComboBox.setEnabled(true);
        callsFilterComboBox.setHeight(selectButton.getHeight(), selectButton.getHeightUnits());
        callsFilterComboBox.setWidth(COMBOBOX_WIDTH);
        callsFilterComboBox.setItems(CallsGrid.CallsFilterOptions.values());
        callsFilterComboBox.setValue(CallsGrid.CallsFilterOptions.ONLY_OPEN_CALLS);
        callsFilterComboBox.setItemCaptionGenerator(filterOption -> LanguageSettings.getLocaleString(filterOption.getTitleKey()));
        callsFilterComboBox.addValueChangeListener(ValueChangeEvent -> {
            if (null != callsFilterComboBox.getValue()) {
                callsGrid.setSelectedCallsFilterOption(callsFilterComboBox.getValue());
                this.refreshData();
            }
        });
        headerLayout.addComponent(callsFilterComboBox, 3, 0);
        headerLayout.setComponentAlignment(callsFilterComboBox, Alignment.MIDDLE_LEFT);
    }

    private void addCustomerFilter() {
        // Button
        CustomButton addButton = this.getButton(VaadinIcons.PLUS, clickEvent -> addCall());
        headerLayout.addComponent(addButton, 2, 1);
        headerLayout.setComponentAlignment(addButton, Alignment.MIDDLE_RIGHT);
        addButton.setEnabled(null != this.selectedCustomer);
        // ComboBox
        customerFilterComboBox = CustomComboBox.getComboBox(customerRepository);
        customerFilterComboBox.setEmptySelectionAllowed(true);
        customerFilterComboBox.setEnabled(true);
        customerFilterComboBox.setHeight(addButton.getHeight(), addButton.getHeightUnits());
        customerFilterComboBox.setWidth(COMBOBOX_WIDTH);
        customerFilterComboBox.addValueChangeListener(valueChangeEvent -> {
            this.selectedCustomer = customerFilterComboBox.getValue();
            callsGrid.setSelectedCustomer(this.selectedCustomer);
            this.refreshData();
            addButton.setEnabled(null != this.selectedCustomer);
        });
        customerFilterComboBox.addFocusListener(focusEvent -> addButton.setClickShortcut(ShortcutAction.KeyCode.ENTER));
        customerFilterComboBox.addBlurListener(event -> addButton.removeClickShortcut());
        headerLayout.addComponent(customerFilterComboBox, 3, 1);
        headerLayout.setComponentAlignment(customerFilterComboBox, Alignment.MIDDLE_LEFT);
    }

    private void addScheduleFields() {
        addNextScheduleDateField();
        addNextScheduleDriverField();
    }

    private void addNextScheduleDateField() {
        nextScheduleDateField = new CustomDateField();
        nextScheduleDateField.setWidth(SCHEDULE_FIELDS_WIDTH);
        nextScheduleDateField.setHeight(SCHEDULE_FIELDS_HEIGHT);
        nextScheduleDateField.setValue(LocalDate.now().plusDays(1));
        nextScheduleDateField.addValueChangeListener(valueChangeEvent ->
                this.callsGrid.setNextScheduleDate(new Date(nextScheduleDateField.getValue()))
        );
        headerLayout.addComponent(nextScheduleDateField, 0, 0);
        headerLayout.setComponentAlignment(nextScheduleDateField, Alignment.MIDDLE_LEFT);
    }

    private void addNextScheduleDriverField() {
        nextScheduleDriverComboBox = CustomComboBox.getComboBox(driverRepository);
        nextScheduleDriverComboBox.setWidth(SCHEDULE_FIELDS_WIDTH);
        nextScheduleDriverComboBox.setHeight(SCHEDULE_FIELDS_HEIGHT);
        nextScheduleDriverComboBox.addValueChangeListener(valueChangeEvent ->
                this.callsGrid.setNextScheduleDriver(nextScheduleDriverComboBox.getValue())
        );
        headerLayout.addComponent(nextScheduleDriverComboBox, 0, 1);
        headerLayout.setComponentAlignment(nextScheduleDriverComboBox, Alignment.MIDDLE_LEFT);
    }

    private void addPrintButton() {
        printButton = this.getButton(VaadinIcons.TRUCK, clickEvent -> print());
        headerLayout.addComponent(printButton, 1, 0, 1, 0);
        headerLayout.setComponentAlignment(printButton, Alignment.MIDDLE_LEFT);
    }

    private void addRefreshButton() {
        refreshButton = getRefreshButton();
        headerLayout.addComponent(refreshButton, 1, 1, 1, 1);
        headerLayout.setComponentAlignment(refreshButton, Alignment.MIDDLE_LEFT);
    }

    void addCallsGrid() {
        removeCallsGrid();
        callsGrid = new CallsGrid(
                this.callsFilterComboBox.getValue(),
                this.customerFilterComboBox.getValue(),
                null,
                null,
                null,
                callRepository, customerRepository, customerTypeRepository, siteRepository, areasRepository,
                vehicleRepository, vehicleTypeRepository, usersRepository, driverRepository, callTypeRepository
        );
        callsGrid.setWidth("100%");
        addComponentsAndExpand(callsGrid.getVerticalLayout(true, true));
    }

    @Override
    void removeGrids() {
        removeCallsGrid();
    }

    void removeCallsGrid() {
        if (null != callsGrid) {
            removeComponent(callsGrid.getVerticalLayout(false, false));
            callsGrid = null;
        }
    }

    @Override
    void setTabIndexes() {
        callsFilterComboBox.setTabIndex(1);
        customerFilterComboBox.setTabIndex(2);
        nextScheduleDateField.setTabIndex(3);
        nextScheduleDriverComboBox.setTabIndex(4);
        printButton.setTabIndex(5);
        refreshButton.setTabIndex(6);
        callsGrid.setTabIndex(7);
    }

    private void addCall() {
        if (null != selectedCustomer) {
            // TODO: Make this path works
            Page.getCurrent().open(UIPaths.EDITCALL.getPath() + "/customer=" + selectedCustomer.getId(),
                    "_new3", 750, 770, BorderStyle.NONE);
        }
    }

    private void print() {
        Page.getCurrent().open(
                UIPaths.PRINT.getPath() + nextScheduleDateField.getValue().format(Date.dateFormatterForUrls),
                "_blank",
                workOrderWidth,
                getUI().getPage().getBrowserWindowHeight(),
                BorderStyle.NONE
        );
    }
}
