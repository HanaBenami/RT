package il.co.rtcohen.rt.dal.dao;

import il.co.rtcohen.rt.dal.dao.interfaces.AbstractTypeWithNameAndActiveFields;
import il.co.rtcohen.rt.dal.dao.interfaces.Nameable;

public class Contact extends AbstractTypeWithNameAndActiveFields implements Nameable {
    private Site site;
    private String phone;
    private String notes;

    public Contact() {
        super();
        setObjectName("contact");
    }

    public Contact(Integer id, String name, boolean active, Site site, String phone, String notes) {
        super(id, name, active);
        this.site = site;
        this.phone = phone;
        this.notes = notes;
    }

    public Site getSite() {
        return this.site;
    }

    public String getPhone() {
        return this.phone;
    }

    public String getNotes() {
        return this.notes;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
