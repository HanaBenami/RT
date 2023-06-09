package il.co.rtcohen.rt.app.ui;

import com.vaadin.server.ErrorHandler;
import com.vaadin.server.Page;
import com.vaadin.server.UserError;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import il.co.rtcohen.rt.app.LanguageSettings;
import il.co.rtcohen.rt.app.uiComponents.UIComponents;
import il.co.rtcohen.rt.utils.NullPointerExceptionWrapper;
import il.co.rtcohen.rt.dal.dao.Call;
import il.co.rtcohen.rt.dal.dao.Contact;
import il.co.rtcohen.rt.utils.Date;
import il.co.rtcohen.rt.dal.repositories.*;
import il.co.rtcohen.rt.dal.services.CallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.ui.NumberField;

import java.sql.SQLException;
import java.time.LocalDate;

@SpringComponent
@SpringUI(path="/editCall")
public class EditCallUI extends AbstractEditUI {

    private final DriverRepository driverRepository;
    private final CallTypeRepository callTypeRepository;
    private Call call;
    private ComboBox<Integer> driverCombo;
    private NumberField order;
    private CheckBox done;
    private TextField area;
    private ComboBox<Integer> siteCombo;
    private ComboBox<Integer> carCombo;
    private ComboBox<Integer> callTypeCombo;
    private ComboBox<Integer> openByCombo;
    private DateField startDate;
    private DateField date1;
    private DateField date2;
    private DateField endDate;
    private TextField description;
    private TextField notes;
    private CheckBox here;
    private CheckBox meeting;
    private final CallService callService;
    private Grid<Contact> contactsGrid;

    @Autowired
    private EditCallUI(ErrorHandler errorHandler, CallRepository callRepository, GeneralRepository generalRepository,
                       SiteRepository siteRepository, CallService callService, ContactRepository contactRepository,
                       VehicleTypeRepository vehicleTypeRepository, DriverRepository driverRepository, CallTypeRepository callTypeRepository) {
        super(siteRepository, errorHandler, callRepository, generalRepository, contactRepository);
        this.callService = callService;
        this.driverRepository = driverRepository;
        this.callTypeRepository = callTypeRepository;
    }

    @Override
    protected void setupLayout() {
        getSelectedId();
        if (call.getId() == 0) {
            Notification.show(LanguageSettings.getLocaleString("error"),
                    LanguageSettings.getLocaleString("call#") + getPage().getUriFragment()
                            + LanguageSettings.getLocaleString("notExistF"),
                    Notification.Type.WARNING_MESSAGE);
            closeWindow();
        } else {
            initLayout(LanguageSettings.getLocaleString("callDetails"));
            addLayoutComponents();
            refreshData();
        }
    }

    @Override
    void getSelectedId() {
        if(hasParameter())
            if (selectedId().isPresent())
                selectedId = Integer.parseInt(selectedId().get().toString());
            else
                selectedId = 0;
            call = callRepository.getCallById(selectedId);
    }

    @Override
    void createNew() {
        selectedId = (int) (callRepository.insertCall(0,LocalDate.now(), getSessionUsernameId()));
        reloadNew();
    }

    @Override
    void reloadNew() {
        Page.getCurrent().open(UIPaths.EDITCALL.getPath()+String.valueOf(selectedId), "_new3");
    }

