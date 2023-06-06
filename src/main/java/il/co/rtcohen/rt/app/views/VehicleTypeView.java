package il.co.rtcohen.rt.app.views;

import com.vaadin.server.ErrorHandler;
import com.vaadin.spring.annotation.SpringView;
import il.co.rtcohen.rt.dal.dao.VehicleType;
import il.co.rtcohen.rt.dal.repositories.VehicleTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;

@SpringView(name = VehicleTypeView.VIEW_NAME)
public class VehicleTypeView extends AbstractTypeWithNameAndActiveFieldsView<VehicleType> {
    static final String VIEW_NAME = "vehicleType";

    @Autowired
    private VehicleTypeView(ErrorHandler errorHandler,
                            VehicleTypeRepository vehicleTypeRepository) {
        super(errorHandler, vehicleTypeRepository, VehicleType::new, "cartypeTitle");
    }
}
