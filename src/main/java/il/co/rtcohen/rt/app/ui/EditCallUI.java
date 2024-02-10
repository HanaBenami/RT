package il.co.rtcohen.rt.app.ui;

import il.co.rtcohen.rt.app.uiComponents.fields.*;
import il.co.rtcohen.rt.dal.dao.interfaces.Cloneable;
import il.co.rtcohen.rt.service.hashavshevet.HashavshevetSync;
import il.co.rtcohen.rt.app.grids.*;
import il.co.rtcohen.rt.app.uiComponents.*;
import il.co.rtcohen.rt.dal.dao.*;
import il.co.rtcohen.rt.dal.dao.interfaces.AbstractType;
import il.co.rtcohen.rt.dal.dao.interfaces.AbstractTypeWithNameAndActiveFields;
import il.co.rtcohen.rt.dal.dao.interfaces.BindRepository;
import il.co.rtcohen.rt.dal.repositories.*;
import il.co.rtcohen.rt.dal.repositories.interfaces.AbstractTypeWithNameAndActiveFieldsRepository;
import il.co.rtcohen.rt.utils.Date;
import il.co.rtcohen.rt.app.LanguageSettings;

import com.vaadin.data.ValueProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.server.Setter;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;

import il.co.rtcohen.rt.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Arrays;
import java.util.Map;

@SpringComponent
@SpringUI(path = "/editCall")
public class EditCallUI extends AbstractUI<GridLayout> {
    private static final String BUTTON_WIDTH = "150px";
    private static final String FIELDS_WIDTH = "200px";

    private Call call;

    // Repositories
    private final CustomerRepository customerRepository;
    private final CustomerTypeRepository customerTypeRepository;
    private final SiteRepository siteRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final CallRepository callRepository;
    private final CallTypeRepository callTypeRepository;
    private final ContactRepository contactRepository;
    private final CityRepository cityRepository;
    private final AreaRepository areaRepository;
    private final UsersRepository usersRepository;
    private final DriverRepository driverRepository;
    private final GarageStatusRepository garageStatusRepository;
    private final WarehouseStatusRepository warehouseStatusRepository;
    private final HashavshevetSync hashavshevetSync;

    // Inner grids
    private CustomerGrid customerGrid;
    CustomComboBox<Customer> customerComboBox;
    private CustomButton changeCustomerButton;
    private SitesGrid sitesGrid;
    CustomComboBox<Site> siteComboBox;
    private CustomButton changeSiteButton;
    private ContactsGrid contactsGrid;
    private VehiclesGrid vehiclesGrid;
    CustomComboBox<Vehicle> vehicleComboBox;
    private CustomButton changeVehicleButton;

    @Autowired
    private EditCallUI(
            ErrorHandler errorHandler,
            GeneralRepository generalRepository,
            CustomerRepository customerRepository,
            CustomerTypeRepository customerTypeRepository,
            SiteRepository siteRepository,
            VehicleRepository vehicleRepository,
            VehicleTypeRepository vehicleTypeRepository,
            CallRepository callRepository,
            CallTypeRepository callTypeRepository,
            ContactRepository contactRepository,
            CityRepository cityRepository,
            AreaRepository areaRepository,
            UsersRepository usersRepository,
            DriverRepository driverRepository,
            GarageStatusRepository garageStatusRepository,
            WarehouseStatusRepository warehouseStatusRepository,
            HashavshevetSync hashavshevetSync) {
        super(errorHandler, callRepository, generalRepository, usersRepository);
        this.customerRepository = customerRepository;
        this.customerTypeRepository = customerTypeRepository;
        this.siteRepository = siteRepository;
        this.vehicleRepository = vehicleRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
        this.callRepository = callRepository;
        this.callTypeRepository = callTypeRepository;
        this.contactRepository = contactRepository;
        this.cityRepository = cityRepository;
        this.areaRepository = areaRepository;
        this.usersRepository = usersRepository;
        this.driverRepository = driverRepository;
        this.garageStatusRepository = garageStatusRepository;
        this.warehouseStatusRepository = warehouseStatusRepository;
        this.hashavshevetSync = hashavshevetSync;
    }

