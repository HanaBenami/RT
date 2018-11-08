package il.co.rtcohen.rt.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.BorderStyle;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import il.co.rtcohen.rt.MyErrorHandler;
import il.co.rtcohen.rt.UIcomponents;
import il.co.rtcohen.rt.dao.Site;
import il.co.rtcohen.rt.repositories.CallRepository;
import il.co.rtcohen.rt.repositories.GeneralRepository;
import il.co.rtcohen.rt.repositories.SiteRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

@UIScope
@SpringComponent
@SpringUI(path="/editsite")
@Theme("myTheme")
public class EditSiteUI extends AbstractUI {

    private GridLayout layout;
    private long siteId;
    private Site site;
    private ComboBox<Integer> areaCombo;
    private TextField name;
    private TextField siteNotes;
    private TextField phone;
    private TextField contact;
    private TextField address;
    private Button addButton;

    private SiteRepository siteRepository;

    @Autowired
    private EditSiteUI(ErrorHandler errorHandler, CallRepository callRepository, GeneralRepository generalRepository, SiteRepository siteRepository) {
        super(errorHandler,callRepository,generalRepository);
        this.siteRepository=siteRepository;
    }

    @Override
    protected void setupLayout() {
        getSelectedSite();
        if (site.getId()==0) {
            Notification.show("שגיאה",
                    "אתר #" + getPage().getUriFragment() + " לא קיים",
                    Notification.Type.WARNING_MESSAGE);
            JavaScript.getCurrent().execute(
                    "setTimeout(function() {self.close();},1000);");
        } else {
            layout = new GridLayout(4, 11);
            addButton();
            addData();
            printButton();
            deleteButton();
            VerticalLayout mainLayout = new VerticalLayout();
            mainLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
            mainLayout.addComponentsAndExpand(layout);
            setContent(mainLayout);
            setErrorHandler(new MyErrorHandler());
        }
    }

    private void getSelectedSite() {
        if ((getPage().getUriFragment()==null)||(getPage().getUriFragment().isEmpty())||getPage().getUriFragment().equals("0"))
            siteId = siteRepository.insertSite("",0,"",
                    0,"","","");
        else if (getPage().getUriFragment().matches("\\d+"))
            siteId = (Integer.parseInt(getPage().getUriFragment()));
        else
            siteId = 0;
        site = siteRepository.getSiteById(((int) siteId));
    }

