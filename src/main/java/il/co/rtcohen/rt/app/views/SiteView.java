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
import il.co.rtcohen.rt.dal.dao.Site;
import il.co.rtcohen.rt.dal.repositories.CallRepository;
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
    private static Logger logger = LoggerFactory.getLogger(CustomerView.class);
    private ComboBox<Integer> selectCustomer;
    private ComboBox<Integer> newArea;
    private TextField newName;
    private Map<String,String> parametersMap;
    private SiteRepository siteRepository;
    private CallRepository callRepository;
    private VerticalLayout headerLayout;
    private HorizontalLayout selectCustomerLayout;
    private Label noCustomer;

    @Autowired
    private SiteView(ErrorHandler errorHandler, SiteRepository siteRepository, GeneralRepository generalRepository, CallRepository callRepository) {
        super(errorHandler,generalRepository);
        this.siteRepository=siteRepository;
        this.callRepository=callRepository;
    }

    @Override
    public void createView(ViewChangeListener.ViewChangeEvent event) {
        parametersMap = event.getParameterMap();
        logger.info("Parameters map  " + Arrays.toString(parametersMap.entrySet().toArray()));
        getSelectedCustomer();
        addHeaderLayout();
        showSelectedCustomer();
        setTabIndexes();
        if (selectCustomer.isEnabled())
            selectCustomer.focus();
        else
            newName.focus();
    }

    private void addEmptyGrid() {
        noCustomer = new Label(LanguageSettings.getLocaleString("pleaseSelectCustomer"));
        noCustomer.setStyleName("LABEL-WARNING");
        addComponentsAndExpand(noCustomer);
    }

    private void addEditColumn() {
        FilterGrid.Column editColumn =
                grid.addComponentColumn((ValueProvider<Site, Component>) site -> {
                    Button editButton = UIComponents.editButton();
                    final BrowserWindowOpener opener = new BrowserWindowOpener
                            (new ExternalResource(UIPaths.EDITSITE.getPath() + site.getId()));
                    opener.setFeatures("height=400,width=700,resizable");
                    opener.extend(editButton);
                    return editButton;
                }).setId("editColumn");
        editColumn.setWidth(60);
        editColumn.setHidable(false).setHidden(false).setSortable(false);
        grid.getDefaultHeaderRow().getCell("editColumn").setText(LanguageSettings.getLocaleString("edit"));
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
    private void addPhoneColumn() {
        FilterGrid.Column<Site, String> phoneColumn = grid.addColumn(Site::getPhone).setId("phoneColumn")
                .setEditorComponent(new TextField(), (site, String) -> {
                    site.setPhone(String);
                    siteRepository.updateSite(site);
                })
                .setExpandRatio(1).setResizable(false).setMinimumWidth(120);
        TextField filterPhone = UIComponents.textField(30);
        phoneColumn.setFilter(filterPhone, UIComponents.stringFilter());
        filterPhone.setWidth("95%");
        grid.getDefaultHeaderRow().getCell("phoneColumn").setText(LanguageSettings.getLocaleString("phone"));
    }
    private void addContactColumn() {
        FilterGrid.Column<Site, String> contactColumn = grid.addColumn(Site::getContact).setId("contactColumn")
                .setEditorComponent(new TextField(), (site, String) -> {
                    site.setContact(String);
                    siteRepository.updateSite(site);
                })
                .setExpandRatio(1).setResizable(false).setMinimumWidth(120);
        TextField filterContact = UIComponents.textField(30);
        contactColumn.setFilter(filterContact, UIComponents.stringFilter());
        filterContact.setWidth("95%");
        grid.getDefaultHeaderRow().getCell("contactColumn").setText(LanguageSettings.getLocaleString("contactShort"));
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

    @Override
    void addColumns() {
        addEditColumn();
        addCallsColumn();
        addActiveColumn();
        addNotesColumn();
        addPhoneColumn();
        addContactColumn();
        addAreaColumn();
        addAddressColumn();
        addNameColumn();
        addIdColumn();
    }

    @Override
    void addGrid() {
        grid.setItems(siteRepository.getSitesByCustomer(selectCustomer.getValue()));
        UI.getCurrent().setPollInterval(10000);
        UI.getCurrent().addPollListener((UIEvents.PollListener) event -> {
            if((selectCustomer.getValue()!=null)&&!(selectCustomer.getValue().toString().equals("0")))
             grid.setItems(siteRepository.getSitesByCustomer(selectCustomer.getValue()));});
        addColumns();
        grid.sort("nameColumn");
        grid.setWidth("100%");
        addComponentsAndExpand(grid);
    }

    private void getSelectedCustomer() {
        List<Integer> customers = generalRepository.getActiveId("cust");
        selectCustomer = new UIComponents().customerComboBox(generalRepository,250,30);
        String selectedCustomer = parametersMap.get("customer");
        if(((selectedCustomer!=null)&&(selectedCustomer.matches("\\d+"))))
            if (customers.contains(Integer.parseInt(selectedCustomer))) {
                selectCustomer.setValue(Integer.parseInt(selectedCustomer));
                selectCustomer.setEnabled(false);
            } else
                selectCustomer.setValue(0);
    }

    private void showSelectedCustomer() {
        initGrid("v-align-right");
        noCustomer = new Label();
        if ((selectCustomer.getValue()!=null)&&!(selectCustomer.getValue().toString().equals("0"))) {
            addGrid();
            newArea.setEnabled(true);
            newArea.focus();
            newName.setValue("");
            newName.setEnabled(false);
        }
        else {
            addEmptyGrid();
            addButton.setEnabled(false);
            newArea.setEnabled(false);
            newName.setEnabled(false);
        }
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
        selectCustomer.setHeight(selectButton.getHeight(),selectButton.getHeightUnits());
        selectCustomer.setWidth("340");
        selectCustomer.addValueChangeListener(ValueChangeEvent -> changeCustomer());
        selectCustomerLayout.addComponent(selectCustomer);
    }

    private void addNewSiteLayout() {
        HorizontalLayout addLayout = new HorizontalLayout();
        addLayout.setWidth("610");
        addLayout.addComponent(addButton);
        addButton.setEnabled(false);
        newName = super.addNewNameField();
        addLayout.addComponentsAndExpand(newName);
        addNewSiteAreaField();
        addLayout.addComponent(newArea);
        addButton.addClickListener(click -> addSite());
        headerLayout.addComponent(addLayout);
    }

    @Override
    void setTabIndexes() {
        selectCustomer.setTabIndex(1);
        newArea.setTabIndex(2);
        newName.setTabIndex(3);
        if(grid.isAttached())
            grid.setTabIndex(4);
    }

    private void addNewSiteAreaField() {
        newArea = new UIComponents().areaComboBox(generalRepository,95,30);
        newArea.setValue(0);
        newArea.setEmptySelectionAllowed(false);
        newArea.addValueChangeListener(valueChangeEvent -> newName.setEnabled(true));
        newArea.setHeight(addButton.getHeight(),addButton.getHeightUnits());
        newArea.addFocusListener(focusEvent -> addButton.setClickShortcut(ShortcutAction.KeyCode.ENTER));
        newArea.addBlurListener(event -> addButton.removeClickShortcut());
    }

    private void addSite() {
        if ((!newName.getValue().isEmpty())&&(selectCustomer.getValue()!=null)) {
            int newSiteArea = 0;
            if (newArea.getValue()!=null)
                newSiteArea = newArea.getValue();
            long n = siteRepository.insertSite(newName.getValue(),newSiteArea,"",
                    selectCustomer.getValue(),"","","");
            Page.getCurrent().open(UIPaths.EDITSITE.getPath()+String.valueOf(n),"_new2",
                    700,400,BorderStyle.NONE);
            newArea.setValue(0);
            newName.setValue("");
            newName.focus();
            grid.setItems(siteRepository.getSitesByCustomer(selectCustomer.getValue()));
        }
    }

    private void changeCustomer() {
        if(noCustomer.isAttached())
            removeComponent(noCustomer);
        if(grid.isAttached())
            removeComponent(grid);
        showSelectedCustomer();
        setTabIndexes();
    }

}
