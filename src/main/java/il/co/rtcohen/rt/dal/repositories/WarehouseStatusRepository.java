package il.co.rtcohen.rt.dal.repositories;

import il.co.rtcohen.rt.dal.dao.WarehouseStatus;
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
import java.util.Comparator;
import java.util.List;

@Repository
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class WarehouseStatusRepository extends AbstractTypeWithNameAndActiveFieldsRepository<WarehouseStatus> implements RepositoryInterface<WarehouseStatus> {
    static protected final String DB_PENDING_GARAGE_COLUMN = "pendingWarehouse";
    static protected final String DB_DISPLAY_ORDER_COLUMN = "displayOrder";

    @Autowired
    public WarehouseStatusRepository(DataSource dataSource) {
        super(dataSource, "warehouseStatus", "Warehouse statuses",
                new String[]{
                        DB_PENDING_GARAGE_COLUMN,
                        DB_DISPLAY_ORDER_COLUMN
                });
    }

    protected WarehouseStatus getItemFromResultSet(ResultSet rs) throws SQLException {
        return new WarehouseStatus(
                rs.getInt(DB_ID_COLUMN),
                rs.getString(DB_NAME_COLUMN),
                rs.getBoolean(DB_PENDING_GARAGE_COLUMN),
                rs.getBoolean(DB_ACTIVE_COLUMN),
                rs.getInt(DB_DISPLAY_ORDER_COLUMN)
        );
    }

    @Override
    protected int updateItemDetailsInStatement(PreparedStatement preparedStatement, WarehouseStatus warehouseStatus) throws SQLException {
        int fieldsCounter = super.updateItemDetailsInStatement(preparedStatement, warehouseStatus);
        fieldsCounter++;
        preparedStatement.setBoolean(fieldsCounter, warehouseStatus.isPendingWarehouse());
        fieldsCounter++;
        preparedStatement.setInt(fieldsCounter, warehouseStatus.getDisplayOrder());
        return fieldsCounter;
    }

    @Override
    public List<WarehouseStatus> getItems() {
        List<WarehouseStatus> list = super.getItems();
        list.sort(Comparator.comparingInt(WarehouseStatus::getDisplayOrder)); // TODO: the order of the records in combo-boxes should be according to this - not working at the moment
        return list;
    }
}