    public void getUrlParameters() {
        Map<String, String> parametersMap = this.getParametersMap();
        Logger.getLogger(this).info("Parameters map " + Arrays.toString(parametersMap.entrySet().toArray()));
        int selectedCallId = Integer.parseInt(parametersMap.getOrDefault("call", "0"));
        int selectedCustomerId = Integer.parseInt(parametersMap.getOrDefault("customer", "0"));
        int selectedSiteId = Integer.parseInt(parametersMap.getOrDefault("site", "0"));
        int selectedVehicleId = Integer.parseInt(parametersMap.getOrDefault("vehicle", "0"));
        // Only if this is a new call, use the above parameters to initiate its values
        if (0 == selectedCallId) {
            this.call = new Call();
            this.call.setOpenedByUser(getSessionUsername());
            if (0 != selectedVehicleId) {
                call.setVehicle(vehicleRepository.getItem(selectedVehicleId));
            } else if (0 != selectedSiteId) {
                call.setSite(siteRepository.getItem(selectedSiteId));
            } else if (0 != selectedCustomerId) {
                call.setCustomer(customerRepository.getItem(selectedCustomerId));
            }
        } else {
            this.call = callRepository.getItem(selectedCallId);
        }
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        getUrlParameters();
        super.init(vaadinRequest);
    }

    @Override
    protected void setupLayout() {
        initLayout();
        addOrRefreshTitle();
        addOrRefreshAllGrids();
        addPrintButton();
        addDeleteButton();
        addOrRefreshCallData();
        setContent(layout);

    }

    private void initLayout() {
        layout = new GridLayout(5, 21);
        for (int i = 0; i < 5; ++i) {
            layout.setColumnExpandRatio(i, 0);
        }
        layout.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        layout.setSpacing(true);
        layout.setMargin(true);
    }

    private void addOrRefreshTitle() {
        String title = ((null == call || null == call.getId() || 0 == call.getId())
                ? LanguageSettings.getLocaleString("addingCall")
                : LanguageSettings.getLocaleString("callDetails") + call.getId());
        int column = 4;
        int row = 0;
        layout.removeComponent(column, row);
        layout.addComponent(new CustomLabel(title, null, true, CustomLabel.LabelStyle.SMALL_TITLE), column, row);

        if (call.isDeleted()) {
            column = 1;
            row = 0;
            layout.removeComponent(column, row);
            layout.addComponent(new CustomLabel("callDeleted", null, true, CustomLabel.LabelStyle.ERROR), column, row);
        }
    }

    private void addOrRefreshScheduleTitle() {
        int row = 14;
        int column = 4;
        layout.removeComponent(column, row);
        layout.addComponent(new CustomLabel("scheduleDetails", null, true, CustomLabel.LabelStyle.SMALL_TITLE), column,
                row);
    }

    private void addOrRefreshAllGrids() {
        addCustomerGrid();
        addSiteGrid();
        addVehicleGrid();
        addContactsGrid();
    }

    private void addCustomerGrid() {
        int column1 = 1;
        int row1 = 2;
        int column2 = 4;
        int row2 = 2;
        this.layout.removeComponent(this.customerGrid);
        this.layout.removeComponent(this.customerComboBox);
        this.layout.removeComponent(this.changeCustomerButton);
        this.layout.removeComponent(column2, row1);
        this.changeCustomerButton = addButtonToLayout("changeCustomer", VaadinIcons.RECYCLE, clickEvent -> {
            this.call.setCustomer(null);
            addCustomerGrid();
        }, 2, 0);
        if (null != call.getCustomer()) {
            this.changeCustomerButton.setEnabled(!call.isDeleted());
            this.customerGrid = new CustomerGrid(
                    call.getCustomer(),
                    customerRepository,
                    customerTypeRepository,
                    siteRepository,
                    callRepository,
                    hashavshevetSync);
            this.customerGrid.initGrid(false, 0);
            this.customerGrid.setEnabled(!call.isDeleted());
            this.addGridToLayout(this.customerGrid, call.getCustomer(), 1, column1, row1, column2, row2);
        } else {
            this.changeCustomerButton.setEnabled(false);
            this.customerComboBox = CustomComboBox.getComboBox(customerRepository);
            this.customerComboBox.setSizeFull();
            this.customerComboBox.setEnabled(!call.isDeleted());
            this.customerComboBox.addValueChangeListener(valueChangeEvent -> {
                this.call.setCustomer(customerComboBox.getValue());
                this.saveData();
                this.addCustomerGrid();
            });
            this.layout.addComponent(customerComboBox, column1, row1, column2 - 1, row2);
            this.layout.addComponent(new CustomLabel("customer", FIELDS_WIDTH), column2, row1);
        }
        this.addSiteGrid();
    }

