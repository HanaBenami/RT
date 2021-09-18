package il.co.rtcohen.rt.app.views;

import com.vaadin.data.ValueProvider;
import com.vaadin.event.UIEvents;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.*;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import il.co.rtcohen.rt.app.LanguageSettings;
import il.co.rtcohen.rt.app.UIComponents;
import il.co.rtcohen.rt.dal.dao.Contact;
import il.co.rtcohen.rt.dal.repositories.ContactRepository;
import il.co.rtcohen.rt.dal.repositories.GeneralRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.addons.filteringgrid.FilterGrid;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SpringView(name = ContactView.VIEW_NAME)
public class ContactView extends AbstractDataView<Contact> {

    static final String VIEW_NAME = "contact";
    private static final Logger logger = LoggerFactory.getLogger(ContactView.class);
    private final ContactRepository contactRepository;
    private ComboBox<Integer> selectCust;
    private ComboBox<Integer> selectSite;
    private TextField newName;
    private Map<String,String> parametersMap;
    private VerticalLayout headerLayout;
    private HorizontalLayout selectCustLayout;
    private HorizontalLayout selectSiteLayout;
    private HorizontalLayout newContactLayout;
    private Label noSite;

    @Autowired
    private ContactView(ErrorHandler errorHandler, GeneralRepository generalRepository, ContactRepository contactRepository) {
        super(errorHandler, generalRepository);
        this.contactRepository = contactRepository;
    }

    @Override
    public void createView(ViewChangeListener.ViewChangeEvent event) {
        parametersMap = event.getParameterMap();
        logger.info("Parameters map " + Arrays.toString(parametersMap.entrySet().toArray()));
        addHeaderLayout();
        showSelectedSite();
        setTabIndexes();
        if (selectSite.isEnabled()) {
            selectSite.focus();
        } else {
            newName.focus();
        }
    }

    private void addEmptyGrid() {
        noSite = new Label(LanguageSettings.getLocaleString("pleaseSelectCustomerAndSite"));
        noSite.setStyleName("LABEL-WARNING");
        addComponentsAndExpand(noSite);
    }

    private void addActiveColumn() {
        FilterGrid.Column<Contact, Component> activeColumn =
                grid.addComponentColumn((ValueProvider<Contact, Component>) contact ->
                    UIComponents.checkBox(contact.getActive(), true));
        activeColumn.setId("activeColumn").setExpandRatio(1).setResizable(false).setWidth(70).setSortable(false);
        activeColumn.setEditorBinding(grid.getEditor().getBinder().forField(new CheckBox()).bind(
                (ValueProvider<Contact, Boolean>) Contact::getActive,
                (Setter<Contact, Boolean>) (contact, Boolean) -> {
                    contact.setActive(Boolean);
                    generalRepository.update(contact);
                }));
        CheckBox filterActive = UIComponents.checkBox(true);
        activeColumn.setFilter(UIComponents.BooleanValueProvider(),
                filterActive, UIComponents.BooleanPredicate());
        grid.getDefaultHeaderRow().getCell("activeColumn").setText(LanguageSettings.getLocaleString("active"));
    }

    private void addNotesColumn() {
        FilterGrid.Column<Contact, String> notesColumn =
                grid.addColumn(Contact::getNotes).setId("notesColumn")
                        .setEditorComponent(new TextField(), (contact, String) -> {
                            contact.setNotes(String);
                            contactRepository.updateContact(contact);
                        })
                        .setExpandRatio(1).setResizable(false).setMinimumWidth(120);
        TextField filterNotes = UIComponents.textField(30);
        notesColumn.setFilter(filterNotes, UIComponents.stringFilter());
        filterNotes.setWidth("95%");
        grid.getDefaultHeaderRow().getCell("notesColumn").setText(LanguageSettings.getLocaleString("notes"));
    }

    private void addPhoneColumn() {
        FilterGrid.Column<Contact, String> phoneColumn = grid.addColumn(Contact::getPhone).setId("phoneColumn")
                .setEditorComponent(new TextField(), (contact, String) -> {
                    contact.setPhone(String);
                    contactRepository.updateContact(contact);
                })
                .setExpandRatio(1).setResizable(false).setMinimumWidth(120);
        TextField filterPhone = UIComponents.textField(30);
        phoneColumn.setFilter(filterPhone, UIComponents.stringFilter());
        filterPhone.setWidth("95%");
        grid.getDefaultHeaderRow().getCell("phoneColumn").setText(LanguageSettings.getLocaleString("phone"));
    }

    private void addNameColumn() {
        FilterGrid.Column<Contact, String> nameColumn = grid.addColumn(Contact::getName).setId("nameColumn")
                .setEditorComponent(new TextField(), (contact, String) -> {
                    contact.setName(String);
                    generalRepository.update(contact);
                })
                .setExpandRatio(1).setResizable(false).setMinimumWidth(120);
        TextField filterName = UIComponents.textField(30);
        nameColumn.setFilter(filterName, UIComponents.stringFilter());
        filterName.setWidth("95%");
        grid.getDefaultHeaderRow().getCell("nameColumn").setText(LanguageSettings.getLocaleString("name"));
    }

    private void addIdColumn() {
        FilterGrid.Column<Contact, Integer> idColumn = grid.addColumn(Contact::getId).setId("idColumn")
                .setWidth(80).setResizable(false);
        grid.getEditor().setEnabled(true);
        TextField filterId = UIComponents.textField(30);
        idColumn.setFilter(filterId, UIComponents.integerFilter());
        filterId.setWidth("95%");
        grid.getDefaultHeaderRow().getCell("idColumn").setText(LanguageSettings.getLocaleString("id"));
    }

