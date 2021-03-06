package il.co.rtcohen.rt.app.ui;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.Page;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.BorderStyle;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import il.co.rtcohen.rt.app.LanguageSettings;
import il.co.rtcohen.rt.app.UIComponents;
import il.co.rtcohen.rt.dal.dao.Site;
import il.co.rtcohen.rt.dal.repositories.CallRepository;
import il.co.rtcohen.rt.dal.repositories.GeneralRepository;
import il.co.rtcohen.rt.dal.repositories.SiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDate;

@SpringComponent
@SpringUI(path="/editSite")
public class EditSiteUI extends AbstractEditUI {

    private Site site;
    private ComboBox<Integer> areaCombo;
    private TextField name;
    private Button addButton;

    @Autowired
    private EditSiteUI(ErrorHandler errorHandler, CallRepository callRepository, GeneralRepository generalRepository, SiteRepository siteRepository) {
        super(siteRepository,errorHandler,callRepository,generalRepository);
    }

    @Override
    protected void setupLayout() {
        getSelectedId();
        if (site.getId()==0) {
            Notification.show(LanguageSettings.getLocaleString("error"), LanguageSettings.getLocaleString("site#")
                            + getPage().getUriFragment() + LanguageSettings.getLocaleString("notExistM"),
                    Notification.Type.WARNING_MESSAGE);
            closeWindow();
        } else {
            initLayout(LanguageSettings.getLocaleString("siteDetails"));
            addAddButton();
            addLayoutComponents();
        }
    }

    @Override
    void getSelectedId() {
        if(hasParameter())
            if (selectedId().isPresent())
                selectedId = Integer.parseInt(selectedId().get().toString());
            else
                selectedId = 0;
            site = siteRepository.getSiteById(selectedId);
    }

    @Override
    void createNew() {
        selectedId = (int) siteRepository.insertSite("",0,"",
                0,"","","");
        reloadNew();
    }

    @Override
    void reloadNew() {
        Page.getCurrent().open(UIPaths.EDITSITE.getPath()+String.valueOf(selectedId), "_new2");
    }

    private void addIdField() {
        TextField id = UIComponents.textField(Integer.toString(site.getId()),
                false,130,40);
        layout.addComponent(id,2,0);
    }
    private void addPhoneField() {
        Label phoneLabel = new Label(LanguageSettings.getLocaleString("phone"));
        layout.addComponent(phoneLabel, 1, 4);
        phone = UIComponents.textField(site.getPhone(), true, 130, 30);
        phone.addValueChangeListener(valueChangeEvent -> {
            site.setPhone(phone.getValue());
            siteRepository.updateSite(site);
        });
        layout.addComponent(phone, 0, 4);
    }
    private void addSiteNotesField() {
        Label notesLabel = new Label(LanguageSettings.getLocaleString("notes"));
        layout.addComponent(notesLabel,3,5);
        siteNotes = UIComponents.textField(site.getNotes(),true,410,30);
        siteNotes.addValueChangeListener(valueChangeEvent -> {
            site.setNotes(siteNotes.getValue());
            siteRepository.updateSite(site);
        });
        layout.addComponent(siteNotes,0,5,2,5);
    }
    private void addContactField() {
        Label contactLabel = new Label(LanguageSettings.getLocaleString("contact"));
        layout.addComponent(contactLabel,3,4);
        contact = UIComponents.textField(site.getContact(),true,130,30);
        contact.addValueChangeListener(valueChangeEvent -> {
            site.setContact(contact.getValue());
            siteRepository.updateSite(site);
        });
        layout.addComponent(contact,2,4);
    }
    private void addAddressField() {
        Label addressLabel = new Label(LanguageSettings.getLocaleString("address"));
        layout.addComponent(addressLabel,3,3);
        address = UIComponents.textField(site.getAddress(),true,410,30);
        address.addValueChangeListener(valueChangeEvent -> {
            site.setAddress(address.getValue());
            siteRepository.updateSite(site);
        });
        layout.addComponent(address,0,3,2,3);
    }
    private void addAreaField() {
        areaCombo = new UIComponents().areaComboBox(generalRepository,120,40);
        areaCombo.setEmptySelectionAllowed(false);
        areaCombo.setValue(site.getAreaId());
        areaCombo.addValueChangeListener(valueChangeEvent -> areaChange());
        layout.addComponent(areaCombo,1,0);
    }
    private void addSiteNameField() {
        Label siteLabel = new Label(LanguageSettings.getLocaleString("siteName"));
        layout.addComponent(siteLabel,3,2);
        name = UIComponents.textField(site.getName(),true,270,30);
        name.addValueChangeListener(valueChangeEvent -> siteNameChange());
        layout.addComponent(name,1,2,2,2);
    }