    private void addSiteGrid() {
        int column1 = 1;
        int row1 = 3;
        int column2 = 4;
        int row2 = 3;
        this.layout.removeComponent(this.sitesGrid);
        this.layout.removeComponent(this.siteComboBox);
        this.layout.removeComponent(this.changeSiteButton);
        this.layout.removeComponent(column2, row1);
        this.changeSiteButton = addButtonToLayout("changeSite", VaadinIcons.RECYCLE, clickEvent -> {
            this.call.setSite(null);
            addSiteGrid();
        }, 3, 0);
        if (null != this.call.getSite()) {
            this.changeSiteButton.setEnabled(!call.isDeleted());
            this.sitesGrid = new SitesGrid(
                    this.call.getCustomer(), customerRepository, contactRepository, siteRepository, callRepository,
                    cityRepository, areaRepository);
            this.sitesGrid.initGrid(false, 0);
            this.sitesGrid.setEnabled(!call.isDeleted());
            this.addGridToLayout(this.sitesGrid, this.call.getSite(), 1, column1, row1, column2, row2);
        } else if (null != this.call.getCustomer()) {
            this.changeSiteButton.setEnabled(false);
            this.siteComboBox = CustomComboBox.getComboBox(siteRepository, this.call.getCustomer());
            assert this.siteComboBox != null;
            this.siteComboBox.setSizeFull();
            this.siteComboBox.setEnabled(!call.isDeleted());
            this.siteComboBox.addValueChangeListener(valueChangeEvent -> {
                this.call.setSite(siteComboBox.getValue());
                this.saveData();
                this.addSiteGrid();
            });
            this.layout.addComponent(siteComboBox, column1, row1, column2 - 1, row2);
            this.layout.addComponent(new CustomLabel("site", FIELDS_WIDTH), column2, row1);
        } else {
            this.changeSiteButton.setEnabled(false);
        }
        this.addVehicleGrid();
        this.addContactsGrid();
    }

    private void addVehicleGrid() {
        int column1 = 0;
        int row1 = 4;
        int column2 = 4;
        int row2 = 4;
        this.layout.removeComponent(this.vehiclesGrid);
        this.layout.removeComponent(this.vehicleComboBox);
        this.layout.removeComponent(this.changeVehicleButton);
        this.layout.removeComponent(column2, row1);
        this.changeVehicleButton = addButtonToLayout("changeVehicle", VaadinIcons.RECYCLE, clickEvent -> {
            this.call.setVehicle(null);
            addVehicleGrid();
        }, 5, 0);
        if (null != this.call.getVehicle()) {
            this.changeVehicleButton.setEnabled(!call.isDeleted());
            this.vehiclesGrid = new VehiclesGrid(
                    this.call.getSite(), siteRepository, vehicleRepository, vehicleTypeRepository, callRepository);
            this.vehiclesGrid.initGrid(false, 0);
            this.vehiclesGrid.setEnabled(!call.isDeleted());
            addGridToLayout(this.vehiclesGrid, this.call.getVehicle(), 1, column1, row1, column2, row2);
        } else if (null != this.call.getSite()) {
            this.changeVehicleButton.setEnabled(false);
            this.vehicleComboBox = CustomComboBox.getComboBox(vehicleRepository, this.call.getSite());
            assert this.vehicleComboBox != null;
            this.vehicleComboBox.setSizeFull();
            this.vehicleComboBox.setEnabled(!call.isDeleted());
            this.vehicleComboBox.addValueChangeListener(valueChangeEvent -> {
                this.call.setVehicle(vehicleComboBox.getValue());
                this.saveData();
                addVehicleGrid();
            });
            this.layout.addComponent(vehicleComboBox, column1, row1, column2 - 1, row2);
            this.layout.addComponent(new CustomLabel("vehicle", FIELDS_WIDTH), column2, row1);
        } else {
            this.changeVehicleButton.setEnabled(false);
        }
    }

