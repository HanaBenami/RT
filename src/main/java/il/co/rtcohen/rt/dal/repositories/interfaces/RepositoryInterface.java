package il.co.rtcohen.rt.dal.repositories.interfaces;

import java.sql.SQLException;
import java.util.List;

public interface RepositoryInterface<T> {
    List<T> getItems() throws SQLException;

    T getItem(Integer id);

    T getItem(String whereClause);

    long insertItem(T t);

    void updateItem(T t);

    void deleteItem(T t);
}
