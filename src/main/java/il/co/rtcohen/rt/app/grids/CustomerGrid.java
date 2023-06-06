package il.co.rtcohen.rt.app.grids;

import com.vaadin.data.ValueProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Setter;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import il.co.rtcohen.rt.app.UiComponents.CustomComboBox;
import il.co.rtcohen.rt.app.UiComponents.CustomComboBoxColumn;
import il.co.rtcohen.rt.app.UiComponents.UIComponents;
import il.co.rtcohen.rt.dal.dao.*;
import il.co.rtcohen.rt.dal.repositories.*;

public class CustomerGrid extends AbstractTypeFilterGrid<Customer> {
    private final Customer selectedCustomer;
    private final CustomerTypeRepository customerTypeRepository;
    private final SiteRepository siteRepository;
    private final CallRepository callRepository;

    public CustomerGrid(Customer selectedCustomer,
                        CustomerRepository customerRepository,
                        CustomerTypeRepository customerTypeRepository,
                        SiteRepository siteRepository,
                        CallRepository callRepository) {
        super(customerRepository, Customer::new, "customers",
              customer -> null != selectedCustomer && !selectedCustomer.getId().equals(customer.getId()));
        this.selectedCustomer = selectedCustomer;
        this.customerTypeRepository = customerTypeRepository;
        this.siteRepository = siteRepository;
        this.callRepository = callRepository;
        this.initGrid();
    }

    protected void addColumns() {
        addActiveColumn();
        addCallsColumn();
        addSitesColumn();
        addCustomerTypeColumn();
        addNameColumn();
        addIdColumn();
    }

    private void addCallsColumn() {
        this.addComponentColumn(
                (ValueProvider<Customer, Component>) Customer -> {
                    if (null == Customer.getId()) {
                        return null;
                    } else {
                        int openCallsCounter = callRepository.countActiveCallsByCustomer(Customer.getId());
                        Button callsButton = countingIcon(VaadinIcons.BELL_O, VaadinIcons.BELL, VaadinIcons.BELL, openCallsCounter);
                        callsButton.addClickListener(clickEvent ->
                                getUI().getNavigator().navigateTo("call/customer=" + Customer.getId()));
                        return callsButton;
                    }
                },
                60,
                "callsColumn",
                "calls"
        );
    }
    private void addSitesColumn() {
        Column<Customer, Component> column = this.addComponentColumn(
                (ValueProvider<Customer, Component>) Customer -> {
                    if (null == Customer.getId()) {
                        return null;
                    } else {
                        int n = siteRepository.getItems(Customer).size();
                        Button sitesButton = AbstractTypeFilterGrid.countingIcon(VaadinIcons.FROWN_O, VaadinIcons.HOME_O, VaadinIcons.HOME, n);
                        sitesButton.addClickListener(clickEvent ->
                                getUI().getNavigator().navigateTo("site/customer=" + Customer.getId()));
                        return sitesButton;
                    }
                },
                85,
                "sitesColumn",
                "sites"
        );
        column.setHidden(true);
    }

    private void addActiveColumn() {
        this.addBooleanColumn(
                (ValueProvider<Customer, Component>) Customer -> UIComponents.checkBox(Customer.isActive(),true),
                (ValueProvider<Customer, Boolean>) AbstractTypeWithNameAndActiveFields::isActive,
                (Setter<Customer, Boolean>) AbstractTypeWithNameAndActiveFields::setActive,
                "activeColumn",
                "active",
                Boolean.TRUE
        );
    }

    private void addCustomerTypeColumn() {
        CustomComboBoxColumn.addToGrid(
                CustomComboBox.customerTypeComboBox(customerTypeRepository),
                CustomComboBox.customerTypeComboBox(customerTypeRepository),
                (ValueProvider<Customer, String>) customer -> {
                    AbstractTypeWithNameAndActiveFields customerType = customer.getCustomerType();
                    return (null == customerType ? "" : customerType.getName());
                },
                (ValueProvider<Customer, CustomerType>) Customer::getCustomerType,
                (Setter<Customer, CustomerType>) Customer::setCustomerType,
                230,
                "custTypeColumn",
                "custType",
                this
        );
    }

    private void addNameColumn() {
        this.addTextColumn(
                Customer::getName,
                Customer::setName,
                180,
                "nameColumn",
                "name"
        );
    }
}