    private void addContactsGrid() {
        this.layout.removeComponent(this.contactsGrid);
        if (null != this.call.getSite()) {
            this.contactsGrid = new ContactsGrid(
                    this.call.getSite(), contactRepository);
            this.contactsGrid.initGrid(false, 1);
            this.addGridToLayout(this.contactsGrid, null, 3, 1, 5, 4, 5);
        }
    }

    private <T extends AbstractType & Cloneable<T>> void addGridToLayout(
            AbstractTypeFilterGrid<T> abstractTypeFilterGrid,
            T selectedItem,
            Integer setHeightByRows,
            int column1, int row1, int column2, int row2) {
        abstractTypeFilterGrid.setWidth("100%");
        abstractTypeFilterGrid.hideFilterRow();
        if (null != selectedItem) {
            abstractTypeFilterGrid.setFilterToSelectedItem(selectedItem.getId());
        }
        if (null != setHeightByRows) {
            abstractTypeFilterGrid.setHeightByRows(setHeightByRows);
        }
        layout.addComponent(abstractTypeFilterGrid, column1, row1, column2, row2);
    }

    private void saveData() {
        boolean wasDraft = call.isDraft();
        callRepository.updateItem(call);
        if (wasDraft && !call.isDraft()) {
            Page.getCurrent().open(UIPaths.EDITCALL.getEditCallPath(call), Page.getCurrent().getWindowName());
        }
        addOrRefreshTitle();
        addOrRefreshCallData();
    }

    private void addPrintButton() {
        CustomButton button = addButtonToLayout("print", VaadinIcons.PRINT,
                clickEvent -> JavaScript.getCurrent().execute("print();"),
                0, 0);
        button.setEnabled(true);
    }

    private void addDeleteButton() {
        CustomButton button = addButtonToLayout("delete", VaadinIcons.TRASH, clickEvent -> deleteCall(), 1, 0);
    }

    void deleteCall() {
        if (this.call.getCurrentScheduledOrder() > 0) {
            Notification.show(LanguageSettings.getLocaleString(
                    "scheduledCallDeleteError"),
                    "",
                    Notification.Type.ERROR_MESSAGE);
        } else {
            // TODO: warning before ?
            if (null != call) {
                this.call.setDeleted(true);
                callRepository.updateItem(call);
            }
            Notification.show(
                    LanguageSettings.getLocaleString("callDeleted"),
                    "",
                    Notification.Type.WARNING_MESSAGE);
            refreshWindow();
        }
    }

    private CustomButton addButtonToLayout(String caption, VaadinIcons vaadinIcons, Button.ClickListener clickListener,
            int row, int column) {
        CustomButton button = new CustomButton(vaadinIcons, true, clickListener);
        button.setCaption(caption);
        button.setWidth(BUTTON_WIDTH);
        button.setEnabled(!call.isDeleted());
        layout.addComponent(button, column, row);
        return button;
    }

