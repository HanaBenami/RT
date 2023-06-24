package il.co.rtcohen.rt.app.grids;

import com.vaadin.data.ValueProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import il.co.rtcohen.rt.app.uiComponents.columns.CustomCheckBoxColumn;
import il.co.rtcohen.rt.app.uiComponents.columns.CustomComboBoxColumn;
import il.co.rtcohen.rt.app.uiComponents.columns.CustomComponentColumn;
import il.co.rtcohen.rt.app.uiComponents.columns.CustomIntegerColumn;
import il.co.rtcohen.rt.app.uiComponents.fields.CustomButton;
import il.co.rtcohen.rt.app.uiComponents.fields.CustomComboBox;
import il.co.rtcohen.rt.dal.dao.*;
import il.co.rtcohen.rt.dal.repositories.*;

public class CustomerGrid extends AbstractTypeWithNameAndActiveFieldsGrid<Customer> {
    private final Customer selectedCustomer;
    private final CustomerTypeRepository customerTypeRepository;
    private final SiteRepository siteRepository;
    private final CallRepository callRepository;

    public CustomerGrid(
            Customer selectedCustomer,
            CustomerRepository customerRepository,
            CustomerTypeRepository customerTypeRepository,
            SiteRepository siteRepository,
            CallRepository callRepository
    ) {
        super(
                customerRepository,
                Customer::new,
                "customers",
                customer -> null != selectedCustomer && !selectedCustomer.getId().equals(customer.getId())
        );
        this.selectedCustomer = selectedCustomer;
        this.customerTypeRepository = customerTypeRepository;
        this.siteRepository = siteRepository;
        this.callRepository = callRepository;
        this.setShowNewNameField(true);
    }

    @Override
    protected void addColumns() {
        addActiveColumn();
        addCallsColumn();
        addSitesColumn();
        addHashKeyColumn();
        addCustomerTypeColumn();
        addNameColumn();
        addIdColumn();
    }

    private void addCallsColumn() {
        addCallsColumn(
                customer -> callRepository.getItems(customer, null, null, false).size(),
                "customer"
        );
    }

    private void addSitesColumn() {
        CustomComponentColumn<Customer, Component> column = CustomComponentColumn.addToGrid(
                (ValueProvider<Customer, Component>) Customer -> {
                    if (null == Customer.getId()) {
                        return null;
                    } else {
                        int n = 0;
                        n = siteRepository.getItems(Customer).size();
                        Button sitesButton = CustomButton.countingIcon(VaadinIcons.FROWN_O, VaadinIcons.HOME_O, VaadinIcons.HOME, n);
                        sitesButton.addClickListener(clickEvent ->
                                getUI().getNavigator().navigateTo("site/customer=" + Customer.getId()));
                        return sitesButton;
                    }
                },
                85,
                "sitesColumn",
                "sites",
                this
        );
        column.getColumn().setHidden(true);
    }

    private void addHashKeyColumn() {
        CustomIntegerColumn.addToGrid(
                Customer::getHashavshevetCustomerId,
                Customer::setHashavshevetCustomerId,
                null, null, 100,
                "hashKeyColumn",
                "hashKey",
            false,
                true,
            true,
            this
        );
    }

    private void addCustomerTypeColumn() {
        CustomComboBoxColumn.addToGrid(
                CustomComboBox.getComboBox(customerTypeRepository),
                CustomComboBox.getComboBox(customerTypeRepository),
                Customer::getCustomerType,
                Customer::setCustomerType,
                true,
                100,
                "custTypeColumn",
                "custType",
                this
        );
    }

    @Override
    protected CustomCheckBoxColumn<Customer> addActiveColumn() {
        CustomCheckBoxColumn<Customer> column = super.addActiveColumn();
        column.getColumn().setHidden(true);
        return column;
    }

    @Override
    protected void addIdColumn() {
        super.addIdColumn();
        this.idColumn.getColumn().setHidden(true);
    }
}
