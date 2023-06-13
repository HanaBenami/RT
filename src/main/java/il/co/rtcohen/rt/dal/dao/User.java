package il.co.rtcohen.rt.dal.dao;

import il.co.rtcohen.rt.dal.dao.interfaces.AbstractTypeWithNameAndActiveFields;
import il.co.rtcohen.rt.dal.dao.interfaces.BindRepository;
import il.co.rtcohen.rt.dal.dao.interfaces.Cloneable;

public class User extends AbstractTypeWithNameAndActiveFields implements BindRepository<User>, Cloneable<User> {
    public User(int id, String name, boolean active) {
        super(id, name, active);
    }

    public User() {
        super();
    }

    public User(User other) {
        super(other);
    }

    @Override
    public User cloneObject() {
        return new User(this);
    }

    public String getObjectName() {
        return "user";
    }
}
