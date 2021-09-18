package il.co.rtcohen.rt.dal.dao;

public class Contact extends GeneralType {
    private Integer siteId;
    private String phone;
    private String notes;

    public Contact(int id, String name, boolean active, int siteId, String phone, String notes) {
        super(id, name, active, "contact");
        this.siteId = siteId;
        this.phone=phone;
        this.notes=notes;
    }

    public int getSiteId() {return this.siteId;}
    public String getPhone () {return this.phone;}
    public String getNotes () {return this.notes;}

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }
    public void setPhone (String phone) {
        this.phone=phone;
    }
    public void setNotes (String notes) {
        this.notes=notes;
    }
}
