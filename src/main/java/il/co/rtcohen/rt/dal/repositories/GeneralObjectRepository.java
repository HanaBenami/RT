package il.co.rtcohen.rt.dal.repositories;

import il.co.rtcohen.rt.dal.dao.interfaces.AbstractTypeWithNameAndActiveFields;
import il.co.rtcohen.rt.dal.repositories.interfaces.AbstractTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

@Repository
@Deprecated
@Qualifier("GeneralObjectRepository")
public class GeneralObjectRepository extends AbstractTypeRepository<AbstractTypeWithNameAndActiveFields> {
    static private  final String DB_ID_COLUMN = "id";
    static private  final String DB_NAME_COLUMN = "name";
    static private  final String DB_ACTIVE_COLUMN = "active";

    @Autowired
    public GeneralObjectRepository(DataSource dataSource) {
        super(dataSource, null, null,
                new String[] {
                        DB_NAME_COLUMN, DB_ACTIVE_COLUMN
                });
    }

    public GeneralObjectRepository(DataSource dataSource, String dbTableName, String repositoryName) {
        super(dataSource, dbTableName, repositoryName,
                new String[] {
                        DB_NAME_COLUMN, DB_ACTIVE_COLUMN
                });
    }

    public List<AbstractTypeWithNameAndActiveFields> getItems(boolean onlyActiveItems) throws SQLException {
        List<AbstractTypeWithNameAndActiveFields> list = this.getItems();
        list.removeIf(generalObject -> !generalObject.isActive());
        return list;
    }

    protected AbstractTypeWithNameAndActiveFields getItemFromResultSet(ResultSet rs) throws SQLException {
        return new AbstractTypeWithNameAndActiveFields(
                rs.getInt(DB_ID_COLUMN),
                rs.getString(DB_NAME_COLUMN),
                rs.getBoolean(DB_ACTIVE_COLUMN)
        );
    }

    protected int updateItemDetailsInStatement(PreparedStatement preparedStatement, AbstractTypeWithNameAndActiveFields abstractTypeWithNameAndActiveFields) throws SQLException {
        int fieldsCounter = 1;
        preparedStatement.setString(fieldsCounter, abstractTypeWithNameAndActiveFields.getName());
        fieldsCounter++;
        preparedStatement.setBoolean(fieldsCounter, abstractTypeWithNameAndActiveFields.isActive());
        return fieldsCounter;
    }
}

