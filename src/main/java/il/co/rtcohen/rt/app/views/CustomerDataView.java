package il.co.rtcohen.rt.app.views;

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
import il.co.rtcohen.rt.app.LanguageSettings;
import il.co.rtcohen.rt.app.UIComponents;
import il.co.rtcohen.rt.app.ui.UIPaths;
import il.co.rtcohen.rt.dal.dao.Customer;
import il.co.rtcohen.rt.dal.dao.GeneralType;
import il.co.rtcohen.rt.dal.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import il.co.rtcohen.rt.app.ui.components.CustomFilterGrid;
import org.springframework.stereotype.Repository;

import java.util.Map;

@SpringView(name = CustomersView.VIEW_NAME)
public class CustomersView extends AbstractDataView<Customer> {
    static final String VIEW_NAME = "customers";
    private static final Logger logger = LoggerFactory.getLogger(CustomersView.class);
    private Map<String,String> parametersMap;

    // Repositories
    private CustomerRepository customerRepository;
    private SiteRepository siteRepository;
    private ContactRepository contactRepository;
    private CallRepository callRepository;

    // Customers
    // New record
    private HorizontalLayout newCustomerLayout;
    private ComboBox<Integer> newCustomerType;
    private TextField newCustomerName;
    // Filters
    private TextField customerNameFilter;

    // Sites

    // Contacts

    // Vehicles

    @Autowired
    private CustomersView(ErrorHandler errorHandler,
                          GeneralRepository generalRepository,
                          CustomerRepository customerRepository,
                          SiteRepository siteRepository,
                          ContactRepository contactRepository,
                          CallRepository callRepository) { // TODO: Add vehicles repo
        super(errorHandler, generalRepository);
        this.customerRepository = customerRepository;
        this.contactRepository = contactRepository;
        this.siteRepository = siteRepository;
        this.callRepository = callRepository;
    }

    @Override
    public void createView(ViewChangeListener.ViewChangeEvent event) {
        title = LanguageSettings.getLocaleString("customersList");
        addHeader();
        addGrid();
        setTabIndexes();
    }

    private void addCallsColumn() {
        CustomFilterGrid.Column callsColumn =
                grid.addComponentColumn((ValueProvider<Customer, Component>) Customer -> {
                    if (null == Customer.getId()) {
                        return null;
                    } else {
                        int openCallsCounter = callRepository.countActiveCallsByCustomer(Customer.getId());
                        Button callsButton = UIComponents.gridSmallButton(VaadinIcons.BELL_O);
                        callsButton.addClickListener(clickEvent ->
                                getUI().getNavigator().navigateTo
                                        ("call/customer=" + Customer.getId()));
                        if (openCallsCounter > 0) {
                            callsButton.setIcon(VaadinIcons.BELL);
                            callsButton.setCaption(String.valueOf((openCallsCounter)));
                        }
                        return callsButton;
                    }
                });
        callsColumn.setId("callsColumn").setExpandRatio(1).setResizable(false).setWidth(85).setSortable(false);
        grid.getDefaultHeaderRow().getCell("callsColumn").setText(LanguageSettings.getLocaleString("calls"));
    }
    private void addSitesColumn() {
        CustomFilterGrid.Column sitesColumn =
                grid.addComponentColumn((ValueProvider<Customer, Component>) Customer -> {
                    if (null == Customer.getId()) {
                        return null;
                    } else {
                        int n = siteRepository.getActiveIdByCustomer(Customer.getId()).size();
                        Button sitesButton = UIComponents.gridSmallButton(VaadinIcons.FROWN_O);
                        sitesButton.addClickListener(clickEvent ->
                                getUI().getNavigator().navigateTo
                                        ("site/customer=" + Customer.getId()));
                        if (n == 1) {
                            sitesButton.setCaption(String.valueOf((n)));
                            sitesButton.setIcon(VaadinIcons.HOME_O);
                        } else if (n > 1) {
                            sitesButton.setCaption(String.valueOf((n)));
                            sitesButton.setIcon(VaadinIcons.HOME);
                        }
                        return sitesButton;
                    }
                });
        sitesColumn.setId("sitesColumn").setExpandRatio(1).setResizable(false).setWidth(85).setSortable(false);
        grid.getDefaultHeaderRow().getCell("sitesColumn").setText(LanguageSettings.getLocaleString("sites"));
    }

