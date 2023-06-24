package il.co.rtcohen.rt.dal.dao;

import il.co.rtcohen.rt.dal.dao.interfaces.AbstractTypeWithNameAndActiveFields;
import il.co.rtcohen.rt.dal.dao.interfaces.BindRepository;
import il.co.rtcohen.rt.dal.dao.interfaces.Cloneable;

public class City extends AbstractTypeWithNameAndActiveFields implements BindRepository<City>, Cloneable<City> {
    private Area area;

    public City() {
        super();
    }

    public City(Integer id, String name, boolean active, Area area) {
        super(id, name, active);
        this.area = area;
    }

    public City(City other) {
        super(other);
        this.area = other.area;
    }

    @Override
    public City cloneObject() {
        return new City(this);
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public boolean isItemValid() {
        return super.isItemValid() && (null != this.getArea());
    }
}
