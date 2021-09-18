package il.co.rtcohen.rt.app.views;

import com.vaadin.data.ValueProvider;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.UIEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.*;
import com.vaadin.shared.ui.BorderStyle;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import il.co.rtcohen.rt.app.LanguageSettings;
import il.co.rtcohen.rt.app.UIComponents;
import il.co.rtcohen.rt.dal.dao.Contact;
import il.co.rtcohen.rt.dal.dao.Site;
import il.co.rtcohen.rt.dal.repositories.CallRepository;
import il.co.rtcohen.rt.dal.repositories.ContactRepository;
import il.co.rtcohen.rt.dal.repositories.GeneralRepository;
import il.co.rtcohen.rt.dal.repositories.SiteRepository;
import il.co.rtcohen.rt.app.ui.UIPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.addons.filteringgrid.FilterGrid;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SpringView(name = SiteView.VIEW_NAME)
public class SiteView extends AbstractDataView<Site> {
    static final String VIEW_NAME = "site";
    private static final Logger logger = LoggerFactory.getLogger(CustomerView.class);
    private Map<String,String> parametersMap;
    private final SiteRepository siteRepository;
    private final ContactRepository contactRepository;
    private final CallRepository callRepository;
    private VerticalLayout headerLayout;
    private HorizontalLayout selectCustomerLayout;
    private ComboBox<Integer> selectCustomerCombo;
    private ComboBox<Integer> newSiteArea;
    private TextField newSiteName;
    private Label noCustomer;
    private VerticalLayout contactsLayout;
    Button addContactButton;
    private FilterGrid<Contact> contactFilterGrid;
    private TextField newContactName;
    private Site lastSelectedSite;

    @Autowired
    private SiteView(ErrorHandler errorHandler, SiteRepository siteRepository, GeneralRepository generalRepository,
                     CallRepository callRepository, ContactRepository contactRepository) {
        super(errorHandler,generalRepository);
        this.siteRepository = siteRepository;
        this.contactRepository = contactRepository;
        this.callRepository = callRepository;
    }

    @Override
    public void createView(ViewChangeListener.ViewChangeEvent event) {
        parametersMap = event.getParameterMap();
        logger.info("Parameters map: " + Arrays.toString(parametersMap.entrySet().toArray()));
        getSelectedCustomer();
        addHeaderLayout();
        addOrRefreshSitesLayout();
        addOrRefreshContactsLayout(null);
        setTabIndexes();
    }

    void resetSelectedSite() {
        lastSelectedSite = null;
    }

    // Header & customer selection -------------------------------------------------------------------------------------

    private void getSelectedCustomer() {
        List<Integer> customers = generalRepository.getActiveId("cust");
        selectCustomerCombo = new UIComponents().customerComboBox(generalRepository,250,30);
        String selectedCustomer = parametersMap.get("customer");
        if ((null != selectedCustomer) && (selectedCustomer.matches("\\d+"))) {
            if (customers.contains(Integer.parseInt(selectedCustomer))) {
                selectCustomerCombo.setValue(Integer.parseInt(selectedCustomer));
                selectCustomerCombo.setEnabled(false);
            } else {
                selectCustomerCombo.setValue(0);
            }
        }
        resetSelectedSite();
    }

    private void addHeaderLayout() {
        headerLayout = new VerticalLayout();
        headerLayout.setWidth("610");
        addSelectCustomerLayout();
        addNewSiteLayout();
        addComponent(headerLayout);
    }

    private void addSelectCustomerLayout() {
        selectCustomerLayout = new HorizontalLayout();
        selectCustomerLayout.setWidth("610");
        addSelectCustomerFields();
        Label header = new Label(LanguageSettings.getLocaleString("sites"));
        header.setStyleName("LABEL-RIGHT");
        selectCustomerLayout.addComponentsAndExpand(header);
        headerLayout.addComponent(selectCustomerLayout);
    }

