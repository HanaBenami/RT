package il.co.rtcohen.rt.views;

import com.vaadin.data.ValueProvider;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.UIEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.Setter;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.BorderStyle;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import il.co.rtcohen.rt.UIcomponents;
import il.co.rtcohen.rt.dao.Customer;
import il.co.rtcohen.rt.dao.GeneralType;
import il.co.rtcohen.rt.repositories.CallRepository;
import il.co.rtcohen.rt.repositories.CustomerRepository;
import il.co.rtcohen.rt.repositories.GeneralRepository;
import il.co.rtcohen.rt.repositories.SiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.addons.filteringgrid.FilterGrid;

@SpringView(name = CustomerView.VIEW_NAME)
public class CustomerView extends AbstractDataView {
    static final String VIEW_NAME = "customer";
    private TextField filterName;
    private ComboBox<Integer> newCustomerType;
    private TextField newName;
    private CallRepository callRepository;
    private SiteRepository siteRepository;
    private CustomerRepository customerRepository;
    private FilterGrid<Customer> grid;
    private HorizontalLayout formLayout;

    @Autowired
    private CustomerView(ErrorHandler errorHandler, CallRepository callRepository, SiteRepository siteRepository, GeneralRepository generalRepository, CustomerRepository customerRepository) {
        super(errorHandler,generalRepository);
        this.callRepository=callRepository;
        this.siteRepository=siteRepository;
        this.customerRepository=customerRepository;
    }

    @Override
    public void createView(ViewChangeListener.ViewChangeEvent event) {
        title="רשימת לקוחות";
        addHeader();
        addForm();
        addGrid(customerRepository);
    }

    private void callsColumn() {
        FilterGrid.Column callsColumn =
                grid.addComponentColumn((ValueProvider<Customer, Component>) Customer -> {
                    int n=callRepository.countActiveCallsByCustomer(Customer.getId());
                    Button callsButton = UIcomponents.gridSmallButton(VaadinIcons.BELL_O);
                    callsButton.addClickListener(clickEvent ->
                            getUI().getNavigator().navigateTo
                                    ("call/customer="+ Customer.getId()));
                    if(n>0) {
                        callsButton.setIcon(VaadinIcons.BELL);
                        callsButton.setCaption(String.valueOf((n)));
                    }
                    return callsButton;
                });
        callsColumn.setId("callsColumn").setExpandRatio(1).setResizable(false).setWidth(85);
        grid.getDefaultHeaderRow().getCell("callsColumn").setText("קריאות");
    }
    private void sitesColumn() {
        FilterGrid.Column sitesColumn =
                grid.addComponentColumn((ValueProvider<Customer, Component>) Customer -> {
                    int n=siteRepository.getActiveIdByCustomer(Customer.getId()).size();
                    Button sitesButton = UIcomponents.gridSmallButton(VaadinIcons.FROWN_O);
                    sitesButton.addClickListener(clickEvent ->
                            getUI().getNavigator().navigateTo
                                    ("site/customer="+ Customer.getId()));
                    if(n==1) {
                        sitesButton.setCaption(String.valueOf((n)));
                        sitesButton.setIcon(VaadinIcons.HOME_O);
                    }
                    else  if (n>1) {
                        sitesButton.setCaption(String.valueOf((n)));
                        sitesButton.setIcon(VaadinIcons.HOME);
                    }
                    return sitesButton;
                });
        sitesColumn.setId("sitesColumn").setExpandRatio(1).setResizable(false).setWidth(85);
        grid.getDefaultHeaderRow().getCell("sitesColumn").setText("אתרים");
    }
    private void activeColumn() {
        FilterGrid.Column activeColumn =
                grid.addComponentColumn((ValueProvider<Customer, Component>) Customer ->
                        UIcomponents.checkBox(Customer.getActive(),true));
        activeColumn.setId("activeColumn").setExpandRatio(1).setResizable(false).setWidth(70);
        activeColumn.setEditorBinding(grid.getEditor().getBinder().forField(new CheckBox()).bind(
                (ValueProvider<Customer, Boolean>) GeneralType::getActive,
                (Setter<Customer, Boolean>) (Customer, Boolean) -> {
                    Customer.setActive(Boolean);
                    generalRepository.update(Customer);
                }));
        CheckBox filterActive = new CheckBox();
        filterActive.setValue(true);
        activeColumn.setFilter(UIcomponents.BooleanValueProvider(),
                filterActive, UIcomponents.BooleanPredicate());
        grid.getDefaultHeaderRow().getCell("activeColumn").setText("פעיל");
    }
    private void customerTypeColumn() {
        ComboBox<Integer> customerTypeCombo = new UIcomponents().custTypeComboBox(generalRepository,130,30);
        customerTypeCombo.setEmptySelectionAllowed(false);
        FilterGrid.Column<Customer, String> customerTypeColumn = grid.addColumn(Customer -> generalRepository.getNameById(Customer.getCustomerTypeID(),"custType")).setId("custTypeColumn")
                .setWidth(200).setEditorBinding(grid.getEditor().getBinder().forField(customerTypeCombo).bind(
                        (ValueProvider<Customer, Integer>) Customer::getCustomerTypeID,
                        (Setter<Customer, Integer>) (Customer, integer) -> {
                            Customer.setCustomerTypeID(integer);
                            customerRepository.updateCustomerType(Customer);
                        }
                )).setExpandRatio(1).setResizable(false);
        ComboBox filterCustomerType = new UIcomponents().custTypeComboBox(generalRepository,130,30);
        filterCustomerType.setWidth("95%");
        customerTypeColumn.setFilter((filterCustomerType),
                (cValue, fValue) -> fValue == null || generalRepository.getNameById((Integer)fValue,"custType").equals(cValue));
        grid.getDefaultHeaderRow().getCell("custTypeColumn").setText("סוג");
    }
    private void nameColumn() {
        FilterGrid.Column<Customer, String> nameColumn = grid.addColumn(Customer::getName).setId("nameColumn")
                .setEditorComponent(new TextField(), (Customer, String) -> {
                    Customer.setName(String);
                    generalRepository.update(Customer);
                }).setExpandRatio(1).setResizable(false).setMinimumWidth(230);
        filterName = UIcomponents.textField(30);
        nameColumn.setFilter(filterName, UIcomponents.stringFilter());
        grid.getDefaultHeaderRow().getCell("nameColumn").setText("שם");
        filterName.setWidth("95%");
    }
    private void idColumn() {
        FilterGrid.Column<Customer, Integer> idColumn = grid.addColumn(Customer::getId).setId("idColumn")
                .setWidth(80).setResizable(false);
        TextField filterId = UIcomponents.textField(30);
        idColumn.setFilter(filterId, UIcomponents.textFilter());
        filterId.setWidth("95%");
        grid.getDefaultHeaderRow().getCell("idColumn").setText("#");
    }
    private void addColumns() {
        callsColumn();
        sitesColumn();
        activeColumn();
        customerTypeColumn();
        nameColumn();
        idColumn();
    }

