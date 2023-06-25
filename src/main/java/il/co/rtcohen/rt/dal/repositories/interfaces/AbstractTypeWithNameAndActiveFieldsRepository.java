package il.co.rtcohen.rt.dal.repositories.interfaces;

import il.co.rtcohen.rt.dal.dao.interfaces.AbstractTypeWithNameAndActiveFields;
import il.co.rtcohen.rt.dal.dao.interfaces.Cloneable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public abstract class AbstractTypeWithNameAndActiveFieldsRepository<T extends AbstractTypeWithNameAndActiveFields & Cloneable<T>>
        extends AbstractTypeRepository<T> implements RepositoryInterface<T> {
    static protected final String DB_NAME_COLUMN = "name";
    static protected final String DB_ACTIVE_COLUMN = "active";

    @Autowired
    public AbstractTypeWithNameAndActiveFieldsRepository(DataSource dataSource, String dbTableName, String repositoryName,
                                                         String[] additionalDbColumns) {
        super(dataSource, dbTableName, repositoryName,
                new String[]{
                        DB_NAME_COLUMN,
                        DB_ACTIVE_COLUMN
                },
                additionalDbColumns);
    }

    public List<T> getItems(boolean onlyActiveItems) {
        List<T> list = this.getItems();
        list.removeIf(t -> !t.isActive());
        return list;
    }

    public T getItemByName(String name) {
        if (null == name || name.isEmpty()) {
            return null;
        }
        String whereClause = "CAST(name as varchar(100))";
        if (name.contains("'")) {
            whereClause += " like '" + name.replaceAll("'", "_") + "'";
        } else {
            whereClause += "='" + name + "'";
        }
        return super.getItem(whereClause);
    }

    abstract protected T getItemFromResultSet(ResultSet rs) throws SQLException;

    protected int updateItemDetailsInStatement(PreparedStatement preparedStatement, T t) throws SQLException {
        int fieldsCounter = 1;
        preparedStatement.setString(fieldsCounter, t.getName());
        fieldsCounter++;
        preparedStatement.setBoolean(fieldsCounter, t.isActive());
        return fieldsCounter;
    }
}
