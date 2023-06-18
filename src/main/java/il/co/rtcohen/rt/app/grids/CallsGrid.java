package il.co.rtcohen.rt.app.grids;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.DetailsGenerator;
import il.co.rtcohen.rt.app.uiComponents.columns.*;
import il.co.rtcohen.rt.app.uiComponents.fields.CustomButton;
import il.co.rtcohen.rt.app.uiComponents.fields.CustomComboBox;
import il.co.rtcohen.rt.app.uiComponents.fields.CustomTextArea;
import org.vaadin.addons.filteringgrid.FilterGrid;
import java.util.List;
import java.util.function.Predicate;
import java.time.LocalDate;
import static java.time.temporal.ChronoUnit.DAYS;

import il.co.rtcohen.rt.app.ui.UIPaths;
import il.co.rtcohen.rt.app.uiComponents.*;
import il.co.rtcohen.rt.dal.dao.*;
import il.co.rtcohen.rt.dal.repositories.*;
import il.co.rtcohen.rt.utils.Date;
import il.co.rtcohen.rt.utils.NullPointerExceptionWrapper;


public class CallsGrid extends AbstractTypeFilterGrid<Call> {
    private final CallRepository callRepository;
    private final CustomerRepository customerRepository;
    private final CustomerTypeRepository customerTypeRepository;
    private final SiteRepository siteRepository;
    private final AreasRepository areasRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final UsersRepository usersRepository;
    private final DriverRepository driverRepository;
    private final CallTypeRepository callTypeRepository;
    private final GarageStatusRepository garageStatusRepository;
    private CallsFilterOptions selectedCallsFilterOption;
    private Customer selectedCustomer;
    private Site selectedSite;
    private Vehicle selectedVehicle;
    private Driver nextScheduleDriver;
    private Date nextScheduleDate;
    private String daysOpenColumnId = "daysOpenColumn";
    List<Call> callsInGrid = null;

    public CallsGrid(
            CallsFilterOptions selectedCallsFilterOption,
            Customer selectedCustomer,
            Site selectedSite,
            Vehicle selectedVehicle,
            Driver selectedDriver,
            CallRepository callRepository,
            CustomerRepository customerRepository,
            CustomerTypeRepository customerTypeRepository,
            SiteRepository siteRepository,
            AreasRepository areasRepository,
            VehicleRepository vehicleRepository,
            VehicleTypeRepository vehicleTypeRepository,
            UsersRepository usersRepository,
            DriverRepository driverRepository,
            CallTypeRepository callTypeRepository,
            GarageStatusRepository garageStatusRepository
    ) {
        super(
                callRepository,
                null,
                "calls",
                call -> (
                    (null != selectedVehicle && !selectedVehicle.equals(call.getVehicle()))
                    || (null != selectedSite && !selectedSite.equals(call.getSite()))
                    || (null != selectedCustomer && !selectedCustomer.equals(call.getCustomer())
                    || (null != selectedDriver && !selectedDriver.equals(call.getCurrentDriver())))
                )
        );
        this.selectedCallsFilterOption = selectedCallsFilterOption;
        this.selectedCustomer = selectedCustomer;
        this.selectedSite = selectedSite;
        this.selectedVehicle = selectedVehicle;
        this.callRepository = callRepository;
        this.customerRepository = customerRepository;
        this.customerTypeRepository = customerTypeRepository;
        this.siteRepository = siteRepository;
        this.areasRepository = areasRepository;
        this.vehicleRepository = vehicleRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
        this.usersRepository = usersRepository;
        this.driverRepository = driverRepository;
        this.callTypeRepository = callTypeRepository;
        this.garageStatusRepository = garageStatusRepository;
        this.setEmptyLinesAllow(false);
    }

    public enum CallsFilterOptions {
        ALL_CALLS("allCalls"),
        ONLY_OPEN_CALLS("openCalls"),
        ONLY_OPEN_CALLS_PENDING_GARAGE("openCallsPendingGarage"),
        ONLY_OPEN_CALLS_PENDING_WAREHOUSE("openCallsPendingWarehouse"),
        CALLS_PLANNED_FOR_YESTERDAY("yesterday"),
        CALLS_PLANNED_FOR_TODAY("today"),
        CALLS_PLANNED_FOR_TOMORROW("tomorrow"),
        CALLS_PLANNED_FOR_THE_DAY_AFTER_TOMORROW("plus2days"),
        RECENTLY_CLOSED_CALLS("recentCloseCalls"),
        ALL_CLOSED_CALLS("closeCalls"),
        RECENTLY_DELETED_CALLS("recentDeleteCalls"),
        ALL_DELETED_CALLS("deleteCalls");