    private void addGrid(CustomerRepository repository) {
        grid = UIcomponents.myGrid("");
        grid.setItems(repository.getCustomers());
        UI.getCurrent().setPollInterval(3000);
        UI.getCurrent().addPollListener((UIEvents.PollListener) event ->
            grid.setItems(repository.getCustomers()));
        addColumns();
        grid.getEditor().setEnabled(true);
        grid.sort("nameColumn", SortDirection.ASCENDING);
        grid.setStyleGenerator((StyleGenerator<Customer>) Customer -> {
            if (generalRepository.getNameById(Customer.getCustomerTypeID(), "custType").equals("פרטי"))
                return "yellow";
            return null;
        });
        dataGrid=grid;
        dataGrid.setWidth("70%");
        addComponentsAndExpand(dataGrid);
    }

    private void addForm() {
        formLayout = new HorizontalLayout();
        formLayout.setWidth("70%");
        refreshButton();
        Label space = new Label("");
        space.setWidth("150");
        formLayout.addComponent(space);
        formLayout.setComponentAlignment(space,Alignment.MIDDLE_LEFT);
        formLayout.addComponent(addButton);
        newCustomerType();
        setNewName();
        addComponent(formLayout);
        addButton.addClickListener(click -> addSite());
        newName.setTabIndex(1);
        newCustomerType.setTabIndex(2);
        addButton.setTabIndex(3);
    }

    private void refreshButton() {
        Button refresh = UIcomponents.refreshButton();
        refresh.addClickListener(clickEvent -> refresh());
        formLayout.addComponent(refresh);
        formLayout.setComponentAlignment(refresh,Alignment.MIDDLE_LEFT);
    }

    private void newCustomerType() {
        newCustomerType = new UIcomponents().custTypeComboBox(generalRepository,130,30);
        newCustomerType.setValue(0);
        newCustomerType.setHeight(addButton.getHeight(),addButton.getHeightUnits());
        newCustomerType.addFocusListener(focusEvent -> {
            addButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        });
        newCustomerType.addBlurListener(event -> {
            addButton.removeClickShortcut();
        });
        formLayout.addComponent(newCustomerType);
    }

    private void setNewName() {
        newName = super.newName();
        newName.setWidth("450");
        newName.addValueChangeListener(valueChangeEvent ->
                filterName.setValue(newName.getValue()));
        formLayout.addComponentsAndExpand(newName);
    }

    private void addSite() {
        if (!newName.getValue().isEmpty()) {
            long n = 0;
            if (((Integer) newCustomerType.getValue())==0)
                n = customerRepository.insertCustomer(newName.getValue(), 0);
            else
                n = customerRepository.insertCustomer(newName.getValue(), ((Integer) newCustomerType.getValue()));
            long siteID = siteRepository.insertSite("",0,"",
                    (Integer.parseInt(String.valueOf(n))),"","","");
            getUI().getPage().open("/editsite#"+String.valueOf(siteID),"_new2",
                    700,400,
                    BorderStyle.NONE);
            refresh();
        }
    }

    private void refresh () {
        newCustomerType.setValue(0);
        newName.setValue("");
        newName.focus();
        removeComponent(dataGrid);
        addGrid(customerRepository);
    }

}
