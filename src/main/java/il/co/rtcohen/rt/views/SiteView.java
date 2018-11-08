package il.co.rtcohen.rt.views;

import com.vaadin.data.ValueProvider;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.UIEvents;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Setter;
import com.vaadin.shared.ui.BorderStyle;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import il.co.rtcohen.rt.UIcomponents;
import il.co.rtcohen.rt.dao.Site;
import il.co.rtcohen.rt.repositories.GeneralRepository;
import il.co.rtcohen.rt.repositories.SiteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.addons.filteringgrid.FilterGrid;
import org.vaadin.addons.filteringgrid.filters.InMemoryFilter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SpringView(name = SiteView.VIEW_NAME)
public class SiteView extends AbstractDataView {
    static final String VIEW_NAME = "site";
    private static Logger logger = LoggerFactory.getLogger(CustomerView.class);
    private ComboBox<Integer> selectCustomer;
    private ComboBox<Integer> newArea;
    private Button addButton;
    private TextField newName;
    private Map<String,String> parametersMap;

    private SiteRepository siteRepository;

    @Autowired
    private SiteView(ErrorHandler errorHandler, SiteRepository siteRepository, GeneralRepository generalRepository) {
        super(errorHandler,generalRepository);
        this.siteRepository=siteRepository;
    }

    @Override
    public void createView(ViewChangeListener.ViewChangeEvent event) {
        parametersMap = event.getParameterMap();
        logger.info("Parameters map  " + Arrays.toString(parametersMap.entrySet().toArray()));
        customerSelection();
        firstLayout();
        showSelectedCustomer();
    }

    private void addEmptyGrid() {
        Label noCustomer = new Label("יש לבחור לקוח");
        noCustomer.setStyleName("LABEL-WARNING");
        dataGrid = noCustomer;
        addComponentsAndExpand(dataGrid);
    }

