package il.co.rtcohen.rt.dal.dao.interfaces;

import il.co.rtcohen.rt.app.GeneralErrorHandler;
import il.co.rtcohen.rt.app.LanguageSettings;
import il.co.rtcohen.rt.dal.repositories.interfaces.RepositoryInterface;

import java.util.Objects;
import java.util.logging.Logger;

abstract public class AbstractType {
    private Integer id;
    private RepositoryInterface<AbstractType> bindRepository;

    public AbstractType() {

    }

    public AbstractType(Integer id) {
        this.id = id;
    }

    public AbstractType(AbstractType other) {
        this.id = other.id;
        this.bindRepository = other.bindRepository;
    }

    public Integer getId() {
        return id;
    }

    public void setId(long id) {
        this.id = (int) id;
    }

    public RepositoryInterface getBindRepository() {
        return bindRepository;
    }

    public void setBindRepository(RepositoryInterface bindRepository) {
        this.bindRepository = bindRepository;
    }

    public void insertItem() {
        assert null != this.getBindRepository();
        this.getBindRepository().insertItem(this);
    }

    public boolean isItemValid() {
        return true;
    }

    abstract public String getObjectName();

    @Override
    public String toString() {
        return (null == getId() ? LanguageSettings.getLocaleString("the") : "")
                + LanguageSettings.getLocaleString((null == getObjectName() ? "item" : getObjectName()))
                + (null == getId() ? "" : (" #" + getId()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractType that = (AbstractType) o;
        return (null != id) && (id.equals(that.id));
    }

    @Override
    public int hashCode() {
        Integer id = this.getId();
        return (null == id ? 0 : id);
    }

    static Logger getLogger() {
        return Logger.getLogger(GeneralErrorHandler.class.getName());
    }

    // Used post record update in the DB
    public void postSave() {
    }

    public boolean isDraft() {
        return (null == this.getId() || 0 == this.getId());
    }
}
