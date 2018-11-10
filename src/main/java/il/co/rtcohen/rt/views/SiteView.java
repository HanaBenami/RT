package il.co.rtcohen.rt.views;

import com.vaadin.data.ValueProvider;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.UIEvents;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.*;
import com.vaadin.shared.ui.BorderStyle;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import il.co.rtcohen.rt.UIComponents;
import il.co.rtcohen.rt.dao.Site;
import il.co.rtcohen.rt.repositories.GeneralRepository;
import il.co.rtcohen.rt.repositories.SiteRepository;
import il.co.rtcohen.rt.ui.UIPaths;
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
    private VerticalLayout headerLayout;
    private HorizontalLayout selectLayout;
    private Label noCustomer;

    @Autowired
    private SiteView(ErrorHandler errorHandler, SiteRepository siteRepository, GeneralRepository generalRepository) {
        super(errorHandler,generalRepository);
        this.siteRepository=siteRepository;
    }

    @Override
    public void createView(ViewChangeListener.ViewChangeEvent event) {
        parametersMap = event.getParameterMap();
        logger.info("Parameters map  " + Arrays.toString(parametersMap.entrySet().toArray()));
        getSelectedCustomer();
        addHeaderLayout();
        showSelectedCustomer();
    }

    private void addEmptyGrid() {
        noCustomer = new Label("יש לבחור לקוח");
        noCustomer.setStyleName("LABEL-WARNING");
        addComponentsAndExpand(noCustomer);
    }

    private void addEditColumn() {
        FilterGrid.Column editColumn =
                grid.addComponentColumn((ValueProvider<Site, Component>) site -> {
                    Button editButton = UIComponents.editButton();
                    final BrowserWindowOpener opener = new BrowserWindowOpener
                            (new ExternalResource(UIPaths.EDITSITE.getPath() + site.getId()));
                    opener.setFeatures("height=400,width=750,resizable");
                    opener.extend(editButton);
                    return editButton;
                }).setId("editColumn");
        editColumn.setWidth(60);
        editColumn.setHidable(false).setHidden(false);
        grid.getDefaultHeaderRow().getCell("editColumn").setText("עריכה");
    }
    private void addActiveColumn() {
        FilterGrid.Column<Site, Component> activeColumn =
                grid.addComponentColumn((ValueProvider<Site, Component>) site ->
                    UIComponents.checkBox(site.getActive(), true));
        activeColumn.setId("activeColumn").setExpandRatio(1).setResizable(false).setWidth(70);
        activeColumn.setEditorBinding(grid.getEditor().getBinder().forField(new CheckBox()).bind(
                (ValueProvider<Site, Boolean>) Site::getActive,
                (Setter<Site, Boolean>) (site, Boolean) -> {
                    site.setActive(Boolean);
                    generalRepository.update(site);
                }));
        CheckBox filterActive = UIComponents.checkBox(true);
        activeColumn.setFilter(UIComponents.BooleanValueProvider(),
                filterActive, UIComponents.BooleanPredicate());
        grid.getDefaultHeaderRow().getCell("activeColumn").setText("פעיל");
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
        grid.getDefaultHeaderRow().getCell("notesColumn").setText("הערות");
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
        grid.getDefaultHeaderRow().getCell("phoneColumn").setText("טלפון");
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
        grid.getDefaultHeaderRow().getCell("contactColumn").setText("א. קשר");
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
        grid.getDefaultHeaderRow().getCell("areaColumn").setText("אזור");
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
        grid.getDefaultHeaderRow().getCell("addressColumn").setText("כתובת");
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
        grid.getDefaultHeaderRow().getCell("nameColumn").setText("שם");
    }
    private void addIdColumn() {
        FilterGrid.Column<Site, Integer> idColumn = grid.addColumn(Site::getId).setId("idColumn")
                .setWidth(80).setResizable(false);
        grid.getEditor().setEnabled(true);
        TextField filterId = UIComponents.textField(30);
        idColumn.setFilter(filterId, UIComponents.integerFilter());
        filterId.setWidth("95%");
        grid.getDefaultHeaderRow().getCell("idColumn").setText("#");
    }
    private void addColumns() {
        addEditColumn();
        addActiveColumn();
        addNotesColumn();
        addPhoneColumn();
        addContactColumn();
        addAreaColumn();
        addAddressColumn();
        addNameColumn();
        addIdColumn();
    }

    private void addGrid(SiteRepository repository) {
        grid.setItems(repository.getSitesByCustomer(selectCustomer.getValue()));
        UI.getCurrent().setPollInterval(3000);
        UI.getCurrent().addPollListener((UIEvents.PollListener) event -> {
            if((selectCustomer.getValue()!=null)&&!(selectCustomer.getValue().toString().equals("0")))
             grid.setItems(repository.getSitesByCustomer(selectCustomer.getValue()));});
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
            addGrid(siteRepository);
            newName.setValue("");
            newName.setEnabled(true);
            newArea.setEnabled(true);
        }
        else {
            addEmptyGrid();
            addButton.setEnabled(false);
            newName.setEnabled(false);
            newArea.setEnabled(false);
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
        selectLayout = new HorizontalLayout();
        selectLayout.setWidth("610");
        addSelectCustomerFields();
        selectCustomer.setTabIndex(4);
        Label header = new Label("אתרים");
        header.setStyleName("LABEL-RIGHT");
        selectLayout.addComponentsAndExpand(header);
        headerLayout.addComponent(selectLayout);
    }

    private void addSelectCustomerFields() {
        Button selectButton = UIComponents.searchButton();
        selectLayout.addComponent(selectButton);
        selectCustomer.setHeight(selectButton.getHeight(),selectButton.getHeightUnits());
        selectCustomer.setWidth("340");
        selectCustomer.addValueChangeListener(ValueChangeEvent -> changeCustomer());
        selectLayout.addComponent(selectCustomer);
    }

    private void addNewSiteLayout() {
        HorizontalLayout addLayout = new HorizontalLayout();
        addLayout.setWidth("610");
        addLayout.addComponent(addButton);
        addButton.setEnabled(false);
        addNewSiteAreaField();
        addLayout.addComponent(newArea);
        newName = super.addNewNameField();
        addLayout.addComponentsAndExpand(newName);
        addButton.addClickListener(click -> addSite());
        newName.setTabIndex(1);
        newArea.setTabIndex(2);
        addButton.setTabIndex(3);
        headerLayout.addComponent(addLayout);
    }

    private void addNewSiteAreaField() {
        newArea = new UIComponents().areaComboBox(generalRepository,95,30);
        newArea.setValue(0);
        newArea.setHeight(addButton.getHeight(),addButton.getHeightUnits());
        newArea.addFocusListener(focusEvent -> addButton.setClickShortcut(ShortcutAction.KeyCode.ENTER));
        newArea.addBlurListener(event -> addButton.removeClickShortcut());
    }

    private void addSite() {
        if ((!newName.getValue().isEmpty())&&(selectCustomer.getValue()!=null)) {
            int newSiteArea = 0;
            if (newArea.getValue() !=null)
                newSiteArea = newArea.getValue();
            long n = siteRepository.insertSite(newName.getValue(),newSiteArea,"",
                    selectCustomer.getValue(),"","","");
            Page.getCurrent().open(UIPaths.EDITSITE.getPath()+String.valueOf(n),"_new2",
                    700,400,BorderStyle.NONE);
            newArea.setValue(0);
            newName.setValue("");
            newName.focus();
            changeCustomer();
        }
    }

    private void changeCustomer() {
        if(noCustomer.isAttached())
            removeComponent(noCustomer);
        if(grid.isAttached())
            removeComponent(grid);
        showSelectedCustomer();
    }

}
