package il.co.rtcohen.rt.service.hashavshevet;

import il.co.rtcohen.rt.dal.dao.*;
import il.co.rtcohen.rt.dal.dao.hashavshevet.HashavshevetDataRecord;
import il.co.rtcohen.rt.dal.repositories.*;
import il.co.rtcohen.rt.service.cities.IsraelCities;
import il.co.rtcohen.rt.utils.Logger;
import il.co.rtcohen.rt.utils.Pair;
import il.co.rtcohen.rt.utils.StringUtils;

import java.util.List;

public class HashavshevetSyncSingleRecord {
    private final HashavshevetDataRecord hashavshevetDataRecord;
    private final String customerCityName;
    private final List<String> customerPhones;
    private final String siteCityName;
    private final String siteContactName;
    private final List<String> siteContactPhones;
    private final int hashavshevetCustomerId;
    private final String vehicleSeries;
    private final int vehicleLicense;
    private final IsraelCities israelCities;

    public HashavshevetSyncSingleRecord(HashavshevetDataRecord hashavshevetDataRecord, IsraelCities israelCities) {
        this.hashavshevetDataRecord = hashavshevetDataRecord;
        this.israelCities = israelCities;
        this.hashavshevetCustomerId = Integer.parseInt(hashavshevetDataRecord.customerKey);
        this.customerCityName = StringUtils.removeNumbers(this.hashavshevetDataRecord.customerCityAndZip);
        Pair<String, List<String>> customerContactNameAndPhones = StringUtils.extractNumbersWithDashes(this.hashavshevetDataRecord.customerPhonesStr, null);
        this.customerPhones = customerContactNameAndPhones.getSecond();
        this.siteCityName = StringUtils.removeNumbers(this.hashavshevetDataRecord.siteCityAndZip);
        Pair<String, List<String>> siteContactNameAndPhones = StringUtils.extractNumbersWithDashes(this.hashavshevetDataRecord.siteContactNameAndPhones, null);
        this.siteContactName = siteContactNameAndPhones.getFirst();
        this.siteContactPhones = siteContactNameAndPhones.getSecond();
        Pair<String, List<String>> vehicleSeriesOrLicense = StringUtils.extractNumbersWithDashes(this.hashavshevetDataRecord.vehicleSeriesOrLicense, 1);
        this.vehicleSeries = vehicleSeriesOrLicense.getFirst();
        this.vehicleLicense = (vehicleSeriesOrLicense.getSecond().isEmpty() ? 0 : Integer.parseInt(vehicleSeriesOrLicense.getSecond().get(0).replaceAll("-", "")));
    }

    public void syncData(
            CustomerRepository customerRepository,
            SiteRepository siteRepository,
            CityRepository cityRepository,
            ContactRepository contactRepository,
            VehicleRepository vehicleRepository,
            VehicleTypeRepository vehicleTypeRepository,
            boolean syncNewCustomers) {
        String msg = "hashavshevetCustomerId=" + hashavshevetCustomerId + " -> ";
        Logger.getLogger(this).debug("Syncing data for " + msg);
        Customer customer = createOrUpdateCustomer(customerRepository, syncNewCustomers);
        if (null != customer) {
            Logger.getLogger(this).debug(msg + "Generated/updated customer: " + customer);
            Site mainSite = createOrUpdateMainSite(customer, siteRepository, cityRepository);
            Logger.getLogger(this).debug(msg + "Generated/updated main site: " + mainSite);
            List<Contact> mainSiteContacts = createOrUpdateMainSiteContacts(mainSite, contactRepository);
            Site vehicleSite = createOrUpdateVehicleSite(customer, siteRepository, cityRepository);
            Logger.getLogger(this).debug(msg + "Generated/updated vehicle site: " + vehicleSite);
            if (null == vehicleSite) {
                vehicleSite = mainSite;
            }
            List<Contact> vehicleSiteContacts = createOrUpdateVehicleSiteContacts(vehicleSite, contactRepository);
            Vehicle vehicle = createOrUpdateVehicle(vehicleSite, vehicleRepository, vehicleTypeRepository);
            Logger.getLogger(this).debug(msg + "Generated/updated vehicle: " + vehicle);
        }
    }

