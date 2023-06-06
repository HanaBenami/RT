package il.co.rtcohen.rt.dal.dao;

public class Site extends AbstractTypeWithNameAndActiveFields {
    static {
        setDbTableName("site");
        setObjectName("site");
    }

    private Customer customer;
    private Area area;
    private String address;
    private String notes;

    public Site() {

    }

    public Site(Customer customer, Integer id, String name, Area area, String address, boolean active, String notes) {
        super(id, name, active);
        this.customer = customer;
        this.area = area;
        this.address = address;
        this.notes = notes;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public String getAddress() {
        return (null == this.address ? "" : this.address);
    }

    public String getNotes() {
        return (null == this.notes ? "" : this.notes);
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public boolean isItemValid() {
        return super.isItemValid() && (null != this.getArea()) && (null != this.getCustomer());
    }
}
