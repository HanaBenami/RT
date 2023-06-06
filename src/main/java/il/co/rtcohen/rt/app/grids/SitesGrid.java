package il.co.rtcohen.rt.app.grids;

import com.vaadin.data.ValueProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Setter;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import il.co.rtcohen.rt.app.UIComponents;
import il.co.rtcohen.rt.dal.dao.Customer;
import il.co.rtcohen.rt.dal.dao.GeneralObject;
import il.co.rtcohen.rt.dal.dao.Site;
import il.co.rtcohen.rt.dal.repositories.*;

public class SitesGrid extends AbstractFilterGrid<Site> {
    private final Customer selectedCustomer;
    private final CustomerRepository customerRepository;
    private final ContactRepository contactRepository;
    private final SiteRepository siteRepository;
    private final CallRepository callRepository;
    private final AreasRepository areasRepository;
    private final GeneralObjectRepository generalObjectRepository;

    public SitesGrid(Customer selectedCustomer,
                     CustomerRepository customerRepository,
                     ContactRepository contactRepository,
                     SiteRepository siteRepository,
                     CallRepository callRepository,
                     AreasRepository areasRepository,
                     GeneralObjectRepository generalObjectRepository) {
        super(siteRepository, () -> {
                    Site site = new Site();
                    site.setCustomerId(selectedCustomer.getId());
                    return site;
                },
                "sitesOfCustomers",
                site -> null == selectedCustomer || !site.getCustomerId().equals(selectedCustomer.getId()));
        this.selectedCustomer = selectedCustomer;
        this.customerRepository = customerRepository;
        this.contactRepository = contactRepository;
        this.siteRepository = siteRepository;
        this.callRepository = callRepository;
        this.areasRepository = areasRepository;
        this.generalObjectRepository = generalObjectRepository;
        this.initGrid();
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

    protected void addColumns() {
        addActiveColumn();
        addContactsColumn();
        addCallsColumn();
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
                        int activeContactsCounter = contactRepository.getContactsBySite(site.getId(), true).size();
                        return AbstractFilterGrid.countingIcon(VaadinIcons.ENVELOPE_OPEN_O, VaadinIcons.ENVELOPE_OPEN, VaadinIcons.ENVELOPE_OPEN, activeContactsCounter);
                    }
                },
                85,
                "contactsColumn",
                "contacts"
        );
        column.setHidden(true);
    }

    private void addCallsColumn() {
        Column<Site, Component> column = this.addComponentColumn(
                (ValueProvider<Site, Component>) site -> {
                    if (null == site.getId()) {
                        return null;
                    } else {
                        int openCallsCounter = callRepository.getCallsBySite(site.getId()).size();
                        Button callsButton = AbstractFilterGrid.countingIcon(VaadinIcons.BELL_O, VaadinIcons.BELL, VaadinIcons.BELL, openCallsCounter);
                        callsButton.addClickListener(clickEvent ->
                                getUI().getNavigator().navigateTo("call/customer=" + site.getCustomerId()));    // TODO: Change to call/site=
                        return callsButton;
                    }
                },
                85,
                "callsColumn",
                "calls"
        );
        column.setHidden(true);
    }

    private void addActiveColumn() {
        this.addBooleanColumn(
                (ValueProvider<Site, Component>) site -> UIComponents.checkBox(site.isActive(),true),
                (ValueProvider<Site, Boolean>) Site::isActive,
                (Setter<Site, Boolean>) Site::setActive,
                "activeColumn",
                "active",
                Boolean.TRUE
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
        this.addComboBoxColumn(
                generalObjectRepository,
                areasRepository.getDbTableName(),
                (ValueProvider<Site, String>) site -> {
                    GeneralObject area = areasRepository.getItem(site.getAreaId());
                    return (null == area ? "" : area.getName());
                },
                (ValueProvider<Integer, String>) id -> areasRepository.getItem(id).getName(),
                (ValueProvider<Site, Integer>) Site::getAreaId,
                (Setter<Site, Integer>) Site::setAreaId,
                130,
                "areaColumn",
                "area"
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

    private void addNameColumn() {
        this.addTextColumn(
                Site::getName,
                Site::setName,
                230,
                "nameColumn",
                "name"
        );
    }
}
