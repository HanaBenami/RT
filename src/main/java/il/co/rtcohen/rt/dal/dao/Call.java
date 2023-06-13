package il.co.rtcohen.rt.dal.dao;

import il.co.rtcohen.rt.dal.dao.interfaces.Cloneable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import il.co.rtcohen.rt.app.views.CallsView;
import il.co.rtcohen.rt.dal.dao.interfaces.AbstractType;
import il.co.rtcohen.rt.dal.dao.interfaces.BindRepository;
import il.co.rtcohen.rt.dal.repositories.CallRepository;
import il.co.rtcohen.rt.utils.Date;

public class Call extends AbstractType implements BindRepository<Call>, Cloneable<Call> {
    private static final Logger logger = LoggerFactory.getLogger(CallsView.class);
    private static boolean inTheMiddleOfBatchScheduleUpdate = false;

    private Customer customer;
    private Site site;
    private Vehicle vehicle;
    private CallType callType;
    private String description = "";
    private String notes = "";
    private Date startDate, planningDate, currentScheduledDate, previousScheduledDate, endDate;
    private int currentScheduledOrder, previousScheduledOrder;
    private Driver currentDriver, previousDriver;
    private boolean meeting, isDone, isHere, isDeleted;
    private User openedByUser;
    private GarageStatus garageStatus;

    public Call() {
        super(0);
        this.setStartDate(new Date(LocalDate.now()));
    }

    public Call(
            Integer id,
            Customer customer,
            Site site,
            Vehicle vehicle,
            CallType callType,
            String description,
            String notes,
            Date startDate,
            Date planningDate,
            Date currentScheduledDate,
            Date endDate,
            int currentScheduledOrder,
            Driver currentDriver,
            boolean isMeeting,
            boolean isDone,
            boolean isHere,
            boolean isDeleted,
            User openedByUser,
            GarageStatus garageStatus
    ) {
        super(id);
        this.customer = customer;
        this.site = site;
        this.vehicle = vehicle;
        this.callType = callType;
        this.description = description;
        this.notes = notes;
        this.startDate = startDate;
        this.planningDate = planningDate;
        this.currentScheduledDate = currentScheduledDate;
        this.previousScheduledDate = currentScheduledDate;
        this.endDate = endDate;
        this.currentScheduledOrder = currentScheduledOrder;
        this.previousScheduledOrder = currentScheduledOrder;
        this.currentDriver = currentDriver;
        this.previousDriver = currentDriver;
        this.meeting = isMeeting;
        this.isDone = isDone;
        this.isHere = isHere;
        this.isDeleted = isDeleted;
        this.openedByUser = openedByUser;
        this.garageStatus = garageStatus;
    }

    public Call(Call other) {
        super(other);
        this.update(other);
    }

    public void update(Call other) {
        this.customer = other.customer;
        this.site = other.site;
        this.vehicle = other.vehicle;
        this.callType = other.callType;
        this.description = other.description;
        this.notes = other.notes;
        this.startDate = other.startDate;
        this.planningDate = other.planningDate;
        this.currentScheduledDate = other.currentScheduledDate;
        this.previousScheduledDate = other.previousScheduledDate;
        this.endDate = other.endDate;
        this.currentScheduledOrder = other.currentScheduledOrder;
        this.previousScheduledOrder = other.previousScheduledOrder;
        this.currentDriver = other.currentDriver;
        this.previousDriver = other.previousDriver;
        this.meeting = other.meeting;
        this.isDone = other.isDone;
        this.isHere = other.isHere;
        this.isDeleted = other.isDeleted;
        this.openedByUser = other.openedByUser;
        this.garageStatus = other.garageStatus;
    }

    @Override
    public Call cloneObject() {
        return new Call(this);
    }

    public String getObjectName() {
        return "call";
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        if (null == customer || (null != this.getSite() && !this.getSite().getCustomer().equals(customer))) {
            this.setSite(null);
        }
        this.customer = customer;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        if (null == site || (null != this.getVehicle() && !this.getVehicle().getSite().equals(site))) {
            this.setVehicle(null);
        }
        this.site = site;
        if (null != this.site) {
            this.setCustomer(site.getCustomer());
        }
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
        if (null != this.vehicle) {
            this.setSite(vehicle.getSite());
        }
    }

