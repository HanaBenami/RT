package il.co.rtcohen.rt.dal.repositories.hashavshevet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class HashavshevetRepositoryFullData extends HashavshevetAbstractRepository
{
    @Autowired
    protected HashavshevetRepositoryFullData(DataSource dataSource) {
        super(dataSource, "v_hash_current_data");
    }
}