        private final String titleKey;

        CallsFilterOptions(String titleKey) {
            this.titleKey = titleKey;
        }

        public String getTitleKey() {
            return titleKey;
        }
    }

    @Override
    protected List<Call> getItems() {
        callsInGrid = null;
        if (null == selectedCallsFilterOption) {
            callsInGrid = super.getItems();
        } else {
            switch (selectedCallsFilterOption) {
                case ALL_CALLS:
                    callsInGrid = callRepository.getItems(null, null, null, null, null, null, null);
                    break;
                case ONLY_OPEN_CALLS:
                    callsInGrid = callRepository.getItems(false, false, null, null, null, null, null);
                    break;
                case ONLY_OPEN_CALLS_PENDING_GARAGE:
                    callsInGrid = callRepository.getItems(false, false, null, null, null, null, null);
                    callsInGrid.removeIf(call -> (null == call.getGarageStatus() || !call.getGarageStatus().isPendingGarage()));
                    break;
                case ONLY_OPEN_CALLS_PENDING_WAREHOUSE:
                    callsInGrid = callRepository.getItems(false, false, null, null, null, null, null);
                    callsInGrid.removeIf(call -> (null == call.getWarehouseStatus() || !call.getWarehouseStatus().isPendingWarehouse()));
                    break;
                case RECENTLY_CLOSED_CALLS:
                    callsInGrid = callRepository.getItems(true, false, null, new Date(LocalDate.now().minusMonths(6)), null, null, null);
                    break;
                case ALL_CLOSED_CALLS:
                    callsInGrid = callRepository.getItems(true, false, null, null, null, null, null);
                    break;
                case RECENTLY_DELETED_CALLS:
                    callsInGrid = callRepository.getItems(null, true, null, new Date(LocalDate.now().minusMonths(6)), null, null, null);
                    break;
                case ALL_DELETED_CALLS:
                    callsInGrid = callRepository.getItems(null, true, null, null, null, null, null);
                    break;
                case CALLS_PLANNED_FOR_YESTERDAY:
                    callsInGrid = callRepository.getScheduledCalls(new Date(LocalDate.now().minusDays(1)));
                    break;
                case CALLS_PLANNED_FOR_TODAY:
                    callsInGrid = callRepository.getScheduledCalls(new Date(LocalDate.now()));
                    break;
                case CALLS_PLANNED_FOR_TOMORROW:
                    callsInGrid = callRepository.getScheduledCalls(new Date(LocalDate.now().plusDays(1)));
                    break;
                case CALLS_PLANNED_FOR_THE_DAY_AFTER_TOMORROW:
                    callsInGrid = callRepository.getScheduledCalls(new Date(LocalDate.now().plusDays(2)));
                    break;
            }
        }
        return callsInGrid;
    }

    public CallsFilterOptions getSelectedCallsFilterOption() {
        return selectedCallsFilterOption;
    }

    public void setSelectedCallsFilterOption(CallsFilterOptions selectedCallsFilterOption) {
        this.selectedCallsFilterOption = selectedCallsFilterOption;
    }

    public Customer getSelectedCustomer() {
        return selectedCustomer;
    }

    public void setSelectedCustomer(Customer selectedCustomer) {
        this.selectedCustomer = selectedCustomer;
    }

    public Site getSelectedSite() {
        return selectedSite;
    }

    public void setSelectedSite(Site selectedSite) {
        this.selectedSite = selectedSite;
    }

    public Vehicle getSelectedVehicle() {
        return selectedVehicle;
    }

    public void setSelectedVehicle(Vehicle selectedVehicle) {
        this.selectedVehicle = selectedVehicle;
    }

    public Driver getNextScheduleDriver() {
        return nextScheduleDriver;
    }

    public void setNextScheduleDriver(Driver nextScheduleDriver) {
        this.nextScheduleDriver = nextScheduleDriver;
    }

    public Date getNextScheduleDate() {
        return nextScheduleDate;
    }

    public void setNextScheduleDate(Date nextScheduleDate) {
        this.nextScheduleDate = nextScheduleDate;
    }

    @Override
    protected void setTitle() {
        super.setTitleKey(this.selectedCallsFilterOption.getTitleKey());
        super.setTitle();
        if (null != this.selectedVehicle) {
            super.setTitleKey("callsOfVehicle");
            super.setTitle();
            this.title += " " + selectedVehicle.getName();
        } else if (null != this.selectedSite) {
            super.setTitleKey("callsOfSite");
            super.setTitle();
            this.title += " " + selectedSite.getName();
        } else if (null != this.selectedCustomer) {
            super.setTitleKey("callsOfCustomer");
            super.setTitle();
            this.title += " " + selectedCustomer.getName();
        }
    }