    private void addSelectCustomerFields() {
        Button selectButton = UIComponents.searchButton();
        selectCustomerLayout.addComponent(selectButton);
        selectCustomerCombo.setHeight(selectButton.getHeight(),selectButton.getHeightUnits());
        selectCustomerCombo.setWidth("340");
        selectCustomerCombo.addValueChangeListener(ValueChangeEvent -> addOrRefreshSitesLayout());
        selectCustomerLayout.addComponent(selectCustomerCombo);
    }

    @Override
    void setTabIndexes() {
        selectCustomerCombo.setTabIndex(1);
        newSiteArea.setTabIndex(2);
        newSiteName.setTabIndex(3);
        if (grid.isAttached()) {
            grid.setTabIndex(4);
        }
        if (null != contactsLayout && contactsLayout.isAttached()) {
            if (null != newContactName) {
                newContactName.setTabIndex(5);
            }
            if (null != contactFilterGrid) {
                contactFilterGrid.setTabIndex(6);
            }
        }
        if (selectCustomerCombo.isEnabled()) {
            selectCustomerCombo.focus();
        } else {
            newSiteName.focus();
        }
    }


    // Sites -----------------------------------------------------------------------------------------------------------

    @Override
    void addGrid() {
        grid.setItems(siteRepository.getSitesByCustomer(selectCustomerCombo.getValue()));
        UI.getCurrent().setPollInterval(10000);
        UI.getCurrent().addPollListener((UIEvents.PollListener) event -> {
            if((selectCustomerCombo.getValue()!=null)&&!(selectCustomerCombo.getValue().toString().equals("0")))
                grid.setItems(siteRepository.getSitesByCustomer(selectCustomerCombo.getValue()));
        });
        addColumns();
        grid.sort("nameColumn");
        grid.setWidth("100%");
        addComponentsAndExpand(grid);
    }

    private void addOrRefreshSitesLayout() {
        if (null != noCustomer && noCustomer.isAttached()) {
            removeComponent(noCustomer);
        }
        if (null != grid && grid.isAttached()) {
            removeComponent(grid);
        }
        initGrid("v-align-right");
        if ((null != selectCustomerCombo.getValue()) && !selectCustomerCombo.getValue().toString().equals("0")) {
            addGrid();
            newSiteArea.setEnabled(true);
            newSiteArea.focus();
            newSiteName.setValue("");
            grid.addItemClickListener(event -> addOrRefreshContactsLayout(event.getItem()));
        }
        else {
            noCustomer = new Label(LanguageSettings.getLocaleString("pleaseSelectCustomer"));
            noCustomer.setStyleName("LABEL-WARNING");
            addComponentsAndExpand(noCustomer);
            addButton.setEnabled(false);
            newSiteArea.setEnabled(false);
        }
        newSiteName.setEnabled(false);
        addOrRefreshContactsLayout(null);
        resetSelectedSite();
    }

    @Override
    void addColumns() {
        addEditColumn();
        addContactsColumn();
        addCallsColumn();
        addActiveColumn();
        addNotesColumn();
        addAreaColumn();
        addAddressColumn();
        addNameColumn();
        addIdColumn();
    }

    // Add site

    private void addNewSiteLayout() {
        HorizontalLayout addLayout = new HorizontalLayout();
        addLayout.setWidth("610");
        addLayout.addComponent(addButton);
        addButton.setEnabled(false);
        newSiteName = super.addNewNameField();
        addLayout.addComponentsAndExpand(newSiteName);
        addNewSiteAreaField();
        addLayout.addComponent(newSiteArea);
        addButton.addClickListener(click -> addSite());
        headerLayout.addComponent(addLayout);
    }

