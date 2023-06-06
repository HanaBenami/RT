package il.co.rtcohen.rt.dal.dao;

import il.co.rtcohen.rt.dal.repositories.AbstractRepository;
import il.co.rtcohen.rt.dal.repositories.RepositoryInterface;

public interface BindRepository<T> {
    abstract public RepositoryInterface<T> getBindRepository();

    abstract public void setBindRepository(RepositoryInterface<T> bindRepository);

    abstract public void insertItem();
}
