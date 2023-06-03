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
import il.co.rtcohen.rt.dal.dao.Site;
import il.co.rtcohen.rt.dal.repositories.*;

public class SitesGrid extends AbstractFilterGrid<Site> {
    private final Customer customer;
    private final CustomerRepository customerRepository;
    private final ContactRepository contactRepository;
    private final SiteRepository siteRepository;
    private final CallRepository callRepository;
    private final AreasRepository areasRepository;
    private final GeneralObjectRepository generalObjectRepository;

    public SitesGrid(Customer customer,
                     CustomerRepository customerRepository,
                     ContactRepository contactRepository,
                     SiteRepository siteRepository,
                     CallRepository callRepository,
                     AreasRepository areasRepository,
                     GeneralObjectRepository generalObjectRepository) {
        super(siteRepository, () -> {
                    Site site = new Site();
                    site.setCustomerId(customer.getId());
                    return site;
                },
                "sites",
                site -> null == customer || !site.getCustomerId().equals(customer.getId()));
        this.customer = customer;
        this.customerRepository = customerRepository;
        this.contactRepository = contactRepository;
        this.siteRepository = siteRepository;
        this.callRepository = callRepository;
        this.areasRepository = areasRepository;
        this.generalObjectRepository = generalObjectRepository;
        this.initGrid();
    }

    protected void addColumns() {
        addContactsColumn();
        addCallsColumn();
        addActiveColumn();
        addNotesColumn();
        addAreaColumn();
        addAddressColumn();
        addNameColumn();
        addIdColumn();
    }

    protected void sort() {
        this.sort("nameColumn", SortDirection.ASCENDING);
    }

    private void addContactsColumn() {
        this.addComponentColumn(
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
    }

    private void addCallsColumn() {
        this.addComponentColumn(
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

    private void addIdColumn() {
        this.addNumericColumn(
                Site::getId,
                null,
                80,
                "idColumn",
                "id"
        );
    }
}
