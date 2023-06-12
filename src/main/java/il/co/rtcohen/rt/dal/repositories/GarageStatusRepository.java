package il.co.rtcohen.rt.dal.repositories;

import il.co.rtcohen.rt.dal.dao.GarageStatus;
import il.co.rtcohen.rt.dal.repositories.interfaces.AbstractTypeWithNameAndActiveFieldsRepository;
import il.co.rtcohen.rt.dal.repositories.interfaces.RepositoryInterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class GarageStatusRepository extends AbstractTypeWithNameAndActiveFieldsRepository<GarageStatus> implements RepositoryInterface<GarageStatus> {
    static protected final String DB_PENDING_GARAGE_COLUMN = "pendingGarage";
    static protected final String DB_DISPLAY_ORDER_COLUMN = "displayOrder";

    @Autowired
    public GarageStatusRepository(DataSource dataSource) {
        super(dataSource, "garageStatus", "Garage statuses",
                new String[]{
                        DB_PENDING_GARAGE_COLUMN,
                        DB_DISPLAY_ORDER_COLUMN
                });
    }

    protected GarageStatus getItemFromResultSet(ResultSet rs) throws SQLException {
        return new GarageStatus(
                rs.getInt(DB_ID_COLUMN),
                rs.getString(DB_NAME_COLUMN),
                rs.getBoolean(DB_PENDING_GARAGE_COLUMN),
                rs.getBoolean(DB_ACTIVE_COLUMN),
                rs.getInt(DB_DISPLAY_ORDER_COLUMN)
        );
    }

    @Override
    protected int updateItemDetailsInStatement(PreparedStatement preparedStatement, GarageStatus garageStatus) throws SQLException {
        int fieldsCounter = super.updateItemDetailsInStatement(preparedStatement, garageStatus);
        fieldsCounter++;
        preparedStatement.setBoolean(fieldsCounter, garageStatus.isPendingGarage());
        fieldsCounter++;
        preparedStatement.setInt(fieldsCounter, garageStatus.getDisplayOrder());
        return fieldsCounter;
    }
}

