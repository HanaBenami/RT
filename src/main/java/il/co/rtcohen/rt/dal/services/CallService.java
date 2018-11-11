package il.co.rtcohen.rt.dal.services;

import il.co.rtcohen.rt.dal.dao.Call;
import il.co.rtcohen.rt.dal.repositories.CallRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CallService {

    private CallRepository callRepository;

    @Autowired
    CallService(CallRepository callRepository) {
        this.callRepository=callRepository;
    }

    public void updateCall(Call call) {
        callRepository.updateCall(call);
        if ((call.getDate2()!=call.getPreDate2()) || (call.getOrder()!=call.getPreOrder())
                || (call.getDriverId()!=call.getPreDriverId()) )
                updateCallPlan(call);
    }

    private void updateCallPlan(Call call) {
        callRepository.resetOrderQuery(call);

        //fix newOrder in case of null values in other fields or too big new value in newOrder
        if ((call.getDriverId() == 0)
                || ((call.getDate2().format(Call.dateFormatter)).equals(Call.nullDateString))
                || (call.getOrder() == 0) || (call.getOrder() > callRepository.newOrder(call)) )
            call.setOrder(callRepository.newOrder(call));

        // if there is no change and date and driver and there are valid values
        if ((call.getPreDriverId() == call.getDriverId())
                && ((call.getPreDate2().format(Call.dateFormatter)).equals(call.getDate2().format(Call.dateFormatter)))
                && (call.getDriverId() != 0)
                && !((call.getDate2().format(Call.dateFormatter)).equals(Call.nullDateString)))
            updateCallPlanNoChange(call);

            // if there is change and date and driver
        else
            updateCallPlanChange(call);

        // update the call with its new values
        callRepository.updateOrderQuery(call);
        call.setPreOrder(call.getOrder());
        call.setPreDriverId(call.getDriverId());
        call.setPreDate2(call.getDate2());
    }

    private void updateCallPlanNoChange(Call call) {

        //validate new order value not too big
        if (call.getOrder() > callRepository.newOrder(call))
            call.setOrder(callRepository.newOrder(call) - 1);

        // if the call had no order value before the change
        // fix others call with its new driver and date
        if ((call.getPreOrder() == 0) && (call.getOrder() != 0)) {
            callRepository.updateQuery("+1",call.getDriverId(),call.getDate2()
                    ,">=",call.getOrder());
        }

        // if the call had order value before
        else {
            // if order value is smaller than before or from next valid value
            // fix order value in other calls with the same date and driver
            if (call.getPreOrder() > call.getOrder()) {
                callRepository.updateQuery("+1",call.getDriverId(),call.getDate2()
                        ,">=",call.getOrder(),"<",call.getPreOrder());
            }

            //if order value is bigger than before
            // fix order value in other calls with the same date and driver
            if (call.getPreOrder() < call.getOrder()) {
                callRepository.updateQuery("-1",call.getDriverId(),call.getDate2()
                        ,"<=",call.getOrder(),">",call.getPreOrder());
            }
        }

    }

    private void updateCallPlanChange(Call call) {

        // if valid driver and date
        // fix order value in other calls with the same date and driver
        // (according to new values)
        if ((call.getDriverId() != 0)
                && !((call.getDate2().format(Call.dateFormatter)).equals(Call.nullDateString))) {
                    call.setOrder(callRepository.newOrder(call));
                    callRepository.updateQuery("+1", call.getDriverId(), call.getDate2(),
                            ">=", call.getOrder());
        }

        // if the call had previous order value with valid driver and date
        // fix order value in other calls with the same date and driver
        // (according to previous values)
        if ((call.getPreOrder() != 0) && (call.getPreDriverId() != 0)
                && !((call.getPreDate2().format(Call.dateFormatter)).equals(Call.nullDateString))) {
                    callRepository.updateQuery("-1", call.getPreDriverId(), call.getPreDate2()
                            , ">", call.getPreOrder());
        }

    }

}