    private void addNewSiteAreaField() {
        newSiteArea = new UIComponents().areaComboBox(generalRepository,95,30);
        newSiteArea.setValue(0);
        newSiteArea.setEmptySelectionAllowed(false);
        newSiteArea.addValueChangeListener(valueChangeEvent -> newSiteName.setEnabled(true));
        newSiteArea.setHeight(addButton.getHeight(),addButton.getHeightUnits());
        newSiteArea.addFocusListener(focusEvent -> addButton.setClickShortcut(ShortcutAction.KeyCode.ENTER));
        newSiteArea.addBlurListener(event -> addButton.removeClickShortcut());
    }

    private void addSite() {
        if ((!newSiteName.getValue().isEmpty())&&(selectCustomerCombo.getValue()!=null)) {
            int newSiteArea = 0;
            if (this.newSiteArea.getValue()!=null)
                newSiteArea = this.newSiteArea.getValue();
            long n = siteRepository.insertSite(newSiteName.getValue(),newSiteArea,"",
                    selectCustomerCombo.getValue(),"","","");
            Page.getCurrent().open(UIPaths.EDITSITE.getPath()+String.valueOf(n),"_new2",
                    700,500, BorderStyle.NONE);
            this.newSiteArea.setValue(0);
            newSiteName.setValue("");
            newSiteName.focus();
            grid.setItems(siteRepository.getSitesByCustomer(selectCustomerCombo.getValue()));
        }
    }

    // Sites grid

    private void addEditColumn() {
        FilterGrid.Column editColumn =
                grid.addComponentColumn((ValueProvider<Site, Component>) site -> {
                    Button editButton = UIComponents.editButton();
                    final BrowserWindowOpener opener = new BrowserWindowOpener
                            (new ExternalResource(UIPaths.EDITSITE.getPath() + site.getId()));
                    opener.setFeatures("height=500,width=700,resizable");
                    opener.extend(editButton);
                    return editButton;
                }).setId("editColumn");
        editColumn.setWidth(60);
        editColumn.setHidable(false).setHidden(false).setSortable(false);
        grid.getDefaultHeaderRow().getCell("editColumn").setText(LanguageSettings.getLocaleString("edit"));
    }

    private void addContactsColumn() {
        FilterGrid.Column contactsColumn =
                grid.addComponentColumn((ValueProvider<Site, Component>) site -> {
                    int activeContactsCounter = contactRepository.getContactsBySite(site.getId(), true).size();
                    Button contactsButton = UIComponents.gridSmallButton(VaadinIcons.ENVELOPE_OPEN_O);
//                    contactsButton.addClickListener(clickEvent ->
//                            getUI().getNavigator().navigateTo
//                                    ("contact/cust=" + site.getCustomerId() + "&site=" + site.getId())
//                    );
                    if(activeContactsCounter > 0) {
                        contactsButton.setIcon(VaadinIcons.ENVELOPE_OPEN);
                        contactsButton.setCaption(String.valueOf((activeContactsCounter)));
                    }
                    return contactsButton;
                });
        contactsColumn.setId("contactsColumn").setExpandRatio(1).setResizable(false).setWidth(85).setSortable(false);
        grid.getDefaultHeaderRow().getCell("contactsColumn").setText(LanguageSettings.getLocaleString("contacts"));
    }

    private void addCallsColumn() {
        FilterGrid.Column callsColumn =
                grid.addComponentColumn((ValueProvider<Site, Component>) site -> {
                    int openCallsCounter=callRepository.getCallsBySite(site.getId()).size();
                    Button callsButton = UIComponents.gridSmallButton(VaadinIcons.BELL_O);
                    callsButton.addClickListener(clickEvent ->
                            getUI().getNavigator().navigateTo
                                    ("call/customer="+ site.getCustomerId()));
                    if(openCallsCounter>0) {
                        callsButton.setIcon(VaadinIcons.BELL);
                        callsButton.setCaption(String.valueOf((openCallsCounter)));
                    }
                    return callsButton;
                });
        callsColumn.setId("callsColumn").setExpandRatio(1).setResizable(false).setWidth(85).setSortable(false);
        grid.getDefaultHeaderRow().getCell("callsColumn").setText(LanguageSettings.getLocaleString("calls"));
    }

