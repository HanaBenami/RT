package il.co.rtcohen.rt.dal.dao;

import il.co.rtcohen.rt.dal.dao.interfaces.AbstractTypeWithNameAndActiveFields;

public class User extends AbstractTypeWithNameAndActiveFields {
    public User(int id, String name, boolean active) {
        super(id, name, active);
    }

    public User() {
        
    }
}
