package il.co.rtcohen.rt.app.ui;

import com.vaadin.data.ValueProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.server.Setter;
import il.co.rtcohen.rt.app.grids.*;
import il.co.rtcohen.rt.app.uiComponents.*;
import il.co.rtcohen.rt.dal.dao.*;
import il.co.rtcohen.rt.dal.dao.interfaces.AbstractType;
import il.co.rtcohen.rt.dal.dao.interfaces.AbstractTypeWithNameAndActiveFields;
import il.co.rtcohen.rt.dal.dao.interfaces.BindRepository;
import il.co.rtcohen.rt.dal.repositories.*;
import il.co.rtcohen.rt.dal.repositories.interfaces.AbstractTypeWithNameAndActiveFieldsRepository;
import il.co.rtcohen.rt.utils.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Arrays;
import java.util.Map;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;

import il.co.rtcohen.rt.app.LanguageSettings;
import il.co.rtcohen.rt.app.views.CallsView;

@SpringComponent
@SpringUI(path="/editCall")
public class EditCallUI extends AbstractUI<GridLayout> {
    private static final Logger logger = LoggerFactory.getLogger(CallsView.class);
    private static final String BUTTON_WIDTH = "100px";
    private static final String FIELDS_WIDTH = "200px";

    // Repositories
    private final CustomerRepository customerRepository;
    private final CustomerTypeRepository customerTypeRepository;
    private final SiteRepository siteRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final CallRepository callRepository;
    private final CallTypeRepository callTypeRepository;
    private final ContactRepository contactRepository;
    private final AreasRepository areasRepository;
    private final UsersRepository usersRepository;
    private final DriverRepository driverRepository;

    // Pre-selected items
    private Call call;
    private Customer selectedCustomer;
    private Site selectedSite;
    private Vehicle selectedVehicle;