    private void addActiveColumn() {
        FilterGrid.Column<Site, Component> activeColumn =
                grid.addComponentColumn((ValueProvider<Site, Component>) site ->
                    UIComponents.checkBox(site.getActive(), true));
        activeColumn.setId("activeColumn").setExpandRatio(1).setResizable(false).setWidth(70).setSortable(false);
        activeColumn.setEditorBinding(grid.getEditor().getBinder().forField(new CheckBox()).bind(
                (ValueProvider<Site, Boolean>) Site::getActive,
                (Setter<Site, Boolean>) (site, Boolean) -> {
                    site.setActive(Boolean);
                    generalRepository.update(site);
                }));
        CheckBox filterActive = UIComponents.checkBox(true);
        activeColumn.setFilter(UIComponents.BooleanValueProvider(),
                filterActive, UIComponents.BooleanPredicate());
        grid.getDefaultHeaderRow().getCell("activeColumn").setText(LanguageSettings.getLocaleString("active"));
    }
    private void addNotesColumn() {
        FilterGrid.Column<Site, String> notesColumn =
                grid.addColumn(Site::getNotes).setId("notesColumn")
                        .setEditorComponent(new TextField(), (site, String) -> {
                            site.setNotes(String);
                            siteRepository.updateSite(site);
                        })
                        .setExpandRatio(1).setResizable(false).setMinimumWidth(120);
        TextField filterNotes = UIComponents.textField(30);
        notesColumn.setFilter(filterNotes, UIComponents.stringFilter());
        filterNotes.setWidth("95%");
        grid.getDefaultHeaderRow().getCell("notesColumn").setText(LanguageSettings.getLocaleString("notes"));
    }
    private void addAreaColumn() {
        ComboBox<Integer> areaCombo = new UIComponents().areaComboBox(generalRepository,95,30);
        areaCombo.setEmptySelectionAllowed(false);
        FilterGrid.Column<Site, String> areaColumn = grid.addColumn(site ->
                generalRepository.getNameById(site.getAreaId(), "area"))
                .setId("areaColumn")
                .setWidth(120).setEditorBinding(grid.getEditor().getBinder().forField(areaCombo).bind(
                        (ValueProvider<Site, Integer>) Site::getAreaId,
                        (Setter<Site, Integer>) (site, integer) -> {
                            site.setAreaId(integer);
                            siteRepository.updateSite(site);
                        }))
                .setExpandRatio(1).setResizable(false);
        ComboBox<Integer> filterArea = new UIComponents().areaComboBox(generalRepository, 95, 30);
        areaColumn.setFilter((filterArea),
                (cValue, fValue) -> fValue == null || generalRepository.getNameById(fValue, "area").equals(cValue));
        filterArea.setWidth("95%");
        grid.getDefaultHeaderRow().getCell("areaColumn").setText(LanguageSettings.getLocaleString("area"));
    }
    private void addAddressColumn() {
        FilterGrid.Column<Site, String> addressColumn = grid.addColumn(Site::getAddress).setId("addressColumn")
                .setEditorComponent(new TextField(), (site, String) -> {
                    site.setAddress(String);
                    siteRepository.updateSite(site);
                })
                .setExpandRatio(1).setResizable(false).setMinimumWidth(120);
        TextField filterAddress = UIComponents.textField(30);
        addressColumn.setFilter(filterAddress, UIComponents.stringFilter());
        filterAddress.setWidth("95%");
        grid.getDefaultHeaderRow().getCell("addressColumn").setText(LanguageSettings.getLocaleString("address"));
    }
    private void addNameColumn() {
        FilterGrid.Column<Site, String> nameColumn = grid.addColumn(Site::getName).setId("nameColumn")
                .setEditorComponent(new TextField(), (site, String) -> {
                    site.setName(String);
                    generalRepository.update(site);
                })
                .setExpandRatio(1).setResizable(false).setMinimumWidth(120);
        TextField filterName = UIComponents.textField(30);
        nameColumn.setFilter(filterName, UIComponents.stringFilter());
        filterName.setWidth("95%");
        grid.getDefaultHeaderRow().getCell("nameColumn").setText(LanguageSettings.getLocaleString("name"));
    }
    private void addIdColumn() {
        FilterGrid.Column<Site, Integer> idColumn = grid.addColumn(Site::getId).setId("idColumn")
                .setWidth(80).setResizable(false);
        grid.getEditor().setEnabled(true);
        TextField filterId = UIComponents.textField(30);
        idColumn.setFilter(filterId, UIComponents.integerFilter());
        filterId.setWidth("95%");
        grid.getDefaultHeaderRow().getCell("idColumn").setText(LanguageSettings.getLocaleString("id"));
    }

