package il.co.rtcohen.rt.app.ui;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.Page;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.BorderStyle;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import il.co.rtcohen.rt.app.LanguageSettings;
import il.co.rtcohen.rt.app.UIComponents;
import il.co.rtcohen.rt.dal.dao.Call;
import il.co.rtcohen.rt.dal.repositories.CallRepository;
import il.co.rtcohen.rt.dal.repositories.GeneralRepository;
import il.co.rtcohen.rt.dal.repositories.SiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.addons.filteringgrid.FilterGrid;
import org.vaadin.ui.NumberField;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@SpringComponent
@SpringUI(path="/print")
public class PrintUI extends AbstractUI<VerticalLayout> {

    private TextField filterId;
    private String condition;
    private Integer currentDriver = null;
    private NumberField filterDays = new NumberField();
    private FilterGrid<Call> grid;
    private SiteRepository siteRepository;
    private Label title;
    private HorizontalLayout titleLayout;

    @Autowired
    private PrintUI(ErrorHandler errorHandler, CallRepository callRepository, GeneralRepository generalRepository, SiteRepository siteRepository) {
        super(errorHandler, callRepository, generalRepository);
        this.siteRepository = siteRepository;
    }

    private List<Call> getCallListByDriver(Integer driver) {
        List<Call> list;
        switch (condition) {
            case "here": {
                list = callRepository.getLocalCalls();
                break;
            }
            case "open": {
                list = callRepository.getCalls(false);
                break;
            }
            default:
                list = callRepository.getCalls(LocalDate.parse(condition, UIComponents.dateFormatter), driver);
        }
        return list;
    }

    private void loadDriversData(Boolean printOneDriver) {
        if (currentDriver != null)
            loadDataForCurrentDriver(currentDriver);
        else {
            List<Integer> drivers = new ArrayList<>();
            drivers.add(0);
            drivers.addAll(generalRepository.getActiveId("driver"));
            for (Integer i : drivers) {
                if (printOneDriver) {
                    if (getCallListByDriver(i).size() > 0) {
                        Page.getCurrent()
                                .open(UIPaths.PRINT.getPath() + condition + "&" + i, "driver" + i
                                        , getPage().getBrowserWindowWidth(),
                                        getPage().getBrowserWindowHeight(), BorderStyle.NONE);
                    }
                } else
                    loadDataForCurrentDriver(i);
            }
        }
    }

    private void loadDataForCurrentDriver(Integer driver) {
        String style;
        if (LanguageSettings.isHebrew())
            style="LABEL-RIGHT-PRINT";
        else
            style="LABEL-LEFT-PRINT";
        if (driver == 0)
            layout.addComponent(UIComponents.label(LanguageSettings.getLocaleString("scheduledWithoutDriver"), style));
        else
            layout.addComponent(UIComponents.label(generalRepository.getNameById(driver, "driver"), style));
        addGridPerDriver(driver);
    }

