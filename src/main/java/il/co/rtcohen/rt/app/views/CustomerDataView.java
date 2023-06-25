package il.co.rtcohen.rt.app.views;

import il.co.rtcohen.rt.app.grids.*;
import il.co.rtcohen.rt.dal.dao.Customer;
import il.co.rtcohen.rt.dal.dao.Site;
import il.co.rtcohen.rt.dal.repositories.*;
import il.co.rtcohen.rt.service.hashavshevet.HashavshevetSync;
import il.co.rtcohen.rt.utils.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ErrorHandler;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.GridLayout;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SpringView(name = CustomerDataView.VIEW_NAME)
public class CustomerDataView extends AbstractDataView<Customer> {
    static final String VIEW_NAME = "customers";

    // Repositories
    private final CustomerRepository customerRepository;
    private final CustomerTypeRepository customerTypeRepository;
    private final SiteRepository siteRepository;
    private final ContactRepository contactRepository;
    private final CallRepository callRepository;
    private final CityRepository cityRepository;
    private final AreaRepository areaRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final HashavshevetSync hashavshevetSync;

    // Grids
    GridLayout gridLayout;
    CustomerGrid customerGrid;
    SitesGrid sitesGrid;
    ContactsGrid contactsGrid;
    VehiclesGrid vehiclesGrid;

    private int selectedCustomerId;
    private int selectedSiteId;
    private int selectedVehicleId;

    @Autowired
    private CustomerDataView(ErrorHandler errorHandler,
                             CustomerRepository customerRepository,
                             CustomerTypeRepository customerTypeRepository,
                             SiteRepository siteRepository,
                             ContactRepository contactRepository,
                             CallRepository callRepository,
                             CityRepository cityRepository,
                             AreaRepository areaRepository,
                             VehicleRepository vehicleRepository,
                             VehicleTypeRepository vehicleTypeRepository,
                             HashavshevetSync hashavshevetSync) {
        super(errorHandler, "customersList");
        this.customerRepository = customerRepository;
        this.customerTypeRepository = customerTypeRepository;
        this.contactRepository = contactRepository;
        this.siteRepository = siteRepository;
        this.callRepository = callRepository;
        this.cityRepository = cityRepository;
        this.areaRepository = areaRepository;
        this.vehicleRepository = vehicleRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
        this.hashavshevetSync = hashavshevetSync;
    }

