package il.co.rtcohen.rt.dal.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.stream.Collectors;

import il.co.rtcohen.rt.dal.dao.Area;
import il.co.rtcohen.rt.dal.repositories.interfaces.AbstractTypeWithNameAndActiveFieldsRepository;
import il.co.rtcohen.rt.dal.repositories.interfaces.RepositoryInterface;

@Repository
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AreaRepository extends AbstractTypeWithNameAndActiveFieldsRepository<Area> implements RepositoryInterface<Area> {
    static protected final String DB_HERE_COLUMN = "here";
    static protected final String DB_DISPLAY_ORDER_COLUMN = "displayOrder";

    @Autowired
    public AreaRepository(DataSource dataSource) {
        super(dataSource, "area", "Areas",
                new String[]{
                        DB_HERE_COLUMN,
                        DB_DISPLAY_ORDER_COLUMN
                });
    }

    protected Area getItemFromResultSet(ResultSet rs) throws SQLException {
        return new Area(
                rs.getInt(DB_ID_COLUMN),
                rs.getString(DB_NAME_COLUMN),
                rs.getBoolean(DB_HERE_COLUMN),
                rs.getBoolean(DB_ACTIVE_COLUMN),
                rs.getInt(DB_DISPLAY_ORDER_COLUMN)
        );
    }

    @Override
    protected int updateItemDetailsInStatement(PreparedStatement preparedStatement, Area area) throws SQLException {
        int fieldsCounter = super.updateItemDetailsInStatement(preparedStatement, area);
        fieldsCounter++;
        preparedStatement.setBoolean(fieldsCounter, area.isHere());
        fieldsCounter++;
        preparedStatement.setInt(fieldsCounter, area.getDisplayOrder());
        return fieldsCounter;
    }

    @Deprecated
    public Area getAreaById(int id) {
        return getItem(id);
    }

    @Deprecated
    public List<Integer> getOutAreaId() throws SQLException {
        return getActiveId(false);
    }

    @Deprecated
    public List<Integer> getHereAreaId() throws SQLException {
        return getActiveId(true);
    }

    @Deprecated
    private List<Integer> getActiveId(Boolean here) throws SQLException {
        List<Area> areas = getItems();
        areas.removeIf(area -> !area.isActive());
        areas.removeIf(area -> area.isHere() != here);
        return areas.stream().map(Area::getId).collect(Collectors.toList());
    }
}

