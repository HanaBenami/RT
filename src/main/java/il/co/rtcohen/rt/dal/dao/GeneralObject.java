package il.co.rtcohen.rt.dal.dao;

public class GeneralType extends AbstractType {

    private String name;
    private boolean active;
    private String dbTableName;

    public GeneralType() {
        super(null);
        this.active = true;
    }

    public GeneralType(String name) {
        super(null);
        this.name = name;
        this.active = true;
    }

    public GeneralType(Integer id, String name, boolean active, String dbTableName) {
        super(id);
        this.name = name;
        this.active = active;
        this.dbTableName = dbTableName;
    }

    public String getDbTableName() {
        return DB_TABLE_NAME;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active=active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (!name.isEmpty()) {
            this.name = name;
        }
    }

}