    private void addOrRefreshCallData() {
        addOrRefreshIsHereCheckBox();
        addOrRefreshIsMeetingCheckBox();
        addOrRefreshIsDoneCheckBox();
        addOrRefreshStartDateField();
        addOrRefreshPlannedDateField();
        addOrRefreshScheduledDateField();
        addOrRefreshEndDateField();
        addOrRefreshDescriptionTextArea();
        addOrRefreshNotesTextArea();
        addOrRefreshCallTypeComboBox();
        addOrRefreshGarageStatusComboBox();
        addOrRefreshWarehouseStatusComboBox();
        addOrRefreshUserComboBox();
        addOrRefreshDriverComboBox();
        addOrRefreshScheduledOrderField();
        addOrRefreshScheduleTitle();
        addOrRefreshInvoiceNumField();
    }

    private void addOrRefreshIsHereCheckBox() {
        addCheckBoxFieldToLayout(Call::isHere, Call::setHere, "currentlyHere", 10, 1);
    }

    private void addOrRefreshIsMeetingCheckBox() {
        addCheckBoxFieldToLayout(Call::isMeeting, Call::setMeeting, "meeting", 10, 3);
    }

    private void addOrRefreshIsDoneCheckBox() {
        addCheckBoxFieldToLayout(Call::isDone, null, "done", 18, 1);
    }

    private void addOrRefreshStartDateField() {
        addDateFieldToLayout(Call::getStartDate, null, "startDate", 6, 3);
    }

    private void addOrRefreshPlannedDateField() {
        addDateFieldToLayout(Call::getPlanningDate, Call::setPlanningDate, "date1", 9, 3);
    }

    private void addOrRefreshScheduledDateField() {
        addDateFieldToLayout(Call::getCurrentScheduledDate, Call::setCurrentScheduledDate, "date2", 15, 3);
    }

    private void addOrRefreshEndDateField() {
        addDateFieldToLayout(Call::getEndDate, Call::setEndDate, "endDate", 17, 3);
    }

    private void addOrRefreshDescriptionTextArea() {
        addTextAreaToLayout(Call::getDescription, Call::setDescription, "description", 11, 11, 1, 3);
    }

    private void addOrRefreshNotesTextArea() {
        addTextAreaToLayout(Call::getNotes, Call::setNotes, "notes", 12, 13, 1, 3);
    }

    private void addOrRefreshCallTypeComboBox() {
        addComboBoxToLayout(callTypeRepository, Call::getCallType, Call::setCallType, "callType", 0, 2);
    }

    private void addOrRefreshWarehouseStatusComboBox() {
        addComboBoxToLayout(warehouseStatusRepository, Call::getWarehouseStatus, Call::setWarehouseStatus,
                "warehouseStatus", 8, 1);
    }

    private void addOrRefreshGarageStatusComboBox() {
        addComboBoxToLayout(garageStatusRepository, Call::getGarageStatus, Call::setGarageStatus, "garageStatus", 9, 1);
    }

    private void addOrRefreshUserComboBox() {
        addComboBoxToLayout(usersRepository, Call::getOpenedByUser, null, "openBy", 6, 1);
    }

    private void addOrRefreshDriverComboBox() {
        addComboBoxToLayout(driverRepository, Call::getCurrentDriver, Call::setCurrentDriver, "driver", 15, 1);
    }

    private void addOrRefreshScheduledOrderField() {
        addNumericFieldToLayout(
                Call::getCurrentScheduledOrder,
                Call::setCurrentScheduledOrder,
                "order",
                16, 1);
    }

    private void addOrRefreshInvoiceNumField() {
        addNumericFieldToLayout(
                Call::getInvoiceNum,
                Call::setInvoiceNum,
                "invoice",
                17, 1);
    }

    private void addNumericFieldToLayout(
            ValueProvider<Call, Integer> valueProvider,
            Setter<Call, Integer> setter,
            String captionKey,
            int row, int column) {
        CustomIntegerField numericField = new CustomIntegerField(
                null,
                valueProvider.apply(call),
                0,
                null,
                false,
                null,
                null);
        if (null != setter) {
            numericField.addValueChangeListener(listener -> {
                setter.accept(call, Integer.parseInt(numericField.getValue()));
                saveData();
            });
        }
        numericField.setWidth(FIELDS_WIDTH);
        numericField.setEnabled(!call.isDeleted());
        this.layout.removeComponent(column, row);
        this.layout.addComponent(numericField, column, row);
        this.layout.removeComponent(column + 1, row);
        this.layout.addComponent(new CustomLabel(captionKey, FIELDS_WIDTH), column + 1, row);
    }

