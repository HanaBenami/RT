package il.co.rtcohen.rt.dal.repositories;

import java.sql.SQLException;
import java.util.List;

public interface RepositoryInterface<T> {
    abstract public List<T> getItems() throws SQLException;

    abstract public T getItem(Integer id);

    abstract public T getItem(String whereClause);

    abstract public long insertItem(T t);

    abstract public void updateItem(T t);

    abstract public void deleteItem(T t);
}
