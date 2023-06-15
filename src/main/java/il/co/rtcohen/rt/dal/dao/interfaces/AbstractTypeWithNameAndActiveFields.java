package il.co.rtcohen.rt.dal.dao.interfaces;

import il.co.rtcohen.rt.app.LanguageSettings;

// TODO: turn to abstract once generalRepository will be deprecated
public class AbstractTypeWithNameAndActiveFields extends AbstractType implements Nameable {
    private String name;
    private boolean active;

    public AbstractTypeWithNameAndActiveFields() {
        super();
        this.active = true;
    }

    public AbstractTypeWithNameAndActiveFields(Integer id, String name, boolean active) {
        super(id);
        this.name = name;
        this.active = active;
    }

    public AbstractTypeWithNameAndActiveFields(AbstractTypeWithNameAndActiveFields other) {
        super(other);
        this.name = other.name;
        this.active = other.active;
    }

    // TODO: delete once generalRepository will be deprecated
    @Override
    public String getObjectName() {
        return null;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active=active;
    }

    public String getName() {
        return (null == name ? "" : name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isItemValid() {
        return (null != this.getName()) && (!this.getName().isEmpty());
    }

    @Override
    public String toString() {
        return super.toString() + " (" + getName() + ")";
    }
}
