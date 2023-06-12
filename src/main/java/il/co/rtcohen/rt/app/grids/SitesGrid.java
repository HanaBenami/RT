package il.co.rtcohen.rt.app.grids;

import com.vaadin.data.ValueProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import il.co.rtcohen.rt.app.uiComponents.CustomButton;
import il.co.rtcohen.rt.app.uiComponents.CustomCheckBoxColumn;
import il.co.rtcohen.rt.app.uiComponents.CustomComboBox;
import il.co.rtcohen.rt.app.uiComponents.CustomComboBoxColumn;
import il.co.rtcohen.rt.dal.dao.*;
import il.co.rtcohen.rt.dal.repositories.*;

import java.sql.SQLException;

public class SitesGrid extends AbstractTypeWithNameAndActiveFieldsGrid<Site> {
    private final Customer selectedCustomer;
    private final ContactRepository contactRepository;
    private final CallRepository callRepository;
    private final AreasRepository areasRepository;

    public SitesGrid(Customer selectedCustomer,
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
        addCallsColumn();
        addContactsColumn();
        addNotesColumn();
        addAreaColumn();
        addAddressColumn();
        addNameColumn();
        addIdColumn();
    }

    private void addContactsColumn() {
        Column<Site, Component> column = this.addComponentColumn(
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
                "contacts"
        );
        column.setHidden(true);
    }

    private void addCallsColumn() {
        addCallsColumn(
                site -> callRepository.getItems(null, site, null, false).size(),
                "site"
        );
    }

    private void addNotesColumn() {
        this.addTextColumn(
                Site::getNotes,
                Site::setNotes,
                230,
                "notesColumn",
                "notes"
        );
    }

    private void addAreaColumn() {
        CustomComboBoxColumn.addToGrid(
                CustomComboBox.getComboBox(areasRepository),
                CustomComboBox.getComboBox(areasRepository),
                Site::getArea,
                Site::setArea,
                130,
                "areaColumn",
                "area",
                this
        );
    }

    private void addAddressColumn() {
        this.addTextColumn(
                Site::getAddress,
                Site::setAddress,
                230,
                "addressColumn",
                "address"
        );
    }
}