    private void addCheckBoxFieldToLayout(
            ValueProvider<Call, Boolean> valueProvider,
            Setter<Call, Boolean> setter,
            String captionKey,
            int row, int column) {
        CustomCheckBox checkBox = new CustomCheckBox(null, valueProvider.apply(call), null == setter);
        if (null != setter) {
            checkBox.addValueChangeListener(listener -> {
                setter.accept(call, checkBox.getValue());
                saveData();
            });
        }
        checkBox.setEnabled(!call.isDeleted());
        this.layout.removeComponent(column, row);
        this.layout.addComponent(checkBox, column, row);
        this.layout.setComponentAlignment(checkBox, Alignment.MIDDLE_RIGHT);
        this.layout.removeComponent(column + 1, row);
        this.layout.addComponent(new CustomLabel(captionKey, FIELDS_WIDTH), column + 1, row);
    }

    private void addDateFieldToLayout(
            ValueProvider<Call, Date> valueProvider,
            Setter<Call, Date> setter,
            String captionKey,
            int row, int column) {
        CustomDateField dateField = new CustomDateField(valueProvider.apply(call));
        if (null != setter) {
            dateField.addValueChangeListener(listener -> {
                setter.accept(call, new Date(dateField.getValue()));
                saveData();
            });
        } else {
            dateField.setReadOnly(true);
        }
        dateField.setWidth(FIELDS_WIDTH);
        dateField.setEnabled(!call.isDeleted());
        this.layout.removeComponent(column, row);
        this.layout.addComponent(dateField, column, row);
        this.layout.removeComponent(column + 1, row);
        this.layout.addComponent(new CustomLabel(captionKey, FIELDS_WIDTH), column + 1, row);
    }

    private void addTextAreaToLayout(
            ValueProvider<Call, String> valueProvider,
            Setter<Call, String> setter,
            String captionKey,
            int row1, int row2, int column1, int column2) {
        CustomTextArea textArea = new CustomTextArea(null, valueProvider.apply(call), null, null);
        if (null != setter) {
            textArea.addValueChangeListener(listener -> {
                setter.accept(call, textArea.getValue());
                saveData();
            });
        }
        textArea.setSizeFull();
        textArea.setEnabled(!call.isDeleted());
        textArea.setHeight("100px");
        this.layout.removeComponent(column1, row1);
        this.layout.addComponent(textArea, column1, row1, column2, row2);
        this.layout.removeComponent(column2 + 1, row1);
        this.layout.addComponent(new CustomLabel(captionKey, FIELDS_WIDTH), column2 + 1, row1);
    }

    private <T extends AbstractTypeWithNameAndActiveFields & BindRepository<T> & Cloneable<T>> void addComboBoxToLayout(
            AbstractTypeWithNameAndActiveFieldsRepository<T> repository,
            ValueProvider<Call, T> valueProvider,
            Setter<Call, T> setter,
            String captionKey,
            int row,
            int column) {
        CustomComboBox<T> comboBox = CustomComboBox.getComboBox(repository);
        T selected = valueProvider.apply(call);
        if (null != selected) {
            comboBox.setSelectedItem(selected);
        }
        if (null != setter) {
            comboBox.addValueChangeListener(listener -> {
                setter.accept(call, comboBox.getValue());
                saveData();
            });
        } else {
            comboBox.setReadOnly(true);
            comboBox.setEnabled(!call.isDeleted());
        }
        comboBox.setWidth(FIELDS_WIDTH);
        comboBox.setEnabled(!call.isDeleted());
        this.layout.removeComponent(column, row);
        this.layout.addComponent(comboBox, column, row);
        this.layout.removeComponent(column + 1, row);
        this.layout.addComponent(new CustomLabel(captionKey, FIELDS_WIDTH), column + 1, row);
    }
}
