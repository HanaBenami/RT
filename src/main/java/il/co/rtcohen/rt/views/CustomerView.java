package il.co.rtcohen.rt.views;

import com.vaadin.data.ValueProvider;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.UIEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.Page;
import com.vaadin.server.Setter;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.BorderStyle;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import il.co.rtcohen.rt.UIComponents;
import il.co.rtcohen.rt.dao.Customer;
import il.co.rtcohen.rt.dao.GeneralType;
import il.co.rtcohen.rt.repositories.CallRepository;
import il.co.rtcohen.rt.repositories.CustomerRepository;
import il.co.rtcohen.rt.repositories.GeneralRepository;
import il.co.rtcohen.rt.repositories.SiteRepository;
import il.co.rtcohen.rt.ui.UIPaths;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.addons.filteringgrid.FilterGrid;

@SpringView(name = CustomerView.VIEW_NAME)
public class CustomerView extends AbstractDataView<Customer> {
    static final String VIEW_NAME = "customer";
    private TextField filterName;
    private ComboBox<Integer> newCustomerType;
    private TextField newName;
    private CallRepository callRepository;
    private SiteRepository siteRepository;
    private CustomerRepository customerRepository;
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
        addNewCustomerForm();
        addGrid(customerRepository);
    }

    private void addCallsColumn() {
        FilterGrid.Column callsColumn =
                grid.addComponentColumn((ValueProvider<Customer, Component>) Customer -> {
                    int n=callRepository.countActiveCallsByCustomer(Customer.getId());
                    Button callsButton = UIComponents.gridSmallButton(VaadinIcons.BELL_O);
                    callsButton.addClickListener(clickEvent ->
                            getUI().getNavigator().navigateTo
                                    ("call/customer="+ Customer.getId()));
                    if(n>0) {
                        callsButton.setIcon(VaadinIcons.BELL);
                        callsButton.setCaption(String.valueOf((n)));
                    }
                    return callsButton;
                });
        callsColumn.setId("callsColumn").setExpandRatio(1).setResizable(false).setWidth(85).setSortable(false);
        grid.getDefaultHeaderRow().getCell("callsColumn").setText("קריאות");
    }
    private void addSitesColumn() {
        FilterGrid.Column sitesColumn =
                grid.addComponentColumn((ValueProvider<Customer, Component>) Customer -> {
                    int n=siteRepository.getActiveIdByCustomer(Customer.getId()).size();
                    Button sitesButton = UIComponents.gridSmallButton(VaadinIcons.FROWN_O);
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
        sitesColumn.setId("sitesColumn").setExpandRatio(1).setResizable(false).setWidth(85).setSortable(false);
        grid.getDefaultHeaderRow().getCell("sitesColumn").setText("אתרים");
    }
    private void addActiveColumn() {
        FilterGrid.Column<Customer, Component> activeColumn =
                grid.addComponentColumn((ValueProvider<Customer, Component>) Customer ->
                        UIComponents.checkBox(Customer.getActive(),true));
        activeColumn.setId("activeColumn").setExpandRatio(1).setResizable(false).setWidth(70).setSortable(false);
        activeColumn.setEditorBinding(grid.getEditor().getBinder().forField(new CheckBox()).bind(
                (ValueProvider<Customer, Boolean>) GeneralType::getActive,
                (Setter<Customer, Boolean>) (Customer, Boolean) -> {
                    Customer.setActive(Boolean);
                    generalRepository.update(Customer);
                }));
        CheckBox filterActive = new CheckBox();
        filterActive.setValue(true);
        activeColumn.setFilter(UIComponents.BooleanValueProvider(),
                filterActive, UIComponents.BooleanPredicate());
        grid.getDefaultHeaderRow().getCell("activeColumn").setText("פעיל");
    }
    private void addCustomerTypeColumn() {
        ComboBox<Integer> customerTypeCombo = new UIComponents().custTypeComboBox(generalRepository,130,30);
        customerTypeCombo.setEmptySelectionAllowed(false);
        FilterGrid.Column<Customer, String> customerTypeColumn = grid.addColumn(Customer -> generalRepository.getNameById(Customer.getCustomerTypeID(),"custType")).setId("custTypeColumn")
                .setWidth(200).setEditorBinding(grid.getEditor().getBinder().forField(customerTypeCombo).bind(
                        (ValueProvider<Customer, Integer>) Customer::getCustomerTypeID,
                        (Setter<Customer, Integer>) (Customer, integer) -> {
                            Customer.setCustomerTypeID(integer);
                            customerRepository.updateCustomerType(Customer);
                        }
                )).setExpandRatio(1).setResizable(false);
        ComboBox<Integer> filterCustomerType = new UIComponents().custTypeComboBox(generalRepository,130,30);
        filterCustomerType.setWidth("95%");
        customerTypeColumn.setFilter((filterCustomerType),
                (cValue, fValue) -> fValue == null || generalRepository.getNameById(fValue,"custType").equals(cValue));
        grid.getDefaultHeaderRow().getCell("custTypeColumn").setText("סוג");
    }
    private void addNameColumn() {
        FilterGrid.Column<Customer, String> nameColumn = grid.addColumn(Customer::getName).setId("nameColumn")
                .setEditorComponent(new TextField(), (Customer, String) -> {
                    Customer.setName(String);
                    generalRepository.update(Customer);
                }).setExpandRatio(1).setResizable(false).setMinimumWidth(230);
        filterName = UIComponents.textField(30);
        nameColumn.setFilter(filterName, UIComponents.stringFilter());
        grid.getDefaultHeaderRow().getCell("nameColumn").setText("שם");
        filterName.setWidth("95%");
    }
    private void addIdColumn() {
        FilterGrid.Column<Customer, Integer> idColumn = grid.addColumn(Customer::getId).setId("idColumn")
                .setWidth(80).setResizable(false);
        TextField filterId = UIComponents.textField(30);
        idColumn.setFilter(filterId, UIComponents.integerFilter());
        filterId.setWidth("95%");
        grid.getDefaultHeaderRow().getCell("idColumn").setText("#");
    }
    private void addColumns() {
        addCallsColumn();
        addSitesColumn();
        addActiveColumn();
        addCustomerTypeColumn();
        addNameColumn();
        addIdColumn();
    }

    private void addGrid(CustomerRepository repository) {
        initGrid("");
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
        grid.setWidth("70%");
        addComponentsAndExpand(grid);
    }

    private void addNewCustomerForm() {
        formLayout = new HorizontalLayout();
        formLayout.setWidth("70%");
        addRefreshButton();
        Label space = new Label("");
        space.setWidth("150");
        formLayout.addComponent(space);
        formLayout.setComponentAlignment(space,Alignment.MIDDLE_LEFT);
        formLayout.addComponent(addButton);
        addNewCustomerTypeField();
        addNewCustomerNameField();
        addComponent(formLayout);
        addButton.addClickListener(click -> addSite());
        newName.setTabIndex(1);
        newCustomerType.setTabIndex(2);
        addButton.setTabIndex(3);
    }

    private void addRefreshButton() {
        Button refresh = UIComponents.refreshButton();
        refresh.addClickListener(clickEvent -> refreshData());
        formLayout.addComponent(refresh);
        formLayout.setComponentAlignment(refresh,Alignment.MIDDLE_LEFT);
    }

    private void addNewCustomerTypeField() {
        newCustomerType = new UIComponents().custTypeComboBox(generalRepository,130,30);
        newCustomerType.setValue(0);
        newCustomerType.setHeight(addButton.getHeight(),addButton.getHeightUnits());
        newCustomerType.addFocusListener(focusEvent -> addButton.setClickShortcut(ShortcutAction.KeyCode.ENTER));
        newCustomerType.addBlurListener(event -> addButton.removeClickShortcut());
        formLayout.addComponent(newCustomerType);
    }

    private void addNewCustomerNameField() {
        newName = super.addNewNameField();
        newName.setWidth("450");
        newName.addValueChangeListener(valueChangeEvent ->
                filterName.setValue(newName.getValue()));
        formLayout.addComponentsAndExpand(newName);
    }

    private void addSite() {
        if (!newName.getValue().isEmpty()) {
            long n;
            if (newCustomerType.getValue() ==0)
                n = customerRepository.insertCustomer(newName.getValue(), 0);
            else
                n = customerRepository.insertCustomer(newName.getValue(), newCustomerType.getValue());
            long siteID = siteRepository.insertSite("",0,"",
                    (Integer.parseInt(String.valueOf(n))),"","","");
            Page.getCurrent().open(UIPaths.EDITSITE.getPath()+String.valueOf(siteID),"_new2",
                    700,400,
                    BorderStyle.NONE);
            refreshData();
        }
    }

    private void refreshData() {
        newCustomerType.setValue(0);
        newName.setValue("");
        newName.focus();
        removeComponent(grid);
        addGrid(customerRepository);
    }

}
