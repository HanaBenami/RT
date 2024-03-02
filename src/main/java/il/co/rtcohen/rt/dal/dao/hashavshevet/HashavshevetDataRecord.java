package il.co.rtcohen.rt.dal.dao.hashavshevet;

import il.co.rtcohen.rt.dal.dao.interfaces.AbstractType;
import il.co.rtcohen.rt.dal.dao.interfaces.Cloneable;
import il.co.rtcohen.rt.utils.Date;

public class HashavshevetDataRecord extends AbstractType implements Cloneable<HashavshevetDataRecord> {
    public enum DocumentType {
        WorkCard("WorkCard"),
        Invoice("Invoice");

        public final String name;

        DocumentType(String name) {
            this.name = name;
        }

        static public DocumentType getDocumentType(String name) {
            for (DocumentType documentType : DocumentType.values()) {
                if (documentType.name.equals(name)) {
                    return documentType;
                }
            }
            return null;
        }
    }

    public final int documentRowID;
    public final int documentID;
    public final DocumentType documentType;
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
    public final float amount;
    public final String itemName;
    public final int invoiceNum;
    public final Date invoiceDate;

    public HashavshevetDataRecord(
            int documentRowID, int documentID, DocumentType documentType,
            String customerKey, String customerName,
            String customerAddress, String customerCityAndZip, String customerPhonesStr,
            String siteAddress, String siteCityAndZip, String siteContactNameAndPhones,
            String vehicleSeriesOrLicense, String vehicleModel, String vehicleType,
            float amount, String itemName, int invoiceNum, Date invoiceDate) {
        this.documentRowID = documentRowID;
        this.documentID = documentID;
        this.documentType = documentType;
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
        this.amount = amount;
        this.itemName = itemName;
        this.invoiceNum = invoiceNum;
        this.invoiceDate = invoiceDate;
    }

    public HashavshevetDataRecord(HashavshevetDataRecord other) {
        super(other);
        this.documentRowID = other.documentRowID;
        this.documentID = other.documentID;
        this.documentType = other.documentType;
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
        this.amount = other.amount;
        this.itemName = other.itemName;
        this.invoiceNum = other.invoiceNum;
        this.invoiceDate = other.invoiceDate;
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

    public int getDocumentRowID() {
        return documentRowID;
    }

    public int getCustomerKey() {
        return Integer.parseInt(customerKey);
    }
}
