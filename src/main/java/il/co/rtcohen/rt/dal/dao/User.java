package il.co.rtcohen.rt.dal.dao;

import il.co.rtcohen.rt.dal.dao.interfaces.AbstractTypeWithNameAndActiveFields;
import il.co.rtcohen.rt.dal.dao.interfaces.BindRepository;

public class User extends AbstractTypeWithNameAndActiveFields implements BindRepository<User> {
    public User(int id, String name, boolean active) {
        super(id, name, active);
    }

    public User() {
        super();
    }
}
