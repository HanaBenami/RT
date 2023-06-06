package il.co.rtcohen.rt.app.views;

import com.vaadin.server.ErrorHandler;
import com.vaadin.spring.annotation.SpringView;
import il.co.rtcohen.rt.dal.dao.Driver;
import il.co.rtcohen.rt.dal.repositories.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;

@SpringView(name = DriversView.VIEW_NAME)
public class DriversView extends AbstractTypeWithNameAndActiveFieldsView<Driver> {
    static final String VIEW_NAME = "drivers";

    @Autowired
    private DriversView(ErrorHandler errorHandler,
                        DriverRepository driverRepository) {
        super(errorHandler, driverRepository, Driver::new, "driverTitle");
    }
}