    private void siteNameChange() {
        site.setName(name.getValue());
        siteRepository.updateSite(site);
        if(name.isEmpty()) {
            addButton.setEnabled(false);
            address.setEnabled(false);
            contact.setEnabled(false);
            phone.setEnabled(false);
            siteNotes.setEnabled(false);

        }
        if(!name.isEmpty()) {
            address.setEnabled(true);
            contact.setEnabled(true);
            phone.setEnabled(true);
            siteNotes.setEnabled(true);
            if((areaCombo.getValue()==0)||(customerCombo.getValue()==0)) {
                addButton.setEnabled(false);
                addButton.removeClickShortcut();
            }
            else {
                addButton.setEnabled(true);
                addButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
            }
        }
    }

    private void addCustomerField() {
        customerCombo = new UIComponents().customerComboBox(generalRepository,400,30);
        customerCombo.setEmptySelectionAllowed(false);
        customerCombo.setValue(site.getCustomerId());
        customerCombo.addValueChangeListener( valueChangeEvent -> customerChange());
        layout.addComponent(customerCombo,1,1,3,1);
    }

    @Override
    void setTabIndexes() {
        areaCombo.focus();
        areaCombo.setTabIndex(1);
        customerCombo.setTabIndex(2);
        name.setTabIndex(3);
        address.setTabIndex(4);
        contact.setTabIndex(5);
        phone.setTabIndex(6);
        siteNotes.setTabIndex(7);
    }

    @Override
    void addFields() {
        addIdField();
        addSiteNotesField();
        addPhoneField();
        addContactField();
        addAddressField();
        addAreaField();
        addSiteNameField();
        addCustomerField();
        customerChange();
        areaChange();
        siteNameChange();
    }

    @Override
    void deleteCurrentId() {
        if (callRepository.getCallsBySite(site.getId()).size()>0) {
            Notification.show(LanguageSettings.getLocaleString("siteWithCallsDeleteError"),
                    "", Notification.Type.ERROR_MESSAGE);
        } else {
            int n = siteRepository.deleteSite(site.getId());
            if (n == 1) {
                Notification.show(LanguageSettings.getLocaleString("siteDeleted"),
                        "", Notification.Type.WARNING_MESSAGE);
                closeWindow();
            }
        }
    }

    private void addAddButton() {
        addButton = UIComponents.addButton();
        addButton.setCaption(LanguageSettings.getLocaleString("addCall"));
        addButton.setWidth("300");
        addButton.addClickListener(clickEvent -> addCall());
        addButton.setTabIndex(8);
        layout.addComponent(addButton,0,7,3,7);
        layout.setComponentAlignment(addButton,Alignment.TOP_CENTER);
    }

    private void addCall() {
        long n = callRepository.insertCall(site.getCustomerId(),
                LocalDate.now(),site.getId());
        Page.getCurrent().open(UIPaths.EDITCALL.getPath()+String.valueOf(n),"_new3",
                700,700,
                BorderStyle.NONE);
        JavaScript.getCurrent().execute(
                "setTimeout(function() {self.close();},0);");
    }

    private void areaChange() {
        if ((areaCombo.getValue()==0)||(areaCombo.isEmpty())) {
            site.setAreaId(0);
            name.setEnabled(false);
            areaCombo.setComponentError(new UserError
                    (LanguageSettings.getLocaleString("pleaseSelectArea")));
        } else {
            try {
                site.setAreaId(areaCombo.getValue());
                areaCombo.setComponentError(null);
            } catch (RuntimeException e) {
                areaCombo.setComponentError(new UserError
                        (LanguageSettings.getLocaleString("pleaseSelectArea")));
            }
        }
        siteRepository.updateSite(site);
        if ((customerCombo.getValue()==0)||(customerCombo.isEmpty())) {
            customerCombo.focus();
        }
        else  {
            name.setEnabled(true);
            name.focus();
        }
    }

    private void customerChange() {
        if ((customerCombo.getValue()==0)||(customerCombo.isEmpty())) {
            name.setEnabled(false);
            site.setCustomerId(0);
            customerCombo.setComponentError(new UserError
                    (LanguageSettings.getLocaleString("pleaseSelectCustomer")));
        }
        else {
            try {
                site.setCustomerId(customerCombo.getValue());
                customerCombo.setEnabled(false);
                customerCombo.setComponentError(null);
            }
            catch (RuntimeException e) {
                customerCombo.setComponentError(new UserError
                        (LanguageSettings.getLocaleString("pleaseSelectCustomer")));
            }
            if(areaCombo.getValue()>0) {
                name.setEnabled(true);
                name.focus();
            }
            else
                areaCombo.focus();
        }
        siteRepository.updateSite(site);
    }
}
