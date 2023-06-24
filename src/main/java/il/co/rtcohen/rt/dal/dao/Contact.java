package il.co.rtcohen.rt.dal.dao;

import il.co.rtcohen.rt.dal.dao.interfaces.AbstractTypeSyncedWithHashavshevet;
import il.co.rtcohen.rt.dal.dao.interfaces.Cloneable;
import il.co.rtcohen.rt.dal.dao.interfaces.Nameable;

public class Contact extends AbstractTypeSyncedWithHashavshevet implements Nameable, Cloneable<Contact> {
    private Site site;
    private String phone;
    private String notes;

    public Contact() {
        super();
    }

    public Contact(Integer id, String name, boolean active, int hashavshevetFirstDocId, Site site, String phone, String notes) {
        super(id, name, active, hashavshevetFirstDocId);
        this.site = site;
        this.phone = phone;
        this.notes = notes;
    }

    public Contact(Contact other) {
        super(other);
        this.site = other.site;
        this.phone = other.phone;
        this.notes = other.notes;
    }

    @Override
    public Contact cloneObject() {
        return new Contact(this);
    }

    @Override
    public String getObjectName() {
        return "contact";
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
