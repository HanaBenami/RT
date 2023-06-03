package il.co.rtcohen.rt.app.ui.grids;

import com.vaadin.data.ValueProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Setter;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import il.co.rtcohen.rt.app.UIComponents;
import il.co.rtcohen.rt.dal.dao.Customer;
import il.co.rtcohen.rt.dal.dao.GeneralObject;
import il.co.rtcohen.rt.dal.repositories.CallRepository;
import il.co.rtcohen.rt.dal.repositories.CustomerRepository;
import il.co.rtcohen.rt.dal.repositories.CustomerTypeRepository;
import il.co.rtcohen.rt.dal.repositories.SiteRepository;

public class GeneralTypeGrid extends AbstractFilterGrid<GeneralObject> {

    public GeneralTypeGrid(CustomerRepository customerRepository,
                           CustomerTypeRepository customerTypeRepository,
                           SiteRepository siteRepository,
                           CallRepository callRepository) {
        super(customerRepository, Customer::new, "customers", null);
        this.customerTypeRepository = customerTypeRepository;
        this.siteRepository = siteRepository;
        this.callRepository = callRepository;
        this.initGrid();
    }

    protected void addColumns() {
        addCallsColumn();
        addSitesColumn();
        addActiveColumn();
        addCustomerTypeColumn();
        addNameColumn();
        addIdColumn();
    }

    protected void sort() {
        this.sort("nameColumn", SortDirection.ASCENDING);
    }

    private void addCallsColumn() {
        this.addComponentColumn(
                (ValueProvider<Customer, Component>) Customer -> {
                    if (null == Customer.getId()) {
                        return null;
                    } else {
                        int openCallsCounter = callRepository.countActiveCallsByCustomer(Customer.getId());
                        Button callsButton = AbstractFilterGrid.countingIcon(VaadinIcons.BELL_O, VaadinIcons.BELL, VaadinIcons.BELL, openCallsCounter);
                        callsButton.addClickListener(clickEvent ->
                                getUI().getNavigator().navigateTo("call/customer=" + Customer.getId()));
                        return callsButton;
                    }
                },
                85,
                "callsColumn",
                "calls"
        );
    }
    private void addSitesColumn() {
        this.addComponentColumn(
                (ValueProvider<Customer, Component>) Customer -> {
                    if (null == Customer.getId()) {
                        return null;
                    } else {
                        int n = siteRepository.getActiveIdByCustomer(Customer.getId()).size();
                        Button sitesButton = AbstractFilterGrid.countingIcon(VaadinIcons.FROWN_O, VaadinIcons.HOME_O, VaadinIcons.HOME, n);
                        sitesButton.addClickListener(clickEvent ->
                                getUI().getNavigator().navigateTo("site/customer=" + Customer.getId()));
                        return sitesButton;
                    }
                },
                85,
                "sitesColumn",
                "sites"
        );
    }

    private void addActiveColumn() {
        this.addBooleanColumn(
                (ValueProvider<Customer, Component>) Customer -> UIComponents.checkBox(Customer.isActive(),true),
                (ValueProvider<Customer, Boolean>) GeneralObject::isActive,
                (Setter<Customer, Boolean>) GeneralObject::setActive,
                "activeColumn",
                "active",
                Boolean.TRUE
        );
    }

    private void addCustomerTypeColumn() {
        this.addComboBoxColumn(
                customerTypeRepository,
                customerTypeRepository.getDbTableName(),
                (ValueProvider<Customer, String>) Customer -> {
                    GeneralObject customerType = customerTypeRepository.getItem(Customer.getCustomerTypeID());
                    return (null == customerType ? "" : customerType.getName());
                },
                (ValueProvider<Integer, String>) id -> customerTypeRepository.getItem(id).getName(),
                (ValueProvider<Customer, Integer>) Customer::getCustomerTypeID,
                (Setter<Customer, Integer>) Customer::setCustomerTypeID,
                130,
                "custTypeColumn",
                "custType"
        );
    }

    private void addNameColumn() {
        this.addTextColumn(
                Customer::getName,
                Customer::setName,
                230,
                "nameColumn",
                "name"
        );
    }

    private void addIdColumn() {
        this.addNumericColumn(
                Customer::getId,
                null,
                80,
                "idColumn",
                "id"
        );
    }
}