    private void addColumns() {
        addEndDateColumn();
        addDescriptionColumn();
        addDriverColumn();
        addMeetingColumn();
        addDate2Column();
        if (condition.equals("open"))
            addDaysColumn();
        addDate1Column();
        addStartDateColumn();
        addPhoneColumn();
        addContactColumn();
        addAddressColumn();
        addAreaColumn();
        addSiteColumn();
        addHereColumn();
        addCarColumn();
        addCallTypeColumn();
        addCustomerColumn();
        addIdColumn();
        addOrderColumn();
    }
    private void addEndDateColumn() {
        FilterGrid.Column<Call, LocalDate> endDateColumn = grid.addColumn(Call::getEndDate, UIComponents.dateRenderer())
                .setId("endDateColumn").setWidth(120).setSortable(true)
                .setExpandRatio(1).setResizable(true).setHidable(true).setHidden(true);
        endDateColumn.setStyleGenerator(call ->
        {
            if (Call.nullDate.equals(call.getEndDate())) return "null";
            else return null;
        });
        DateField filterEndDate = UIComponents.dateField("95%","30");
        endDateColumn.setFilter(filterEndDate, UIComponents.dateFilter());
        grid.getDefaultHeaderRow().getCell("endDateColumn").setText(LanguageSettings.getLocaleString("endDateShort"));
    }
    private void addDescriptionColumn() {
        FilterGrid.Column<Call, String> descriptionColumn = grid.addColumn(Call::getDescription);
        descriptionColumn.setId("descriptionColumn").setResizable(true);
        descriptionColumn.setHidable(true);
        descriptionColumn.setHidden(false);
        if ((condition.equals("open") || (condition.equals("here"))))
            descriptionColumn.setWidth(320);
        else
            descriptionColumn.setWidth(280);
        TextField filterDescription = UIComponents.textField("95%","30");
        descriptionColumn.setFilter(filterDescription, UIComponents.stringFilter());
        grid.getDefaultHeaderRow().getCell("descriptionColumn").setText(LanguageSettings.getLocaleString("description"));
    }
    private void addDriverColumn() {
        FilterGrid.Column<Call, String> driverColumn = grid.addColumn(call ->
                generalRepository.getNameById(call.getDriverId(), "driver"))
                .setId("driverColumn")
                .setWidth(90)
                .setExpandRatio(1).setResizable(true);
        driverColumn.setHidable(true);
        driverColumn.setHidden(true);
        ComboBox<Integer> filterDriver = new UIComponents().driverComboBox(generalRepository,100,30);
        filterDriver.setWidth("95%");
        driverColumn.setFilter((filterDriver),
                (cValue, fValue) -> fValue == null || generalRepository.getNameById(fValue,
                        "driver").equals(cValue));
        grid.getDefaultHeaderRow().getCell("driverColumn").setText(LanguageSettings.getLocaleString("driver"));
    }
    private void addMeetingColumn() {
        FilterGrid.Column<Call, Component> meetingColumn =
                grid.addComponentColumn((ValueProvider<Call, Component>) call ->
                        UIComponents.checkBox(call.isMeeting(), true));
        meetingColumn.setId("meetingColumn").setExpandRatio(1).setResizable(true).setWidth(65);
        meetingColumn.setHidable(true);
        meetingColumn.setHidden(true);
        meetingColumn.setFilter(UIComponents.BooleanValueProvider(),
                new CheckBox(), UIComponents.BooleanPredicateWithShowAll());
        grid.getDefaultHeaderRow().getCell("meetingColumn").setText(LanguageSettings.getLocaleString("meetingShort"));
    }
    private void addDate2Column() {
        FilterGrid.Column<Call, LocalDate> date2Column = grid.addColumn(Call::getDate2, UIComponents.dateRenderer())
                .setId("date2Column").setWidth(120).setSortable(true)
                .setExpandRatio(1).setResizable(true);
        date2Column.setStyleGenerator(call -> {
            if (Call.nullDate.equals(call.getDate2())) return "null";
            else return "bold";
        });
        date2Column.setHidable(true);
        if ((condition.equals("open") || (condition.equals("here"))))
            date2Column.setHidden(false);
        else
            date2Column.setHidden(true);
        DateField filterDate2 = UIComponents.dateField("95%","30");
        date2Column.setFilter(filterDate2, UIComponents.dateFilter());
        grid.getDefaultHeaderRow().getCell("date2Column").setText(LanguageSettings.getLocaleString("date2short"));
    }
    private void addDaysColumn() {
        FilterGrid.Column<Call, Integer> daysColumn = grid.addColumn(call -> {
            if (call.isDone())
                return 0;
            else
                return ((int) (DAYS.between(call.getStartDate(), LocalDate.now())));
        });
        daysColumn.setId("daysColumn").setWidth(80).setResizable(true);
        daysColumn.setHidable(true);
        daysColumn.setHidden(false);
        filterDays.setWidth("95%");
        filterDays.setHeight("30");
        daysColumn.setFilter((filterDays), (v, fv) -> fv.isEmpty() || Integer.parseInt(fv) <= v);
        grid.sort("daysColumn", SortDirection.ASCENDING);
        grid.getDefaultHeaderRow().getCell("daysColumn").setText(LanguageSettings.getLocaleString("days"));
    }
    private void addDate1Column() {
        FilterGrid.Column<Call, LocalDate> date1Column = grid.addColumn(Call::getDate1, UIComponents.dateRenderer())
                .setId("date1Column").setWidth(120).setSortable(true).setExpandRatio(1).setResizable(true);
        date1Column.setStyleGenerator(call -> {
            if (Call.nullDate.equals(call.getDate1())) return "null";
            else return null;
        });
        date1Column.setHidable(true);
        date1Column.setHidden(true);
        DateField filterDate1 = UIComponents.dateField("95%","30");
        date1Column.setFilter(filterDate1, UIComponents.dateFilter());
        grid.getDefaultHeaderRow().getCell("date1Column").setText(LanguageSettings.getLocaleString("date1short"));
    }
    private void addStartDateColumn() {
        FilterGrid.Column<Call, LocalDate> startDateColumn = grid.addColumn(Call::getStartDate, UIComponents.dateRenderer())
                .setId("startDateColumn").setWidth(120).setSortable(true)
                .setExpandRatio(1).setResizable(true);
        startDateColumn.setStyleGenerator(call ->
        {
            if (Call.nullDate.equals(call.getStartDate())) return "null";
            else return null;
        });
        startDateColumn.setHidable(true);
        if ((condition.equals("open") || (condition.equals("here"))))
            startDateColumn.setHidden(false);
        else
            startDateColumn.setHidden(true);
        DateField filterStartDate = UIComponents.dateField("95%","30");
        startDateColumn.setFilter(filterStartDate, UIComponents.dateFilter());
        grid.getDefaultHeaderRow().getCell("startDateColumn").setText(LanguageSettings.getLocaleString("startDateShort"));
    }
    private void addPhoneColumn() {
        FilterGrid.Column<Call, String> phoneColumn = grid.addColumn(call -> {
            if (call.getSiteId() == 0) return "";
            else return siteRepository.getSiteById(call.getSiteId()).getPhone();
        });
        phoneColumn.setId("phoneColumn").setWidth(190).setHidable(true);
        if ((condition.equals("open") || (condition.equals("here"))))
            phoneColumn.setHidden(true);
        else
            phoneColumn.setHidden(false);
        TextField filterPhone = UIComponents.textField("95%","30");
        phoneColumn.setFilter(filterPhone, UIComponents.stringFilter());
        grid.getDefaultHeaderRow().getCell("phoneColumn").setText(LanguageSettings.getLocaleString("phone"));
    }
    private void addContactColumn() {
        FilterGrid.Column<Call, String> contactColumn = grid.addColumn(call -> {
            if (call.getSiteId() == 0) return "";
            else return siteRepository.getSiteById(call.getSiteId()).getContact();
        });
        contactColumn.setId("contactColumn").setWidth(110);
        contactColumn.setHidable(true);
        if ((condition.equals("open") || (condition.equals("here"))))
            contactColumn.setHidden(true);
        else
            contactColumn.setHidden(false);
        TextField filterContact = UIComponents.textField("95%","30");
        contactColumn.setFilter(filterContact, UIComponents.stringFilter());
        grid.getDefaultHeaderRow().getCell("contactColumn").setText(LanguageSettings.getLocaleString("contactShort"));
    }
    private void addAddressColumn() {
        FilterGrid.Column<Call, String> addressColumn = grid.addColumn(call -> {
            if (call.getSiteId() == 0) return "";
            else return siteRepository.getSiteById(call.getSiteId()).getAddress();
        }).setId("addressColumn").setWidth(190);
        addressColumn.setHidable(true);
        if ((condition.equals("open") || (condition.equals("here"))))
            addressColumn.setHidden(true);
        else
            addressColumn.setHidden(false);
        TextField filterAddress = UIComponents.textField("95%","30");
        addressColumn.setFilter(filterAddress, UIComponents.stringFilter());
        grid.getDefaultHeaderRow().getCell("addressColumn").setText(LanguageSettings.getLocaleString("address"));
    }
    private void addAreaColumn() {
        FilterGrid.Column<Call, String> areaColumn = grid.addColumn(call -> {
            if (call.getSiteId() == 0) return "";
            else return
                    generalRepository.getNameById(siteRepository.getSiteById(call.getSiteId()).getAreaId(), "area");
        })
                .setId("areaColumn")
                .setWidth(100).setExpandRatio(1).setResizable(true);
        areaColumn.setHidable(true);
        if ((condition.equals("open") || (condition.equals("here"))))
            areaColumn.setHidden(false);
        else
            areaColumn.setHidden(true);
        ComboBox<Integer> filterArea = new UIComponents().areaComboBox(generalRepository,120,30);
        filterArea.setWidth("95%");
        areaColumn.setFilter((filterArea),
                (cValue, fValue) -> fValue == null || generalRepository.getNameById(fValue,"area").equals(cValue));
        grid.getDefaultHeaderRow().getCell("areaColumn").setText(LanguageSettings.getLocaleString("area"));
    }
    private void addSiteColumn() {
    FilterGrid.Column<Call, String> siteColumn = grid.addColumn(call -> generalRepository.getNameById(call.getSiteId(), "site"))
            .setId("siteColumn")
            .setWidth(190)
            .setExpandRatio(1).setResizable(true);
        siteColumn.setHidable(true);
        siteColumn.setHidden(false);
        TextField filterSite = UIComponents.textField("95%","30");
        siteColumn.setFilter(filterSite, UIComponents.stringFilter());
        grid.getDefaultHeaderRow().getCell("siteColumn").setText(LanguageSettings.getLocaleString("site"));
    }
    private void addHereColumn() {
        FilterGrid.Column<Call, Component> hereColumn =
                grid.addComponentColumn((ValueProvider<Call, Component>) call -> {
                    CheckBox done = new CheckBox("");
                    done.setValue(call.isHere());
                    done.setReadOnly(true);
                    return done;
                });
        hereColumn.setId("hereColumn")
                .setExpandRatio(1)
                .setResizable(true)
                .setWidth(60);
        hereColumn.setHidable(true);
        if (condition.equals("here"))
            hereColumn.setHidden(true);
        else
            hereColumn.setHidden(true);
        hereColumn.setFilter(UIComponents.BooleanValueProvider(),
                new CheckBox(), UIComponents.BooleanPredicateWithShowAll());
        grid.getDefaultHeaderRow().getCell("hereColumn").setText(LanguageSettings.getLocaleString("here"));
    }
    private void addCarColumn() {
        FilterGrid.Column<Call, String> carColumn = grid.addColumn(call ->
                generalRepository.getNameById(call.getCarTypeId(), "cartype"))
                .setId("carColumn")
                .setWidth(200).setExpandRatio(1).setResizable(true);
        carColumn.setHidable(true);
        carColumn.setHidden(false);
        ComboBox<Integer> filterCar = new UIComponents().carComboBox(generalRepository,200,30);
        filterCar.setWidth("95%");
        carColumn.setFilter((filterCar),
                (cValue, fValue) -> fValue == null || generalRepository.getNameById(fValue,"cartype").equals(cValue));
        grid.getDefaultHeaderRow().getCell("carColumn").setText(LanguageSettings.getLocaleString("carTypeShort"));
    }
    private void addCallTypeColumn() {
        FilterGrid.Column<Call, String> callTypeColumn = grid.addColumn(call ->
                generalRepository.getNameById(call.getCallTypeId(), "calltype"))
                .setId("callTypeColumn")
                .setWidth(90)
                .setExpandRatio(1).setResizable(true);
        callTypeColumn.setHidable(true);
        callTypeColumn.setHidden(true);
        ComboBox<Integer> filterCallType = new UIComponents().callTypeComboBox(generalRepository,60,30);
        filterCallType.setWidth("95%");
        filterCallType.setPopupWidth("90");
        callTypeColumn.setFilter((filterCallType),
                (cValue, fValue) -> fValue == null || generalRepository.getNameById(fValue,"calltype").equals(cValue));
        grid.getDefaultHeaderRow().getCell("callTypeColumn").setText(LanguageSettings.getLocaleString("callType"));
    }
    private void addCustomerColumn() {
        FilterGrid.Column<Call, String> customerColumn = grid.addColumn(call ->
            generalRepository.getNameById(call.getCustomerId(), "cust"))
            .setId("customerColumn")
            .setWidth(150)
            .setExpandRatio(1).setResizable(true);
        customerColumn.setHidable(true);
        customerColumn.setHidden(false);
        ComboBox<Integer> filterCustomer = new UIComponents().customerComboBox(generalRepository,120,30);
        filterCustomer.setWidth("95%");
        customerColumn.setFilter((filterCustomer),
                (cValue, fValue) -> fValue == null || generalRepository.getNameById(fValue,"cust").equals(cValue));
        grid.getDefaultHeaderRow().getCell("customerColumn").setText(LanguageSettings.getLocaleString("customer"));
    }
    private void addIdColumn() {
        FilterGrid.Column<Call, Integer> idColumn = grid.addColumn(Call::getId).setId("idColumn")
                .setWidth(80).setResizable(true);
        idColumn.setHidable(true);
        idColumn.setHidden(true);
        filterId = UIComponents.textField("95%","30");
        filterId.addFocusListener(focusEvent -> filterId.setValue(""));
        idColumn.setFilter(filterId, UIComponents.integerFilter());
        grid.getDefaultHeaderRow().getCell("idColumn").setText(LanguageSettings.getLocaleString("id"));
    }
    private void addOrderColumn() {
        FilterGrid.Column<Call, Integer> orderColumn = grid.addColumn(Call::getOrder);
        orderColumn.setId("orderColumn").setWidth(60).setResizable(true);
        orderColumn.setStyleGenerator(call -> {
            if (call.getOrder() == 0) return "null";
            else return "bold";
        });
        orderColumn.setHidable(true);
        if ((condition.equals("open") || (condition.equals("here"))))
            orderColumn.setHidden(true);
        else
            orderColumn.setHidden(false);
        orderColumn.setFilter(UIComponents.textField("95%","30"), UIComponents.integerFilter());
        grid.getDefaultHeaderRow().getCell("orderColumn").setText(LanguageSettings.getLocaleString("order"));
    }

