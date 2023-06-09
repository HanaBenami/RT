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
        if ((call.getCurrentScheduledDate()!=call.getPreviousScheduledDate())
                || (call.getCurrentScheduledOrder()!=call.getPreviousScheduledOrder())
                || (call.getCurrentDriver().getId()!=call.getPreviousDriver().getId()) )
                updateCallPlan(call);
    }

    private void updateCallPlan(Call call) {
        callRepository.resetOrderQuery(call);

        //fix newOrder in case of null values in other fields or too big new value in newOrder
        if ((call.getCurrentDriver() == null)
                || (call.getCurrentScheduledDate() == null)
                || (call.getCurrentScheduledOrder() == 0) || (call.getCurrentScheduledOrder() > callRepository.newOrder(call)) )
            call.setCurrentScheduledOrder(callRepository.newOrder(call));

        // if there is no change and date and driver and there are valid values
        if ((call.getPreviousDriver().equals(call.getCurrentDriver()))
                && ((call.getPreviousScheduledDate().toString()).equals(call.getCurrentScheduledDate().toString()))
                && (call.getCurrentDriver() != null)
                && (call.getCurrentScheduledDate() != null))
            updateCallPlanNoChange(call);

            // if there is change and date and driver
        else
            updateCallPlanChange(call);

        // update the call with its new values
        callRepository.updateOrderQuery(call);
        call.setPreviousScheduledOrder(call.getCurrentScheduledOrder());
        call.setPreviousDriver(call.getCurrentDriver());
        call.setPreviousScheduledDate(call.getCurrentScheduledDate());
    }

    private void updateCallPlanNoChange(Call call) {

        //validate new order value not too big
        if (call.getCurrentScheduledOrder() > callRepository.newOrder(call))
            call.setCurrentScheduledOrder(callRepository.newOrder(call) - 1);

        // if the call had no order value before the change
        // fix others call with its new driver and date
        if ((call.getPreviousScheduledOrder() == 0) && (call.getCurrentScheduledOrder() != 0)) {
            callRepository.updateQuery("+1",call.getCurrentDriver().getId(),call.getCurrentScheduledDate().getLocalDate()
                    ,">=",call.getCurrentScheduledOrder());
        }

        // if the call had order value before
        else {
            // if order value is smaller than before or from next valid value
            // fix order value in other calls with the same date and driver
            if (call.getPreviousScheduledOrder() > call.getCurrentScheduledOrder()) {
                callRepository.updateQuery("+1",call.getCurrentDriver().getId(),call.getCurrentScheduledDate().getLocalDate()
                        ,">=",call.getCurrentScheduledOrder(),"<",call.getPreviousScheduledOrder());
            }

            //if order value is bigger than before
            // fix order value in other calls with the same date and driver
            if (call.getPreviousScheduledOrder() < call.getCurrentScheduledOrder()) {
                callRepository.updateQuery("-1",call.getCurrentDriver().getId(),call.getCurrentScheduledDate().getLocalDate()
                        ,"<=",call.getCurrentScheduledOrder(),">",call.getPreviousScheduledOrder());
            }
        }

    }

    private void updateCallPlanChange(Call call) {

        // if valid driver and date
        // fix order value in other calls with the same date and driver
        // (according to new values)
        if (null != call.getCurrentDriver() && null != call.getCurrentScheduledDate()) {
                    call.setCurrentScheduledOrder(callRepository.newOrder(call));
                    callRepository.updateQuery("+1", call.getCurrentDriver().getId(), call.getCurrentScheduledDate().getLocalDate(),
                            ">=", call.getCurrentScheduledOrder());
        }

        // if the call had previous order value with valid driver and date
        // fix order value in other calls with the same date and driver
        // (according to previous values)
        if ((call.getPreviousScheduledOrder() != 0) && (call.getPreviousDriver() != null)
                && (null != call.getPreviousScheduledDate())) {
                    callRepository.updateQuery("-1", call.getPreviousDriver().getId(), call.getPreviousScheduledDate().getLocalDate()
                            , ">", call.getPreviousScheduledOrder());
        }

    }

}