    // Looks for a customer with the same hashavshevet ID.
    // If there such customer, updates it. If not, creates a new one.
    private Customer createOrUpdateCustomer(
            CustomerRepository customerRepository,
            boolean syncNewCustomers) {
        Customer customer = customerRepository.getItemByHashKey(this.hashavshevetCustomerId);
        if (null == customer) {
            if (!syncNewCustomers) {
                return null;
            }
            customer = new Customer();
            customer.setHashavshevetCustomerId(this.hashavshevetCustomerId);
            customer.setHashavshevetFirstDocId(this.hashavshevetDataRecord.documentID);
        } else {
            assert customer.getHashavshevetCustomerId() == this.hashavshevetCustomerId;
        }
        customer.setName(this.hashavshevetDataRecord.customerName);
        customerRepository.updateItem(customer);
        return customer;
    }

    private Site createOrUpdateMainSite(
            Customer customer, SiteRepository siteRepository, CityRepository cityRepository
    ) {
        return createOrUpdateSite(customer, siteRepository, cityRepository,
                this.hashavshevetDataRecord.customerAddress,
                this.customerCityName, Integer.parseInt(this.hashavshevetDataRecord.documentID + "001"));
    }

    private Site createOrUpdateVehicleSite(
            Customer customer, SiteRepository siteRepository, CityRepository cityRepository
    ) {
        return createOrUpdateSite(customer, siteRepository, cityRepository,
                this.hashavshevetDataRecord.siteAddress,
                this.siteCityName, Integer.parseInt(this.hashavshevetDataRecord.documentID + "002"));

    }

    // Looks for a site that was created due to the same hashavshevet data record.
    // If there is no such site, look for a site with exactly the same address.
    // If there such site, updates it. If not, creates a new one.
    private Site createOrUpdateSite(
            Customer customer, SiteRepository siteRepository, CityRepository cityRepository,
            String address, String cityName, int hashavshevetDocumentID
    ) {
        assert null != customer;
        if (null == cityName) {
            cityName = "";
        }
        if (null == address) {
            address = "";
        }
        if (address.isEmpty() && cityName.isEmpty()) {
            return null;
        }

        City city = findCity(cityRepository, address, cityName);
        address += " " + cityName;

        Site site = siteRepository.getItemByHashDocId(hashavshevetDocumentID);
        if (null == site) {
            site = findSiteWithTheSameAddress(customer, siteRepository, address, city);
            if (null == site) {
                site = new Site();
                site.setCustomer(customer);
                site.setHashavshevetFirstDocId(hashavshevetDocumentID);
            }
        }

        site.setCity(city);
        site.setAddress(address);
        siteRepository.updateItem(site);
        return site;
    }

    private City findCity(CityRepository cityRepository,
                          String address, String cityName) {
        City city = israelCities.findCityNameInAddress(cityName);
        if (null == city) {
            city = israelCities.findCityNameInAddress(address);
        }
        if (null == city) {
            city = cityRepository.getItemByName(StringUtils.reduceSpaces(cityName));
        }
        if (null == city) {
            city = cityRepository.getItemByName(StringUtils.reduceSpaces(address));
        }
        return city;
    }

    private Site findSiteWithTheSameAddress(Customer customer, SiteRepository siteRepository,
                                            String address, City city) {
        List<Site> customerSites = siteRepository.getItems(customer);
        for (Site customerSite : customerSites) {
            boolean sameAddress = StringUtils.areEquals(customerSite.getAddress(), address);
            boolean sameCity = (null == city && null == customerSite.getCity())
                    || (null != city && city.equals(customerSite.getCity()));
            if (sameAddress && sameCity) {
                return customerSite;
            }
        }
        return null;
    }