    // TODO: Move to an upper/separate generic class
    // TODO: Save during session ?
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Map<String, String> parametersMap = event.getParameterMap();
        Logger.getLogger(this).info("Parameters map " + Arrays.toString(parametersMap.entrySet().toArray()));
        this.selectedCustomerId = Integer.parseInt(parametersMap.getOrDefault("customer", "0"));
        this.selectedSiteId = Integer.parseInt(parametersMap.getOrDefault("site", "0"));
        this.selectedVehicleId = Integer.parseInt(parametersMap.getOrDefault("vehicle", "0"));
        super.enter(event);
    }

    @Override
    void addGrids() {
        this.setScrollable(true);
        this.gridLayout = new GridLayout(6, 6);
        this.gridLayout.setWidth("100%");
        this.gridLayout.setStyleName("scrollable");
        this.gridLayout.addStyleName("custom-grid-margins");
        addCustomerGrid();
        addSitesGrid(customerGrid.getCurrentItem());
        this.addComponentsAndExpand(this.gridLayout);
    }

    void addCustomerGrid() {
        removeCustomerGrid();
        this.customerGrid = new CustomerGrid(
                (0 == selectedCustomerId ? null : customerRepository.getItem(selectedCustomerId)),
                customerRepository, customerTypeRepository, siteRepository, callRepository,
                hashavshevetSync);
        this.customerGrid.initGrid(true, 0);
        this.customerGrid.setSelectedItem(selectedCustomerId, true);
        this.selectedCustomerId = 0;
        this.customerGrid.addItemClickListener(listener -> {
            Customer newSelectedCustomer = listener.getItem();
            this.selectedSiteId = 0;
            if (null == newSelectedCustomer.getId() || 0 == newSelectedCustomer.getId() || newSelectedCustomer.getId() != selectedCustomerId) {
                this.selectedCustomerId = (null == newSelectedCustomer.getId() ? 0 : newSelectedCustomer.getId());
                addSitesGrid(newSelectedCustomer);
            }
        });
        this.gridLayout.addComponent(customerGrid.getVerticalLayout(true, true), 4, 0, 5, 3);
    }

    void addSitesGrid(Customer customer) {
        removeSitesGrid();
        this.sitesGrid = new SitesGrid(customer, customerRepository, contactRepository, siteRepository, callRepository, cityRepository, areaRepository);
        this.sitesGrid.initGrid(true, 0);
        if (0 == selectedSiteId) {
            List<Site> sites = this.sitesGrid.getGridItems();
            if (!sites.isEmpty() && null != sites.get(0).getId()) {
                this.selectedSiteId = sites.get(0).getId();
            }
        }
        this.sitesGrid.setSelectedItem(selectedSiteId, false);
        this.selectedSiteId = 0;
        this.sitesGrid.addItemClickListener(listener -> {
            Site newSelectedSite = listener.getItem();
            if (null == newSelectedSite.getId() || 0 == newSelectedSite.getId() || newSelectedSite.getId() != selectedSiteId) {
                this.selectedSiteId = (null == newSelectedSite.getId() ? 0 : newSelectedSite.getId());
                addContactsGrid(newSelectedSite);
                addVehicleGrid(newSelectedSite);
            }
        });
        this.gridLayout.addComponent(sitesGrid.getVerticalLayout(true, true), 0, 0, 3, 1);
        addContactsGrid(sitesGrid.getCurrentItem());
        addVehicleGrid(sitesGrid.getCurrentItem());
    }

    void addContactsGrid(Site site) {
        removeContactsGrid();
        this.contactsGrid = new ContactsGrid(site, contactRepository);
        this.contactsGrid.initGrid(true, 0);
        this.gridLayout.addComponent(contactsGrid.getVerticalLayout(true, true), 0, 2, 3, 3);
    }

    void addVehicleGrid(Site site) {
        removeVehiclesGrid();
        this.vehiclesGrid = new VehiclesGrid(site, siteRepository, vehicleRepository, vehicleTypeRepository, callRepository);
        this.vehiclesGrid.initGrid(true, 0);
        this.vehiclesGrid.setSelectedItem(selectedVehicleId, true);
        this.selectedVehicleId = 0;
        this.gridLayout.addComponent(vehiclesGrid.getVerticalLayout(true, true), 0, 4, 5, 5);
    }

    void removeGrids() {
        removeSitesGrid();
        removeCustomerGrid();
        this.removeComponent(this.gridLayout);
    }

    void removeVehiclesGrid() {
        if (null != vehiclesGrid) {
            this.gridLayout.removeComponent(vehiclesGrid.getVerticalLayout());
            vehiclesGrid = null;
        }
    }

    void removeContactsGrid() {
        if (null != contactsGrid) {
            this.gridLayout.removeComponent(contactsGrid.getVerticalLayout());
            contactsGrid = null;
        }
    }

    void removeSitesGrid() {
        removeContactsGrid();
        removeVehiclesGrid();
        if (null != sitesGrid) {
            this.gridLayout.removeComponent(sitesGrid.getVerticalLayout());
            sitesGrid = null;
        }
    }

    void removeCustomerGrid() {
        if (null != this.customerGrid) {
            this.gridLayout.removeComponent(customerGrid.getVerticalLayout());
            this.customerGrid = null;
        }
    }



    @Override
    void setTabIndexesAndFocus() {
        customerGrid.focus();
        int tabIndex = 1;
        customerGrid.setTabIndex(tabIndex);
        ++tabIndex;
        sitesGrid.setTabIndex(tabIndex);
        ++tabIndex;
        contactsGrid.setTabIndex(tabIndex);
        ++tabIndex;
        vehiclesGrid.setTabIndex(tabIndex);
        ++tabIndex;
        getRefreshButton().setTabIndex(tabIndex);
        ++tabIndex;
    }
}