    @Override
    public void initGrid(boolean fullSize, int numOfEmptyLines) {
        super.initGrid(fullSize, numOfEmptyLines);
        this.setDetailsGenerator((DetailsGenerator<Call>) this::getCallDetails);
        this.setStyleGenerator((StyleGenerator<Call>) StyleSettings::callStyle);
    }

    private VerticalLayout getCallDetails(Call call) {
        TextArea description = new CustomTextArea("description", call.getDescription(), "100%","55");
        description.addValueChangeListener(valueChangeEvent -> {
            call.setDescription(description.getValue());
        });
        TextArea notes =  new CustomTextArea("notes", call.getNotes(), "100%","55");
        notes.addValueChangeListener(valueChangeEvent -> {
            call.setNotes(notes.getValue());
        });
        VerticalLayout layout = new VerticalLayout(description,notes);
        layout.setComponentAlignment(notes, Alignment.MIDDLE_CENTER);
        layout.setSpacing(false);
        layout.setMargin(false);
        return layout;
    }

    protected void addColumns() {
        addSetScheduledOrderColumn();
        addEditColumn();
        addNotesColumn();
        addDescriptionColumn();
        addIsDeletedColumn();
        addIsDoneColumn();
        addEndDateColumn();
        addDriverColumn();
        addScheduledOrderColumn();
        addIsMeetingColumn();
        addCurrentScheduledDateColumn();
        addPlanningDateColumn();
        addDaysOpenColumn();
        addStartDateColumn();
        addIsHereColumn();
        addGarageStatusColumn();
        addVehicleTypeColumn();
        addCallTypeColumn();
        addAddressColumn();
        addAreaColumn();
        addSiteColumn();
        addCustomerColumn();
        addIdColumn();
    }

    private void refreshGridData() {
        for (Call c : callsInGrid) {
            c = callRepository.getItem(c.getId()); // Get the latest version of this call
            this.getDataProvider().refreshItem(c); // Refresh its view in the grid
        }
    }

    private void addSetScheduledOrderColumn() {
        Predicate<Call> isScheduled = call -> (
                null != call.getCurrentScheduledDate()
                && !call.getCurrentScheduledDate().equals(Date.nullDate())
                && null != call.getCurrentDriver()
                && 0 != call.getCurrentScheduledOrder()
        );
        CustomComponentColumn<Call, Component> column = CustomComponentColumn.addToGrid(
                call -> new CustomButton(
                        (isScheduled.test(call) ? VaadinIcons.CLOSE_SMALL : VaadinIcons.CALENDAR_USER),
                        false,
                        clickEvent ->
                        {
                            if (isScheduled.test(call)) {
                                call.setCurrentDriver(null);
                                call.setCurrentScheduledDate(Date.nullDate());
                            } else {
                                if (null != nextScheduleDriver) {
                                    call.setCurrentDriver(nextScheduleDriver);
                                }
                                if (null != nextScheduleDate) {
                                    call.setCurrentScheduledDate(nextScheduleDate);
                                }
                            }
                            callRepository.updateItem(call);
                            this.refreshGridData();
                        }
                ),
                60,
                "setOrderColumn",
                "setOrder",
                this
        );
        column.getColumn().setHidable(true);
        column.getColumn().setHidden(false);
    }

    private void addEditColumn() {
        CustomComponentColumn<Call, Component> column = CustomComponentColumn.addToGrid(
                call -> new CustomButton(
                        VaadinIcons.EDIT,
                        false,
                        UIPaths.EDITCALL.getEditCallPath(call),
                        UIPaths.EDITCALL.getWindowHeight(),
                        UIPaths.EDITCALL.getWindowWidth(),
                        UIPaths.EDITCALL.getWindowName()),
                60,
                "editColumn",
                "edit",
                this
        );
        column.getColumn().setHidable(true);
        column.getColumn().setHidden(false);
    }

    private void addNotesColumn() {
        CustomComponentColumn<Call, Component> column = CustomComponentColumn.addToGrid(
                call -> new CustomButton(
                        (call.getNotes().isEmpty() ? VaadinIcons.COMMENT_O : VaadinIcons.COMMENT),
                        false,
                        clickEvent -> this.setDetailsVisible(call, !this.isDetailsVisible(call))
                ),
                60,
                "notesColumn",
                "notes",
                this
        );
    }

