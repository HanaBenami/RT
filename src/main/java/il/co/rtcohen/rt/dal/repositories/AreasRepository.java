package il.co.rtcohen.rt.dal.repositories;

import il.co.rtcohen.rt.dal.dao.Area;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class AreasRepository extends AbstractRepository<Area> {
    @Autowired
    public AreasRepository(DataSource dataSource) {
        super(dataSource, "area", "Areas", null);
    }

    protected Area getItemFromResultSet(ResultSet rs) throws SQLException {
        return new Area(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getBoolean("here"),
                rs.getBoolean("active"),
                rs.getInt("displayOrder")
        );
    }

    protected PreparedStatement generateInsertStatement(Connection connection, Area area) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "insert into " + this.DB_TABLE_NAME
                        + " (name, here, displayOrder)"
                        + " values (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        );
        updateAreaDetailsInStatement(stmt, area);
        return stmt;
    }

    protected PreparedStatement generateUpdateStatement(Connection connection, Area area) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "update " + this.DB_TABLE_NAME + " set name=?, here=?, displayOrder=? where id=?",
                Statement.RETURN_GENERATED_KEYS
        );
        updateAreaDetailsInStatement(stmt, area);
        stmt.setInt( 4, area.getId());
        return stmt;
    }

    private void updateAreaDetailsInStatement(PreparedStatement stmt, Area area) throws SQLException {
        stmt.setString(1, area.getName());
        stmt.setBoolean(2, area.getHere());
        stmt.setInt( 3, area.getDisplayOrder());
    }

    @Deprecated
    public List<Area> getAreas() {
        return getItems();
    }

    @Deprecated
    public Area getAreaById(int id) {
        return getItem(id);
    }

    @Deprecated
    public List<Integer> getOutAreaId() {
        return getActiveId(false);
    }

    @Deprecated
    public List<Integer> getHereAreaId() {
        return getActiveId(true);
    }

    @Deprecated
    private List<Integer> getActiveId(Boolean here) {
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

