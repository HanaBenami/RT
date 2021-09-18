package il.co.rtcohen.rt.dal.repositories;
import il.co.rtcohen.rt.dal.dao.GeneralType;
import il.co.rtcohen.rt.dal.InsertException;
import il.co.rtcohen.rt.dal.UpdateException;
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
        return getNames(table, false);
    }

    public List<GeneralType> getNames(String table, boolean activeOnly) {
        List<GeneralType> list = new ArrayList<>();
        String sql = "SELECT * FROM " + table + (activeOnly ? " WHERE active=1" : "");
        try (Connection con = dataSource.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new GeneralType(rs.getInt("id"),rs.getString("name"),rs.getBoolean("active"),table));
            }
            return list;
        }
        catch (SQLException e) {
            log.error(sql);
            String msg = "error in getNames (table=" + table + ", activeOnly=" + activeOnly + "): ";
            log.error(msg, e);
            throw new DataRetrievalFailureException(msg, e);
        }
    }

    public int getIdByName(String table, String name) {
        List<GeneralType> list = getNames(table);
        for (GeneralType obj : list) {
            if (obj.getName().equals(name)) {
                return obj.getId();
            }
        }
        return -1;
    }

    public List<Integer> getActiveId(String table) {
        return getIds(table, true);
    }

    public List<Integer> getIds(String table) {
        return getIds(table, false);
    }

    public List<Integer> getIds(String table, boolean onlyActive) {
        return getIds(table, onlyActive, null);
    }

    public List<Integer> getIds(String table, boolean onlyActive, String additionalWhere) {
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT * FROM " + table
                + (onlyActive ? " where active=1" : "")
                + (null != additionalWhere ? (onlyActive ? " and " : " where ") + additionalWhere : "");
        try (Connection con = dataSource.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getInt("id"));
            }
            return list;
        }
        catch (SQLException e) {
            log.error(sql);
            String msg = "error in getIds (table=" + table + ", onlyActive=" + onlyActive + ", additionalWhere=" + additionalWhere + "): ";
            log.error(msg, e);
            throw new DataRetrievalFailureException(msg, e);
        }
    }

    public String getNameById (int id, String table) {
        return getNameById(id, table, null);
    }

    public String getNameById(int id, String table, String additionalWhere) {
        String sql = "SELECT * FROM " + table + " WHERE id= " + id
                + (null != additionalWhere ? " and " + additionalWhere : "");
        if (id==0)
            return "";
        try (Connection con = dataSource.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
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
            log.error("error in update generalType (\"id=\"+x.getId()+\", table=\"+x.getTable())",e);
            throw new UpdateException("error in update generalType (\"id=\"+x.getId()+\", table=\"+x.getTable())",e);
        }
    }

}