    private FilterGrid<Call> loadData(Integer driver) {
        initGrid();
        grid.setItems(getCallListByDriver(driver));
        addColumns();
        sortGrid();
        grid.setWidth("100%");
        grid.setHeightByRows(getCallListByDriver(driver).size());
        grid.setHeightMode(HeightMode.ROW);
        return grid;
    }

    private void initGrid() {
        getUI().setLocale(LanguageSettings.locale);
        grid = new FilterGrid<>();
        grid.getEditor().setSaveCaption(LanguageSettings.getLocaleString("save"));
        grid.getEditor().setCancelCaption(LanguageSettings.getLocaleString("cancel"));
        grid.setStyleName("print");

    }

    private void sortGrid() {
        FilterGrid.Column<Call, String> sortColumn = grid.addColumn(call-> {
            String sort="";
            sort+=call.getDate2();
            if(call.getDriverId()<10)
                sort+="0";
            sort+=String.valueOf(call.getDriverId());
            if(call.getOrder()<10)
                sort+="0";
            sort+=String.valueOf(call.getOrder());
            sort+=call.getStartDate();
            return sort;
        });
        sortColumn.setId("sortColumn").setWidth(70).setResizable(true);
        sortColumn.setHidable(false);
        sortColumn.setHidden(true);
        if (!(condition.equals("open")))
            grid.sort(sortColumn,SortDirection.ASCENDING);
    }

