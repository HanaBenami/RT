package il.co.rtcohen.rt.dal.dao.interfaces;

import il.co.rtcohen.rt.dal.repositories.interfaces.RepositoryInterface;

public interface BindRepository<T> {
    abstract public RepositoryInterface<T> getBindRepository();

    abstract public void setBindRepository(RepositoryInterface<T> bindRepository);

    abstract public void insertItem();
}
