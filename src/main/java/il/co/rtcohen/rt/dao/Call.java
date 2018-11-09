package il.co.rtcohen.rt.dao;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Call {

    final static public DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    final static public String nullDateString = "1901-01-01";
    final static public LocalDate nullDate = LocalDate.parse(nullDateString, dateFormatter);
    private int id;
    private int customerId;
    private int siteId;
    private String description;
    private int carTypeId;
    private int callTypeId;
    private String notes;
    private String startDate, date1, date2, endDate, preDate2;
    private boolean meeting, done, here;
    private int driverID, order, preDriverId, preOrder;

    public Call() {
        this(0,0,0,"",0,0,"",nullDateString,nullDateString,nullDateString,nullDateString,false,false,false,0,0);
    }

    public Call(int id, int customerId, int siteId, String description, int carTypeId, int callTypeId, String notes, String startDate, String date1,
                String date2, String endDate, boolean meeting, boolean done, boolean here, int driverID, int order) {
        this.id = id;
        this.customerId = customerId;
        this.siteId = siteId;
        this.description = description;
        this.carTypeId = carTypeId;
        this.callTypeId = callTypeId;
        this.notes = notes;
        this.startDate = startDate;
        this.date1 = date1;
        this.date2 = date2;
        this.endDate = endDate;
        this.meeting = meeting;
        this.done = done;
        this.here = here;
        this.driverID = driverID;
        this.order = order;
        this.preDate2 = this.date2;
        this.preDriverId = this.driverID;
        this.preOrder = this.order;
    }

    public void setCustomerId(Integer newCustomerId) {
        if (newCustomerId != this.customerId) {
            this.customerId = newCustomerId;
            this.siteId = 0;
        }
    }

    private String dateCheck(LocalDate date) {
        if (date == null)
            return nullDateString;
        else
            return date.format(dateFormatter);
    }

    public void setDate2(LocalDate date) {
        this.date2 = dateCheck(date);
    }

    public void setPreDate2(LocalDate date) {
        this.preDate2 = dateCheck(date);
    }

    public void setDate1(LocalDate date) {
        this.date1 = dateCheck(date);
    }

    public void setStartDate(LocalDate date) {
        this.startDate = dateCheck(date);
    }

    public void setEndDate(LocalDate date) {
        this.endDate = dateCheck(date);
        this.done = !nullDateString.equals(this.endDate);
    }

    public void setDescription(String newDescr) {
        this.description = newDescr;
    }

    public void setSiteId(int newSite) {
        this.siteId = newSite;
    }

    public void setDriverID(int newDriver) {
        this.driverID = newDriver;
    }

    public void setPreDriverId(int driver) {
        this.preDriverId = driver;
    }

    public void setCarTypeId(int newCarType) {
        this.carTypeId = newCarType;
    }

    public void setCallTypeId(int newCallType) {
        this.callTypeId = newCallType;
    }

    public void setHere(boolean here) {
        this.here = here;
    }

    public void setMeeting(boolean meeting) {
        this.meeting = meeting;
    }

    public void setOrder(int newOrder) {
        this.order = newOrder;
    }

    public void setPreOrder(int order) {
        this.preOrder = order;
    }

    public void setNotes(String newNotes) {
        this.notes = newNotes;
    }

    public int getId() {
        return id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getSiteId() {
        return siteId;
    }

    public String getDescription() {
        return description;
    }

    public int getCarTypeId() {
        return carTypeId;
    }

    public int getCallTypeId() {
        return callTypeId;
    }

    public String getNotes() {
        return notes;
    }

    public LocalDate getStartDate() {
        return LocalDate.parse(startDate, dateFormatter);
    }

    public LocalDate getDate1() {
        return LocalDate.parse(date1, dateFormatter);
    }

    public LocalDate getDate2() {
        return LocalDate.parse(date2, dateFormatter);
    }

    public LocalDate getPreDate2() {
        return LocalDate.parse(preDate2, dateFormatter);
    }

    public LocalDate getEndDate() {
        return LocalDate.parse(endDate, dateFormatter);
    }

    public boolean isMeeting() {
        return meeting;
    }

    public boolean isDone() {
        return done;
    }

    public boolean isHere() {
        return here;
    }

    public int getDriverId() {
        return driverID;
    }

    public int getPreDriverId() {
        return preDriverId;
    }

    public int getOrder() {
        return order;
    }

    public int getPreOrder() {
        return preOrder;
    }

}