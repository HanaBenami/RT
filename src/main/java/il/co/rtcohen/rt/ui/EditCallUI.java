package il.co.rtcohen.rt.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.UserError;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import il.co.rtcohen.rt.MyErrorHandler;
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

    @Autowired
    private EditCallUI(ErrorHandler errorHandler, CallRepository callRepository, GeneralRepository generalRepository, SiteRepository siteRepository) {
        super(siteRepository,errorHandler,callRepository,generalRepository);
    }

    @Override
    protected void setupLayout() {
        getCall();
        if (call.getId()==0) {
            Notification.show("שגיאה",
                    "קריאה #" + getPage().getUriFragment() + " לא קיימת",
                    Notification.Type.WARNING_MESSAGE);
            JavaScript.getCurrent().execute(
                    "setTimeout(function() {self.close();},1000);");
        } else {
            layout = new GridLayout(4, 11);
            addData();
            printButton();
            deleteButton();
            VerticalLayout mainLayout = new VerticalLayout();
            mainLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
            mainLayout.addComponentsAndExpand(layout);
            setContent(mainLayout);
            refresh_call();
        }
    }

    private void getCall () {
        long callId;
        if ((getPage().getUriFragment()==null)||(getPage().getUriFragment().isEmpty())
                ||getPage().getUriFragment().equals("0"))
            callId = callRepository.insertCall(0,LocalDate.now());
        else if (getPage().getUriFragment().matches("\\d+"))
            callId = (Integer.parseInt(getPage().getUriFragment()));
        else
            callId = 0;
        call = callRepository.getCallById(((int) callId));
    }

    private void addData() {

        layout.addComponent(UIcomponents.smallHeader("פרטי קריאה"),3,0);

        //data
        TextField id = UIcomponents.textField(Integer.toString(call.getId()),
                false,130,40);
        layout.addComponent(id,2,0);

        //siteNotes
        siteNotes = UIcomponents.textField(false,130,30);
        layout.addComponent(siteNotes,0,4);

        //phone
        phone = UIcomponents.textField(false,130,30);
        layout.addComponent(phone,1,4);

        //contact
        Label contactLabel = new Label("איש קשר");
        layout.addComponent(contactLabel,3,4);
        contact = UIcomponents.textField(false,130,30);
        layout.addComponent(contact,2,4);

        //address
        address = UIcomponents.textField(false,130,30);
        layout.addComponent(address,1,3);

        //area
        area = UIcomponents.textField(false,130,30);
        layout.addComponent(area,0,3);

        //site
        Label sitetLabel = new Label("אתר");
        layout.addComponent(sitetLabel,3,3);
        ComboBox<Integer> siteCombo = new UIcomponents().siteComboBox(generalRepository,130,30);
        if(call.getCustomerId()>0)
            siteCombo.setItems(siteRepository.getActiveIdByCustomer(call.getCustomerId()));
        siteCombo.setValue(call.getSiteId());
        siteCombo.setEmptySelectionAllowed(true);
        siteCombo.addValueChangeListener(valueChangeEvent -> {
            if(siteCombo.getValue()==null) {
                call.setSiteId(0);
            }
            else {
                call.setSiteId(siteCombo.getValue());
            }
            callRepository.updateCall(call);
            refresh_call();
        });
        layout.addComponent(siteCombo,2,3);

        //here
        CheckBox here = new CheckBox();
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

        //cartype
        Label carLabel = new Label("סוג כלי");
        layout.addComponent(carLabel,3,2);

        ComboBox<Integer> carCombo = new UIcomponents().carComboBox(generalRepository,130,30);
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

        ComboBox<Integer> callTypeCombo = new UIcomponents().callTypeComboBox(generalRepository,130,40);
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

        //customer
        ComboBox<Integer> customerCombo = new UIcomponents().customerComboBox(generalRepository,400,30);
        customerCombo.setEmptySelectionAllowed(false);
        customerCombo.setValue(call.getCustomerId());
        customerCombo.addValueChangeListener( valueChangeEvent -> {
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
        });
        layout.addComponent(customerCombo,1,1,3,1);

        //startdate
        Label start = new Label("תאריך פתיחה");
        layout.addComponent(start,3,5);
        DateField startDate = UIcomponents.dateField(130,30);
        if(Call.nullDate.equals(call.getStartDate()))
            startDate.setValue(null);
        else
            startDate.setValue(call.getStartDate());
        startDate.addValueChangeListener(valueChangeEvent -> {
            call.setStartDate(startDate.getValue());
            callRepository.updateCall(call);
        });
        layout.addComponent(startDate,2,5);

        //date1
        Label date1Label = new Label("תאריך מתוכנן");
        layout.addComponent(date1Label,1,5);
        DateField date1 = UIcomponents.dateField(130,30);
        if(Call.nullDate.equals(call.getDate1()))
            date1.setValue(null);
        else
            date1.setValue(call.getDate1());
        date1.addValueChangeListener(valueChangeEvent -> {
            call.setDate1(date1.getValue());
            callRepository.updateCall(call);
        });
        layout.addComponent(date1,0,5);

        //descr
        Label descriptionLabel = new Label("תיאור");
        layout.addComponent(descriptionLabel,3,6);
        layout.setComponentAlignment(descriptionLabel,Alignment.TOP_LEFT);
        TextField description = UIcomponents.textField(true,400,50);
        description.setValue(call.getDescription());
        description.addValueChangeListener(valueChangeEvent -> {
            call.setDescription(description.getValue());
            callRepository.updateCall(call);
        });
        layout.addComponent(description,0,6,2,6);

        //notes
        Label notesLabel = new Label("הערות");
        layout.addComponent(notesLabel,3,7);
        layout.setComponentAlignment(notesLabel,Alignment.TOP_LEFT);
        TextField notes = UIcomponents.textField(true,400,50);
        notes.setValue(call.getNotes());
        notes.addValueChangeListener(valueChangeEvent -> {
            call.setNotes(notes.getValue());
            callRepository.updateCall(call);
        });
        layout.addComponent(notes,0,7,2,7);

        //plan
        layout.addComponent(UIcomponents.smallHeader("פרטי שיבוץ"),3,8);

        //meeting
        CheckBox meeting = UIcomponents.checkBox(call.isMeeting(),"תואם מראש");
        meeting.addValueChangeListener(valueChangeEvent -> {
            call.setMeeting(meeting.getValue());
            callRepository.updateCall(call);
        });
        layout.addComponent(meeting,1,10);

        //date2
        Label date2Label = new Label("תאריך שיבוץ");
        layout.addComponent(date2Label,3,9);
        DateField date2 = UIcomponents.dateField(130,30);
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

        //driver
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

        //order
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

        //enddate
        Label rndDateLabel = new Label("תאריך סיום");
        layout.addComponent(rndDateLabel,3,10);
        DateField endDate = UIcomponents.dateField(130,30);
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

        //done
        done = UIcomponents.checkBox(call.isDone(),"בוצע",true);
        layout.addComponent(done,0,10);

        //focus and tabs
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

        layout.setSpacing(true);
        layout.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);

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

    private void deleteButton() {
        Button delete = UIcomponents.trashButton();
        delete.addClickListener(clickEvent -> deleteCall());
        layout.addComponent(delete,0,1,0,1);
        layout.setComponentAlignment(delete,Alignment.TOP_LEFT);
    }

    private void deleteCall() {
        if (call.getOrder()>0) {
            Notification.show("לא ניתן למחוק קריאה שמשובצת לסידור העבודה",
                    "", Notification.Type.ERROR_MESSAGE);
        } else {
            int n = callRepository.deleteCall(call.getId());
            if (n == 1) {
                Notification.show("הקריאה נמחקה",
                        "", Notification.Type.WARNING_MESSAGE);
                JavaScript.getCurrent().execute(
                        "setTimeout(function() {self.close();},500);");
            }
        }
    }
}