    private void addGridPerDriver(Integer driver) {
        VerticalLayout dataLayout = new VerticalLayout();
        dataLayout.setWidth("100%");
        dataLayout.setSpacing(false);
        if (getCallListByDriver(driver).size() > 0)
            dataLayout.addComponents(loadData(driver));
        else {
            Label noData = new Label(LanguageSettings.getLocaleString("noData"));
            noData.setStyleName("LABEL-WARNING");
            dataLayout.addComponents(noData);
            dataLayout.setComponentAlignment(noData,Alignment.TOP_CENTER);
        }
        dataLayout.setDefaultComponentAlignment(Alignment.TOP_CENTER);
        layout.addComponent(dataLayout);
        layout.setComponentAlignment(dataLayout, Alignment.TOP_CENTER);
    }

    private Button createPrintButton() {
        Button print = UIComponents.printButton();
        print.addClickListener(clickEvent -> {
            if((condition.equals("open"))||(condition.equals("here"))||(currentDriver !=null))
                JavaScript.getCurrent().execute(
                        "setTimeout(function() {print();self.close();}, 0);");
            else
                loadDriversData(true);
        });
        return print;
    }

    private void uploadWorkOrder() {
        if((condition.matches("^\\d{4}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])$"))) {
            title.setValue(LanguageSettings.getLocaleString("workScheduleTitle"));
            DateField date = UIComponents.dateField(150, 40);
            date.setValue(LocalDate.parse(condition, UIComponents.dateFormatter));
            date.addValueChangeListener(valueChangeEvent -> {
                condition = date.getValue().format(UIComponents.dateFormatter);
                layout.removeAllComponents();
                layout.addComponents(titleLayout);
                loadDriversData(false);
            });
            if(LanguageSettings.isHebrew())
                titleLayout.addComponents(date, title);
            else
                titleLayout.addComponents(title, date);
            addHeader();
            loadDriversData(false);
        }
        else
            setDateError();
    }

