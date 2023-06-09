package il.co.rtcohen.rt.dal.repositories;
import il.co.rtcohen.rt.dal.dao.interfaces.AbstractTypeWithNameAndActiveFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Deprecated
@Repository
public class GeneralRepository {

    final static private Logger log = LoggerFactory.getLogger(GeneralRepository.class);

    private final DataSource dataSource;

    @Autowired
    public GeneralRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<AbstractTypeWithNameAndActiveFields> getNames(String table) {
        return getNames(table, false);
    }

    public List<AbstractTypeWithNameAndActiveFields> getNames(String table, boolean activeOnly) {
        List<AbstractTypeWithNameAndActiveFields> list = new ArrayList<>();
        String sql = "SELECT * FROM " + table + (activeOnly ? " WHERE active=1" : "");
        try (Connection con = dataSource.getConnection();
            PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                list.add(new AbstractTypeWithNameAndActiveFields(rs.getInt("id"),
                        rs.getString("name"),
                        rs.getBoolean("active")));
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

    public List<Integer> getActiveId(String table) {
        return getIds(table, true);
    }

    public List<Integer> getIds(String table, boolean onlyActive) {
        return getIds(table, onlyActive, null);
    }

    public List<Integer> getIds(String table, boolean onlyActive, String additionalWhere) {
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT * FROM " + table
                + (onlyActive ? " where active=1" : "")
                + (null != additionalWhere ? (onlyActive ? " and " : " where ") + additionalWhere : "");
        try (Connection con = dataSource.getConnection(); PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            ResultSet rs = preparedStatement.executeQuery();
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

    public String getNameById (Integer id, String table) {
        return (null == id ? "" : getNameById(id, table, null));
    }

    public String getNameById(int id, String table, String additionalWhere) {
        String sql = "SELECT * FROM " + table + " WHERE id= " + id
                + (null != additionalWhere ? " and " + additionalWhere : "");
        if (id==0)
            return "";
        try (Connection con = dataSource.getConnection(); PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            ResultSet rs = preparedStatement.executeQuery();
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
}

