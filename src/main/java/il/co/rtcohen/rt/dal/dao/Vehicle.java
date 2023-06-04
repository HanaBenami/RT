package il.co.rtcohen.rt.dal.dao;

public class Contact extends GeneralObject {
    static {
        setDbTableName("contact");
        setObjectName("contact");
    }

    private Integer siteId;
    private String phone;
    private String notes;

    public Contact() {
        super();
    }

    public Contact(Integer id, String name, boolean active, int siteId, String phone, String notes) {
        super(id, name, active);
        this.siteId = siteId;
        this.phone=phone;
        this.notes=notes;
    }

    public Integer getSiteId() {return this.siteId;}
    public String getPhone () {return this.phone;}
    public String getNotes () {return this.notes;}

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }
    public void setPhone (String phone) {
        this.phone = phone;
    }
    public void setNotes (String notes) {
        this.notes = notes;
    }
}
