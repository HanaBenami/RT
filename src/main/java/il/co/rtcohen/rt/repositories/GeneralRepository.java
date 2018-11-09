package il.co.rtcohen.rt.repositories;
import il.co.rtcohen.rt.dao.GeneralType;
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
public class GeneralRepository {

    final static private Logger log = LoggerFactory.getLogger(GeneralRepository.class);

    private DataSource dataSource;

    @Autowired
    public GeneralRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<GeneralType> getNames (String table) {
        List<GeneralType> list = new ArrayList<>();
        String sql = "SELECT * FROM "+table+"";
        try (Connection con = dataSource.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new GeneralType(rs.getInt("id"),rs.getString("name"),rs.getBoolean("active"),table));
            }
            return list;
        }
        catch (SQLException e) {
            log.error(sql);
            log.error("error in getNames: ",e);
            throw new DataRetrievalFailureException("error in getNames: ",e);
        }
    }

    public List<Integer> getActiveId(String table) {
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT * FROM "+table+" where active=1";
        try (Connection con = dataSource.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getInt("id"));
            }
            return list;
        }
        catch (SQLException e) {
            log.error(sql);
            log.error("error in getActiveId: ",e);
            throw new DataRetrievalFailureException("error in getActiveId: ",e);
        }
    }

    public String getNameById (int id,String table) {
        String sql = "SELECT * FROM "+table+" WHERE id= "+id;
        if (id==0)
            return "";
        try (Connection con = dataSource.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                return rs.getString("name");
            }
            log.info("no record with id="+id);
            return "";
        }
        catch (SQLException e) {
            log.error(sql);
            log.error("error in getNameById: ",e);
            throw new DataRetrievalFailureException("error in getNameById: ",e);
        }
    }

    public long insertName (String name,String table) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement stmt = con.prepareStatement("insert into "+table+" (name,active) values (?,1)",
                     Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1,name);
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
                        log.info("new record has been insert to table "+table+" (id="+id+")");
                        return id;
                    }
                    throw new SQLException("error getting the new key");
                }
            }
        }
        catch (SQLException e) {
            log.error("error in insertName (\""+name+"\") to table + "+table+": ",e);
            throw new InsertException("error in insertName (\""+name+"\") to table + "+table+":",e);
        }
    }

    public int update(GeneralType x) {
        try (Connection con = dataSource.getConnection(); PreparedStatement stmt = con.prepareStatement
                ("update "+x.getTable()+" set name=?, active=? where id=?")) {
            stmt.setString(1,x.getName());
            stmt.setBoolean(2,x.getActive());
            stmt.setInt(3,x.getId());
            int n=stmt.executeUpdate();
            log.info("Update "+x.getTable()+" where id="+x.getId()+" > "+n+" records has been updated");
            return n;
        }
        catch (SQLException e) {
            log.error("error in update genetalType (\"id=\"+x.getId()+\", table=\"+x.getTable())",e);
            throw new UpdateException("error in update genetalType (\"id=\"+x.getId()+\", table=\"+x.getTable())",e);
        }
    }

}