    private void addData() {

        Label header = UIcomponents.smallHeader("פרטי אתר");
        layout.addComponent(header,3,0);

        //data
        TextField id = UIcomponents.textField(Integer.toString(site.getId()),
                false,130,40);
        layout.addComponent(id,2,0);

        //siteNotes
        Label notesLabel = new Label("הערות");
        layout.addComponent(notesLabel,3,5);
        siteNotes = UIcomponents.textField(site.getNotes(),true,410,30);
        siteNotes.addValueChangeListener(valueChangeEvent -> {
            site.setNotes(siteNotes.getValue());
            siteRepository.updateSite(site);
        });
        layout.addComponent(siteNotes,0,5,2,5);

        //phone
        Label phoneLabel = new Label("טלפון");
        layout.addComponent(phoneLabel,1,4);
        phone = UIcomponents.textField(site.getPhone(),true,130,30);
        phone.addValueChangeListener(valueChangeEvent -> {
            site.setPhone(phone.getValue());
            siteRepository.updateSite(site);
        });
        layout.addComponent(phone,0,4);

        //contact
        Label contactLabel = new Label("איש קשר");
        layout.addComponent(contactLabel,3,4);
        contact = UIcomponents.textField(site.getContact(),true,130,30);
        contact.addValueChangeListener(valueChangeEvent -> {
            site.setContact(contact.getValue());
            siteRepository.updateSite(site);
        });
        layout.addComponent(contact,2,4);

        //address
        Label addressLabel = new Label("כתובת");
        layout.addComponent(addressLabel,3,3);
        address = UIcomponents.textField(site.getAddress(),true,410,30);
        address.addValueChangeListener(valueChangeEvent -> {
            site.setAddress(address.getValue());
            siteRepository.updateSite(site);
        });
        layout.addComponent(address,0,3,2,3);

        //area
        areaCombo = new UIcomponents().areaComboBox(generalRepository,120,40);
        areaCombo.setEmptySelectionAllowed(false);
        areaCombo.setValue(site.getAreaId());
        areaCombo.addValueChangeListener( valueChangeEvent -> {
            if(areaCombo.getValue()==null) {
                site.setAreaId(0);
            }
            else {
                try {
                    site.setAreaId(areaCombo.getValue());
                    areaCombo.setComponentError(null);
                }
                catch (RuntimeException e) {
                    areaCombo.setComponentError(new UserError("יש לבחור אזור"));
                }
            }
            siteRepository.updateSite(site);
        });
        layout.addComponent(areaCombo,1,0);

        //site
        Label siteLabel = new Label("שם האתר");
        layout.addComponent(siteLabel,3,2);
        name = UIcomponents.textField(site.getName(),true,270,30);
        name.addValueChangeListener(valueChangeEvent -> {
            site.setName(name.getValue());
            siteRepository.updateSite(site);
        });
        layout.addComponent(name,1,2,2,2);

        //customer
        ComboBox<Integer> customerCombo = new UIcomponents()
                .customerComboBox(generalRepository,400,30);
        customerCombo.setEmptySelectionAllowed(false);
        customerCombo.setValue(site.getCustomerId());
        if(customerCombo.getValue()>0) {
            customerCombo.setEnabled(false);
            addButton.setEnabled(true);
        }
        else {
            customerCombo.setEnabled(true);
            addButton.setEnabled(false);
        }
        customerCombo.addValueChangeListener( valueChangeEvent -> {
            if(customerCombo.getValue()==null) {
                site.setCustomerId(0);
                addButton.setEnabled(false);
            }
            else {
                try {
                    site.setCustomerId(customerCombo.getValue());
                    customerCombo.setEnabled(false);
                    customerCombo.setComponentError(null);
                    addButton.setEnabled(true);
                }
                catch (RuntimeException e) {
                    customerCombo.setComponentError(new UserError("יש לבחור לקוח"));
                }
            }
            siteRepository.updateSite(site);
        });
        layout.addComponent(customerCombo,1,1,3,1);

        areaCombo.focus();
        areaCombo.setTabIndex(1);
        customerCombo.setTabIndex(2);
        name.setTabIndex(3);
        address.setTabIndex(4);
        contact.setTabIndex(5);
        phone.setTabIndex(6);
        siteNotes.setTabIndex(7);


        layout.setSpacing(true);
        layout.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);

    }

    private void printButton () {
        Button print = UIcomponents.printButton();
        print.addClickListener(clickEvent ->
                JavaScript.getCurrent().execute("print();"));
        layout.addComponent(print,0,0,0,0);
        layout.setComponentAlignment(print,Alignment.TOP_LEFT);
    }

    private void deleteButton() {
        Button delete = UIcomponents.trashButton();
        delete.addClickListener(clickEvent -> deleteSite());
        layout.addComponent(delete,0,1,0,1);
        layout.setComponentAlignment(delete,Alignment.TOP_LEFT);
    }

    private void deleteSite() {
        if (callRepository.getCallsBySite(site.getId()).size()>0) {
            Notification.show("לא ניתן למחוק אתר אליו משויכות קריאות",
                    "", Notification.Type.ERROR_MESSAGE);
        } else {
            int n = siteRepository.deleteSite(site.getId());
            if (n == 1) {
                Notification.show("האתר נמחק",
                        "", Notification.Type.WARNING_MESSAGE);
                JavaScript.getCurrent().execute(
                        "setTimeout(function() {self.close();},500);");
            }
        }
    }

    private void addButton() {
        addButton = UIcomponents.addButton();
        addButton.setWidth("300");
        addButton.addClickListener(clickEvent -> addCall());
        addButton.setTabIndex(8);
        layout.addComponent(addButton,0,7,3,7);
        layout.setComponentAlignment(addButton,Alignment.TOP_CENTER);
    }

    private void addCall() {
        long n = callRepository.insertCall(site.getCustomerId(),
                LocalDate.now(),site.getId());
        getPage().getCurrent().open("/editcall#"+String.valueOf(n),"_new3",
                700,750,
                BorderStyle.NONE);
        JavaScript.getCurrent().execute(
                "setTimeout(function() {self.close();},0);");
    }

}