    public CallType getCallType() {
        return callType;
    }

    public void setCallType(CallType callType) {
        this.callType = callType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getPlanningDate() {
        return planningDate;
    }

    public void setPlanningDate(Date planningDate) {
        this.planningDate = planningDate;
    }

    public Date getCurrentScheduledDate() {
        return currentScheduledDate;
    }

    public void setCurrentScheduledDate(Date currentScheduledDate) {
        this.currentScheduledDate = currentScheduledDate;
    }

    public Date getPreviousScheduledDate() {
        return previousScheduledDate;
    }

    public void setPreviousScheduledDate(Date previousScheduledDate) {
        this.previousScheduledDate = previousScheduledDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
        this.isDone = (null != this.endDate) && !this.endDate.equals(Date.nullDate());
    }

    public int getCurrentScheduledOrder() {
        return currentScheduledOrder;
    }

    public void setCurrentScheduledOrder(int currentScheduledOrder) {
        this.currentScheduledOrder = currentScheduledOrder;
    }

    public int getPreviousScheduledOrder() {
        return previousScheduledOrder;
    }

    public void setPreviousScheduledOrder(int previousScheduledOrder) {
        this.previousScheduledOrder = previousScheduledOrder;
    }

    public Driver getCurrentDriver() {
        return currentDriver;
    }

    public void setCurrentDriver(Driver currentDriver) {
        this.currentDriver = currentDriver;
    }

    public Driver getPreviousDriver() {
        return previousDriver;
    }

    public void setPreviousDriver(Driver previousDriver) {
        this.previousDriver = previousDriver;
    }

    public boolean isMeeting() {
        return meeting;
    }

    public void setMeeting(boolean meeting) {
        this.meeting = meeting;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        this.isDone = done;
    }

    public boolean isHere() {
        return isHere;
    }

    public void setHere(boolean isHere) {
        if (isHere && !this.isHere) {
            this.setCurrentDriver(null);
            this.setCurrentScheduledDate(null);
        }
        this.isHere = isHere;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public User getOpenedByUser() {
        return openedByUser;
    }

    public void setOpenedByUser(User openedByUser) {
        this.openedByUser = openedByUser;
    }

    public GarageStatus getGarageStatus() {
        return garageStatus;
    }

    public void setGarageStatus(GarageStatus garageStatus) {
        this.garageStatus = garageStatus;
    }

    // TODO: Check carefully
    @Override
    public void postSave() {
        CallRepository callRepository = (CallRepository) this.getBindRepository();
        String loggerPrefix = "Executing postSave for call #" + this.getId() + ": ";
        logger.info(loggerPrefix + "Driver change: " + this.getPreviousDriver() + " -> " + this.getCurrentDriver());
        logger.info(loggerPrefix + "Schedule date: " + this.getPreviousScheduledDate() + " -> " + this.getCurrentScheduledDate());
        logger.info(loggerPrefix + "Schedule order: " + this.getPreviousScheduledOrder() + " -> " + this.getCurrentScheduledOrder());

        if ((this.currentScheduledDate == this.previousScheduledDate)
                && (this.currentDriver == this.previousDriver)
                && (this.currentScheduledOrder == this.previousScheduledOrder)) {
            logger.info(loggerPrefix + "No schedule change detected");
            return;
        }

        if (!inTheMiddleOfBatchScheduleUpdate) {
            inTheMiddleOfBatchScheduleUpdate = true;

            fixScheduledOrder();

            // Remove from schedule according to previous parameters - Decrease the schedule order of all the calls that was scheduled afterwards
            if (0 != this.getPreviousScheduledOrder()) {
                List<Call> list = callRepository.getItems(this.getPreviousScheduledDate(), this.getPreviousDriver());
                list.removeIf(call -> call.equals(this));
                logger.info(
                        loggerPrefix + "Going to update all the calls w/ the previous driver and date, where" +
                                " currentScheduledDate=" + this.getPreviousScheduledDate()
                                + " and currentDriver=" + this.getPreviousDriver()
                                + " (" +list.size() + " calls)"
                );
                for (Call otherCall : list) {
                    if (this.getPreviousScheduledOrder() < otherCall.getCurrentScheduledOrder()) {
                        logger.info(
                                loggerPrefix + "Reducing the schedule order of call #" + otherCall.getId()
                                + " from " + otherCall.getCurrentScheduledOrder()
                                + " to " + (otherCall.getCurrentScheduledOrder() - 1)
                        );
                        otherCall.setCurrentScheduledOrder(otherCall.getCurrentScheduledOrder() - 1);
                        callRepository.updateItem(otherCall);
                    }
                }
            }

            // Put back in the schedule - Increase the schedule order
            if (0 != this.getCurrentScheduledOrder()) {
                List<Call> list = callRepository.getItems(this.getCurrentScheduledDate(), this.getCurrentDriver());
                list.removeIf(call -> call.equals(this));
                logger.info(
                        loggerPrefix + "Going to update all the calls w/ the current driver and date, where" +
                                " currentScheduledDate=" + this.getCurrentScheduledDate()
                                + " and currentDriver=" + this.getCurrentDriver()
                                + " (" +list.size() + " calls)"
                );
                for (Call otherCall : list) {
                    if (this.getCurrentScheduledOrder() <= otherCall.getCurrentScheduledOrder()) {
                        logger.info(
                                loggerPrefix + "Increasing the schedule order of call #" + otherCall.getId()
                                        + " from " + otherCall.getCurrentScheduledOrder()
                                        + " to " + (otherCall.getCurrentScheduledOrder() + 1)
                        );
                        otherCall.setCurrentScheduledOrder(otherCall.getCurrentScheduledOrder() + 1);
                        callRepository.updateItem(otherCall);
                    }
                }
            }

            inTheMiddleOfBatchScheduleUpdate = false;
        } else {
            logger.info(loggerPrefix + "inTheMiddleOfBatchScheduleUpdate=true -> Not going to update other calls");
        }

        // Fixing the new scheduled order again in order to avoid "holes" in the queue
        fixScheduledOrder();

        this.setPreviousScheduledDate(this.getCurrentScheduledDate());
        this.setPreviousDriver(this.getCurrentDriver());
        this.setPreviousScheduledOrder(this.getCurrentScheduledOrder());
        callRepository.updateItem(this);
        logger.info(loggerPrefix + "Done");
    }

    private void fixScheduledOrder() {
        String loggerPrefix = "Executing fixScheduledOrder for call #" + this.getId() + ": ";
        CallRepository callRepository = (CallRepository) this.getBindRepository();
        if (null == this.getCurrentDriver() || null == this.getCurrentScheduledDate()) {
            logger.info(loggerPrefix + "No driver / no date -> Removing its schedule order");
            this.setCurrentScheduledOrder(0);
        } else {
            List<Call> list = callRepository.getItems(this.getCurrentScheduledDate(), this.getCurrentDriver());
            list.removeIf(call -> call.equals(this));
            Optional<Call> previouslyLastCallInTheSameSchedule = list.stream().max(Comparator.comparing(Call::getCurrentScheduledOrder));
            if (previouslyLastCallInTheSameSchedule.isPresent()) {
                int previouslyLastCallInTheSameScheduleOrder = previouslyLastCallInTheSameSchedule.get().getCurrentScheduledOrder();
                logger.info(loggerPrefix + "previouslyLastCallInTheSameScheduleOrder=" + previouslyLastCallInTheSameScheduleOrder);
                if (previouslyLastCallInTheSameScheduleOrder + 1 < this.getCurrentScheduledOrder()
                        || 0 == this.getCurrentScheduledOrder()) {
                    logger.info(
                            loggerPrefix
                                    + "previouslyLastCallInTheSameScheduleOrder=" + previouslyLastCallInTheSameScheduleOrder
                                    + ", but currentScheduledOrder=" + this.getCurrentScheduledOrder()
                                    + " -> Reducing it to " + (previouslyLastCallInTheSameScheduleOrder + 1)
                    );
                    this.setCurrentScheduledOrder(previouslyLastCallInTheSameScheduleOrder + 1);
                }
            } else {
                logger.info(
                        loggerPrefix
                                + "previouslyLastCallInTheSameScheduleOrder=null (no other call w/ this driver and date)"
                                + " -> Reducing currentScheduledOrder to 1"
                );
                this.setCurrentScheduledOrder(1);
            }
        }
        logger.info(loggerPrefix + "Schedule order: " + this.getPreviousScheduledOrder() + " -> " + this.getCurrentScheduledOrder());
    }
}