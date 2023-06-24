package il.co.rtcohen.rt.dal.repositories.interfaces;

import il.co.rtcohen.rt.dal.dao.GarageStatus;
import il.co.rtcohen.rt.dal.dao.interfaces.AbstractTypeSyncedWithHashavshevet;
import il.co.rtcohen.rt.dal.dao.interfaces.Cloneable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.google.common.collect.ObjectArrays;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

@Repository
public abstract class AbstractTypeSyncedWithHashavshevetRepository<T extends AbstractTypeSyncedWithHashavshevet & Cloneable<T>>
        extends AbstractTypeWithNameAndActiveFieldsRepository<T> implements RepositoryInterface<T> {
    static protected final String DB_HASH_FIRST_DOC_ID = "hashDocID";

    @Autowired
    public AbstractTypeSyncedWithHashavshevetRepository(DataSource dataSource, String dbTableName, String repositoryName,
                                                        String[] additionalDbColumns) {
        super(dataSource, dbTableName, repositoryName,
              ObjectArrays.concat(new String[]{DB_HASH_FIRST_DOC_ID}, additionalDbColumns, String.class));
    }

    protected int updateItemDetailsInStatement(PreparedStatement preparedStatement, T t) throws SQLException {
        int fieldsCounter = super.updateItemDetailsInStatement(preparedStatement, t);
        fieldsCounter++;
        preparedStatement.setInt(fieldsCounter, t.getHashavshevetFirstDocId());
        return fieldsCounter;
    }

    public T getItemByHashDocId(Integer hashDocId) {
        return super.getItem(DB_HASH_FIRST_DOC_ID + "=" + hashDocId);
    }
}