    private void addIdField() {
        TextField id = UIComponents.textField(Integer.toString(call.getId()),
                false,130,40);
        layout.addComponent(id,2,0);
    }
    private void addSiteNotesField() {
        siteNotes = UIComponents.textField(false,470,30);
        layout.addComponent(siteNotes,0,4, 2, 4);
    }
    private void addContactsGrid() {
        Label contactLabel = new Label(LanguageSettings.getLocaleString("contacts"));
        layout.addComponent(contactLabel,3,5);
        contactsGrid = new Grid<>(Contact.class);
        contactsGrid.setHeaderVisible(false);
        contactsGrid.setColumns("notes", "phone", "name");
        contactsGrid.setWidth("470");
        contactsGrid.setHeightByRows(3);
        layout.addComponent(contactsGrid,0,5, 2,5);
    }
    private void addAddressField() {
        address = UIComponents.textField(false,130,30);
        layout.addComponent(address,1,3);
    }
    private void addAreaField() {
        area = UIComponents.textField(false,130,30);
        layout.addComponent(area,0,3);
    }
    private void addSiteField() {
        Label siteLabel = new Label(LanguageSettings.getLocaleString("site"));
        layout.addComponent(siteLabel,3,3);
        siteCombo = new UIComponents().siteComboBox(generalRepository,130,30);
        if (null != call.getCustomer()) {
            try {
                siteCombo.setItems(siteRepository.getActiveIdByCustomer(call.getCustomer().getId()));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            if(call.getSite().getId()==0) {
                try {
                    siteCombo.setValue(siteRepository.getActiveIdByCustomer(call.getCustomer().getId()).get(0));
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        siteCombo.setValue(NullPointerExceptionWrapper.getWrapper(call, c -> c.getSite().getId(), 0));
        siteCombo.setEmptySelectionAllowed(false);
        siteCombo.addValueChangeListener(valueChangeEvent -> siteChange());
        layout.addComponent(siteCombo,2,3);
    }
    private void addHereField() {
        here = new CheckBox();
        here.setValue(call.isHere());
        here.setCaption(LanguageSettings.getLocaleString("currentlyHere"));
        here.addValueChangeListener(valueChangeEvent -> {
            if((!call.isHere())&&(here.getValue())) {
                call.setCurrentDriver(null);
                call.setCurrentScheduledDate(null);
            }
            call.setHere(here.getValue());
            callService.updateCall(call);
            refreshData();
        });
        layout.addComponent(here,1,2);
    }
    private void addCarTypeField() {
        Label carLabel = new Label(LanguageSettings.getLocaleString("vehicleType"));
        layout.addComponent(carLabel,3,2);
        carCombo = new UIComponents().carComboBox(generalRepository,130,30);
        carCombo.setEmptySelectionAllowed(true);
        carCombo.setValue(NullPointerExceptionWrapper.getWrapper(call, c -> c.getVehicle().getVehicleType().getId(), 0));
        carCombo.addValueChangeListener(valueChangeEvent -> {
            if (carCombo.getValue()==null) {
//                call.setCarTypeId(0); // TODO
            }
            else {
//                call.setCarTypeId(carCombo.getValue()); // TODO
            }
            callService.updateCall(call);
        });
        layout.addComponent(carCombo,2,2);
    }
    private void addCallTypeField() {
        callTypeCombo = new UIComponents().callTypeComboBox(generalRepository,130,40);
        callTypeCombo.setEmptySelectionAllowed(true);
        callTypeCombo.setValue(NullPointerExceptionWrapper.getWrapper(call, c -> c.getCallType().getId(), 0));
        callTypeCombo.addValueChangeListener(valueChangeEvent -> {
            if(callTypeCombo.getValue()==null) {
                call.setCallType(null);
            }
            else {
                call.setCallType(callTypeRepository.getItem(callTypeCombo.getValue())); }
            callService.updateCall(call);
        });
        layout.addComponent(callTypeCombo,1,0);
    }
    private void addCustomerField() {
        customerCombo = new UIComponents().customerComboBox(generalRepository,470,30);
        customerCombo.setEmptySelectionAllowed(false);
        customerCombo.setValue(NullPointerExceptionWrapper.getWrapper(call, c -> c.getCustomer().getId(), 0));
        if(customerCombo.getValue()==0)
            customerCombo.setComponentError(
                    new UserError(LanguageSettings.getLocaleString("pleaseSelectCustomer")));
        customerCombo.addValueChangeListener( valueChangeEvent -> customerChange());
        layout.addComponent(customerCombo,1,1,3,1);
    }
    private void addStartDateField() {
        Label start = new Label(LanguageSettings.getLocaleString("startDate"));
        layout.addComponent(start,3,6);
        startDate = UIComponents.dateField(130,30);
        if(null == call.getStartDate())
            startDate.setValue(null);
        else
            startDate.setValue(call.getStartDate().getLocalDate());
        startDate.addValueChangeListener(valueChangeEvent -> {
            call.setStartDate(new Date(startDate.getValue()));
            callService.updateCall(call);
        });
        layout.addComponent(startDate,2,6);
    }
    private void addDate1Field() {
        Label date1Label = new Label(LanguageSettings.getLocaleString("date1"));
        layout.addComponent(date1Label,1,6);
        date1 = UIComponents.dateField(130,30);
        if(null == call.getPlanningDate())
            date1.setValue(null);
        else
            date1.setValue(call.getPlanningDate().getLocalDate());
        date1.addValueChangeListener(valueChangeEvent -> {
            call.setPlanningDate(new Date(date1.getValue()));
            callService.updateCall(call);
        });
        layout.addComponent(date1,0,6);
    }
    private void addDescriptionField() {
        Label descriptionLabel = new Label(LanguageSettings.getLocaleString("description"));
        layout.addComponent(descriptionLabel,3,7);
        description = UIComponents.textField(true,470,50);
        description.setValue(call.getDescription());
        description.addValueChangeListener(valueChangeEvent -> {
            call.setDescription(description.getValue());
            callService.updateCall(call);
        });
        layout.addComponent(description,0,7,2,7);
    }
    private void addNotesField() {
        Label notesLabel = new Label(LanguageSettings.getLocaleString("notes"));
        layout.addComponent(notesLabel,3,8);
        notes = UIComponents.textField(true,470,50);
        notes.setValue(call.getNotes());
        notes.addValueChangeListener(valueChangeEvent -> {
            call.setNotes(notes.getValue());
            callService.updateCall(call);
        });
        layout.addComponent(notes,0,8,2,8);
    }

    private void addOpenByField() {
        Label openByLabel = new Label(LanguageSettings.getLocaleString("openBy"));
        layout.addComponent(openByLabel,3,9);
        openByCombo = new UIComponents().userComboBox(generalRepository, 130, 30);
        openByCombo.setValue(call.getUser().getId());
        openByCombo.setEnabled(false);
        layout.addComponent(openByCombo,2,9);
    }

    private void addDate2Field() {
        Label date2Label = new Label(LanguageSettings.getLocaleString("date2"));
        layout.addComponent(date2Label,3,11);
        date2 = UIComponents.dateField(130,30);
        if(null == call.getCurrentScheduledDate())
            date2.setValue(null);
        else
            date2.setValue(call.getCurrentScheduledDate().getLocalDate());
        date2.addValueChangeListener(valueChangeEvent -> {
            call.setCurrentScheduledDate(new Date(date2.getValue()));
            callService.updateCall(call);
            refreshData();
        });
        layout.addComponent(date2,2,11);
    }

    private void addDriverField() {
        driverCombo = new UIComponents().driverComboBox(generalRepository,130,30);
        driverCombo.setValue(NullPointerExceptionWrapper.getWrapper(call, c->c.getCurrentDriver().getId(), 0));
        driverCombo.addValueChangeListener(valueChangeEvent -> {
            if(driverCombo.getValue()==0)
                call.setCurrentDriver(null);
            else
                call.setCurrentDriver(driverRepository.getItem(driverCombo.getValue()));
            callService.updateCall(call);
            refreshData();
        });
        layout.addComponent(driverCombo,1,11);
    }
    private void addOrderField() {
        order = UIComponents.numberField("130","30");
        order.setValue(String.valueOf(call.getCurrentScheduledOrder()));
        order.addValueChangeListener(valueChangeEvent -> {
            if (order.getValue().matches("\\d+")) {
                call.setCurrentScheduledOrder(Integer.parseInt(order.getValue()));
                callService.updateCall(call);
                refreshData();
            }
        });
        layout.addComponent(order,0,11);
    }

    private void addMeetingField() {
        meeting = UIComponents.checkBox(call.isMeeting(),LanguageSettings.getLocaleString("meeting"));
        meeting.addValueChangeListener(valueChangeEvent -> {
            call.setMeeting(meeting.getValue());
            callService.updateCall(call);
        });
        layout.addComponent(meeting,1,12);
    }

    private void addEndDateField() {
        Label endDateLabel = new Label(LanguageSettings.getLocaleString("endDate"));
        layout.addComponent(endDateLabel,3,12);
        endDate = UIComponents.dateField(130,30);
        if(null == call.getEndDate())
            endDate.setValue(null);
        else
            endDate.setValue(call.getEndDate().getLocalDate());
        endDate.addValueChangeListener(valueChangeEvent -> {
            call.setEndDate(new Date(endDate.getValue()));
            callService.updateCall(call);
            refreshData();
        });
        layout.addComponent(endDate,2,12);
    }

    private void addDoneField() {
        done = UIComponents.checkBox(call.isDone(),LanguageSettings.getLocaleString("done"),true);
        layout.addComponent(done,0,12);
    }

    @Override
    void addFields() {
        addIdField();
        addSiteNotesField();
        addContactsGrid();
        addAddressField();
        addAreaField();
        addSiteField();
        addHereField();
        addCarTypeField();
        addCallTypeField();
        addCustomerField();
        addStartDateField();
        addDate1Field();
        addDescriptionField();
        addOpenByField();
        layout.addComponent(UIComponents.smallHeader(LanguageSettings.getLocaleString("scheduleDetails")),3,10);
        addNotesField();
        addMeetingField();
        addDate2Field();
        addDriverField();
        addOrderField();
        addEndDateField();
        addDoneField();
    }

    @Override
    void setTabIndexes() {
        callTypeCombo.focus();
        callTypeCombo.setTabIndex(1);
        customerCombo.setTabIndex(2);
        carCombo.setTabIndex(3);
        here.setTabIndex(4);
        siteCombo.setTabIndex(5);
        startDate.setTabIndex(6);
        date1.setTabIndex(7);
        description.setTabIndex(9);
        notes.setTabIndex(10);
        date2.setTabIndex(11);
        driverCombo.setTabIndex(12);
        endDate.setTabIndex(13);
        meeting.setTabIndex(14);
    }

    void disableAll() {
        callTypeCombo.setEnabled(false);
        callTypeCombo.setEnabled(false);
        customerCombo.setEnabled(false);
        carCombo.setEnabled(false);
        here.setEnabled(false);
        siteCombo.setEnabled(false);
        startDate.setEnabled(false);
        date1.setEnabled(false);
        description.setEnabled(false);
        notes.setEnabled(false);
        date2.setEnabled(false);
        driverCombo.setEnabled(false);
        endDate.setEnabled(false);
        meeting.setEnabled(false);
        deleteBtn.setEnabled(false);
    }

    private void refreshData() {
        siteNotes.setValue(NullPointerExceptionWrapper.getWrapper(call, c -> c.getSite().getNotes(), ""));
        address.setValue(siteRepository.getSiteById(call.getSite().getId()).getAddress());
        area.setValue(generalRepository.getNameById(siteRepository.getSiteById(call.getSite().getId()).getArea().getId(),"area"));
        try {
            contactsGrid.setItems(contactRepository.getContactsBySite(call.getSite().getId(), true));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        driverCombo.setValue(call.getCurrentDriver().getId());
        openByCombo.setValue(call.getUser().getId());
        order.setValue(String.valueOf(call.getCurrentScheduledOrder()));
        done.setValue(call.isDone());
        if (call.isDeleted()) {
            Label deleteLabel = new Label(LanguageSettings.getLocaleString("callDeleted"));
            deleteLabel.setStyleName("LABEL-RIGHT-RED");
            layout.addComponent(deleteLabel, 0, 9, 1, 9);
            disableAll();
        }
    }

    @Override
    void deleteCurrentId() {
        if (call.getCurrentScheduledOrder() > 0) {
            Notification.show(LanguageSettings.getLocaleString("scheduledCallDeleteError"),
                    "", Notification.Type.ERROR_MESSAGE);
        } else {
            call.setDeleted(true);
            callService.updateCall(call);
            refreshData();
//            int n = callRepository.deleteCall(call.getId());
//            if (n == 1) {
//                Notification.show(LanguageSettings.getLocaleString("callDeleted"),
//                        "", Notification.Type.WARNING_MESSAGE);
//                closeWindow();
//            }
        }
    }

    private void customerChange() {
        // TODO
//        if(customerCombo.getValue()==null) {
//            call.setCustomerId(0);
//        }
//        else {
//            try {
//                call.setCustomerId(customerCombo.getValue());
//                customerCombo.setComponentError(null);
//            }
//            catch (RuntimeException e) {
//                customerCombo.setComponentError(
//                        new UserError(LanguageSettings.getLocaleString("pleaseSelectCustomer")));
//            }
//        }
//        callService.updateCall(call);
//        siteCombo.setValue(0);
//        try {
//            siteCombo.setItems(siteRepository.getActiveIdByCustomer(customerCombo.getValue()));
//            if(call.getSiteId()==0)
//                siteCombo.setValue(siteRepository.getActiveIdByCustomer(call.getCustomerId()).get(0));
//            else
//                siteCombo.setValue(call.getSiteId());
//            customerCombo.setComponentError(null);
//        }
//        catch (RuntimeException e) {
//            customerCombo.setComponentError(
//                    new UserError(LanguageSettings.getLocaleString("pleaseSelectCustomer")));
//        }
//        refreshData();
    }

    private void siteChange() {
        // TODO
//        if(siteCombo.getValue()==null) {
//            call.setSiteId(0);
//        }
//        else {
//            call.setSiteId(siteCombo.getValue());
//        }
//        callService.updateCall(call);
//        refreshData();
    }

}
