package il.co.rtcohen.rt.app.views;

import com.vaadin.server.ErrorHandler;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import il.co.rtcohen.rt.app.UIComponents;
import il.co.rtcohen.rt.app.ui.grids.ContactsGrid;
import il.co.rtcohen.rt.app.ui.grids.CustomerGrid;
import il.co.rtcohen.rt.app.ui.grids.SitesGrid;
import il.co.rtcohen.rt.dal.dao.Customer;
import il.co.rtcohen.rt.dal.dao.Site;
import il.co.rtcohen.rt.dal.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@SpringView(name = CustomerDataView.VIEW_NAME)
public class CustomerDataView extends AbstractDataView<Customer> {
    static final String VIEW_NAME = "customers";
    private static final Logger logger = LoggerFactory.getLogger(CustomerDataView.class);
    private Map<String,String> parametersMap;

    // Repositories
    private GeneralObjectRepository generalObjectRepository;
    private CustomerRepository customerRepository;
    private CustomerTypeRepository customerTypeRepository;
    private SiteRepository siteRepository;
    private ContactRepository contactRepository;
    private CallRepository callRepository;
    private AreasRepository areasRepository;

    // Grids
    CustomerGrid customerGrid;
    SitesGrid sitesGrid;
    ContactsGrid contactsGrid;

    @Autowired
    private CustomerDataView(ErrorHandler errorHandler,
                             GeneralObjectRepository generalObjectRepository,
                             CustomerRepository customerRepository,
                             CustomerTypeRepository customerTypeRepository,
                             SiteRepository siteRepository,
                             ContactRepository contactRepository,
                             CallRepository callRepository,
                             AreasRepository areasRepository) { // TODO: Add vehicles repo
        super(errorHandler, "customersList");
        this.generalObjectRepository = generalObjectRepository;
        this.customerRepository = customerRepository;
        this.customerTypeRepository = customerTypeRepository;
        this.contactRepository = contactRepository;
        this.siteRepository = siteRepository;
        this.callRepository = callRepository;
        this.areasRepository = areasRepository;
    }

    @Override
    void addGrids() {
        // TODO: Layout / accordion / ...
        addCustomerGrid();
        addSitesGrid(null);
    }

    void addCustomerGrid() {
        removeCustomerGrid();
        customerGrid = new CustomerGrid(customerRepository, customerTypeRepository, siteRepository, callRepository);
        addComponentsAndExpand(customerGrid.getVerticalLayout(true, false));
        customerGrid.addItemClickListener(listener -> addSitesGrid(listener.getItem()));
    }

    void addSitesGrid(Customer customer) {
        removeContactsGrid();
        removeSitesGrid();
        sitesGrid = new SitesGrid(customer, customerRepository, contactRepository, siteRepository, callRepository, areasRepository, generalObjectRepository);
        addComponentsAndExpand(sitesGrid.getVerticalLayout(true, true));
        sitesGrid.addItemClickListener(listener -> addContactsGrid(listener.getItem()));
        addContactsGrid(sitesGrid.getCurrentItem());
    }

    void addContactsGrid(Site site) {
        removeContactsGrid();
        contactsGrid = new ContactsGrid(site, contactRepository);
        addComponentsAndExpand(contactsGrid.getVerticalLayout(true, true));
    }

    void removeGrids() {
        removeSitesGrid();
        removeCustomerGrid();
    }

    void removeContactsGrid() {
        if (null != contactsGrid) {
            removeComponent(contactsGrid.getVerticalLayout());
            contactsGrid = null;
        }
    }

    void removeSitesGrid() {
        if (null != sitesGrid) {
            removeComponent(sitesGrid.getVerticalLayout());
            sitesGrid = null;
        }
    }

    void removeCustomerGrid() {
        if (null != customerGrid) {
            removeComponent(customerGrid.getVerticalLayout());
            customerGrid = null;
        }
    }

    // TODO
    @Override
    void setTabIndexes() {
        customerGrid.setTabIndex(1);
    }
}
