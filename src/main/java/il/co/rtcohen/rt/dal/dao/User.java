package il.co.rtcohen.rt.dal.dao;

public class User extends GeneralType {

    public User(int id, String name, boolean active) {
        super(id, name, active, "user");
    }
}