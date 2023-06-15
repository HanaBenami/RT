package il.co.rtcohen.rt.app.grids;

import com.vaadin.data.ValueProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Component;
import il.co.rtcohen.rt.app.ui.UIPaths;
import il.co.rtcohen.rt.app.uiComponents.columns.CustomComboBoxColumn;
import il.co.rtcohen.rt.app.uiComponents.columns.CustomComponentColumn;
import il.co.rtcohen.rt.app.uiComponents.columns.CustomTextColumn;
import il.co.rtcohen.rt.app.uiComponents.fields.CustomButton;
import il.co.rtcohen.rt.app.uiComponents.fields.CustomComboBox;
import il.co.rtcohen.rt.dal.dao.*;
import il.co.rtcohen.rt.dal.repositories.*;

import java.sql.SQLException;

public class SitesGrid extends AbstractTypeWithNameAndActiveFieldsGrid<Site> {
    private final Customer selectedCustomer;
    private final CustomerRepository customerRepository;
    private final ContactRepository contactRepository;
    private final CallRepository callRepository;
    private final AreasRepository areasRepository;

    public SitesGrid(Customer selectedCustomer,
                     CustomerRepository customerRepository,
                     ContactRepository contactRepository,
                     SiteRepository siteRepository,
                     CallRepository callRepository,
                     AreasRepository areasRepository) {
        super(
                siteRepository, () -> {
                    Site site = new Site();
                    site.setCustomer(selectedCustomer);
                    return site;
                },
                "sitesOfCustomers",
                site -> null == selectedCustomer || null == site.getCustomer() || !site.getCustomer().getId().equals(selectedCustomer.getId())
        );
        this.selectedCustomer = (null == selectedCustomer || selectedCustomer.isDraft() ? null : selectedCustomer);
        this.customerRepository = customerRepository;
        this.contactRepository = contactRepository;
        this.callRepository = callRepository;
        this.areasRepository = areasRepository;
    }

    @Override
    protected void setTitle() {
        super.setTitle();
        if (null != this.selectedCustomer) {
            this.title += " " + selectedCustomer.getName();
        }
    }

    @Override
    protected void changeErrorMessage() {
        String errorMessageKey = null;
        String warningMessageKey = null;
        if (null == selectedCustomer) {
            errorMessageKey = "noCustomer";
        } else if (0 == this.getItemsCounter()) {
            warningMessageKey = "noSitesToCustomer";
        }
        this.setErrorMessage(errorMessageKey);
        this.setWarningMessage(warningMessageKey);
    }

    @Override
    protected void addColumns() {
        addActiveColumn();
        addAddingCallColumn();
        addCallsColumn();
        addContactsColumn();
        addNotesColumn();
        addAreaColumn();
        addAddressColumn();
        addNameColumn();
        addCustomerColumn();
        addIdColumn();
    }

    private void addContactsColumn() {
        CustomComponentColumn<Site, Component> column = CustomComponentColumn.addToGrid(
                (ValueProvider<Site, Component>) site -> {
                    if (null == site.getId()) {
                        return null;
                    } else {
                        int activeContactsCounter = 0;
                        try {
                            activeContactsCounter = contactRepository.getItems(site).size();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        return CustomButton.countingIcon(VaadinIcons.ENVELOPE_OPEN_O, VaadinIcons.ENVELOPE_OPEN, VaadinIcons.ENVELOPE_OPEN, activeContactsCounter);
                    }
                },
                85,
                "contactsColumn",
                "contacts",
                this
        );
        column.getColumn().setHidden(true);
    }

    private void addAddingCallColumn() {
        CustomComponentColumn<Site, Component> column = CustomComponentColumn.addToGrid(
                site -> new CustomButton(
                        VaadinIcons.PLUS,
                        false,
                        UIPaths.EDITCALL.getEditCallPath(null, site, null),
                        UIPaths.EDITCALL.getWindowHeight(),
                        UIPaths.EDITCALL.getWindowWidth(),
                        UIPaths.EDITCALL.getWindowName()),
                60,
                "addCallColumn",
                "addCall",
                this
        );
        column.getColumn().setStyleGenerator(vehicle -> "red");
        column.getColumn().setHidable(false);
        column.getColumn().setHidden(false);
    }

    private void addCallsColumn() {
        addCallsColumn(
                site -> callRepository.getItems(null, site, null, false).size(),
                "site"
        );
    }

    private void addNotesColumn() {
        CustomTextColumn<Site> column = CustomTextColumn.addToGrid(
                Site::getNotes,
                Site::setNotes,
                false, 230,
                "notesColumn",
                "notes",
                false,
                true,
                false,
                this
        );
    }

    private void addAreaColumn() {
        CustomComboBoxColumn.addToGrid(
                CustomComboBox.getComboBox(areasRepository),
                CustomComboBox.getComboBox(areasRepository),
                Site::getArea,
                Site::setArea,
                true,
                130,
                "areaColumn",
                "area",
                this
        );
    }

    private void addAddressColumn() {
        CustomTextColumn<Site> column = CustomTextColumn.addToGrid(
                Site::getAddress,
                Site::setAddress,
                false, 230,
                "addressColumn",
                "address",
                false,
                true,
                false,
                this
        );
    }

    private void addCustomerColumn() {
        CustomComboBoxColumn<Customer, Site> column = CustomComboBoxColumn.addToGrid(
                CustomComboBox.getComboBox(customerRepository),
                CustomComboBox.getComboBox(customerRepository),
                Site::getCustomer,
                null,
                true,
                130,
                "customerColumn",
                "customer",
                this
        );
        column.getColumn().setHidable(true);
        column.getColumn().setHidden(true);
    }
}
