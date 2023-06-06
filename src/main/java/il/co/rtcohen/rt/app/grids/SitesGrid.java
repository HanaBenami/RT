package il.co.rtcohen.rt.app.grids;

import com.vaadin.data.ValueProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Setter;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import il.co.rtcohen.rt.app.UiComponents.CustomComboBox;
import il.co.rtcohen.rt.app.UiComponents.CustomComboBoxColumn;
import il.co.rtcohen.rt.app.UiComponents.UIComponents;
import il.co.rtcohen.rt.dal.dao.*;
import il.co.rtcohen.rt.dal.repositories.*;

public class SitesGrid extends AbstractFilterGrid<Site> {
    private final Customer selectedCustomer;
    private final ContactRepository contactRepository;
    private final CallRepository callRepository;
    private final AreasRepository areasRepository;

    public SitesGrid(Customer selectedCustomer,
                     ContactRepository contactRepository,
                     SiteRepository siteRepository,
                     CallRepository callRepository,
                     AreasRepository areasRepository) {
        super(siteRepository, () -> {
                    Site site = new Site();
                    site.setCustomer(selectedCustomer);
                    return site;
                },
                "sitesOfCustomers",
                site -> null == selectedCustomer || null == site.getCustomer() || !site.getCustomer().getId().equals(selectedCustomer.getId()));
        this.selectedCustomer = selectedCustomer;
        this.contactRepository = contactRepository;
        this.callRepository = callRepository;
        this.areasRepository = areasRepository;
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
                                getUI().getNavigator().navigateTo("call/customer=" + site.getCustomer().getId()));    // TODO: Change to call/site=
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
        CustomComboBoxColumn.addToGrid(
                CustomComboBox.areaComboBox(areasRepository),
                CustomComboBox.areaComboBox(areasRepository),
                (ValueProvider<Site, String>) site -> {
                    AbstractTypeWithNameAndActiveFields area = site.getArea();
                    return (null == area ? "" : area.getName());
                },
                (ValueProvider<Site, Area>) Site::getArea,
                (Setter<Site, Area>) Site::setArea,
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
