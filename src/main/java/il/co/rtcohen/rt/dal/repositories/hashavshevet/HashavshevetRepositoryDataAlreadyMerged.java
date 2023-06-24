package il.co.rtcohen.rt.dal.repositories.hashavshevet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;

@Repository
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class HashavshevetRepositoryDataAlreadyMerged extends HashavshevetAbstractRepository
{
    @Autowired
    protected HashavshevetRepositoryDataAlreadyMerged(DataSource dataSource) {
        super(dataSource, "hash_data_already_merged");
    }
}

