package il.co.rtcohen.rt.dal.repositories;

import il.co.rtcohen.rt.dal.dao.Area;
import il.co.rtcohen.rt.dal.repositories.interfaces.AbstractTypeWithNameAndActiveFieldsRepository;
import il.co.rtcohen.rt.dal.repositories.interfaces.RepositoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class AreasRepository extends AbstractTypeWithNameAndActiveFieldsRepository<Area> implements RepositoryInterface<Area> {
    static protected final String DB_HERE_COLUMN = "here";
    static protected final String DB_DISPLAY_ORDER_COLUMN = "displayOrder";

    @Autowired
    public AreasRepository(DataSource dataSource) {
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
        preparedStatement.setBoolean(fieldsCounter, area.getHere());
        fieldsCounter++;
        preparedStatement.setInt(fieldsCounter, area.getDisplayOrder());
        return fieldsCounter;
    }

    @Deprecated
    public List<Area> getAreas() throws SQLException {
        return getItems();
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
        areas.removeIf(area -> area.getHere() != here);
        return areas.stream().map(Area::getId).collect(Collectors.toList());
    }

    @Deprecated
    public long insertArea(String name) {
        insertItem(new Area(name));
        return 1; // TODO: change to real value, if needed
    }

    @Deprecated
    public int updateArea(Area area) {
        updateItem(area);
        return 1; // TODO: change to real value, if needed
    }
}