    private void addDescriptionColumn() {
        CustomComponentColumn<Call, Component> column = CustomComponentColumn.addToGrid(
                call -> new CustomButton(
                            (call.getDescription().isEmpty() ? VaadinIcons.COMMENT_O : VaadinIcons.COMMENT),
                            false,
                            clickEvent -> this.setDetailsVisible(call, !this.isDetailsVisible(call))
                ),
                60,
                "descriptionColumn",
                "description",
                 this
        );
    }

    private void addIsDeletedColumn() {
        CustomCheckBoxColumn<Call> column = CustomCheckBoxColumn.addToGrid(
                Call::isDeleted,
                null,
                "deletedColumn",
                "deleted",
                null,
                this
        );
        column.getColumn().setHidable(true);
        column.getColumn().setHidden(true);
    }

    private void addIsDoneColumn() {
        CustomCheckBoxColumn<Call> column = CustomCheckBoxColumn.addToGrid(
                Call::isDone,
                null,
                "doneColumn",
                "done",
                null,
                this
        );
        column.getColumn().setHidable(true);
        column.getColumn().setHidden(false);
    }

    private void addEndDateColumn() {
        CustomDateColumn<Call> column = CustomDateColumn.addToGrid(
                Call::getEndDate,
                Call::setEndDate,
                LocalDate::now,
                "endDateColumn",
                "endDateShort",
                false,
                this
        );
        column.getColumn().setHidable(true);
        column.getColumn().setHidden(false);
    }

    private void addDriverColumn() {
        CustomComboBoxColumn<Driver, Call> column = CustomComboBoxColumn.addToGrid(
                CustomComboBox.getComboBox(driverRepository),
                CustomComboBox.getComboBox(driverRepository),
                Call::getCurrentDriver,
                (call, driver) -> {
                    call.setCurrentDriver(driver);
                    this.refreshGridData();
                },
                false, 100,
                "driverColumn",
                "driver",
                this
        );
        column.getColumn().setHidable(true);
        column.getColumn().setHidden(false);
    }

    private void addScheduledOrderColumn() {
        CustomIntegerColumn<Call> column = CustomIntegerColumn.addToGrid(
                Call::getCurrentScheduledOrder,
                (call, order) -> {
                    call.setCurrentScheduledOrder(order);
                    this.refreshGridData();
                },
                null, null, 60,
                "orderColumn",
                "order",
                true,
                false,
                false,
                this
        );
        column.getColumn().setHidable(true);
        column.getColumn().setHidden(false);
    }

    private void addIsMeetingColumn() {
        CustomCheckBoxColumn<Call> column = CustomCheckBoxColumn.addToGrid(
                Call::isMeeting,
                Call::setMeeting,
                "meetingColumn",
                "meetingShort",
                null,
                this
        );
        column.getColumn().setHidable(true);
        column.getColumn().setHidden(true);
    }

    private void addCurrentScheduledDateColumn() {
        CustomDateColumn<Call> column = CustomDateColumn.addToGrid(
                Call::getCurrentScheduledDate,
                (call, date) -> {
                    call.setCurrentScheduledDate(date);
                    this.refreshGridData();
                },
                LocalDate::now,
                "date2Column",
                "date2short",
                true,
                this
        );
        column.getColumn().setHidable(true);
        column.getColumn().setHidden(false);
    }

    private void addDaysOpenColumn() {
        CustomIntegerColumn<Call> column = CustomIntegerColumn.addToGrid(
                call -> (call.isDone() ? 0 : (int)DAYS.between(call.getStartDate().getLocalDate(), LocalDate.now())),
                null,
                null, null, 80,
                daysOpenColumnId,
                "days",
                false,
                true,
                true,
                this
        );
        column.getColumn().setHidable(true);
        column.getColumn().setHidden(true);
    }

    private void addPlanningDateColumn() {
        CustomDateColumn<Call> column = CustomDateColumn.addToGrid(
                Call::getPlanningDate,
                Call::setPlanningDate,
                LocalDate::now,
                "date1Column",
                "date1short",
                false,
                this
        );
        column.getColumn().setHidable(true);
        column.getColumn().setHidden(true);
    }

    private void addStartDateColumn() {
        CustomDateColumn<Call> column = CustomDateColumn.addToGrid(
                Call::getStartDate,
                Call::setStartDate,
                null, "startDateColumn",
                "startDateShort",
                false,
                this
        );
        column.getColumn().setHidable(true);
        column.getColumn().setHidden(false);
    }

    private void addIsHereColumn() {
        CustomCheckBoxColumn<Call> column = CustomCheckBoxColumn.addToGrid(
                Call::isHere,
                Call::setHere,
                "hereColumn",
                "here",
                null,
                this
        );
        column.getColumn().setHidable(true);
        column.getColumn().setHidden(false);
    }

