package il.co.rtcohen.rt.dal.dao;

import il.co.rtcohen.rt.dal.dao.interfaces.AbstractTypeSyncedWithHashavshevet;
import il.co.rtcohen.rt.dal.dao.interfaces.BindRepository;
import il.co.rtcohen.rt.dal.dao.interfaces.Cloneable;

public class Customer extends AbstractTypeSyncedWithHashavshevet implements BindRepository<Customer>, Cloneable<Customer> {
    private Integer hashavshevetCustomerId;
    private CustomerType customerType;

    public Customer() {
        super();
    }

    public Customer(Integer id, String name, boolean active, int hashavshevetFirstDocId, Integer hashavshevetCustomerId, CustomerType customerType) {
        super(id, name, active, hashavshevetFirstDocId);
        this.hashavshevetCustomerId = hashavshevetCustomerId;
        this.customerType = customerType;
    }

    public Customer(Customer other) {
        super(other);
        this.hashavshevetCustomerId = other.hashavshevetCustomerId;
        this.customerType = other.customerType;
    }

    @Override
    public Customer cloneObject() {
        return new Customer(this);
    }

    public String getObjectName() {
        return "customer";
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    public Integer getHashavshevetCustomerId() {
        return hashavshevetCustomerId;
    }

    public void setHashavshevetCustomerId(Integer hashavshevetCustomerId) {
        this.hashavshevetCustomerId = hashavshevetCustomerId;
    }

    @Override
    public boolean isItemValid() {
        return super.isItemValid() && (null != this.getCustomerType());
    }
}