    private void addGrid(SiteRepository repository) {
        FilterGrid<Site> grid = UIcomponents.myGrid("v-align-right");

        //data
        grid.setItems(repository.getSitesByCustomer((Integer) selectCustomer.getValue()));
        UI.getCurrent().setPollInterval(3000);
        UI.getCurrent().addPollListener((UIEvents.PollListener) event -> {
            grid.setItems(repository.getSitesByCustomer((Integer) selectCustomer.getValue()));;
        });

        ComboBox<Integer> areaCombo = new UIcomponents().areaComboBox(generalRepository,95,30);
        areaCombo.setEmptySelectionAllowed(false);

        //columns

        //edit
        FilterGrid.Column editColumn =
                grid.addComponentColumn((ValueProvider<Site, Component>) site -> {
                    Button editButton = UIcomponents.editButton();
                    final BrowserWindowOpener opener = new BrowserWindowOpener
                            (new ExternalResource("/editsite#"+site.getId()));
                    opener.setFeatures("height=400,width=750,resizable");
                    opener.extend(editButton);
                    return editButton;
                }).setId("editColumn");
        editColumn.setWidth(60);
        editColumn.setHidable(false).setHidden(false);
        grid.getDefaultHeaderRow().getCell("editColumn").setText("עריכה");

        CheckBox active = new CheckBox();
        FilterGrid.Column activeColumn =
                grid.addComponentColumn((ValueProvider<Site, Component>) site -> {
                    return UIcomponents.checkBox(site.getActive(),true);
                });
        activeColumn.setId("activeColumn").setExpandRatio(1).setResizable(false).setWidth(70);
        activeColumn.setEditorBinding(grid.getEditor().getBinder().forField(active).bind(
                (ValueProvider<Site, Boolean>) Site::getActive,
                (Setter<Site, Boolean>) (site, Boolean) -> {
                    site.setActive(Boolean);
                    generalRepository.update(site);
                }));

        TextField notes = new TextField();
        FilterGrid.Column<Site, String> notesColumn =
                grid.addColumn(Site::getNotes).setId("notesColumn")
                .setEditorComponent(notes, (site,String) -> {
                    site.setNotes(String);
                    siteRepository.updateSite(site);
                })
                .setExpandRatio(1).setResizable(false).setMinimumWidth(120);

        TextField phone = new TextField();
        FilterGrid.Column<Site, String> phoneColumn = grid.addColumn(Site::getPhone).setId("phoneColumn")
                .setEditorComponent(phone,(site,String) -> {
                    site.setPhone(String);
                    siteRepository.updateSite(site);
                })
                .setExpandRatio(1).setResizable(false).setMinimumWidth(120);

        TextField contact = new TextField();
        FilterGrid.Column<Site, String> contactColumn = grid.addColumn(Site::getContact).setId("contactColumn")
                .setEditorComponent(contact,(site,String) -> {
                    site.setContact(String);
                    siteRepository.updateSite(site);
                })
                .setExpandRatio(1).setResizable(false).setMinimumWidth(120);

        FilterGrid.Column<Site, String> areaColumn = grid.addColumn(site ->
                generalRepository.getNameById(site.getAreaId(),"area"))
                .setId("areaColumn")
            .setWidth(120).setEditorBinding(grid.getEditor().getBinder().forField(areaCombo).bind(
                        (ValueProvider<Site, Integer>) Site::getAreaId,
                        (Setter<Site, Integer>) (site, integer) -> {
                            site.setAreaId(integer);
                            siteRepository.updateSite(site);
                        }))
                .setExpandRatio(1).setResizable(false);
        TextField address = new TextField();
        FilterGrid.Column<Site, String> addressColumn = grid.addColumn(Site::getAddress).setId("addressColumn")
                .setEditorComponent(address,(site,String) -> {
                    site.setAddress(String);
                    siteRepository.updateSite(site);
                })
                .setExpandRatio(1).setResizable(false).setMinimumWidth(120);

        TextField name = new TextField();
        FilterGrid.Column<Site, String> nameColumn = grid.addColumn(Site::getName).setId("nameColumn")
                .setEditorComponent(name,(site,String) -> {
                    site.setName(String);
                    generalRepository.update(site);
                })
                .setExpandRatio(1).setResizable(false).setMinimumWidth(120);

        FilterGrid.Column<Site, Integer> idColumn = grid.addColumn(Site::getId).setId("idColumn")
                .setWidth(80).setResizable(false);
        grid.getEditor().setEnabled(true);

        //headers
        grid.sort("nameColumn");
        grid.getDefaultHeaderRow().getCell("activeColumn").setText("פעיל");
        grid.getDefaultHeaderRow().getCell("notesColumn").setText("הערות");
        grid.getDefaultHeaderRow().getCell("phoneColumn").setText("טלפון");
        grid.getDefaultHeaderRow().getCell("contactColumn").setText("א. קשר");
        grid.getDefaultHeaderRow().getCell("areaColumn").setText("אזור");
        grid.getDefaultHeaderRow().getCell("addressColumn").setText("כתובת");
        grid.getDefaultHeaderRow().getCell("nameColumn").setText("שם");
        grid.getDefaultHeaderRow().getCell("idColumn").setText("#");

        //filters
        TextField filterId = UIcomponents.textField(30);
        idColumn.setFilter(filterId, InMemoryFilter.StringComparator.containsIgnoreCase());
        filterId.setWidth("95%");

        TextField filterName = UIcomponents.textField(30);
        nameColumn.setFilter(filterName, InMemoryFilter.StringComparator.containsIgnoreCase());
        filterName.setWidth("95%");

        TextField filterAddress = UIcomponents.textField(30);
        addressColumn.setFilter(filterAddress, InMemoryFilter.StringComparator.containsIgnoreCase());
        filterAddress.setWidth("95%");

        TextField filterContact = UIcomponents.textField(30);
        contactColumn.setFilter(filterContact, InMemoryFilter.StringComparator.containsIgnoreCase());
        filterContact.setWidth("95%");

        TextField filterPhone = UIcomponents.textField(30);
        phoneColumn.setFilter(filterPhone, InMemoryFilter.StringComparator.containsIgnoreCase());
        filterPhone.setWidth("95%");

        TextField filterNotes = UIcomponents.textField(30);
        notesColumn.setFilter(filterNotes, InMemoryFilter.StringComparator.containsIgnoreCase());
        filterNotes.setWidth("95%");

        ComboBox<Integer> filterArea = new UIcomponents().areaComboBox(generalRepository,95,30);
        areaColumn.setFilter((filterArea),
                (cValue, fValue) -> fValue == null || generalRepository.getNameById((Integer)fValue,"area").equals(cValue));
        filterArea.setWidth("95%");

        CheckBox filterActive = UIcomponents.checkBox(true);
        activeColumn.setFilter(UIcomponents.BooleanValueProvider(),
            filterActive, UIcomponents.BooleanPredicate());

        dataGrid=grid;
        dataGrid.setWidth("100%");
        addComponentsAndExpand(dataGrid);
    }