    // Contacts --------------------------------------------------------------------------------------------------------

    private void addOrRefreshContactsLayout(Site site) {
        if (null != contactsLayout && contactsLayout.isAttached()) {
            removeComponent(contactsLayout);
        }
        contactsLayout = new VerticalLayout();
        contactsLayout.setStyleName("v-align-center");
        contactFilterGrid = new FilterGrid<>();
        initGrid("v-align-center", contactFilterGrid);
        if (null != site) {
            lastSelectedSite = site;
            addNewContactLayout();
            addContactGrid(site);
            newContactName.focus();
        }
        else {
            resetSelectedSite();
            Label noSite = new Label(LanguageSettings.getLocaleString("pleaseSelectSite"));
            noSite.setStyleName("LABEL-WARNING");
            contactsLayout.addComponentsAndExpand(noSite);
        }
        this.addComponentsAndExpand(contactsLayout);
    }

    // Add contact

    void addContactGrid(Site site) {
        contactFilterGrid.setItems(contactRepository.getContactsBySite(site.getId(), false));
        UI.getCurrent().setPollInterval(10000);
        addContactColumns();
        contactFilterGrid.sort("nameColumn");
        contactFilterGrid.setWidth("100%");
        contactsLayout.addComponentsAndExpand(contactFilterGrid);
    }

    private void addNewContactLayout() {
        // Button
        addContactButton = UIComponents.addButton();
        addContactButton.setEnabled(false);
        addContactButton.addClickListener(click -> addNewContact());
        // Name - Text box
        newContactName = super.addNewNameField();
        newContactName.setValue("");
        newContactName.focus();
        newContactName.addFocusListener(focusEvent ->
            addContactButton.setClickShortcut(ShortcutAction.KeyCode.ENTER));
        newContactName.addBlurListener(event -> addContactButton.removeClickShortcut());
        newContactName.addValueChangeListener(valueChangeEvent -> {
            addContactButton.setEnabled(!newContactName.getValue().isEmpty());
        });
        // Header
        Label header = new Label(LanguageSettings.getLocaleString("contacts")
                + " - " + lastSelectedSite.getName());
        header.setStyleName("LABEL-RIGHT");
        header.setWidth("300");
        // Layout
        HorizontalLayout addLayout = new HorizontalLayout();
        addLayout.setWidth("800");
        addLayout.setHeight("60");
        addLayout.setStyleName("v-align-center");
        addLayout.addComponents(addContactButton, newContactName);
        addLayout.addComponentsAndExpand(header);
        contactsLayout.addComponents(addLayout);
    }

    private void addNewContact() {
        if (!newContactName.getValue().isEmpty() && (null != lastSelectedSite)) {
            contactRepository.insertContact(newContactName.getValue(), lastSelectedSite.getId(),"", "");
            addOrRefreshContactsLayout(lastSelectedSite);
        }
    }

    // Contacts grid

    void addContactColumns() {
        addContactActiveColumn();
        addContactNotesColumn();
        addContactPhoneColumn();
        addContactNameColumn();
        addContactIdColumn();
    }

