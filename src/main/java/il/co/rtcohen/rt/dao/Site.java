package il.co.rtcohen.rt.dao;

public class Site extends GeneralType {

    private Integer customerId;
    private Integer areaId;
    private String address;
    private String contact;
    private String phone;
    private String notes;

    public Site() {
        this(0,0,"",0,"",true,"","","");
    }

    public Site(int customerId, int id, String name, int areaId, String address, boolean active, String contact, String phone, String notes) {
        super(id,name,active,"site");
        this.customerId = customerId;
        this.areaId = areaId;
        this.address=address;
        this.contact=contact;
        this.phone=phone;
        this.notes=notes;
    }

    public int getAreaId() {
        return areaId;
    }
    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public String getAddress () {
        if (this.address==null) return "";
        else
            return this.address;
    }
    public String getContact () {return this.contact;}
    public String getPhone () {return this.phone;}
    public String getNotes () {return this.notes;}
    public int getCustomerId() {return this.customerId;}

    public void setAddress (String address) {
        this.address=address;
    }
    public void setContact (String contact) {
        this.contact=contact;
    }
    public void setPhone (String phone) {
        this.phone=phone;
    }
    public void setNotes (String notes) {
        this.notes=notes;
    }
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

}
