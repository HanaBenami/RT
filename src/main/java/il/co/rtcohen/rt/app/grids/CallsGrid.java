package il.co.rtcohen.rt.app.grids;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.DetailsGenerator;
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

    private CallsFilterOptions selectedCallsFilterOption;
    private Customer selectedCustomer;
    private Site selectedSite;
    private Vehicle selectedVehicle;
    private Driver nextScheduleDriver;
    private Date nextScheduleDate;

    private String daysOpenColumnId = "daysOpenColumn";

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
            CallTypeRepository callTypeRepository
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
        this.initGrid();
    }

    public enum CallsFilterOptions {
        ALL_CALLS("allCalls"),
        ONLY_OPEN_CALLS("openCalls"),
        RECENTLY_CLOSED_CALLS("recentCloseCalls"),
        ALL_CLOSED_CALLS("closeCalls"),
        RECENTLY_DELETED_CALLS("recentDeleteCalls"),
        ALL_DELETED_CALLS("deleteCalls"),
        CALLS_PLANNED_FOR_YESTERDAY("yesterday"),
        CALLS_PLANNED_FOR_TODAY("today"),
        CALLS_PLANNED_FOR_TOMORROW("tomorrow"),
        CALLS_PLANNED_FOR_THE_DAY_AFTER_TOMORROW("plus2days");

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
        List<Call> list = null;
        if (null == selectedCallsFilterOption) {
            list = super.getItems();
        } else {
            switch (selectedCallsFilterOption) {
                case ALL_CALLS:
                    list = callRepository.getItems(null, false, null);
                    break;
                case ONLY_OPEN_CALLS:
                    list = callRepository.getItems(false, false, null);
                    break;
                case RECENTLY_CLOSED_CALLS:
                    list = callRepository.getItems(true, false, new Date(LocalDate.now().minusMonths(6)));
                    break;
                case ALL_CLOSED_CALLS:
                    list = callRepository.getItems(true, false, null);
                    break;
                case RECENTLY_DELETED_CALLS:
                    list = callRepository.getItems(null, true, new Date(LocalDate.now().minusMonths(6)));
                    break;
                case ALL_DELETED_CALLS:
                    list = callRepository.getItems(null, true, null);
                    break;
                case CALLS_PLANNED_FOR_YESTERDAY:
                    list = callRepository.getItems(new Date(LocalDate.now().minusDays(1)));
                    break;
                case CALLS_PLANNED_FOR_TODAY:
                    list = callRepository.getItems(new Date(LocalDate.now()));
                    break;
                case CALLS_PLANNED_FOR_TOMORROW:
                    list = callRepository.getItems(new Date(LocalDate.now().plusDays(1)));
                    break;
                case CALLS_PLANNED_FOR_THE_DAY_AFTER_TOMORROW:
                    list = callRepository.getItems(new Date(LocalDate.now().plusDays(2)));
                    break;
            }
        }
        return list;
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
        super.setTitle();
        if (null != this.selectedVehicle) {
            super.setTitleKey("callsOfVehicle");
            this.title += " " + selectedVehicle.getName();
        } else if (null != this.selectedSite) {
            super.setTitleKey("callsOfSite");
            this.title += " " + selectedSite.getName();
        } else if (null != this.selectedCustomer) {
            super.setTitleKey("callsOfCustomer");
            this.title += " " + selectedCustomer.getName();
        }
    }

    @Override
    protected void initGrid() {
        super.initGrid();
        this.setDetailsGenerator((DetailsGenerator<Call>) this::getCallDetails);
        this.setStyleGenerator((StyleGenerator<Call>) StyleSettings::callStyle);
    }

    private VerticalLayout getCallDetails(Call call) {
        TextArea description = new CustomTextArea("description","100%","55");
        description.setValue(call.getDescription());
        description.addValueChangeListener(valueChangeEvent -> {
            call.setDescription(description.getValue());
        });
        TextArea notes =  new CustomTextArea("notes","100%","55");
        notes.setValue(call.getNotes());
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
        addSetOrderColumn();
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
        addVehicleTypeColumn();
        addCallTypeColumn();
        addAddressColumn();
        addAreaColumn();
        addSiteColumn();
        addCustomerColumn();
        addIdColumn();
    }

    private void addSetOrderColumn() {
        Predicate<Call> isScheduled = call -> (null != call.getCurrentScheduledDate() && null != call.getCurrentDriver());
        Column<Call, Component> column = this.addComponentColumn(
                call -> new CustomButton(
                        (isScheduled.test(call) ? VaadinIcons.CLOSE_SMALL : VaadinIcons.CALENDAR_USER),
                        false,
                        clickEvent ->
                        {
                            if (isScheduled.test(call)) {
                                call.setCurrentDriver(null);
                                call.setCurrentScheduledDate(null);
                            } else {
                                if (null != nextScheduleDriver) {
                                    call.setCurrentDriver(nextScheduleDriver);
                                }
                                if (null != nextScheduleDate) {
                                    call.setCurrentScheduledDate(nextScheduleDate);
                                }
                            }
                            callRepository.updateItem(call);
                            this.getDataProvider().refreshItem(call);
                        }
                ),
                60,
                "setOrderColumn",
                "setOrder"
        );
        column.setHidable(true);
        column.setHidden(false);
    }

    private void addEditColumn() {
        Column<Call, Component> column = this.addComponentColumn(
                call -> new CustomButton(
                        VaadinIcons.EDIT,
                        false,
                        UIPaths.EDITCALL.getPath() + call.getId(),
                        770,
                        750
                ),
                60,
                "editColumn",
                "edit"
        );
        column.setHidable(false);
        column.setHidden(false);
    }

    private void addNotesColumn() {
        this.addComponentColumn(
                call -> new CustomButton(
                        (call.getNotes().isEmpty() ? VaadinIcons.COMMENT_O : VaadinIcons.COMMENT),
                        false,
                        clickEvent -> this.setDetailsVisible(call, !this.isDetailsVisible(call))
                ),
                60,
                "notesColumn",
                "notes"
        );
    }

    private void addDescriptionColumn() {
         this.addComponentColumn(
                call -> new CustomButton(
                            (call.getDescription().isEmpty() ? VaadinIcons.COMMENT_O : VaadinIcons.COMMENT),
                            false,
                            clickEvent -> this.setDetailsVisible(call, !this.isDetailsVisible(call))
                ),
                60,
                "descriptionColumn",
                "description"
        );
    }

    private void addIsDeletedColumn() {
        Column<Call, Component> column = CustomCheckBoxColumn.addToGrid(
                Call::isDeleted,
                null,
                "deletedColumn",
                "deleted",
                null, // TODO
                // TODO
                this
        );
        column.setHidable(true);
        column.setHidden(true);
    }

    private void addIsDoneColumn() {
        Column<Call, Component> column = CustomCheckBoxColumn.addToGrid(
                Call::isDone,
                Call::setDone,
                "doneColumn",
                "done",
                null,
                this
        );
        column.setHidable(true);
        column.setHidden(false);
    }

    private void addEndDateColumn() {
        Column<Call, LocalDate> column = CustomDateColumn.addToGrid(
                Call::getEndDate,
                Call::setEndDate,
                "endDateColumn",
                "endDateShort",
                false,
                this
        );
        column.setHidable(true);
        column.setHidden(false);
    }

    private void addDriverColumn() {
        Column<Call, String> column = CustomComboBoxColumn.addToGrid(
                CustomComboBox.getComboBox(driverRepository),
                CustomComboBox.getComboBox(driverRepository),
                Call::getCurrentDriver,
                Call::setCurrentDriver,
                100,
                "driverColumn",
                "driver",
                this
        );
        column.setHidable(true);
        column.setHidden(false);
    }

    private void addScheduledOrderColumn() {
        Column<Call, Integer> column = CustomNumericColumn.addToGrid(
                Call::getCurrentScheduledOrder,
                Call::setCurrentScheduledOrder,
                60,
                "orderColumn",
                "order",
                true,
                false,
                false,
                this
        );
        column.setHidable(true);
        column.setHidden(false);
    }

    private void addIsMeetingColumn() {
        Column<Call, Component> column = CustomCheckBoxColumn.addToGrid(
                Call::isMeeting,
                Call::setMeeting,
                "meetingColumn",
                "meetingShort",
                null,
                this
        );
        column.setHidable(true);
        column.setHidden(false);
    }

    private void addCurrentScheduledDateColumn() {
        Column<Call, LocalDate> column = CustomDateColumn.addToGrid(
                Call::getCurrentScheduledDate,
                Call::setCurrentScheduledDate,
                "date2Column",
                "date2short",
                true,
                this
        );
        column.setHidable(true);
        column.setHidden(false);
    }

    private void addDaysOpenColumn() {
        Column<Call, Integer> column = CustomNumericColumn.addToGrid(
                call -> (call.isDone() ? 0 : (int)DAYS.between(call.getStartDate().getLocalDate(), LocalDate.now())),
                null,
                80,
                daysOpenColumnId,
                "days",
                false,
                true,
                true,
                this
        );
        column.setHidable(true);
        column.setHidden(true);
    }

    private void addPlanningDateColumn() {
        Column<Call, LocalDate> column = CustomDateColumn.addToGrid(
                Call::getPlanningDate,
                Call::setPlanningDate,
                "date1Column",
                "date1short",
                false,
                this
        );
        column.setHidable(true);
        column.setHidden(true);
    }

    private void addStartDateColumn() {
        Column<Call, LocalDate> column = CustomDateColumn.addToGrid(
                Call::getStartDate,
                Call::setStartDate,
                "startDateColumn",
                "startDateShort",
                false,
                this
        );
        column.setHidable(true);
        column.setHidden(false);
    }

    private void addIsHereColumn() {
        Column<Call, Component> column = CustomCheckBoxColumn.addToGrid(
                Call::isHere,
                Call::setHere,
                "hereColumn",
                "here",
                null,
                this
        );
        column.setHidable(true);
        column.setHidden(false);
    }

    private void addVehicleTypeColumn() {
        Column<Call, String> column = CustomComboBoxColumn.addToGrid(
                CustomComboBox.getComboBox(vehicleTypeRepository),
                CustomComboBox.getComboBox(vehicleTypeRepository),
                call -> call.getVehicle().getVehicleType(),
                null,
                100,
                "vehicleTypeColumn",
                "vehicleType",
                this
        );
        column.setHidable(true);
        column.setHidden(false);
    }

    private void addCallTypeColumn() {
        Column<Call, String> column = CustomComboBoxColumn.addToGrid(
                CustomComboBox.getComboBox(callTypeRepository),
                CustomComboBox.getComboBox(callTypeRepository),
                Call::getCallType,
                Call::setCallType,
                100,
                "callTypeColumn",
                "callType",
                this
        );
        column.setHidable(true);
        column.setHidden(false);
    }

    private void addAddressColumn() {
        Column<Call, String> column = this.addTextColumn(
                call -> NullPointerExceptionWrapper.getWrapper(call, c -> c.getSite().getAddress(), ""),
                null,
                180,
                "addressColumn",
                "address"
        );
        column.setHidable(true);
        column.setHidden(true);
    }

    private void addAreaColumn() {
        FilterGrid.Column<Call, String> column = CustomComboBoxColumn.addToGrid(
                CustomComboBox.getComboBox(areasRepository),
                CustomComboBox.getComboBox(areasRepository),
                call -> call.getSite().getArea(),
                null,
                100,
                "areaColumn",
                "area",
                this
        );
        column.setHidable(true);
        column.setHidden(true);
    }

    private void addSiteColumn() {
        FilterGrid.Column<Call, String> column = CustomComboBoxColumn.addToGrid(
                CustomComboBox.getComboBox(siteRepository),
                CustomComboBox.getComboBox(siteRepository),
                Call::getSite,
                null,
                120,
                "siteColumn",
                "site",
                this
        );
        column.setHidable(true);
        column.setHidden(false);
    }

    private void addCustomerColumn() {
        FilterGrid.Column<Call, String> column = CustomComboBoxColumn.addToGrid(
                CustomComboBox.getComboBox(customerRepository),
                CustomComboBox.getComboBox(customerRepository),
                Call::getCustomer,
                null,
                120,
                "customerColumn",
                "customer",
                this
        );
        column.setHidable(true);
        column.setHidden(true);
    }

    @Override
    protected void sort() {
        if (null != getCustomSortColumnId()) {
            super.sort(getCustomSortColumnId(), SortDirection.ASCENDING);
        } else {
            FilterGrid.Column<Call, String> sortColumn = this.addColumn(call -> {
                String sortKey = call.getCurrentScheduledDate().toString();
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
}