    private void addActiveColumn() {
         grid.addBooleanColumn(
                  (ValueProvider<Customer, Component>) Customer -> UIComponents.checkBox(Customer.getActive(),true),
                  (ValueProvider<Customer, Boolean>) GeneralType::getActive,
                  (Setter<Customer, Boolean>) (Customer, Boolean) -> {
                            Customer.setActive(Boolean);
//                            customerRepository.updateItem(Customer);
                        },
                  "activeColumn",
                 "active",
                 Boolean.TRUE
         );
    }




    private void addCustomerTypeColumn() {
        ComboBox<Integer> customerTypeCombo = new UIComponents().custTypeComboBox(generalRepository,130,30);
        customerTypeCombo.setEmptySelectionAllowed(false);
        CustomFilterGrid.Column<Customer, String> customerTypeColumn = grid.addColumn(
                Customer -> generalRepository.getNameById(Customer.getCustomerTypeID(),"custType")).setId("custTypeColumn")
                .setWidth(200).setEditorBinding(grid.getEditor().getBinder().forField(customerTypeCombo).bind(
                        (ValueProvider<Customer, Integer>) Customer::getCustomerTypeID,
                        (Setter<Customer, Integer>) (Customer, integer) -> {
                            Customer.setCustomerTypeID(integer);
//                            customerRepository.updateItem(Customer);
                        }
                )).setExpandRatio(1).setResizable(false);
        ComboBox<Integer> filterCustomerType = new UIComponents().custTypeComboBox(generalRepository,130,30);
        filterCustomerType.setWidth("95%");
        customerTypeColumn.setFilter((filterCustomerType),
                (cValue, fValue) -> fValue == null || generalRepository.getNameById(fValue,"custType").equals(cValue));
        grid.getDefaultHeaderRow().getCell("custTypeColumn").setText(LanguageSettings.getLocaleString("callType"));
    }
    private void addNameColumn() {
        CustomFilterGrid.Column<Customer, String> nameColumn = grid.addColumn(Customer::getName).setId("nameColumn")
                .setEditorComponent(new TextField(), (Customer, String) -> {
                    Customer.setName(String);
//                    generalRepository.update(Customer);
                }).setExpandRatio(1).setResizable(false).setMinimumWidth(230);
        customerNameFilter = UIComponents.textField(30);
        nameColumn.setFilter(customerNameFilter, UIComponents.stringFilter());
        grid.getDefaultHeaderRow().getCell("nameColumn").setText(LanguageSettings.getLocaleString("name"));
        customerNameFilter.setWidth("95%");
    }
    private void addIdColumn() {
        CustomFilterGrid.Column<Customer, Integer> idColumn = grid.addColumn(Customer::getId).setId("idColumn")
                .setWidth(80).setResizable(false);
        TextField filterId = UIComponents.textField(30);
        idColumn.setFilter(filterId, UIComponents.integerFilter());
        filterId.setWidth("95%");
        grid.getDefaultHeaderRow().getCell("idColumn").setText(LanguageSettings.getLocaleString("id"));
    }

    @Override
    void addColumns() {
        addCallsColumn();
        addSitesColumn();
        addActiveColumn();
        addCustomerTypeColumn();
        addNameColumn();
        addIdColumn();
    }

    @Override
    void addGrid() {

        initGrid("");
        grid.setGridRepository(customerRepository);
        grid.setNewItemSupplier(Customer::new);
        grid.setObjectName("customers");
        grid.populateGrid(true);

//        UI.getCurrent().setPollInterval(5000);
//        UI.getCurrent().addPollListener((UIEvents.PollListener) event ->
//            grid.setItems(customerRepository.getItems()));
        addColumns();
        grid.getEditor().setEnabled(true);
        grid.sort("nameColumn", SortDirection.ASCENDING);
        grid.setStyleGenerator((StyleGenerator<Customer>) Customer -> {
            if (generalRepository.getNameById(Customer.getCustomerTypeID(), "custType")
                    .equals(LanguageSettings.getLocaleString("privateCustomerType")))
                return "yellow";
            return null;
        });
        grid.setWidth("70%");
        addComponentsAndExpand(grid);
        addComponent(grid.newLinesLayout());
    }

    @Override
    void setTabIndexes() {
        grid.setTabIndex(1);
    }

    private void addRefreshButton() {
        Button refresh = UIComponents.refreshButton();
        refresh.addClickListener(clickEvent -> refreshData());
    }
    private void refreshData() {
        removeComponent(grid);
        addGrid();
        setTabIndexes();
    }

}
