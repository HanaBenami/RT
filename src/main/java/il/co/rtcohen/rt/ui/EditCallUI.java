package il.co.rtcohen.rt.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.UserError;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import il.co.rtcohen.rt.UIcomponents;
import il.co.rtcohen.rt.dao.Call;
import il.co.rtcohen.rt.repositories.CallRepository;
import il.co.rtcohen.rt.repositories.GeneralRepository;
import il.co.rtcohen.rt.repositories.SiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.ui.NumberField;
import java.time.LocalDate;

@UIScope
@SpringComponent
@SpringUI(path="/editcall")
@Theme("myTheme")
public class EditCallUI extends AbstractEditUI {

    private Call call;
    private ComboBox<Integer> driverCombo;
    private NumberField order;
    private CheckBox done;
    private TextField area;
    private ComboBox<Integer> siteCombo;
    private ComboBox<Integer> carCombo;
    private ComboBox<Integer> callTypeCombo;
    private DateField startDate;
    private DateField date1;
    private DateField date2;
    private DateField endDate;
    private TextField description;
    private TextField notes;
    private CheckBox here;
    private CheckBox meeting;

    @Autowired
    private EditCallUI(ErrorHandler errorHandler, CallRepository callRepository, GeneralRepository generalRepository, SiteRepository siteRepository) {
        super(siteRepository,errorHandler,callRepository,generalRepository);
    }

    @Override
    protected void setupLayout() {
        getSelected();
        if (call.getId()==0) {
            Notification.show("שגיאה",
                    "קריאה #" + getPage().getUriFragment() + " לא קיימת",
                    Notification.Type.WARNING_MESSAGE);
            closeWindow();
        } else {
            startLayout("פרטי קריאה");
            continueLayout();
            setContent(mainLayout());
            refresh_call();
        }
    }

    @Override
    void getSelected() {
        int callId;
        if (noParameter())
            callId = (int) (callRepository.insertCall(0,LocalDate.now()));
        else if (selectedId().isPresent())
            callId = Integer.parseInt(selectedId().get().toString());
        else
            callId = 0;
        call = callRepository.getCallById(callId);
    }

