package il.co.rtcohen.rt.dal.dao;

import il.co.rtcohen.rt.dal.dao.interfaces.AbstractTypeSyncedWithHashavshevet;
import il.co.rtcohen.rt.dal.dao.interfaces.BindRepository;
import il.co.rtcohen.rt.dal.dao.interfaces.Cloneable;

// TODO: Set cites for all the existing customers + areas for all the cities X
public class Site extends AbstractTypeSyncedWithHashavshevet implements BindRepository<Site>, Cloneable<Site> {
    private Customer customer;
    private String address;
    private City city;
    @Deprecated private Area area; // TODO: delete
    private String notes;

    public Site() { }

    public Site(Customer customer, Integer id, String name, boolean active, int hashavshevetFirstDocId, String address, City city, Area area, String notes) {
        super(id, name, active, hashavshevetFirstDocId);
        this.customer = customer;
        this.address = address;
        this.city = city;
        this.area = area; // TODO: delete
        this.notes = notes;
    }

    public Site(Site other) {
        super(other);
        this.customer = other.customer;
        this.city = other.city;
        this.address = other.address;
        this.area = other.area; // TODO: delete
        this.notes = other.notes;
    }

    @Override
    public Site cloneObject() {
        return new Site(this);
    }

    public String getAddress() {
        return (null == this.address ? "" : this.address);
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Area getArea() {
        return (null == this.getCity() ? this.area : this.getCity().getArea()); // TODO: delete area
    }

    public String getNotes() {
        return (null == this.notes ? "" : this.notes);
    }

    public Customer getCustomer() {
        return this.customer;
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
        // TODO: return super.isItemValid() && (null != this.getCity()) && (null != this.getCustomer());
    }
}
