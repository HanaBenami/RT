package il.co.rtcohen.rt.dal.dao;

public class Customer extends GeneralType {

    private Integer customerTypeID;

    public Customer(int id, String name, int customerTypeID, boolean active) {
        super(id,name,active,"cust");
        this.customerTypeID = customerTypeID;
    }

    public int getCustomerTypeID() {
        return customerTypeID;
    }

    public void setCustomerTypeID(Integer customerTypeID) {
        this.customerTypeID = customerTypeID;
    }

}
