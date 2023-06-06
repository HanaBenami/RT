package il.co.rtcohen.rt.dal.repositories;

import il.co.rtcohen.rt.dal.dao.GeneralObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
@Qualifier("UsersRepository")
public class UsersRepository extends GeneralObjectRepository {
    @Autowired
    public UsersRepository(DataSource dataSource) {
        super(dataSource, "users", "users types");
    }

    public GeneralObject getItem(String name) {
        if (null == name || name.isEmpty()) {
            return null;
        }
        return super.getItem("CAST(name as varchar(100))='" + name + "'");
    }
}