    private List<Contact> createOrUpdateMainSiteContacts(
            Site mainSite,
            ContactRepository contactRepository
    ) {
        return createOrUpdateContacts(mainSite, contactRepository, null, customerPhones, this.hashavshevetDataRecord.documentID);
    }

    private List<Contact> createOrUpdateVehicleSiteContacts(
            Site vehicleSiteContacts, ContactRepository contactRepository
    ) {
        return createOrUpdateContacts(vehicleSiteContacts, contactRepository, siteContactName, siteContactPhones, this.hashavshevetDataRecord.documentID);
    }

    // For each phone number, looks for a contact with the same number.
    // If there is no such contact, creates a new one.
    private static List<Contact> createOrUpdateContacts(
            Site site, ContactRepository contactRepository, String name, List<String> phones, int hashavshevetDocumentID
    ) {
        assert null != site;
        
        List<Contact> contacts = contactRepository.getItems(site);
        for (String phone : phones) {
            boolean phoneExist = false;
            for (Contact contact : contacts) {
                if (null != contact.getPhone() && contact.getPhone().equals(phone)) {
                    phoneExist = true;
                    break;
                }
            }
            if (!phoneExist) {
                contacts.add(createContact(site, contactRepository, name, phone, hashavshevetDocumentID));
            }
        }

        return contacts;
    }

    private static Contact createContact(
            Site site, ContactRepository contactRepository, String name, String phone, int hashavshevetDocumentID
    ) {
        Contact contact = new Contact();
        contact.setSite(site);
        contact.setName(name);
        contact.setPhone(phone);
        contact.setHashavshevetFirstDocId(hashavshevetDocumentID);
        contactRepository.insertItem(contact);
        return contact;
    }

    // Looks for a vehicle that was created due to the same hashavshevet data record.
    // If there is no such vehicle, look for a vehicle with exactly the same details.
    // If there such vehicle, updates it. If not, creates a new one.
    private Vehicle createOrUpdateVehicle(
            Site site, VehicleRepository vehicleRepository, VehicleTypeRepository vehicleTypeRepository
    ) {
        assert null != site;

        VehicleType vehicleType = createOrUpdateVehicleType(vehicleTypeRepository, this.hashavshevetDataRecord.vehicleType);

        Vehicle currentVehicle = vehicleRepository.getItemByHashDocId(this.hashavshevetDataRecord.documentID);
        if (null == currentVehicle) {
            List<Vehicle> siteVehicles = vehicleRepository.getItems(site);
            for (Vehicle siteVehicle : siteVehicles) {
                if (this.vehicleSeries.equals(siteVehicle.getSeries())
                        && this.hashavshevetDataRecord.vehicleModel.equals(siteVehicle.getModel())
                        && this.vehicleLicense == siteVehicle.getLicense()
                        && vehicleType.equals(siteVehicle.getVehicleType())
                ) {
                    currentVehicle = siteVehicle;
                    break;
                }
            }
            if (null == currentVehicle) {
                currentVehicle = new Vehicle();
                currentVehicle.setSite(site);
                currentVehicle.setHashavshevetFirstDocId(this.hashavshevetDataRecord.documentID);
            }
        }

        currentVehicle.setSeries(this.vehicleSeries);
        currentVehicle.setModel(this.hashavshevetDataRecord.vehicleModel);
        currentVehicle.setLicense(this.vehicleLicense);
        currentVehicle.setVehicleType(vehicleType);
        vehicleRepository.updateItem(currentVehicle);

        return currentVehicle;
    }

    private static VehicleType createOrUpdateVehicleType(VehicleTypeRepository vehicleTypeRepository, String vehicleTypeName) {
        VehicleType vehicleType = vehicleTypeRepository.getItemByName(vehicleTypeName);
        if (null == vehicleType) {
            vehicleType = new VehicleType();
            vehicleType.setName(vehicleTypeName);
            vehicleTypeRepository.insertItem(vehicleType);
        }
        return vehicleType;
    }
}