    // Inner grids
    private CustomerGrid customerGrid;
    private SitesGrid sitesGrid;
    private ContactsGrid contactsGrid;
    private VehiclesGrid vehiclesGrid;

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
           AreasRepository areasRepository,
           UsersRepository usersRepository,
           DriverRepository driverRepository
    ) {
        super(errorHandler, callRepository, generalRepository, usersRepository);
        this.customerRepository = customerRepository;
        this.customerTypeRepository = customerTypeRepository;
        this.siteRepository = siteRepository;
        this.vehicleRepository = vehicleRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
        this.callRepository = callRepository;
        this.callTypeRepository = callTypeRepository;
        this.contactRepository = contactRepository;
        this.areasRepository = areasRepository;
        this.usersRepository = usersRepository;
        this.driverRepository = driverRepository;
    }

    public void getUrlParameters() {
        Map<String, String> parametersMap = this.getParametersMap();
        logger.info("Parameters map " + Arrays.toString(parametersMap.entrySet().toArray()));
        int selectedCallId = Integer.parseInt(parametersMap.getOrDefault("call", "0"));
        int selectedCustomerId = Integer.parseInt(parametersMap.getOrDefault("customer", "0"));
        int selectedSiteId = Integer.parseInt(parametersMap.getOrDefault("site", "0"));
        int selectedVehicleId = Integer.parseInt(parametersMap.getOrDefault("vehicle", "0"));
        if (0 != selectedCallId) {
            this.call = callRepository.getItem(selectedCallId);
            this.selectedCustomer = this.call.getCustomer();
            this.selectedSite = this.call.getSite();
            this.selectedVehicle = this.call.getVehicle();
        } else {
            this.call = new Call();
            this.call.setOpenedByUser(getSessionUsername());
            if (0 != selectedVehicleId) {
                this.selectedVehicle = vehicleRepository.getItem(selectedVehicleId);
                call.setVehicle(this.selectedVehicle);
                this.selectedSite = this.selectedVehicle.getSite();
                call.setSite(this.selectedSite);
                this.selectedCustomer = this.selectedSite.getCustomer();
                call.setCustomer(this.selectedCustomer);
            } else if (0 != selectedSiteId) {
                this.selectedSite = siteRepository.getItem(selectedSiteId);
                call.setSite(this.selectedSite);
                this.selectedCustomer = this.selectedSite.getCustomer();
                call.setCustomer(this.selectedCustomer);
            } else if (0 != selectedCustomerId) {
                this.selectedCustomer = customerRepository.getItem(selectedCustomerId);
                call.setCustomer(this.selectedCustomer);
            }
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
        addCustomerGrid();
        addSiteGrid();
        addVehicleGrid();
        addContactsGrid();
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
                : LanguageSettings.getLocaleString("callDetails") + call.getId()
        );
        int column = 4;
        int row = 0;
        layout.removeComponent(column, row);
        layout.addComponent(new CustomLabel(title, null, CustomLabel.LabelStyle.TITLE), column, row);

        if (call.isDeleted()) {
            column = 1;
            row = 0;
            layout.removeComponent(column, row);
            layout.addComponent(new CustomLabel("callDeleted", null, CustomLabel.LabelStyle.ERROR), column, row);
        }
    }

    private void addOrRefreshScheduleTitle() {
        int row = 17;
        int column = 4;
        layout.removeComponent(column, row);
        layout.addComponent(new CustomLabel("scheduleDetails", null, CustomLabel.LabelStyle.TITLE), column, row);
    }

    private void addCustomerGrid() {
        if (null != this.selectedCustomer) {
            this.customerGrid = new CustomerGrid(
                    this.selectedCustomer,
                    customerRepository,
                    customerTypeRepository,
                    siteRepository,
                    callRepository
            );
            this.addGridToLayout(this.customerGrid, this.selectedCustomer, 1,1, 2, 4, 2);
        } else {
            // TODO: selection combobox
        }
    }

    private void addSiteGrid() {
        if (null != this.selectedSite) {
            sitesGrid = new SitesGrid(
                    this.selectedCustomer, contactRepository, siteRepository, callRepository, areasRepository
            );
            this.addGridToLayout(this.sitesGrid, this.selectedSite, 1,1, 3, 4, 3);
        } else {
            // TODO: selection combobox
        }
    }

    private void addVehicleGrid() {
        if (null != this.selectedVehicle) {
            this.vehiclesGrid = new VehiclesGrid(
                    this.selectedSite, vehicleRepository, vehicleTypeRepository, callRepository
            );
            addGridToLayout(this.vehiclesGrid, this.selectedVehicle, 1,1, 4, 4, 4);
        } else {
            // TODO: selection combobox
        }
    }

    private void addContactsGrid() {
        if (null != this.selectedSite) {
            this.contactsGrid = new ContactsGrid(
                    this.selectedSite, contactRepository
            );
            this.addGridToLayout(this.contactsGrid, null, 3,1, 5, 4, 5);
        } else {
            // TODO: selection combobox
        }
    }

    private <T extends AbstractType> void addGridToLayout(
            AbstractTypeFilterGrid<T> abstractTypeFilterGrid,
            T selectedItem,
            Integer setHeightByRows,
            int column1, int row1, int column2, int row2
    ) {
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
        Integer previousId = call.getId();
        callRepository.updateItem(call);
        addOrRefreshTitle();
        addOrRefreshCallData();
    }

    private void addPrintButton() {
        CustomButton button = addButtonToLayout("print", VaadinIcons.PRINT,
                clickEvent -> JavaScript.getCurrent().execute("print();"),
                0, 0);
    }

    private void addDeleteButton() {
        CustomButton button = addButtonToLayout("delete", VaadinIcons.TRASH, clickEvent -> deleteCall(), 1, 0);
    }

    void deleteCall() {
        if (this.call.getCurrentScheduledOrder() > 0) {
            Notification.show(LanguageSettings.getLocaleString(
                    "scheduledCallDeleteError"),
                    "",
                    Notification.Type.ERROR_MESSAGE
            );
        } else {
            // TODO: warning before ?
            if (null != call) {
                this.call.setDeleted(true);
                callRepository.updateItem(call);
            }
            Notification.show(
                    LanguageSettings.getLocaleString("callDeleted"),
                    "",
                    Notification.Type.WARNING_MESSAGE
            );
            refreshWindow();
        }
    }

    private CustomButton addButtonToLayout(String caption, VaadinIcons vaadinIcons, Button.ClickListener clickListener, int row, int column) {
        CustomButton button = new CustomButton(vaadinIcons, true, clickListener);
        button.setCaption(caption);
        button.setWidth(BUTTON_WIDTH);
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
        addOrRefreshUserComboBox();
        addOrRefreshDriverComboBox();
        addOrRefreshScheduledOrderField();
        addOrRefreshScheduleTitle();
    }

    private void addOrRefreshIsHereCheckBox() {
        addCheckBoxFieldToLayout(Call::isHere, Call::setHere, "currentlyHere", 12, 1);
    }

    private void addOrRefreshIsMeetingCheckBox() {
        addCheckBoxFieldToLayout(Call::isMeeting, Call::setMeeting, "meeting", 11, 1);
    }

    private void addOrRefreshIsDoneCheckBox() {
        addCheckBoxFieldToLayout(Call::isDone, null, "done", 20, 1);
    }

    private void addOrRefreshStartDateField() {
        addDateFieldToLayout(Call::getStartDate, Call::setStartDate, "startDate", 10, 3);
    }

    private void addOrRefreshPlannedDateField() {
        addDateFieldToLayout(Call::getPlanningDate, Call::setPlanningDate, "date1", 11, 3);
    }

    private void addOrRefreshScheduledDateField() {
        addDateFieldToLayout(Call::getCurrentScheduledDate, Call::setCurrentScheduledDate, "date2", 18, 3);
    }

    private void addOrRefreshEndDateField() {
        addDateFieldToLayout(Call::getEndDate, Call::setEndDate, "endDate", 20, 3);
    }

    private void addOrRefreshDescriptionTextArea() {
        addTextAreaToLayout(Call::getDescription, Call::setDescription, "description", 13, 13, 1, 3);
    }

    private void addOrRefreshNotesTextArea() {
        addTextAreaToLayout(Call::getNotes, Call::setNotes, "notes", 14, 15, 1, 3);
    }

    private void addOrRefreshCallTypeComboBox() {
        addComboBoxToLayout(callTypeRepository, Call::getCallType, Call::setCallType, "callType", 0, 2);
    }

    private void addOrRefreshUserComboBox() {
        addComboBoxToLayout(usersRepository, Call::getOpenedByUser, null, "openBy", 10, 1);
    }

    private void addOrRefreshDriverComboBox() {
        addComboBoxToLayout(driverRepository, Call::getCurrentDriver, Call::setCurrentDriver, "driver", 18, 1);
    }

    // TODO: Generic method ?
    private void addOrRefreshScheduledOrderField() {
        CustomNumericField numericField = new CustomNumericField(
            null,
                call.getCurrentScheduledOrder(),
                0,
                99,
                null
        );
        numericField.addValueChangeListener(listener -> {
                call.setCurrentScheduledOrder(Integer.parseInt(numericField.getValue()));
                saveData();
        });
        numericField.setWidth(FIELDS_WIDTH);
        numericField.setEnabled(!call.isDeleted());
        int column = 1;
        int row = 19;
        this.layout.removeComponent(column, row);
        this.layout.addComponent(numericField, column, row);
        this.layout.removeComponent(column + 1, row);
        this.layout.addComponent(new CustomLabel("order", FIELDS_WIDTH), column + 1, row);
    }

    private void addCheckBoxFieldToLayout(
            ValueProvider<Call, Boolean> valueProvider,
            Setter<Call, Boolean> setter,
            String captionKey,
            int row, int column
    ) {
        CustomCheckBox checkBox = new CustomCheckBox(null, valueProvider.apply(call), null == setter);
        if (null != setter) {
            checkBox.addValueChangeListener(listener -> {
                        setter.accept(call, checkBox.getValue());
                        saveData();
                    }
            );
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
            int row, int column
    ) {
        CustomDateField dateField = new CustomDateField(valueProvider.apply(call));
        if (null != setter) {
            dateField.addValueChangeListener(listener -> {
                        setter.accept(call, new Date(dateField.getValue()));
                        saveData();
                    }
            );
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
            int row1, int row2, int column1, int column2
    ) {
        CustomTextArea textArea = new CustomTextArea(null, valueProvider.apply(call), null, null);
        if (null != setter) {
            textArea.addValueChangeListener(listener -> {
                        setter.accept(call, textArea.getValue());
                        saveData();
                    }
            );
        }
        textArea.setSizeFull();
        textArea.setEnabled(!call.isDeleted());
        textArea.setHeight("100px");
        this.layout.removeComponent(column1, row1);
        this.layout.addComponent(textArea, column1, row1, column2, row2);
        this.layout.removeComponent(column2 + 1, row1);
        this.layout.addComponent(new CustomLabel(captionKey, FIELDS_WIDTH), column2 + 1, row1);
    }

    private <T extends AbstractTypeWithNameAndActiveFields & BindRepository<T>> void addComboBoxToLayout(
            AbstractTypeWithNameAndActiveFieldsRepository<T> repository,
            ValueProvider<Call, T> valueProvider,
            Setter<Call, T> setter,
            String captionKey,
            int row, int column
    ) {
        CustomComboBox<T> comboBox = CustomComboBox.getComboBox(repository);
        T selected = valueProvider.apply(call);
        if (null != selected) {
            comboBox.setSelectedItem(selected);
        }
        if (null != setter) {
            comboBox.addValueChangeListener(listener -> {
                        setter.accept(call, comboBox.getValue());
                        saveData();
                    }
            );
        } else {
            comboBox.setReadOnly(true);
            comboBox.setEnabled(!call.isDeleted());
        }
        comboBox.setWidth(FIELDS_WIDTH);
        this.layout.removeComponent(column, row);
        this.layout.addComponent(comboBox, column, row);
        this.layout.removeComponent(column + 1, row);
        this.layout.addComponent(new CustomLabel(captionKey, FIELDS_WIDTH), column + 1, row);
    }
}
