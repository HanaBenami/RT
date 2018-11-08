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
import org.vaadin.addons.filteringgrid.filters.InMemoryFilter;

import java.util.List;

@SpringView(name = CustomerView.VIEW_NAME)
public class CustomerView extends AbstractDataView {
    public static final String VIEW_NAME = "customer";
    private TextField filterName;
    private ComboBox<Integer> newCustomerType;
    private TextField newName;
    private CallRepository callRepository;
    private SiteRepository siteRepository;
    private CustomerRepository customerRepository;

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

    private void addGrid(CustomerRepository repository) {
        FilterGrid<Customer> grid = UIcomponents.myGrid("");

        //data
        List<Customer> list = repository.getCustomers();
        grid.setItems(list);
        UI.getCurrent().setPollInterval(3000);
        UI.getCurrent().addPollListener((UIEvents.PollListener) event -> {
            grid.setItems(repository.getCustomers());;
        });

        TextField id = new TextField();
        ComboBox<Integer> custTypeCombo = new UIcomponents().custTypeComboBox(generalRepository,130,30);
        custTypeCombo.setEmptySelectionAllowed(false);
        TextField name = new TextField();
        CheckBox active = new CheckBox();

        //columns

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

        FilterGrid.Column activeColumn =
                grid.addComponentColumn((ValueProvider<Customer, Component>) Customer ->
                        UIcomponents.checkBox(Customer.getActive(),true));
        activeColumn.setId("activeColumn").setExpandRatio(1).setResizable(false).setWidth(70);
        activeColumn.setEditorBinding(grid.getEditor().getBinder().forField(active).bind(
                (ValueProvider<Customer, Boolean>) GeneralType::getActive,
                (Setter<Customer, Boolean>) (Customer, Boolean) -> {
                    Customer.setActive(Boolean);
                    generalRepository.update(Customer);
                }));

        FilterGrid.Column<Customer, String> custTypeColumn = grid.addColumn(Customer -> generalRepository.getNameById(Customer.getCustomerTypeID(),"custType")).setId("custTypeColumn")
            .setWidth(200).setEditorBinding(grid.getEditor().getBinder().forField(custTypeCombo).bind(
                        (ValueProvider<Customer, Integer>) Customer::getCustomerTypeID,
                        (Setter<Customer, Integer>) (Customer, integer) -> {
                            Customer.setCustomerTypeID(integer);
                            customerRepository.updateCustomerType(Customer);
                        }
                )).setExpandRatio(1).setResizable(false);

        FilterGrid.Column<Customer, String> nameColumn = grid.addColumn(Customer::getName).setId("nameColumn")
                .setEditorComponent(name, (Customer, String) -> {
                    Customer.setName(String);
                    generalRepository.update(Customer);
                }).setExpandRatio(1).setResizable(false).setMinimumWidth(230);

        FilterGrid.Column<Customer, Integer> idColumn = grid.addColumn(Customer::getId).setId("idColumn")
                .setWidth(80).setResizable(false);
        grid.getEditor().setEnabled(true);

        //headers
        grid.sort("nameColumn", SortDirection.ASCENDING);
        grid.getDefaultHeaderRow().getCell("callsColumn").setText("קריאות");
        grid.getDefaultHeaderRow().getCell("sitesColumn").setText("אתרים");
        grid.getDefaultHeaderRow().getCell("activeColumn").setText("פעיל");
        grid.getDefaultHeaderRow().getCell("custTypeColumn").setText("סוג");
        grid.getDefaultHeaderRow().getCell("idColumn").setText("#");
        grid.getDefaultHeaderRow().getCell("nameColumn").setText("שם");

        //style
        grid.setStyleGenerator((StyleGenerator<Customer>) Customer -> {
            if (generalRepository.getNameById(Customer.getCustomerTypeID(), "custType").equals("פרטי"))
                return "yellow";
            return null;
        });


        //filters
        TextField filterId = UIcomponents.textField(30);
        idColumn.setFilter(filterId, InMemoryFilter.StringComparator.containsIgnoreCase());
        filterId.setWidth("95%");
        filterName = UIcomponents.textField(30);
        nameColumn.setFilter(filterName, InMemoryFilter.StringComparator.containsIgnoreCase());
        filterName.setWidth("95%");
        ComboBox filtercustType = new UIcomponents().custTypeComboBox(generalRepository,130,30);
        filtercustType.setWidth("95%");
        custTypeColumn.setFilter((filtercustType),
                (cValue, fValue) -> fValue == null || generalRepository.getNameById((Integer)fValue,"custType").equals(cValue));
        CheckBox filterActive = new CheckBox();
        filterActive.setValue(true);
        activeColumn.setFilter(UIcomponents.BooleanValueProvider(),
            filterActive, UIcomponents.BooleanPredicate());

        dataGrid=grid;
        dataGrid.setWidth("70%");
        addComponentsAndExpand(dataGrid);
    }

    private void addForm() {
        HorizontalLayout formLayout = new HorizontalLayout();
        formLayout.setWidth("70%");

        Button refresh = UIcomponents.refreshButton();
        refresh.addClickListener(clickEvent -> refresh());
        formLayout.addComponent(refresh);
        formLayout.setComponentAlignment(refresh,Alignment.MIDDLE_LEFT);

        Label space = new Label("");
        space.setWidth("150");
        formLayout.addComponent(space);
        formLayout.setComponentAlignment(space,Alignment.MIDDLE_LEFT);

        Button addButton = UIcomponents.addButton();
        formLayout.addComponent(addButton);
        addButton.setEnabled(false);

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
        newName.setWidth("450");
        newName.addValueChangeListener(valueChangeEvent ->
                filterName.setValue(newName.getValue()));
        formLayout.addComponentsAndExpand(newName);

        addComponent(formLayout);

        addButton.addClickListener(click -> addSite());

        newName.setTabIndex(1);
        newCustomerType.setTabIndex(2);
        addButton.setTabIndex(3);

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
