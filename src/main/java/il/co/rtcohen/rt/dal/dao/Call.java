package il.co.rtcohen.rt.dal.dao;

import il.co.rtcohen.rt.dal.dao.interfaces.AbstractType;
import il.co.rtcohen.rt.dal.dao.interfaces.BindRepository;
import il.co.rtcohen.rt.utils.Date;

import java.time.LocalDate;

public class Call extends AbstractType implements BindRepository<Call> {
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
            User openedByUser
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
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
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
        this.isDone = (null != this.endDate);
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

    @Override
    public void postSave() {}; // TODO instead of call service

}