    private void setDateError() {
        Notification.show(LanguageSettings.getLocaleString("dateError"),
                "", Notification.Type.WARNING_MESSAGE);
        JavaScript.getCurrent().execute(
                "setTimeout(function() {self.close();},500);");
        condition="error";
    }

    private void uploadOpenCalls () {
        title.setValue(LanguageSettings.getLocaleString("openCallsReportTitle"));
        NumberField x = UIComponents.numberField("60","40");
        x.setValue("6");
        x.focus();
        filterDays.setValue(x.getValue());
        x.addValueChangeListener(valueChangeEvent -> {
            if(x.getValue().matches("\\d+"))
                filterDays.setValue(x.getValue());
            x.focus();
        });
        Label days = new Label(LanguageSettings.getLocaleString("days"));
        days.setStyleName("LABEL-RIGHT");
        if(LanguageSettings.isHebrew())
            titleLayout.addComponents(days, x, title);
        else
            titleLayout.addComponents(title, x, days);
        addHeader();
        addGridPerDriver(null);
    }

    private void uploadHere() {
        title.setValue(LanguageSettings.getLocaleString("currentlyHere"));
        addHeader();
        titleLayout.addComponents(title);
        addGridPerDriver(null);
    }

    private void addPrintButton() {
        Button print = createPrintButton();
        titleLayout.addComponent(print);
        titleLayout.setComponentAlignment(print,Alignment.TOP_LEFT);
    }

