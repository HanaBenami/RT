package il.co.rtcohen.rt.dal.repositories;

import il.co.rtcohen.rt.utils.NullPointerExceptionWrapper;
import il.co.rtcohen.rt.dal.dao.Customer;
import il.co.rtcohen.rt.dal.dao.Site;
import il.co.rtcohen.rt.dal.repositories.interfaces.AbstractTypeSyncedWithHashavshevetRepository;
import il.co.rtcohen.rt.dal.repositories.interfaces.RepositoryInterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

@Repository
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SiteRepository extends AbstractTypeSyncedWithHashavshevetRepository<Site> implements RepositoryInterface<Site> {
    static protected final String DB_CUSTOMER_ID_COLUMN = "custID";
    static protected final String DB_ADDRESS_COLUMN = "address";
    static protected final String DB_CITY_ID_COLUMN = "cityID";
    static protected final String DB_AREA_ID_COLUMN = "areaID"; // TODO: deprecate & delete
    static protected final String DB_NOTES_COLUMN = "notes";

    final private CityRepository cityRepository;
    final private AreaRepository areaRepository;
    final private CustomerRepository customerRepository;

    @Autowired
    public SiteRepository(DataSource dataSource, CityRepository cityRepository, AreaRepository areaRepository, CustomerRepository customerRepository) {
        super(
                dataSource, "SITE", "Sites",
                new String[]{
                        DB_CUSTOMER_ID_COLUMN,
                        DB_ADDRESS_COLUMN,
                        DB_CITY_ID_COLUMN,
                        DB_AREA_ID_COLUMN, // TODO: delete
                        DB_NOTES_COLUMN
                }
        );
        this.cityRepository = cityRepository;
        this.areaRepository = areaRepository;
        this.customerRepository = customerRepository;
    }

    protected Site getItemFromResultSet(ResultSet rs) throws SQLException {
        return new Site(
                this.customerRepository.getItem(rs.getInt(DB_CUSTOMER_ID_COLUMN)),
                rs.getInt(DB_ID_COLUMN),
                rs.getString(DB_NAME_COLUMN),
                rs.getBoolean(DB_ACTIVE_COLUMN),
                rs.getInt(DB_HASH_FIRST_DOC_ID),
                rs.getString(DB_ADDRESS_COLUMN),
                this.cityRepository.getItem(rs.getInt(DB_CITY_ID_COLUMN)),
                this.areaRepository.getItem(rs.getInt(DB_AREA_ID_COLUMN)), // TODO: delete
                rs.getString(DB_NOTES_COLUMN));
    }

    @Override
    protected int updateItemDetailsInStatement(PreparedStatement preparedStatement, Site site) throws SQLException {
        int fieldsCounter = super.updateItemDetailsInStatement(preparedStatement, site);
        fieldsCounter++;
        preparedStatement.setInt(fieldsCounter, site.getCustomer().getId());
        fieldsCounter++;
        preparedStatement.setString(fieldsCounter, NullPointerExceptionWrapper.getWrapper(site, Site::getAddress, ""));
        fieldsCounter++;
        preparedStatement.setInt(fieldsCounter, NullPointerExceptionWrapper.getWrapper(site, s -> s.getCity().getId(), 0));
        fieldsCounter++;
        preparedStatement.setInt(fieldsCounter, NullPointerExceptionWrapper.getWrapper(site, s -> s.getArea().getId(), 0)); // TODO: delete
        fieldsCounter++;
        preparedStatement.setString(fieldsCounter, site.getNotes());
        return fieldsCounter;
    }

    public List<Site> getItems(boolean onlyActiveItems) {
        List<Site> list = this.getItems();
        list.removeIf(generalObject -> !generalObject.isActive());
        return list;
    }

    public List<Site> getItems(Customer customer) {
        return this.getItems(DB_CUSTOMER_ID_COLUMN + "=" + customer.getId() + " and " + DB_ACTIVE_COLUMN + "=1");
    }
}

