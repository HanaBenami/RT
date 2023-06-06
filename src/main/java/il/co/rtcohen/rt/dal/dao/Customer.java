package il.co.rtcohen.rt.dal.dao;

import il.co.rtcohen.rt.app.LanguageSettings;

public class Customer extends AbstractTypeWithNameAndActiveFields {
    static {
        setDbTableName("cust");
        setObjectName("customer");
    }

    private CustomerType customerType;

    public Customer() {
        super();
    }

    public Customer(Integer id, String name, CustomerType customerType, boolean active) {
        super(id, name, active);
        this.customerType = customerType;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    @Override
    public boolean isItemValid() {
        return super.isItemValid() && (null != this.getCustomerType());
    }

    @Override
    public String toString() {
        return LanguageSettings.getLocaleString("the")
                + LanguageSettings.getLocaleString(getObjectName())
                + " \"" + getName() + "\"";
    }
}