    private void customerSelection () {
        List<Integer> custList = generalRepository.getActiveId("cust");
        selectCustomer = new UIcomponents().customerComboBox(generalRepository,250,30);
        String selectedCustomer = parametersMap.get("customer");
        if(((selectedCustomer!=null)&&(selectedCustomer.matches("\\d+"))))
            if (custList.contains(Integer.parseInt(selectedCustomer))) {
                selectCustomer.setValue(Integer.parseInt(selectedCustomer));
                selectCustomer.setEnabled(false);
            } else {
                selectCustomer.setValue(0);
            }
    }

    private void showSelectedCustomer() {
        if ((selectCustomer.getValue()!=null)&&!(selectCustomer.getValue().toString().equals("0"))) {
            addGrid(siteRepository);
            newName.setValue("");
            newName.setEnabled(true);
        }
        else {
            addEmptyGrid();
            addButton.setEnabled(false);
            newName.setEnabled(false);
        }
    }

    private void firstLayout() {
        VerticalLayout firstLayout = new VerticalLayout();
        firstLayout.setWidth("610");
        firstLayout.addComponent(selectCustomerForm());
        firstLayout.addComponent(addForm());
        addComponent(firstLayout);
    }

    private HorizontalLayout selectCustomerForm() {
        HorizontalLayout formLayout = new HorizontalLayout();
        formLayout.setWidth("610");

        Button selectButton = UIcomponents.searchButton();
        formLayout.addComponent(selectButton);

        selectCustomer.setHeight(selectButton.getHeight(),selectButton.getHeightUnits());
        selectCustomer.setWidth("340");
        selectCustomer.addValueChangeListener(ValueChangeEvent -> {
            removeComponent(dataGrid);
            showSelectedCustomer();
        });
        formLayout.addComponent(selectCustomer);

        selectCustomer.setTabIndex(4);
        //selectButton.setTabIndex(5);

        Label header = new Label("אתרים");
        header.setStyleName("LABEL-RIGHT");

        formLayout.addComponentsAndExpand(header);

        return formLayout;
    }

    private HorizontalLayout addForm() {
        HorizontalLayout formLayout = new HorizontalLayout();
        formLayout.setWidth("610");

        addButton = UIcomponents.addButton();
        formLayout.addComponent(addButton);

        newArea = new UIcomponents().areaComboBox(generalRepository,95,30);
        newArea.setValue(0);
        newArea.setHeight(addButton.getHeight(),addButton.getHeightUnits());
        newArea.addFocusListener(focusEvent -> {
            addButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        });
        newArea.addBlurListener(event -> {
            addButton.removeClickShortcut();
        });
        addButton.setEnabled(false);
        formLayout.addComponent(newArea);

        newName = new TextField();
        newName.focus();
        newName.addFocusListener(focusEvent -> {
            addButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        });
        newName.addBlurListener(event -> {
            addButton.removeClickShortcut();
        });
        newName.addValueChangeListener(valueChangeEvent -> {
                    if (newName.getValue().isEmpty())
                        addButton.setEnabled(false);
                    else
                        addButton.setEnabled(true);
                });
        formLayout.addComponentsAndExpand(newName);

        addComponent(formLayout);

        addButton.addClickListener(click -> addSite());

        newName.setTabIndex(1);
        newArea.setTabIndex(2);
        addButton.setTabIndex(3);

        return formLayout;

    }

    private void addSite() {
        if ((!newName.getValue().isEmpty())&&(selectCustomer.getValue()!=null)) {
            int newSiteArea = 0;
            if (((Integer)newArea.getValue())!=null)
                newSiteArea = ((Integer)newArea.getValue());
            long n = siteRepository.insertSite(newName.getValue(),newSiteArea,"",
                    (Integer) selectCustomer.getValue(),"","","");
            getUI().getPage().open("/editsite#"+String.valueOf(n),"_new2",
                    700,400,BorderStyle.NONE);
            newArea.setValue(0);
            newName.setValue("");
            newName.focus();
            removeComponent(dataGrid);
            showSelectedCustomer();
        }
    }

}
