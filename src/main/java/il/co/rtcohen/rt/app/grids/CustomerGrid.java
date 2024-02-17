package il.co.rtcohen.rt.app.grids;

import com.vaadin.data.ValueProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import il.co.rtcohen.rt.app.LanguageSettings;
import il.co.rtcohen.rt.app.uiComponents.columns.CustomCheckBoxColumn;
import il.co.rtcohen.rt.app.uiComponents.columns.CustomComboBoxColumn;
import il.co.rtcohen.rt.app.uiComponents.columns.CustomComponentColumn;
import il.co.rtcohen.rt.app.uiComponents.columns.CustomIntegerColumn;
import il.co.rtcohen.rt.app.uiComponents.fields.CustomButton;
import il.co.rtcohen.rt.app.uiComponents.fields.CustomComboBox;
import il.co.rtcohen.rt.dal.dao.*;
import il.co.rtcohen.rt.dal.repositories.*;
import il.co.rtcohen.rt.service.hashavshevet.HashavshevetSync;
import org.springframework.dao.CannotAcquireLockException;

public class CustomerGrid extends AbstractTypeWithNameAndActiveFieldsGrid<Customer> {
    private Customer selectedCustomer;
    private final CustomerTypeRepository customerTypeRepository;
    private final SiteRepository siteRepository;
    private final CallRepository callRepository;
    private final HashavshevetSync hashavshevetSync;

    public CustomerGrid(
            Customer selectedCustomer,
            CustomerRepository customerRepository,
            CustomerTypeRepository customerTypeRepository,
            SiteRepository siteRepository,
            CallRepository callRepository,
            HashavshevetSync hashavshevetSync) {
        super(
                customerRepository,
                Customer::new,
                "customers",
                customer -> null != selectedCustomer && !selectedCustomer.getId().equals(customer.getId()));
        this.selectedCustomer = selectedCustomer;
        this.customerTypeRepository = customerTypeRepository;
        this.siteRepository = siteRepository;
        this.callRepository = callRepository;
        this.hashavshevetSync = hashavshevetSync;
        this.setShowNewNameField(true);
    }

    @Override
    protected void addColumns() {
        addActiveColumn();
        addCallsColumn();
        addSitesColumn();
        addHashSyncColumn();
        addHashKeyColumn();
        addCustomerTypeColumn();
        addNameColumn();
        addIdColumn();
    }

    public void setSelectedCutomer(Customer customer) {
        this.selectedCustomer = customer;
    }

    public Customer getSelectedCustomer() {
        return this.selectedCustomer;
    }

    private void addCallsColumn() {
        addCallsColumn(
                customer -> callRepository.getItems(customer, null, null, false).size(),
                "customer");
    }

    private void addSitesColumn() {
        CustomComponentColumn<Customer, Component> column = CustomComponentColumn.addToGrid(
                (ValueProvider<Customer, Component>) Customer -> {
                    if (null == Customer.getId()) {
                        return null;
                    } else {
                        int n = 0;
                        n = siteRepository.getItems(Customer).size();
                        Button sitesButton = CustomButton.countingIcon(VaadinIcons.FROWN_O, VaadinIcons.HOME_O,
                                VaadinIcons.HOME, n);
                        sitesButton.addClickListener(
                                clickEvent -> getUI().getNavigator().navigateTo("site/customer=" + Customer.getId()));
                        return sitesButton;
                    }
                },
                85,
                "sitesColumn",
                "sites",
                this);
        column.getColumn().setHidden(true);
    }

    private void addHashSyncColumn() {
        CustomComponentColumn<Customer, Component> column = CustomComponentColumn.addToGrid(
                (ValueProvider<Customer, Component>) customer -> {
                    if (null == customer.getId() || 0 == customer.getId()
                            || 0 == customer.getHashavshevetCustomerId()) {
                        return null;
                    } else {
                        return (Button) new CustomButton(VaadinIcons.RECYCLE, false, clickEvent -> {
                            try {
                                boolean newData = hashavshevetSync.syncData(customer.getHashavshevetCustomerId(),
                                        false);
                                Notification.show(LanguageSettings
                                        .getLocaleString(newData ? "syncDonePleaseRefresh" : "syncDoneNothingNew"));
                            } catch (CannotAcquireLockException ignored) {
                                Notification.show(LanguageSettings.getLocaleString("syncLocked"),
                                        Notification.Type.ERROR_MESSAGE);
                            }
                        });
                    }
                },
                30,
                "hashSyncColumn",
                "hashSyncColumn",
                this);
        column.getColumn().setHidden(false);
        column.getColumn().setHidable(true);
    }

    private void addHashKeyColumn() {
        CustomIntegerColumn.addToGrid(
                Customer::getHashavshevetCustomerId,
                Customer::setHashavshevetCustomerId,
                null, null,
                hashKey -> (0 == (Integer.parseInt(hashKey))
                        || ((null != this.getSelectedCustomer()
                                && this.getSelectedCustomer().getHashavshevetCustomerId() == Integer.parseInt(hashKey)))
                        || ((CustomerRepository) this.getMainReopository())
                                .getItemByHashKey(Integer.parseInt(hashKey)) == null),
                LanguageSettings.getLocaleString("duplicateHashKey"),
                100,
                "hashKeyColumn",
                "hashKey",
                false,
                true, true, this);
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
                this);
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