    private void addContactIdColumn() {
        FilterGrid.Column<Contact, Integer> idColumn = contactFilterGrid.addColumn(Contact::getId).setId("idColumn")
                .setWidth(80).setResizable(false);
        contactFilterGrid.getEditor().setEnabled(true);
        TextField filterId = UIComponents.textField(30);
        idColumn.setFilter(filterId, UIComponents.integerFilter());
        filterId.setWidth("95%");
        contactFilterGrid.getDefaultHeaderRow().getCell("idColumn").setText(LanguageSettings.getLocaleString("id"));
    }

    private void addContactNameColumn() {
        FilterGrid.Column<Contact, String> nameColumn = contactFilterGrid.addColumn(Contact::getName).setId("nameColumn")
                .setEditorComponent(new TextField(), (contact, String) -> {
                    contact.setName(String);
                    generalRepository.update(contact);
                })
                .setExpandRatio(1).setResizable(false).setMinimumWidth(120);
        TextField filterName = UIComponents.textField(30);
        nameColumn.setFilter(filterName, UIComponents.stringFilter());
        filterName.setWidth("95%");
        contactFilterGrid.getDefaultHeaderRow().getCell("nameColumn").setText(LanguageSettings.getLocaleString("name"));
    }

    private void addContactPhoneColumn() {
        FilterGrid.Column<Contact, String> phoneColumn = contactFilterGrid.addColumn(Contact::getPhone).setId("phoneColumn")
                .setEditorComponent(new TextField(), (contact, String) -> {
                    contact.setPhone(String);
                    contactRepository.updateContact(contact);
                })
                .setExpandRatio(1).setResizable(false).setMinimumWidth(120);
        TextField filterPhone = UIComponents.textField(30);
        phoneColumn.setFilter(filterPhone, UIComponents.stringFilter());
        filterPhone.setWidth("95%");
        contactFilterGrid.getDefaultHeaderRow().getCell("phoneColumn").setText(LanguageSettings.getLocaleString("phone"));
    }

    private void addContactNotesColumn() {
        FilterGrid.Column<Contact, String> notesColumn =
                contactFilterGrid.addColumn(Contact::getNotes).setId("notesColumn")
                        .setEditorComponent(new TextField(), (contact, String) -> {
                            contact.setNotes(String);
                            contactRepository.updateContact(contact);
                        })
                        .setExpandRatio(1).setResizable(false).setMinimumWidth(120);
        TextField filterNotes = UIComponents.textField(30);
        notesColumn.setFilter(filterNotes, UIComponents.stringFilter());
        filterNotes.setWidth("95%");
        contactFilterGrid.getDefaultHeaderRow().getCell("notesColumn").setText(LanguageSettings.getLocaleString("notes"));
    }

    private void addContactActiveColumn() {
        FilterGrid.Column<Contact, Component> activeColumn =
                contactFilterGrid.addComponentColumn((ValueProvider<Contact, Component>) contact ->
                        UIComponents.checkBox(contact.getActive(), true));
        activeColumn.setId("activeColumn").setExpandRatio(1).setResizable(false).setWidth(70).setSortable(false);
        activeColumn.setEditorBinding(contactFilterGrid.getEditor().getBinder().forField(new CheckBox()).bind(
                (ValueProvider<Contact, Boolean>) Contact::getActive,
                (Setter<Contact, Boolean>) (contact, Boolean) -> {
                    contact.setActive(Boolean);
                    generalRepository.update(contact);
                }));
        CheckBox filterActive = UIComponents.checkBox(true);
        activeColumn.setFilter(UIComponents.BooleanValueProvider(),
                filterActive, UIComponents.BooleanPredicate());
        contactFilterGrid.getDefaultHeaderRow().getCell("activeColumn").setText(LanguageSettings.getLocaleString("active"));
    }
}
