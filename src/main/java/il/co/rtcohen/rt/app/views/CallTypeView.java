package il.co.rtcohen.rt.app.views;

import com.vaadin.server.ErrorHandler;
import com.vaadin.spring.annotation.SpringView;
import il.co.rtcohen.rt.dal.dao.CallType;
import il.co.rtcohen.rt.dal.repositories.CallTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;

@SpringView(name = CallTypeView.VIEW_NAME)
public class CallTypeView extends AbstractTypeWithNameAndActiveFieldsView<CallType> {
    static final String VIEW_NAME = "callType";

    @Autowired
    private CallTypeView(ErrorHandler errorHandler,
                         CallTypeRepository callTypeRepository) {
        super(errorHandler, callTypeRepository, CallType::new, "calltypeTitle");
    }
}
