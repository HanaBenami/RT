package il.co.rtcohen.rt.dal.dao.hashavshevet;

import il.co.rtcohen.rt.dal.dao.interfaces.AbstractType;
import il.co.rtcohen.rt.dal.dao.interfaces.Cloneable;

public class HashavshevetDataRecord extends AbstractType implements Cloneable<HashavshevetDataRecord> {
    public final int documentID;
    public final String customerKey;
    public final String customerName;
    public final String customerAddress;
    public final String customerCityAndZip;
    public final String customerPhonesStr;
    public final String siteAddress;
    public final String siteCityAndZip;
    public final String siteContactNameAndPhones;
    public final String vehicleSeriesOrLicense;
    public final String vehicleModel;
    public final String vehicleType;

    public HashavshevetDataRecord(
            int documentID,
            String customerKey, String customerName,
            String customerAddress, String customerCityAndZip, String customerPhonesStr,
            String siteAddress, String siteCityAndZip, String siteContactNameAndPhones,
            String vehicleSeriesOrLicense, String vehicleModel, String vehicleType
    ) {
        this.documentID = documentID;
        this.customerKey = customerKey;
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.customerCityAndZip = customerCityAndZip;
        this.customerPhonesStr = customerPhonesStr;
        this.siteAddress = siteAddress;
        this.siteCityAndZip = siteCityAndZip;
        this.siteContactNameAndPhones = siteContactNameAndPhones;
        this.vehicleSeriesOrLicense = vehicleSeriesOrLicense;
        this.vehicleModel = vehicleModel;
        this.vehicleType = vehicleType;
    }

    public HashavshevetDataRecord(HashavshevetDataRecord other) {
        super(other);
        this.documentID = other.documentID;
        this.customerKey = other.customerKey;
        this.customerName = other.customerName;
        this.customerAddress = other.customerAddress;
        this.customerCityAndZip = other.customerCityAndZip;
        this.customerPhonesStr = other.customerPhonesStr;
        this.siteAddress = other.siteAddress;
        this.siteCityAndZip = other.siteCityAndZip;
        this.siteContactNameAndPhones = other.siteContactNameAndPhones;
        this.vehicleSeriesOrLicense = other.vehicleSeriesOrLicense;
        this.vehicleModel = other.vehicleModel;
        this.vehicleType = other.vehicleType;
    }

    @Override
    public HashavshevetDataRecord cloneObject() {
        return new HashavshevetDataRecord(this);
    }

    @Override
    public String getObjectName() {
        return this.getClass().getName();
    }

    public int getDocumentID() {
        return documentID;
    }

    public int getCustomerKey() {
        return Integer.parseInt(customerKey);
    }
}
