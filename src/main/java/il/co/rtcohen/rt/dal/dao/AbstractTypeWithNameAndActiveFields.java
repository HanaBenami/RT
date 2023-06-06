package il.co.rtcohen.rt.dal.dao;

import java.util.List;
import java.util.stream.Collectors;

public class AbstractTypeWithNameAndActiveFields extends AbstractType implements Nameable {
    private String name;
    private boolean active;

    public AbstractTypeWithNameAndActiveFields() {
        super(null);
        this.active = true;
    }

    public AbstractTypeWithNameAndActiveFields(String name) {
        super(null);
        this.name = name;
        this.active = true;
    }

    public AbstractTypeWithNameAndActiveFields(Integer id, String name, boolean active) {
        super(id);
        this.name = name;
        this.active = active;
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

    public static List<Integer> generateListOfIds(List<AbstractTypeWithNameAndActiveFields> listOfObjects) {
        return listOfObjects.stream().map(AbstractTypeWithNameAndActiveFields::getId).collect(Collectors.toList());
    }

    public static List<String> generateListOfNames(List<AbstractTypeWithNameAndActiveFields> listOfObjects) {
        return listOfObjects.stream().map(AbstractTypeWithNameAndActiveFields::getName).collect(Collectors.toList());
    }
}