    private void addGarageStatusColumn() {
        CustomComboBoxColumn<GarageStatus, Call> column = CustomComboBoxColumn.addToGrid(
                CustomComboBox.getComboBox(garageStatusRepository),
                CustomComboBox.getComboBox(garageStatusRepository),
                Call::getGarageStatus,
                Call::setGarageStatus,
                false, 120,
                "garageStatusColumn",
                "garageStatus",
                this
        );
        column.getColumn().setHidable(true);
        column.getColumn().setHidden(false);
    }

    private void addVehicleTypeColumn() {
        CustomComboBoxColumn<VehicleType, Call> column = CustomComboBoxColumn.addToGrid(
                CustomComboBox.getComboBox(vehicleTypeRepository),
                CustomComboBox.getComboBox(vehicleTypeRepository),
                call -> call.getVehicle().getVehicleType(),
                null,
                false, 120,
                "vehicleTypeColumn",
                "vehicleType",
                this
        );
        column.getColumn().setHidable(true);
        column.getColumn().setHidden(false);
    }

    private void addCallTypeColumn() {
        CustomComboBoxColumn<CallType, Call> column = CustomComboBoxColumn.addToGrid(
                CustomComboBox.getComboBox(callTypeRepository),
                CustomComboBox.getComboBox(callTypeRepository),
                Call::getCallType,
                Call::setCallType,
                false, 120,
                "callTypeColumn",
                "callType",
                this
        );
        column.getColumn().setHidable(true);
        column.getColumn().setHidden(false);
    }

    private void addAddressColumn() {
        CustomTextColumn<Call>  column = CustomTextColumn.addToGrid(
            call -> NullPointerExceptionWrapper.getWrapper(call, c -> c.getSite().getAddress(), ""),
            null,
                false, 180,
            "addressColumn",
            "address",
            false,
            true,
            false,
            this
            );
        column.getColumn().setHidable(true);
        column.getColumn().setHidden(true);
    }

    private void addAreaColumn() {
        CustomComboBoxColumn<Area, Call> column = CustomComboBoxColumn.addToGrid(
                CustomComboBox.getComboBox(areasRepository),
                CustomComboBox.getComboBox(areasRepository),
                call -> call.getSite().getArea(),
                null,
                false, 100,
                "areaColumn",
                "area",
                this
        );
        column.getColumn().setHidable(true);
        column.getColumn().setHidden(true);
    }

    private void addSiteColumn() {
        CustomComboBoxColumn<Site, Call> column = CustomComboBoxColumn.addToGrid(
                CustomComboBox.getComboBox(siteRepository, this.selectedCustomer),
                CustomComboBox.getComboBox(siteRepository, this.selectedCustomer),
                Call::getSite,
                null,
                false, 150,
                "siteColumn",
                "site",
                this
        );
        column.getColumn().setHidable(true);
        column.getColumn().setHidden(false);
    }

    private void addCustomerColumn() {
        CustomComboBoxColumn<Customer, Call> column = CustomComboBoxColumn.addToGrid(
                CustomComboBox.getComboBox(customerRepository),
                CustomComboBox.getComboBox(customerRepository),
                Call::getCustomer,
                null,
                false, 120,
                "customerColumn",
                "customer",
                this
        );
        column.getColumn().setHidable(true);
        column.getColumn().setHidden(false);
    }

    @Override
    protected void sort() {
        if (null != getCustomSortColumnId()) {
            super.sort(getCustomSortColumnId(), SortDirection.ASCENDING);
        } else {
            FilterGrid.Column<Call, String> sortColumn = this.addColumn(call -> {
                String sortKey = ((null == call.getCurrentScheduledDate()) ? Date.nullDate() : call.getCurrentScheduledDate()).toString();
                if (null == call.getCurrentDriver() || call.getCurrentDriver().getId() < 10) {
                    sortKey += "0";
                }
                sortKey += (null == call.getCurrentDriver() ? "0" : String.valueOf(call.getCurrentDriver().getId()));
                if (call.getCurrentScheduledOrder() < 10) {
                    sortKey += "0";
                }
                sortKey += String.valueOf(call.getCurrentScheduledOrder());
                sortKey += call.getStartDate();
                return sortKey;
            });
            sortColumn.setId("sortColumn");
            sortColumn.setHidable(false);
            sortColumn.setHidden(true);
            super.sort(sortColumn, SortDirection.ASCENDING);
        }
    }

    @Override
    protected void addIdColumn() {
        super.addIdColumn();
        this.idColumn.getColumn().setHidden(true);
    }
}
