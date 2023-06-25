package il.co.rtcohen.rt.app.views;

import il.co.rtcohen.rt.app.uiComponents.fields.CustomButton;
import il.co.rtcohen.rt.app.uiComponents.fields.CustomComboBox;
import il.co.rtcohen.rt.app.uiComponents.fields.CustomDateField;
import il.co.rtcohen.rt.dal.dao.*;
import il.co.rtcohen.rt.utils.Date;
import il.co.rtcohen.rt.app.LanguageSettings;
import il.co.rtcohen.rt.app.grids.CallsGrid;
import il.co.rtcohen.rt.app.ui.UIPaths;
import il.co.rtcohen.rt.dal.repositories.*;
import il.co.rtcohen.rt.utils.Logger;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.*;
import com.vaadin.shared.ui.BorderStyle;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;

@SpringView(name = CallsView.VIEW_NAME)
public class CallsView extends AbstractDataView<Call> {
    static final String VIEW_NAME = "calls";

    // Repositories
    private final CallRepository callRepository;
    private final CustomerRepository customerRepository;
    private final CustomerTypeRepository customerTypeRepository;
    private final SiteRepository siteRepository;
    private final CityRepository cityRepository;
    private final AreaRepository areaRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final UsersRepository usersRepository;
    private final DriverRepository driverRepository;
    private final CallTypeRepository callTypeRepository;
    private final GarageStatusRepository garageStatusRepository;

    // Grids & layouts
    private GridLayout headerLayout;
    private CallsGrid callsGrid;

    // Form fields
    private final String COMBOBOX_WIDTH = "500";
    private ComboBox<CallsGrid.CallsFilterOptions> callsFilterComboBox;
    private ComboBox<Customer> customerFilterComboBox;
    CustomButton addCallForCustomerButton;
    private Customer selectedCustomer;
    private Site selectedSite;
    private Vehicle selectedVehicle;
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
                      CityRepository cityRepository,
                      AreaRepository areaRepository,
                      VehicleRepository vehicleRepository,
                      VehicleTypeRepository vehicleTypeRepository,
                      UsersRepository usersRepository,
                      DriverRepository driverRepository,
                      CallTypeRepository callTypeRepository,
                      GarageStatusRepository garageStatusRepository) {
        super(errorHandler, "calls");
        this.callRepository = callRepository;
        this.customerRepository = customerRepository;
        this.customerTypeRepository = customerTypeRepository;
        this.siteRepository = siteRepository;
        this.cityRepository = cityRepository;
        this.areaRepository = areaRepository;
        this.vehicleRepository = vehicleRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
        this.usersRepository = usersRepository;
        this.driverRepository = driverRepository;
        this.callTypeRepository = callTypeRepository;
        this.garageStatusRepository = garageStatusRepository;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Map<String, String> parametersMap = event.getParameterMap();
        Logger.getLogger(this).info("Parameters map " + Arrays.toString(parametersMap.entrySet().toArray()));
        int selectedVehicleId = Integer.parseInt(parametersMap.getOrDefault("vehicle", "0"));
        int selectedCustomerId = Integer.parseInt(parametersMap.getOrDefault("customer", "0"));
        int selectedSiteId = Integer.parseInt(parametersMap.getOrDefault("site", "0"));
        if (0 != selectedVehicleId) {
            this.selectedVehicle = vehicleRepository.getItem(selectedVehicleId);
            this.selectedSite = this.selectedVehicle.getSite();
            this.selectedCustomer = this.selectedSite.getCustomer();
        } else if (0 != selectedSiteId) {
            this.selectedSite = siteRepository.getItem(selectedSiteId);
            this.selectedCustomer = this.selectedSite.getCustomer();
        } else if (0 != selectedCustomerId) {
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
        addCallForCustomerButton = this.getButton(VaadinIcons.PLUS, clickEvent -> addCall());
        headerLayout.addComponent(addCallForCustomerButton, 2, 1);
        headerLayout.setComponentAlignment(addCallForCustomerButton, Alignment.MIDDLE_RIGHT);
        addCallForCustomerButton.setEnabled(null != this.selectedCustomer);
        // ComboBox
        customerFilterComboBox = CustomComboBox.getComboBox(customerRepository);
        customerFilterComboBox.setEmptySelectionAllowed(true);
        customerFilterComboBox.setEnabled(true);
        customerFilterComboBox.setHeight(addCallForCustomerButton.getHeight(), addCallForCustomerButton.getHeightUnits());
        customerFilterComboBox.setWidth(COMBOBOX_WIDTH);
        if (null != this.selectedCustomer) {
            this.customerFilterComboBox.setSelectedItem(selectedCustomer);
        }
        customerFilterComboBox.addValueChangeListener(valueChangeEvent -> {
            this.selectedCustomer = customerFilterComboBox.getValue();
            callsGrid.setSelectedCustomer(this.selectedCustomer);
            this.refreshData();
            addCallForCustomerButton.setEnabled(null != this.selectedCustomer);
        });
        customerFilterComboBox.addFocusListener(focusEvent -> addCallForCustomerButton.setClickShortcut(ShortcutAction.KeyCode.ENTER));
        customerFilterComboBox.addBlurListener(event -> addCallForCustomerButton.removeClickShortcut());
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
                this.selectedSite,
                this.selectedVehicle,
                null,
                callRepository, customerRepository, customerTypeRepository, siteRepository, areaRepository,
                vehicleRepository, vehicleTypeRepository, usersRepository, driverRepository, callTypeRepository,
                garageStatusRepository, cityRepository);
        callsGrid.initGrid(true, 0);
        callsGrid.setWidth("100%");
        callsGrid.setNextScheduleDate(new Date(nextScheduleDateField.getValue()));
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
    void setTabIndexesAndFocus() {
        callsFilterComboBox.focus();
        int tabIndex = 1;
        callsFilterComboBox.setTabIndex(tabIndex);
        ++tabIndex;
        customerFilterComboBox.setTabIndex(tabIndex);
        ++tabIndex;
        addCallForCustomerButton.setTabIndex(tabIndex);
        ++tabIndex;
        printButton.setTabIndex(tabIndex);
        ++tabIndex;
        refreshButton.setTabIndex(tabIndex);
        ++tabIndex;
        nextScheduleDateField.setTabIndex(tabIndex);
        ++tabIndex;
        nextScheduleDriverComboBox.setTabIndex(tabIndex);
        ++tabIndex;
        callsGrid.setTabIndex(tabIndex);
        ++tabIndex;
    }

    private void addCall() {
        if (null != selectedCustomer) {
            Page.getCurrent().open(UIPaths.EDITCALL.getEditCallPath(selectedCustomer, selectedSite, selectedVehicle),
                    UIPaths.EDITCALL.getWindowName(), UIPaths.EDITCALL.getWindowWidth(), UIPaths.EDITCALL.getWindowHeight(), BorderStyle.NONE);
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