    private void id() {
        TextField id = UIcomponents.textField(Integer.toString(call.getId()),
                false,130,40);
        layout.addComponent(id,2,0);
    }
    private void siteNotes() {
        siteNotes = UIcomponents.textField(false,130,30);
        layout.addComponent(siteNotes,0,4);
    }
    private void phone() {
        phone = UIcomponents.textField(false,130,30);
        layout.addComponent(phone,1,4);
    }
    private void contact() {
        Label contactLabel = new Label("איש קשר");
        layout.addComponent(contactLabel,3,4);
        contact = UIcomponents.textField(false,130,30);
        layout.addComponent(contact,2,4);
    }
    private void address() {
        address = UIcomponents.textField(false,130,30);
        layout.addComponent(address,1,3);
    }
    private void area() {
        area = UIcomponents.textField(false,130,30);
        layout.addComponent(area,0,3);
    }
    private void site() {
        Label siteLabel = new Label("אתר");
        layout.addComponent(siteLabel,3,3);
        siteCombo = new UIcomponents().siteComboBox(generalRepository,130,30);
        if(call.getCustomerId()>0)
            siteCombo.setItems(siteRepository.getActiveIdByCustomer(call.getCustomerId()));
        siteCombo.setValue(call.getSiteId());
        siteCombo.setEmptySelectionAllowed(true);
        siteCombo.addValueChangeListener(valueChangeEvent -> siteChange());
        layout.addComponent(siteCombo,2,3);
    }
    private void here() {
        here = new CheckBox();
        here.setValue(call.isHere());
        here.setCaption("נמצא כאן");
        here.addValueChangeListener(valueChangeEvent -> {
            if((!call.isHere())&&(here.getValue())) {
                call.setDriverID(0);
                call.setDate2(Call.nullDate);
            }
            call.setHere(here.getValue());
            callRepository.updateCall(call);
            refresh_call();
        });
        layout.addComponent(here,1,2);
    }
    private void carType() {
        Label carLabel = new Label("סוג כלי");
        layout.addComponent(carLabel,3,2);
        carCombo = new UIcomponents().carComboBox(generalRepository,130,30);
        carCombo.setEmptySelectionAllowed(true);
        carCombo.setValue(call.getCarTypeId());
        carCombo.addValueChangeListener(valueChangeEvent -> {
            if(carCombo.getValue()==null) {
                call.setCarTypeId(0);
            }
            else {
                call.setCarTypeId(carCombo.getValue()); }
            callRepository.updateCall(call);
        });
        layout.addComponent(carCombo,2,2);
    }
    private void callType() {
        callTypeCombo = new UIcomponents().callTypeComboBox(generalRepository,130,40);
        callTypeCombo.setEmptySelectionAllowed(true);
        callTypeCombo.setValue(call.getCallTypeId());
        callTypeCombo.addValueChangeListener(valueChangeEvent -> {
            if(callTypeCombo.getValue()==null) {
                call.setCallTypeId(0);
            }
            else {
                call.setCallTypeId(callTypeCombo.getValue()); }
            callRepository.updateCall(call);
        });
        layout.addComponent(callTypeCombo,1,0);
    }
    private void customer() {
        customerCombo = new UIcomponents().customerComboBox(generalRepository,400,30);
        customerCombo.setEmptySelectionAllowed(false);
        customerCombo.setValue(call.getCustomerId());
        customerCombo.addValueChangeListener( valueChangeEvent -> customerChange());
        layout.addComponent(customerCombo,1,1,3,1);
    }
    private void startDate() {
        Label start = new Label("תאריך פתיחה");
        layout.addComponent(start,3,5);
        startDate = UIcomponents.dateField(130,30);
        if(Call.nullDate.equals(call.getStartDate()))
            startDate.setValue(null);
        else
            startDate.setValue(call.getStartDate());
        startDate.addValueChangeListener(valueChangeEvent -> {
            call.setStartDate(startDate.getValue());
            callRepository.updateCall(call);
        });
        layout.addComponent(startDate,2,5);
    }
    private void date1() {
        Label date1Label = new Label("תאריך מתוכנן");
        layout.addComponent(date1Label,1,5);
        date1 = UIcomponents.dateField(130,30);
        if(Call.nullDate.equals(call.getDate1()))
            date1.setValue(null);
        else
            date1.setValue(call.getDate1());
        date1.addValueChangeListener(valueChangeEvent -> {
            call.setDate1(date1.getValue());
            callRepository.updateCall(call);
        });
        layout.addComponent(date1,0,5);
    }
    private void description() {
        Label descriptionLabel = new Label("תיאור");
        layout.addComponent(descriptionLabel,3,6);
        layout.setComponentAlignment(descriptionLabel,Alignment.TOP_LEFT);
        description = UIcomponents.textField(true,400,50);
        description.setValue(call.getDescription());
        description.addValueChangeListener(valueChangeEvent -> {
            call.setDescription(description.getValue());
            callRepository.updateCall(call);
        });
        layout.addComponent(description,0,6,2,6);
    }
    private void notes() {
        Label notesLabel = new Label("הערות");
        layout.addComponent(notesLabel,3,7);
        layout.setComponentAlignment(notesLabel,Alignment.TOP_LEFT);
        notes = UIcomponents.textField(true,400,50);
        notes.setValue(call.getNotes());
        notes.addValueChangeListener(valueChangeEvent -> {
            call.setNotes(notes.getValue());
            callRepository.updateCall(call);
        });
        layout.addComponent(notes,0,7,2,7);
    }
    private void meeting() {
        meeting = UIcomponents.checkBox(call.isMeeting(),"תואם מראש");
        meeting.addValueChangeListener(valueChangeEvent -> {
            call.setMeeting(meeting.getValue());
            callRepository.updateCall(call);
        });
        layout.addComponent(meeting,1,10);
    }
    private void date2() {
        Label date2Label = new Label("תאריך שיבוץ");
        layout.addComponent(date2Label,3,9);
        date2 = UIcomponents.dateField(130,30);
        if(Call.nullDate.equals(call.getDate2()))
            date2.setValue(null);
        else
            date2.setValue(call.getDate2());
        date2.addValueChangeListener(valueChangeEvent -> {
            call.setDate2(date2.getValue());
            callRepository.updateCall(call);
            refresh_call();
        });
        layout.addComponent(date2,2,9);
    }
    private void driver() {
        driverCombo = new UIcomponents().driverComboBox(generalRepository,130,30);
        driverCombo.setValue(call.getDriverId());
        driverCombo.addValueChangeListener(valueChangeEvent -> {
            if(driverCombo.getValue()==0)
                call.setDriverID(0);
            else
                call.setDriverID(driverCombo.getValue());
            callRepository.updateCall(call);
            refresh_call();
        });
        layout.addComponent(driverCombo,1,9);
    }
    private void order() {
        order = UIcomponents.numberField("130","30");
        order.setValue(String.valueOf(call.getOrder()));
        order.addValueChangeListener(valueChangeEvent -> {
            if (order.getValue().matches("\\d+")) {
                call.setOrder(Integer.parseInt(order.getValue()));
                callRepository.updateCall(call);
                refresh_call();
            }
        });
        layout.addComponent(order,0,9);
    }
    private void endDate() {
        Label endDateLabel = new Label("תאריך סיום");
        layout.addComponent(endDateLabel,3,10);
        endDate = UIcomponents.dateField(130,30);
        if(Call.nullDate.equals(call.getEndDate()))
            endDate.setValue(null);
        else
            endDate.setValue(call.getEndDate());
        endDate.addValueChangeListener(valueChangeEvent -> {
            call.setEndDate(endDate.getValue());
            callRepository.updateCall(call);
            refresh_call();
        });
        layout.addComponent(endDate,2,10);
    }
    private void done() {
        done = UIcomponents.checkBox(call.isDone(),"בוצע",true);
        layout.addComponent(done,0,10);
    }

