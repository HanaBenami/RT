package il.co.rtcohen.rt.repositories;

import il.co.rtcohen.rt.dao.Area;
import il.co.rtcohen.rt.exceptions.InsertException;
import il.co.rtcohen.rt.exceptions.UpdateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AreaRepository {

    final static private Logger log = LoggerFactory.getLogger(AreaRepository.class);

    private DataSource dataSource;

    @Autowired
    public AreaRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Area> getAreas() {
        String sql="SELECT * FROM area";
        try (Connection con = dataSource.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                return getListFromRS(rs);
            else return new ArrayList<>();
        }
        catch (SQLException e) {
            log.error(sql);
            log.error("error in getAreas: ",e);
            throw new DataRetrievalFailureException("error in getAreas: ",e);
        }
    }

    private List<Area> getListFromRS(ResultSet rs) throws SQLException {
        List<Area> list = new ArrayList<>();
        do {
            list.add(areaFromRS(rs));
        }  while (rs.next());
        return list;
    }

    private Area areaFromRS(ResultSet rs) throws SQLException {
        return new Area(rs.getInt("id"),rs.getString("name"),
                rs.getBoolean("here"),rs.getBoolean("active"),
                rs.getInt("displayOrder"));
    }

    public Area getAreaById(int id) {
        try (Connection con = dataSource.getConnection(); PreparedStatement stmt = con.prepareStatement
                ("SELECT * FROM area where id=?")) {
            stmt.setInt(1,id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return areaFromRS(rs);
            }
            return new Area(0,"",false,true,0);
        } catch (SQLException e) {
            log.error("error in getAreaById where id="+id+": ", e);
            throw new DataRetrievalFailureException("error in getAreaById where id="+id+": ", e);
        }
    }

    public List<Integer> getOutAreaId() {
        return getActiveId(" and here=0");
    }

    public List<Integer> getHereAreaId() {
        return getActiveId(" and here=1");
    }

    private List<Integer> getActiveId(String where) {
        List<Integer> idList = new ArrayList<>();
        try (Connection con = dataSource.getConnection(); PreparedStatement stmt = con.prepareStatement
                ("SELECT * FROM area where active=1"+where)) {
            ResultSet rs = stmt.executeQuery();
            List<Area> list = new ArrayList<>();
            if (rs.next())
                list = getListFromRS(rs);
            list.sort((o1, o2) -> o2.getName().compareTo(o1.getName()));
            for (Area area : list)
                idList.add(area.getId());
            return idList;
        }
        catch (SQLException e) {
            log.error("SQL statement: SELECT * FROM area where active=1"+where);
            log.error("error in getActiveId: ",e);
            throw new DataRetrievalFailureException("error in getActiveId: ",e);
        }
    }

    public long insertArea(String name) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement stmt = con.prepareStatement("insert into area (name) values (?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString( 1,name);
            int n=stmt.executeUpdate();
            if (n == 0) {
                throw new SQLException("no record has been created");
            }
            else if (n > 1) {
                throw new SQLException("more than one record has been created");
            }
            else {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long id = generatedKeys.getLong(1);
                        log.info("area id=" + id + " has been created");
                        return id;
                    }
                    throw new SQLException("error getting the new key");
                }
            }
        }
        catch (SQLException e) {
            log.error("error in insertArea (\""+name+"\")");
            throw new InsertException("error in insertArea (\""+name+"\")");
        }
    }

    public int updateArea(Area area) {
        try (Connection con = dataSource.getConnection();  PreparedStatement stmt = con.prepareStatement
                ("update area set here=?, displayOrder=? where id=?")) {
            stmt.setBoolean(1,area.getHere());
            stmt.setInt( 2,area.getDisplayOrder());
            stmt.setInt( 3,area.getId());
            int n=stmt.executeUpdate();
            log.info("Updating area #"+area.getId()+" > "+n+" records has been updated");
            return n;
        }
        catch (SQLException e) {
            log.error("error in updateArea (id="+area.getId()+"): ",e);
            throw new UpdateException("error in updateArea (id="+area.getId()+"): ",e);
        }
    }

}