    @Override
    void addColumns() {
        addActiveColumn();
        addNotesColumn();
        addPhoneColumn();
        addNameColumn();
        addIdColumn();
    }

    @Override
    void addGrid() {
        grid.setItems(contactRepository.getContactsBySite(selectSite.getValue()));
        UI.getCurrent().setPollInterval(10000);
        UI.getCurrent().addPollListener((UIEvents.PollListener) event -> {
            if ((selectSite.getValue()!=null) && !(selectSite.getValue().toString().equals("0")))
                grid.setItems(contactRepository.getContactsBySite(selectSite.getValue()));
        });
        addColumns();
        grid.sort("nameColumn");
        grid.setWidth("100%");
        addComponentsAndExpand(grid);
    }

    private void getSelectedCust() {
        List<Integer> sites = generalRepository.getIds("cust");
        selectCust = new UIComponents().customerComboBox(generalRepository,250,30);
        String selectedCust = parametersMap.get("cust");
        if (((selectedCust != null) && (selectedCust.matches("\\d+")))) {
            if (sites.contains(Integer.parseInt(selectedCust))) {
                selectCust.setValue(Integer.parseInt(selectedCust));
                selectCust.setEnabled(false);
            } else {
                selectCust.setValue(0);
            }
        }
    }

    private void getSelectedSite() {
        List<Integer> sites = generalRepository.getIds("site");
        Integer cust = selectCust.getValue();
        int w = 250;
        int h = 30;
        logger.info("cust=" + cust);
        if (null == cust || 0 == cust) {
            selectSite = new UIComponents().emptyComboBox(w, h);
            selectSite.setEnabled(false);
        } else {
            selectSite = new UIComponents().custSiteComboBox(generalRepository, w, h, cust);
            String selectedSite = parametersMap.get("site");
            logger.info("selectedSite=" + selectedSite);
            if (((selectedSite != null) && (selectedSite.matches("\\d+")))) {
                if (sites.contains(Integer.parseInt(selectedSite))) {
                    selectSite.setValue(Integer.parseInt(selectedSite));
                    selectSite.setEnabled(false);
                } else {
                    selectSite.setValue(0);
                    selectSite.setEnabled(true);
                }
            }
        }
    }

    private void showSelectedSite() {
        initGrid("v-align-right");
        noSite = new Label();
        if ((selectSite.getValue() != null) && !(selectSite.getValue().toString().equals("0"))) {
            addGrid();
            newName.setValue("");
            newName.setEnabled(true);
            newName.focus();
        }
        else {
            addEmptyGrid();
            addButton.setEnabled(false);
            newName.setEnabled(false);
        }
    }

    private void addHeaderLayout() {
        headerLayout = new VerticalLayout();
        headerLayout.setWidth("610");
        addSelectCustLayout();
        addSelectSiteLayout();
        addNewContactLayout();
        addComponent(headerLayout);
    }

    private void addSelectCustLayout() {
        selectCustLayout = new HorizontalLayout();
        selectCustLayout.setWidth("610");
        // select button
        Button selectButton = UIComponents.searchButton();
        selectCustLayout.addComponent(selectButton);
        // customer combo-box
        getSelectedCust();
        selectCust.setHeight(selectButton.getHeight(), selectButton.getHeightUnits());
        selectCust.setWidth("340");
        selectCust.addValueChangeListener(ValueChangeEvent -> changeCust());
        selectCustLayout.addComponent(selectCust);
        // header
        Label header = new Label(LanguageSettings.getLocaleString("contacts"));
        header.setStyleName("LABEL-RIGHT");
        selectCustLayout.addComponentsAndExpand(header);
        // add layout
        headerLayout.addComponent(selectCustLayout);
    }

    private void addSelectSiteLayout() {
        selectSiteLayout = new HorizontalLayout();
        selectSiteLayout.setWidth("610");
        // select button
        Button selectButton = UIComponents.searchButton();
        selectSiteLayout.addComponent(selectButton);
        // site combo-box
        getSelectedSite();
        selectSite.setHeight(selectButton.getHeight(),selectButton.getHeightUnits());
        selectSite.addValueChangeListener(ValueChangeEvent -> changeSite());
        selectSiteLayout.addComponentsAndExpand(selectSite);
        // add layout
        headerLayout.addComponent(selectSiteLayout);
    }

    private void addNewContactLayout() {
        newContactLayout = new HorizontalLayout();
        newContactLayout.setWidth("610");
        newContactLayout.addComponent(addButton);
        addButton.setEnabled(false);
        newName = super.addNewNameField();
        newContactLayout.addComponentsAndExpand(newName);
        addButton.addClickListener(click -> addContact());
        headerLayout.addComponent(newContactLayout);
    }

    @Override
    void setTabIndexes() {
        selectSite.setTabIndex(1);
        newName.setTabIndex(2);
        if(grid.isAttached()) {
            grid.setTabIndex(3);
        }
    }

    private void addContact() {
        if ((!newName.getValue().isEmpty()) && (selectSite.getValue() != null)) {
            long n = contactRepository.insertContact(newName.getValue(), selectSite.getValue(),"","");
            newName.setValue("");
            newName.focus();
            grid.setItems(contactRepository.getContactsBySite(selectSite.getValue()));
        }
    }

    private void changeCust() {
        headerLayout.removeComponent(selectSiteLayout);
        addSelectSiteLayout();
        headerLayout.removeComponent(newContactLayout);
        addNewContactLayout();
        changeSite();
    }

    private void changeSite() {
        if (noSite.isAttached()) {
            removeComponent(noSite);
        }
        if (grid.isAttached()) {
            removeComponent(grid);
        }
        showSelectedSite();
        setTabIndexes();
    }
}
