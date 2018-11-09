package il.co.rtcohen.rt.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.data.ValueProvider;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.Page;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.BorderStyle;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import il.co.rtcohen.rt.UIcomponents;
import il.co.rtcohen.rt.dao.Call;
import il.co.rtcohen.rt.repositories.CallRepository;
import il.co.rtcohen.rt.repositories.GeneralRepository;
import il.co.rtcohen.rt.repositories.SiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.addons.filteringgrid.FilterGrid;
import org.vaadin.ui.NumberField;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@SpringComponent
@SpringUI(path="/print")
@Theme("myTheme")
public class PrintUI extends AbstractUI {

    private VerticalLayout layout;
    private TextField filterId;
    private String condition;
    private Integer printDriver = 0;
    private NumberField filterDays = new NumberField();
    private FilterGrid<Call> grid;
    private SiteRepository siteRepository;

    @Autowired
    private PrintUI(ErrorHandler errorHandler, CallRepository callRepository, GeneralRepository generalRepository, SiteRepository siteRepository) {
        super(errorHandler,callRepository,generalRepository);
        this.siteRepository=siteRepository;
    }

    private List<Call> callList(int driver) {
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
                list = callRepository.getCalls(LocalDate.parse(condition,UIcomponents.dateFormatter), driver);
        }
        return list;
    }

    private void addGridPerDriver(Boolean printing) {
        Label driverName;
        if (printDriver > 0) {
            if (printDriver==999)
                driverName = new Label("לא משובץ לנהג");
            else
                driverName = new Label(generalRepository.getNameById(printDriver, "driver"));
            driverName.setStyleName("LABEL-RIGHT-PRINT");
            layout.addComponent(driverName);
            addGrid(printDriver);
        } else {
            if (printing == false) {
                if (callList(0).size() > 0) {
                    driverName = UIcomponents.label("לא משובץ לנהג","LABEL-RIGHT-PRINT");
                    driverName.setHeight("30");
                    layout.addComponent(driverName);
                    addGrid(0);
            }}
            List<Integer> drivers = generalRepository.getActiveId("driver");
            for (Integer i : drivers) {
                if (printing == true) {
                    if (callList(i).size() > 0) {
                        Page.getCurrent()
                                .open("/print#" + condition + "&" + i, "driver" + i
                                        , getPage().getBrowserWindowWidth(),
                                        getPage().getBrowserWindowHeight(), BorderStyle.NONE);
                    }
                } else {
                    driverName = UIcomponents.label(generalRepository.getNameById(i, "driver"),
                            "LABEL-RIGHT-PRINT");
                    layout.addComponent(driverName);
                    addGrid(i);
                }
            }
            if (printing == true) {
                if (callList(0).size() > 0) {
                    Page.getCurrent()
                            .open("/print#" + condition + "&" + 999, "driver" + 999
                                    , getPage().getBrowserWindowWidth(),
                                    getPage().getBrowserWindowHeight(), BorderStyle.NONE);
                }
            }
        }
    }

    private FilterGrid<Call> dataGrid(Integer driver) {
        //data
        grid = new FilterGrid();
        grid.setItems(callList(driver));

        //enddate
        FilterGrid.Column<Call, LocalDate> endDateColumn = grid.addColumn(call ->
                        call.getEndDate(),UIcomponents.dateRenderer())
                .setId("endDateColumn").setWidth(100).setSortable(true)
                .setExpandRatio(1).setResizable(true).setHidable(true).setHidden(true);
        endDateColumn.setStyleGenerator(call ->
            { if(Call.nullDate.equals(call.getEndDate())) return "null"; else return null ;});

        //descr
        FilterGrid.Column<Call, String> descrColumn = grid.addColumn(Call::getDescription);
        descrColumn.setId("descrColumn").setResizable(true);
        descrColumn.setWidth(280);
        descrColumn.setHidable(true);
        descrColumn.setHidden(false);

        //driver
        FilterGrid.Column<Call, String> driverColumn = grid.addColumn(call ->
                generalRepository.getNameById(call.getDriverId(),"driver"))
                .setId("driverColumn")
                .setWidth(90)
                .setExpandRatio(1).setResizable(true);
        driverColumn.setHidable(true);
        driverColumn.setHidden(true);

        //meeting
        FilterGrid.Column meetingColumn =
                grid.addComponentColumn((ValueProvider<Call, Component>) call ->
                        UIcomponents.checkBox(call.isMeeting(),true));
        meetingColumn.setId("meetingColumn").setExpandRatio(1).setResizable(true).setWidth(65);
        meetingColumn.setHidable(true);
        meetingColumn.setHidden(true);

        //date2
        FilterGrid.Column<Call, LocalDate> date2Column = grid.addColumn(Call::getDate2,UIcomponents.dateRenderer())
                .setId("date2Column").setWidth(100).setSortable(true)
                .setExpandRatio(1).setResizable(true);
        date2Column.setStyleGenerator(call -> {
            if(Call.nullDate.equals(call.getDate2())) return "null"; else return "bold" ;});
        date2Column.setHidable(true);
        if ((condition.equals("open")||(condition.equals("hete"))))
            date2Column.setHidden(false);
        else
            date2Column.setHidden(true);

        if(condition.equals("open")) {
            //daysopen
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
            grid.getDefaultHeaderRow().getCell("daysColumn").setText("ימים");
        }

        //date1
        FilterGrid.Column<Call, LocalDate> date1Column = grid.addColumn(Call::getDate1,UIcomponents.dateRenderer())
                .setId("date1Column").setWidth(100).setSortable(true).setExpandRatio(1).setResizable(true);
        date1Column.setStyleGenerator(call -> {
            if(Call.nullDate.equals(call.getDate1())) return "null"; else return null ;});
        date1Column.setHidable(true);
        date1Column.setHidden(true);

        //startdate
        FilterGrid.Column<Call, LocalDate> startDateColumn = grid.addColumn(Call::getStartDate,UIcomponents.dateRenderer())
                .setId("startDateColumn").setWidth(100).setSortable(true)
                .setExpandRatio(1).setResizable(true);
        startDateColumn.setStyleGenerator(call ->
        { if(Call.nullDate.equals(call.getStartDate())) return "null"; else return null ;});
        startDateColumn.setHidable(true);
        if ((condition.equals("open")||(condition.equals("hete"))))
            startDateColumn.setHidden(false);
        else
            startDateColumn.setHidden(true);

        //phone
        FilterGrid.Column<Call, String> phoneColumn = grid.addColumn(call -> {
            if(call.getSiteId()==0) return "";
            else return siteRepository.getSiteById(call.getSiteId()).getPhone();})
                .setId("phoneColumn").setWidth(180);
        phoneColumn.setHidable(true);
        if ((condition.equals("open")||(condition.equals("hete"))))
            phoneColumn.setHidden(true);
        else
            phoneColumn.setHidden(false);

        //contact
        FilterGrid.Column<Call, String> contactColumn = grid.addColumn(call -> {
            if(call.getSiteId()==0) return "";
            else return siteRepository.getSiteById(call.getSiteId()).getContact();})
                .setId("contactColumn").setWidth(110);;
        contactColumn.setHidable(true);
        if ((condition.equals("open")||(condition.equals("hete"))))
            contactColumn.setHidden(true);
        else
            contactColumn.setHidden(false);

        //address
        FilterGrid.Column<Call, String> addressColumn = grid.addColumn(call -> {
            if(call.getSiteId()==0) return "";
            else return siteRepository.getSiteById(call.getSiteId()).getAddress();})
                .setId("addressColumn").setWidth(190);;
        addressColumn.setHidable(true);
        if ((condition.equals("open")||(condition.equals("hete"))))
            addressColumn.setHidden(true);
        else
            addressColumn.setHidden(false);

        //area
        FilterGrid.Column<Call, String> areaColumn = grid.addColumn(call ->{
            if(call.getSiteId()==0) return "";
            else return
                    generalRepository.getNameById(siteRepository.getSiteById(call.getSiteId()).getAreaId(),"area");})
                .setId("areaColumn")
                .setWidth(100).setExpandRatio(1).setResizable(true);
        areaColumn.setHidable(true);
        if ((condition.equals("open")||(condition.equals("hete"))))
            areaColumn.setHidden(false);
        else
            areaColumn.setHidden(true);

        //site
        FilterGrid.Column<Call, String> siteColumn = grid.addColumn(call -> generalRepository.getNameById(call.getSiteId(),"site"))
                .setId("siteColumn")
                .setWidth(190)
                .setExpandRatio(1).setResizable(true);
        siteColumn.setHidable(true);
        siteColumn.setHidden(false);

        //here
        FilterGrid.Column hereColumn =
                grid.addComponentColumn((ValueProvider<Call, Component>) call -> {
                    CheckBox done = new CheckBox("");
                    done.setValue(call.isHere());
                    done.setReadOnly(true);
                    return done;
                });
        hereColumn.setId("hereColumn").setExpandRatio(1).setResizable(true).setWidth(60);
        hereColumn.setHidable(true);
        if (condition.equals("here"))
            hereColumn.setHidden(true);
        else
            hereColumn.setHidden(true);

        //cartype
        FilterGrid.Column<Call, String> carColumn = grid.addColumn(call ->
                generalRepository.getNameById(call.getCarTypeId(),"cartype"))
                .setId("carColumn")
                .setWidth(200).setExpandRatio(1).setResizable(true);
        carColumn.setHidable(true);
        carColumn.setHidden(false);

        //calltype
        FilterGrid.Column<Call, String> callTypeColumn = grid.addColumn(call ->
                generalRepository.getNameById(call.getCallTypeId(),"calltype"))
                .setId("callTypeColumn")
                .setWidth(90)
                .setExpandRatio(1).setResizable(true);
        callTypeColumn.setHidable(true);
        callTypeColumn.setHidden(true);

        //customer
        FilterGrid.Column<Call, String> customerColumn = grid.addColumn(call ->
                generalRepository.getNameById(call.getCustomerId(),"cust"))
                .setId("customerColumn")
                .setWidth(150)
                .setExpandRatio(1).setResizable(true);
        customerColumn.setHidable(true);
        customerColumn.setHidden(false);

        //id
        FilterGrid.Column<Call, Integer> idColumn = grid.addColumn(Call::getId).setId("idColumn")
                .setWidth(80).setResizable(true);
        idColumn.setHidable(true);
        idColumn.setHidden(true);

        //ord
        FilterGrid.Column<Call, Integer> ordColumn = grid.addColumn(Call::getOrder);
        ordColumn.setId("ordColumn").setWidth(60).setResizable(true);
        ordColumn.setStyleGenerator(call -> {
            if(call.getOrder()==0) return "null"; else return "bold" ;});
        ordColumn.setHidable(true);
        if ((condition.equals("open")||(condition.equals("hete"))))
            ordColumn.setHidden(true);
        else
            ordColumn.setHidden(false);

        //headers
        grid.getDefaultHeaderRow().getCell("meetingColumn").setText("תואם");
        grid.getDefaultHeaderRow().getCell("hereColumn").setText("כאן");
        grid.getDefaultHeaderRow().getCell("carColumn").setText("כלי");
        grid.getDefaultHeaderRow().getCell("descrColumn").setText("תיאור");
        grid.getDefaultHeaderRow().getCell("endDateColumn").setText("ת' סגירה");
        grid.getDefaultHeaderRow().getCell("driverColumn").setText("נהג");
        grid.getDefaultHeaderRow().getCell("ordColumn").setText("סדר");
        grid.getDefaultHeaderRow().getCell("date2Column").setText("ת' שיבוץ");
        grid.getDefaultHeaderRow().getCell("date1Column").setText("ת' מתוכנן");
        grid.getDefaultHeaderRow().getCell("startDateColumn").setText("ת' פתיחה");
        grid.getDefaultHeaderRow().getCell("phoneColumn").setText("טלפון");
        grid.getDefaultHeaderRow().getCell("contactColumn").setText("א.קשר");
        grid.getDefaultHeaderRow().getCell("addressColumn").setText("כתובת");
        grid.getDefaultHeaderRow().getCell("areaColumn").setText("אזור");
        grid.getDefaultHeaderRow().getCell("siteColumn").setText("אתר");
        grid.getDefaultHeaderRow().getCell("callTypeColumn").setText("סוג");
        grid.getDefaultHeaderRow().getCell("customerColumn").setText("לקוח");
        grid.getDefaultHeaderRow().getCell("idColumn").setText("#");

        //filters
        filterId = UIcomponents.textField("95%","30");
        filterId.addFocusListener(focusEvent -> filterId.setValue(""));
        idColumn.setFilter(filterId, UIcomponents.stringFilter());

        TextField filterOrd = UIcomponents.textField("95%","30");
        ordColumn.setFilter(filterOrd, UIcomponents.stringFilter());

        ComboBox filterCustomer = new UIcomponents().customerComboBox(generalRepository,120,30);
        filterCustomer.setWidth("95%");
        customerColumn.setFilter((filterCustomer),
                (cValue, fValue) -> fValue == null || generalRepository.getNameById((Integer)fValue,"cust").equals(cValue));

        ComboBox filterDriver = new UIcomponents().driverComboBox(generalRepository,100,30);
        filterDriver.setWidth("95%");
        driverColumn.setFilter((filterDriver),
                (cValue, fValue) -> fValue == null || generalRepository.getNameById((Integer)fValue,"driver").equals(cValue));

        ComboBox filterCar = new UIcomponents().carComboBox(generalRepository,200,30);
        filterCar.setWidth("95%");
        carColumn.setFilter((filterCar),
                (cValue, fValue) -> fValue == null || generalRepository.getNameById((Integer)fValue,"cartype").equals(cValue));

        ComboBox filterCallType = new UIcomponents().callTypeComboBox(generalRepository,60,30);
        filterCallType.setWidth("95%");
        filterCallType.setPopupWidth("90");
        callTypeColumn.setFilter((filterCallType),
                (cValue, fValue) -> fValue == null || generalRepository.getNameById((Integer)fValue,"calltype").equals(cValue));

        TextField filterSite = UIcomponents.textField("95%","30");
        siteColumn.setFilter(filterSite, UIcomponents.stringFilter());

        ComboBox filterArea = new UIcomponents().areaComboBox(generalRepository,120,30);
        filterArea.setWidth("95%");
        areaColumn.setFilter((filterArea),
                (cValue, fValue) -> fValue == null || generalRepository.getNameById((Integer)fValue,"area").equals(cValue));

        TextField filterAddress = UIcomponents.textField("95%","30");
        addressColumn.setFilter(filterAddress, UIcomponents.stringFilter());
        TextField filterContact = UIcomponents.textField("95%","30");
        contactColumn.setFilter(filterContact, UIcomponents.stringFilter());
        TextField filterPhone = UIcomponents.textField("95%","30");
        phoneColumn.setFilter(filterPhone, UIcomponents.stringFilter());
        TextField filterDescr = UIcomponents.textField("95%","30");
        descrColumn.setFilter(filterDescr, UIcomponents.stringFilter());

        sortGrid();

        DateField filterDate1 = UIcomponents.dateField("95%","30");
        date1Column.setFilter(filterDate1, UIcomponents.dateFilter());
        DateField filterDate2 = UIcomponents.dateField("95%","30");
        date2Column.setFilter(filterDate2, UIcomponents.dateFilter());
        DateField filterStartDate = UIcomponents.dateField("95%","30");
        startDateColumn.setFilter(filterStartDate, UIcomponents.dateFilter());
        DateField filterEndDate = UIcomponents.dateField("95%","30");
        endDateColumn.setFilter(filterEndDate, UIcomponents.dateFilter());

        hereColumn.setFilter(UIcomponents.BooleanValueProvider(),
                new CheckBox(), UIcomponents.BooleanPredicateWithShowAll());

        meetingColumn.setFilter(UIcomponents.BooleanValueProvider(),
                new CheckBox(), UIcomponents.BooleanPredicateWithShowAll());

        grid.addStyleName("print");
        grid.setWidth("100%");
        grid.setHeightByRows(callList(driver).size());
        grid.setHeightMode(HeightMode.ROW);
        return grid;
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

    private void addGrid (Integer driver) {
        VerticalLayout dataLayout = new VerticalLayout();
        dataLayout.setWidth("100%");
        dataLayout.setSpacing(false);
        if (driver==999)
            driver=0;
        if (callList(driver).size() > 0)
            dataLayout.addComponents(dataGrid(driver));
        else {
            Label noData = new Label("אין נתונים להצגה");
            noData.setStyleName("LABEL-WARNING");
            dataLayout.addComponents(noData);
            dataLayout.setComponentAlignment(noData,Alignment.TOP_CENTER);
        }
        dataLayout.setDefaultComponentAlignment(Alignment.TOP_CENTER);
        layout.addComponent(dataLayout);
        layout.setComponentAlignment(dataLayout, Alignment.TOP_CENTER);
    }

    private Button PrintButton() {
        Button print = UIcomponents.printButton();
        print.addClickListener(clickEvent -> {
            if((condition.equals("open"))||(condition.equals("here"))||(printDriver>0))
                JavaScript.getCurrent().execute(
                        "setTimeout(function() {print();self.close();}, 0);");
            else
                addGridPerDriver(true);
        });
        return print;
    }

    @Override
    protected void setupLayout() {

        HorizontalLayout titleLayout = new HorizontalLayout();

        Button print = PrintButton();
        titleLayout.addComponent(print);
        titleLayout.setComponentAlignment(print,Alignment.TOP_LEFT);

        Label time = new Label(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                +"  " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        titleLayout.addComponentsAndExpand(time);
        titleLayout.setComponentAlignment(time,Alignment.TOP_LEFT);

        titleLayout.setDefaultComponentAlignment(Alignment.TOP_RIGHT);

        condition = getPage().getUriFragment();
        if((condition.contains("&"))&&(condition.substring(condition.indexOf("&")+1).matches("\\d+"))) {
            printDriver=Integer.valueOf(condition.substring(condition.indexOf("&")+1));
            condition=condition.substring(0,condition.indexOf("&"));
        }

        Label title = UIcomponents.label("LABEL-RIGHT");
        switch (condition) {
            case "here": {
                title.setValue("נמצאים כאן");
                break;
            }
            case "open": {
                title.setValue("קריאות שפתוחות יותר מ");
                NumberField x = UIcomponents.numberField("60","40");
                x.setValue("6");
                x.focus();
                filterDays.setValue(x.getValue());
                x.addValueChangeListener(valueChangeEvent -> {
                    if(x.getValue().matches("\\d+"))
                        filterDays.setValue(x.getValue());
                    x.focus();
                });
                Label days = new Label("ימים");
                days.setStyleName("LABEL-RIGHT");
                titleLayout.addComponents(days, x);
                break;
            }
            default: {
                if((condition.matches("^\\d{4}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])$"))) {
                    title.setValue("סידור עבודה לתאריך");
                    DateField date = UIcomponents.dateField(150, 40);
                    date.setValue(LocalDate.parse(condition, UIcomponents.dateFormatter));
                    date.addValueChangeListener(valueChangeEvent -> {
                        condition = date.getValue().format(UIcomponents.dateFormatter);
                        layout.removeAllComponents();
                        layout.addComponents(titleLayout);
                        addGridPerDriver(false);
                    });
                    titleLayout.addComponents(date);
                }
                else {
                    Notification.show("תאריך לא חוקי",
                            "", Notification.Type.WARNING_MESSAGE);
                    JavaScript.getCurrent().execute(
                            "setTimeout(function() {self.close();},500);");
                    condition="error";
                }
            }
        }
        if(!condition.equals("error")) {
            titleLayout.addComponents(title);
            titleLayout.setWidth("100%");
            titleLayout.setHeight("60px");
            layout = new VerticalLayout();
            layout.setSpacing(false);
            layout.setWidth("100%");
            layout.addComponents(titleLayout);
            layout.setDefaultComponentAlignment(Alignment.TOP_CENTER);
            switch (condition) {
                case "here": {
                    addGrid(0);
                    break;
                }
                case "open": {
                    addGrid(0);
                    break;
                }
                default: {
                    addGridPerDriver(false);
                }
            }
            setContent(layout);
        }
    }

}