    @Override
    void addData() {
        id();
        siteNotes();
        phone();
        contact();
        address();
        area();
        site();
        here();
        carType();
        callType();
        customer();
        startDate();
        date1();
        description();
        layout.addComponent(UIcomponents.smallHeader("פרטי שיבוץ"),3,8);
        notes();
        meeting();
        date2();
        driver();
        order();
        endDate();
        done();
    }

    @Override
    void tabIndexes() {
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

    private void  refresh_call () {
        siteNotes.setValue(siteRepository.getSiteById(call.getSiteId()).getNotes());
        phone.setValue(siteRepository.getSiteById(call.getSiteId()).getPhone());
        contact.setValue(siteRepository.getSiteById(call.getSiteId()).getContact());
        address.setValue(siteRepository.getSiteById(call.getSiteId()).getAddress());
        area.setValue(generalRepository.getNameById(siteRepository.getSiteById(call.getSiteId()).getAreaId()
                ,"area"));
        driverCombo.setValue(call.getDriverId());
        order.setValue(String.valueOf(call.getOrder()));
        done.setValue(call.isDone());
    }

    @Override
    void deleteId() {
        if (call.getOrder()>0) {
            Notification.show("לא ניתן למחוק קריאה שמשובצת לסידור העבודה",
                    "", Notification.Type.ERROR_MESSAGE);
        } else {
            int n = callRepository.deleteCall(call.getId());
            if (n == 1) {
                Notification.show("הקריאה נמחקה",
                        "", Notification.Type.WARNING_MESSAGE);
                closeWindow();
            }
        }
    }

    void customerChange() {
        if(customerCombo.getValue()==null) {
            call.setCustomerId(0);
        }
        else {
            try {
                call.setCustomerId(customerCombo.getValue());
                customerCombo.setComponentError(null);
            }
            catch (RuntimeException e) {
                customerCombo.setComponentError(
                        new UserError("יש לבחור לקוח"));
            }
        }
        callRepository.updateCall(call);
        siteCombo.setValue(0);
        try {
            siteCombo.setItems(siteRepository.getActiveIdByCustomer(customerCombo.getValue()));
            siteCombo.setValue(call.getSiteId());
            customerCombo.setComponentError(null);
        }
        catch (RuntimeException e) {
            customerCombo.setComponentError(
                    new UserError("יש לבחור לקוח"));
        }
        refresh_call();
    }
    private void siteChange() {
        if(siteCombo.getValue()==null) {
            call.setSiteId(0);
        }
        else {
            call.setSiteId(siteCombo.getValue());
        }
        callRepository.updateCall(call);
        refresh_call();
    }
}
