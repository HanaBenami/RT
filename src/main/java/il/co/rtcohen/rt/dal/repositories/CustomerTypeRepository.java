package il.co.rtcohen.rt.dal.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
@Qualifier("CustomerTypeRepository")
public class CustomerTypeRepository extends GeneralObjectRepository {
    @Autowired
    public CustomerTypeRepository(DataSource dataSource) {
        super(dataSource, "custType", "Customer types");
    }
}

