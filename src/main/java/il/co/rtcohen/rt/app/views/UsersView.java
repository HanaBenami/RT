package il.co.rtcohen.rt.app.views;

import com.vaadin.server.ErrorHandler;
import com.vaadin.spring.annotation.SpringView;
import il.co.rtcohen.rt.dal.dao.User;
import il.co.rtcohen.rt.dal.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;

@SpringView(name = UsersView.VIEW_NAME)
public class UsersView extends AbstractTypeWithNameAndActiveFieldsView<User> {
    static final String VIEW_NAME = "users";

    @Autowired
    private UsersView(ErrorHandler errorHandler,
                      UsersRepository usersRepository) {
        super(errorHandler, usersRepository, User::new, "usersTitle");
    }
}
