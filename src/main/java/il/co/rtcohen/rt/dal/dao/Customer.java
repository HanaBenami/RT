package il.co.rtcohen.rt.dal.dao;

import il.co.rtcohen.rt.dal.dao.interfaces.AbstractTypeWithNameAndActiveFields;
import il.co.rtcohen.rt.dal.dao.interfaces.BindRepository;

public class Customer extends AbstractTypeWithNameAndActiveFields implements BindRepository<Customer> {
    private CustomerType customerType;
    private Integer hashavshevetId;

    public Customer() {
        super();
        setObjectName("customer");
    }

    public Customer(Integer id, String name, CustomerType customerType, Integer hashavshevetId, boolean active) {
        super(id, name, active);
        this.customerType = customerType;
        this.hashavshevetId = hashavshevetId;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    public Integer getHashavshevetId() {
        return hashavshevetId;
    }

    public void setHashavshevetId(Integer hashavshevetId) {
        this.hashavshevetId = hashavshevetId;
    }

    @Override
    public boolean isItemValid() {
        return super.isItemValid() && (null != this.getCustomerType());
    }
}
