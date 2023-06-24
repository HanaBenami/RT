package il.co.rtcohen.rt.dal.repositories;

import il.co.rtcohen.rt.dal.dao.City;
import il.co.rtcohen.rt.dal.repositories.interfaces.AbstractTypeWithNameAndActiveFieldsRepository;
import il.co.rtcohen.rt.dal.repositories.interfaces.RepositoryInterface;
import il.co.rtcohen.rt.utils.NullPointerExceptionWrapper;
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
public class CityRepository extends AbstractTypeWithNameAndActiveFieldsRepository<City> implements RepositoryInterface<City> {
    static protected final String DB_AREA_ID_COLUMN = "areaID";

    private final AreaRepository areaRepository;

    @Autowired
    public CityRepository(DataSource dataSource, AreaRepository areaRepository) {
        super(dataSource, "CITY", "Cities",
                new String[] {
                        DB_AREA_ID_COLUMN
                });
        this.areaRepository = areaRepository;
    }

    protected City getItemFromResultSet(ResultSet rs) throws SQLException {
        return new City(
                rs.getInt(DB_ID_COLUMN),
                rs.getString(DB_NAME_COLUMN),
                rs.getBoolean(DB_ACTIVE_COLUMN),
                this.areaRepository.getItem(rs.getInt(DB_AREA_ID_COLUMN))
        );
    }

    @Override
    protected int updateItemDetailsInStatement(PreparedStatement preparedStatement, City city) throws SQLException {
        int fieldsCounter = super.updateItemDetailsInStatement(preparedStatement, city);
        fieldsCounter++;
        preparedStatement.setInt(fieldsCounter, NullPointerExceptionWrapper.getWrapper(city, c -> c.getArea().getId(), 0));
        return fieldsCounter;
    }
}
