package il.co.rtcohen.rt.dao;

public class GeneralType {

    private Integer id;
    private String name;
    private boolean active;
    private String table;

    public GeneralType(Integer id, String name, boolean active, String table) {
        this.id=id;
        this.name=name;
        this.active=active;
        this.table=table;
    }

    public String getName() {
        return name;
    }

    public Integer getId() {
        return id;
    }

    public Boolean getActive() {return active; }

    public String getTable() {
        return table;
    }

    public void setName(String name) {
        if (!name.isEmpty())
            this.name = name;
    }

    public void setActive(boolean active) {
        this.active=active;
    }

}
