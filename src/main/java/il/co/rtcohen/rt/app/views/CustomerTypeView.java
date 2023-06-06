package il.co.rtcohen.rt.app.views;

import com.vaadin.server.ErrorHandler;
import com.vaadin.spring.annotation.SpringView;
import il.co.rtcohen.rt.dal.dao.CustomerType;
import il.co.rtcohen.rt.dal.repositories.CustomerTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;

@SpringView(name = CustomerTypeView.VIEW_NAME)
public class CustomerTypeView extends AbstractTypeWithNameAndActiveFieldsView<CustomerType> {

    static final String VIEW_NAME = "customerType";

    @Autowired
    private CustomerTypeView(ErrorHandler errorHandler,
                             CustomerTypeRepository customerTypeRepository) {
        super(errorHandler, customerTypeRepository, CustomerType::new, "custtypeTitle");
    }
}