    private void addTimeLabel() {
        Label time = new Label(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                +"  " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        titleLayout.addComponentsAndExpand(time);
        titleLayout.setComponentAlignment(time,Alignment.TOP_LEFT);
    }

    private void getParameters() {
        condition = getPage().getUriFragment();
        if((condition.contains("&"))&&(condition.substring(condition.indexOf("&")+1).matches("\\d+"))) {
            currentDriver =Integer.valueOf(condition.substring(condition.indexOf("&")+1));
            condition=condition.substring(0,condition.indexOf("&"));
        }
    }

    @Override
    protected void setupLayout() {
        titleLayout = new HorizontalLayout();
        addPrintButton();
        addTimeLabel();
        titleLayout.setDefaultComponentAlignment(Alignment.TOP_CENTER);
        getParameters();
        if(LanguageSettings.isHebrew())
            title = UIComponents.label("LABEL-RIGHT");
        else
            title = UIComponents.label("LABEL-LEFT");
        switch (condition) {
            case "here": {
                uploadHere();
                break;
            }
            case "open": {
                uploadOpenCalls();
                break;
            }
            default: {
                uploadWorkOrder();
            }
        }
        setContent(layout);
    }

    private void addHeader() {
        titleLayout.setWidth("100%");
        titleLayout.setHeight("60px");
        layout = new VerticalLayout();
        layout.setSpacing(false);
        layout.setWidth("100%");
        layout.addComponents(titleLayout);
        layout.setDefaultComponentAlignment(Alignment.TOP_CENTER);
    }

}
