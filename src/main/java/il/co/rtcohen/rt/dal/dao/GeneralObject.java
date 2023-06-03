package il.co.rtcohen.rt.dal.dao;

import java.util.List;
import java.util.stream.Collectors;

public class GeneralObject extends AbstractType {

    private String name;
    private boolean active;

    public GeneralObject() {
        super(null);
        this.active = true;
    }

    public GeneralObject(String name) {
        super(null);
        this.name = name;
        this.active = true;
    }

    public GeneralObject(Integer id, String name, boolean active) {
        super(id);
        this.name = name;
        this.active = active;
    }

    @Deprecated
    public GeneralObject(Integer id, String name, boolean active, String dbTableName) {
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

    public static List<Integer> generateListOfIds(List<GeneralObject> listOfObjects) {
        return listOfObjects.stream().map(GeneralObject::getId).collect(Collectors.toList());
    }

    public static List<String> generateListOfNames(List<GeneralObject> listOfObjects) {
        return listOfObjects.stream().map(GeneralObject::getName).collect(Collectors.toList());
    }
}
