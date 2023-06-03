package il.co.rtcohen.rt.dal.dao;

import il.co.rtcohen.rt.app.LanguageSettings;

public class Customer extends GeneralObject {
    static {
        setDbTableName("cust");
        setObjectName("customer");
    }

    private Integer customerTypeID;

    public Customer() {
        super();
    }

    public Customer(Integer id, String name, int customerTypeID, boolean active) {
        super(id, name, active);
        this.customerTypeID = customerTypeID;
    }

    public Integer getCustomerTypeID() {
        return customerTypeID;
    }

    public void setCustomerTypeID(Integer customerTypeID) {
        this.customerTypeID = customerTypeID;
    }

    @Override
    public boolean isItemValid() {
        return super.isItemValid() && (null != this.getCustomerTypeID());
    }

    @Override
    public String toString() {
        return LanguageSettings.getLocaleString("the")
                + LanguageSettings.getLocaleString(getObjectName())
                + " \"" + getName() + "\"";
    }
}
