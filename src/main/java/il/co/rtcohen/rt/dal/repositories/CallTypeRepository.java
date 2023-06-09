package il.co.rtcohen.rt.dal.repositories;

import il.co.rtcohen.rt.dal.dao.CallType;
import il.co.rtcohen.rt.dal.repositories.interfaces.AbstractTypeWithNameAndActiveFieldsRepository;
import il.co.rtcohen.rt.dal.repositories.interfaces.RepositoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class CallTypeRepository extends AbstractTypeWithNameAndActiveFieldsRepository<CallType> implements RepositoryInterface<CallType> {
    @Autowired
    public CallTypeRepository(DataSource dataSource) {
        super(dataSource, "calltype", "Calls types",
                new String[] {

                }
        );
    }

    protected CallType getItemFromResultSet(ResultSet rs) throws SQLException {
        return new CallType(
                rs.getInt(DB_ID_COLUMN),
                rs.getString(DB_NAME_COLUMN),
                rs.getBoolean(DB_ACTIVE_COLUMN)
        );
    }
}
