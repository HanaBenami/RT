package il.co.rtcohen.rt.app.views;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ErrorHandler;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.GridLayout;
import il.co.rtcohen.rt.app.grids.*;
import il.co.rtcohen.rt.dal.dao.Customer;
import il.co.rtcohen.rt.dal.dao.Site;
import il.co.rtcohen.rt.dal.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Map;

@SpringView(name = CustomerDataView.VIEW_NAME)
public class CustomerDataView extends AbstractDataView<Customer> {
    static final String VIEW_NAME = "customers";
    private static final Logger logger = LoggerFactory.getLogger(CustomerDataView.class);

    // Repositories
    private final CustomerRepository customerRepository;
    private final CustomerTypeRepository customerTypeRepository;
    private final SiteRepository siteRepository;
    private final ContactRepository contactRepository;
    private final CallRepository callRepository;
    private final AreasRepository areasRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleTypeRepository vehicleTypeRepository;

    // Grids
    GridLayout gridLayout;  // TODO
    CustomerGrid customerGrid;
    SitesGrid sitesGrid;
    ContactsGrid contactsGrid;
    VehiclesGrid vehiclesGrid;

    // TODO: Change to the relevant objects
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
                             AreasRepository areasRepository,
                             VehicleRepository vehicleRepository,
                             VehicleTypeRepository vehicleTypeRepository) {
        super(errorHandler, "customersList");
        this.customerRepository = customerRepository;
        this.customerTypeRepository = customerTypeRepository;
        this.contactRepository = contactRepository;
        this.siteRepository = siteRepository;
        this.callRepository = callRepository;
        this.areasRepository = areasRepository;
        this.vehicleRepository = vehicleRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
    }

    // TODO: MOve to an upper/separate generic class
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Map<String, String> parametersMap = event.getParameterMap();
        logger.info("Parameters map " + Arrays.toString(parametersMap.entrySet().toArray()));
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
                customerRepository, customerTypeRepository, siteRepository, callRepository
        );
        this.customerGrid.initGrid();
        this.customerGrid.setSelectedItem(selectedCustomerId);
        this.selectedCustomerId = 0;
        this.customerGrid.addItemClickListener(listener -> {
            Customer newSelectedCustomer = listener.getItem();
            if (null == newSelectedCustomer.getId() || 0 == newSelectedCustomer.getId() || newSelectedCustomer.getId() != selectedCustomerId) {
                this.selectedCustomerId = (null == newSelectedCustomer.getId() ? 0 : newSelectedCustomer.getId());
                addSitesGrid(newSelectedCustomer);
            }
        });
        this.gridLayout.addComponent(customerGrid.getVerticalLayout(true, true), 4, 0, 5, 3);
    }

    void addSitesGrid(Customer customer) {
        removeSitesGrid();
        this.sitesGrid = new SitesGrid(customer, contactRepository, siteRepository, callRepository, areasRepository);
        this.sitesGrid.initGrid();
        this.sitesGrid.setSelectedItem(selectedSiteId);
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
        this.contactsGrid.initGrid();
        this.gridLayout.addComponent(contactsGrid.getVerticalLayout(true, true), 0, 2, 3, 3);
    }

    void addVehicleGrid(Site site) {
        removeVehiclesGrid();
        this.vehiclesGrid = new VehiclesGrid(site, vehicleRepository, vehicleTypeRepository, callRepository);
        this.vehiclesGrid.setSelectedItem(selectedVehicleId);
        this.selectedVehicleId = 0;
        this.gridLayout.addComponent(vehiclesGrid.getVerticalLayout(true, true), 0, 4, 5, 5);
    }

    void removeGrids() {
        removeSitesGrid();
        removeCustomerGrid();
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
        if (null != customerGrid) {
            this.removeComponent(customerGrid.getVerticalLayout());
            customerGrid = null;
        }
    }

    // TODO
    @Override
    void setTabIndexes() {
        customerGrid.setTabIndex(1);
    }
}
