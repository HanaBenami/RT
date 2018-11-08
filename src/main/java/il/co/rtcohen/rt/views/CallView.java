package il.co.rtcohen.rt.views;

import com.vaadin.data.ValueProvider;
import com.vaadin.event.ShortcutAction;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.*;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.BorderStyle;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.DetailsGenerator;
import il.co.rtcohen.rt.UIcomponents;
import il.co.rtcohen.rt.dao.Call;
import il.co.rtcohen.rt.repositories.CallRepository;
import il.co.rtcohen.rt.repositories.GeneralRepository;
import il.co.rtcohen.rt.repositories.SiteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.addons.filteringgrid.FilterGrid;
import org.vaadin.addons.filteringgrid.filters.InMemoryFilter;
import org.vaadin.ui.NumberField;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SpringView(name = CallView.VIEW_NAME)
public class CallView extends AbstractDataView {
    static final String VIEW_NAME = "call";
    private static Logger logger = LoggerFactory.getLogger(CustomerView.class);
    private Integer defaultDaysAhead=1;
    private TextField filterId;
    private Boolean filterDoneActive = false;
    private ComboBox<Options> selectCall = new ComboBox<>();
    private DateField newDate;
    private ComboBox newDriver;
    private ComboBox<Integer> selectedCustomer;
    private Button addButton;
    private ComboBox<Integer> filterCustomer;
    private Map<String,String> parametersMap;
    private Options open;
    private CallRepository callRepository;
    private SiteRepository siteRepository;

    @Autowired
    private CallView(ErrorHandler errorHandler, CallRepository callRepository, SiteRepository siteRepository, GeneralRepository generalRepository) {
        super(errorHandler,generalRepository);
        this.callRepository = callRepository;
        this.siteRepository = siteRepository;
    }

    @Override
    public void createView(ViewChangeListener.ViewChangeEvent event) {
        parametersMap = event.getParameterMap();
        logger.info("Parameters map  " + Arrays.toString(parametersMap.entrySet().toArray()));
        selectedCustomer = new UIcomponents().customerComboBox(generalRepository,120,30);
        firstLayout();
        addGrid();
        showSelectedCustomer();
    }

    public class Options {
        String heb,eng;
        Options(String eng, String heb) {
            this.heb=heb;
            this.eng=eng;
        }
        String getHeb() {return heb;}
        String getEng() {return eng;}
    }

    private List<Options> options () {
        List<Options> options = new ArrayList<>();
        Options all = new Options("all", "כל הקריאות");
        open = new Options("open", "קריאות פתוחות");
        Options close = new Options("close", "קריאות סגורות");
        Options yesterday = new Options("yesterday", "אתמול");
        Options today = new Options("today", "היום");
        Options tomorrow = new Options("tommorow", "מחר");
        Options plus2 = new Options("plus2", "מחרתיים");
        options.add(all);
        options.add(open);
        options.add(close);
        options.add(yesterday);
        options.add(today);
        options.add(tomorrow);
        options.add(plus2);
        return options;
    }

    private List<Call> callList () {
        List<Call> list;
        switch (selectCall.getValue().getEng()) {
            case "all":
                filterDoneActive=false;
                list = callRepository.getCalls();
                break;
            case "yesterday":
                filterDoneActive=false;
                list = callRepository.getCalls(LocalDate.now().minusDays(1));
                break;
            case "tommorow":
                filterDoneActive=false;
                list = callRepository.getCalls(LocalDate.now().plusDays(1));
                break;
            case "plus2":
                filterDoneActive=false;
                list = callRepository.getCalls(LocalDate.now().plusDays(2));
                break;
            case "today":
                filterDoneActive=false;
                list = callRepository.getCalls(LocalDate.now());
                break;
            case "open":
                filterDoneActive=false;
                list = callRepository.getCalls(false);
                break;
            case "close":
                filterDoneActive=true;
                list = callRepository.getCalls(true);
                break;
            default:
                list = callRepository.getCalls();
                break;

        }
        return list;
    }

    private void addGrid() {

        //data
        FilterGrid<Call> grid = UIcomponents.myGrid("");
        grid.setItems(callList());

        //set
        FilterGrid.Column setColumn =
                grid.addComponentColumn((ValueProvider<Call, Component>) call -> {
                    Button setButton = new Button();
                    if ((call.getDate2().equals(Call.nullDate))||call.getDriverId()==0) {
                        setButton.setIcon(VaadinIcons.CALENDAR_USER);
                        setButton.addClickListener(clickEvent -> {
                                    if (!(newDriver.isEmpty()))
                                        call.setDriverID((Integer)newDriver.getValue());
                                    call.setDate2(newDate.getValue());
                                    callRepository.updateCall(call);
                                    grid.setItems(callList());
                        });
                    }
                    else {
                        setButton.setIcon(VaadinIcons.CLOSE_SMALL);
                        setButton.addClickListener(clickEvent -> {
                            call.setDriverID(0);
                            call.setDate2(Call.nullDate);
                            callRepository.updateCall(call);
                            grid.setItems(callList());
                        });
                    }
                    setButton.setStyleName("noBorderButton");
                    return setButton;
                }
                ).setId("setColumn");
        setColumn.setWidth(60);
        setColumn.setHidable(true);
        setColumn.setHidden(false);

        //edit
        FilterGrid.Column editColumn =
                grid.addComponentColumn((ValueProvider<Call, Component>) call -> {
                    Button editButton = UIcomponents.editButton();
                    final BrowserWindowOpener opener = new BrowserWindowOpener
                            (new ExternalResource("/editcall#"+call.getId()));
                    opener.setFeatures("height=700,width=750,resizable");
                    opener.extend(editButton);
                    return editButton;
                }).setId("editColumn");
        editColumn.setWidth(60);
        editColumn.setHidable(true);
        editColumn.setHidden(false);

        //notes
        FilterGrid.Column notesColumn =
                grid.addComponentColumn((ValueProvider<Call, Component>) call -> {
                    Button notesButton = new Button();
                    if (call.getNotes().equals(""))
                        notesButton.setIcon(VaadinIcons.COMMENT_O);
                    else
                        notesButton.setIcon(VaadinIcons.COMMENT);
                    notesButton.setStyleName("noBorderButton");
                    notesButton.addClickListener(clickEvent ->
                            grid.setDetailsVisible(call,!grid.isDetailsVisible(call)));
                    return notesButton;
                }
                ).setId("notesColumn");
        notesColumn.setWidth(60);
        notesColumn.setHidable(true);
        notesColumn.setHidden(false);

        //descr
        FilterGrid.Column descrColumn =
                grid.addComponentColumn((ValueProvider<Call, Component>) call -> {
                    Button descrButton = new Button();
                    if (call.getDescription().equals(""))
                        descrButton.setIcon(VaadinIcons.COMMENT_O);
                    else
                        descrButton.setIcon(VaadinIcons.COMMENT);
                    descrButton.setStyleName("noBorderButton");
                    descrButton.addClickListener(clickEvent ->
                            grid.setDetailsVisible(call,!grid.isDetailsVisible(call)));
                    return descrButton;
                }
                ).setId("descrColumn");
        descrColumn.setWidth(60);
        descrColumn.setHidable(true);
        descrColumn.setHidden(false);


        //done
        FilterGrid.Column doneColumn =
                grid.addComponentColumn((ValueProvider<Call, Component>) call ->
                        UIcomponents.checkBox(call.isDone(),true));
        doneColumn.setId("doneColumn").setExpandRatio(1).setResizable(true).setWidth(60);
        doneColumn.setHidable(true);
        doneColumn.setHidden(false);

        //enddate
        DateField endDate = UIcomponents.dateField();
        FilterGrid.Column<Call, LocalDate> endDateColumn = grid.addColumn(
                Call::getEndDate,UIcomponents.dateRenderer())
                .setId("endDateColumn").setWidth(130).setSortable(true)
                .setEditorBinding(grid.getEditor().getBinder().forField(endDate).bind(
                        (ValueProvider<Call, LocalDate>) Call::getEndDate,
                        (Setter<Call, LocalDate>) (call, LocalDate) ->  {
                            call.setEndDate(LocalDate);
                            callRepository.updateCall(call); }
                )).setExpandRatio(1).setResizable(true);
        endDate.addFocusListener(focusEvent -> endDate.setValue(LocalDate.now()));
        endDateColumn.setStyleGenerator(call -> UIcomponents.regularDateStyle(call.getEndDate()));
        endDateColumn.setHidable(true);
        endDateColumn.setHidden(false);

        //driver
        ComboBox<Integer> driverCombo = new UIcomponents().driverComboBox(generalRepository,100,30);
        driverCombo.setEmptySelectionAllowed(true);
        driverCombo.setWidth("95");
        FilterGrid.Column<Call, String> driverColumn = grid.addColumn(call ->
                generalRepository.getNameById(call.getDriverId(),"driver"))
                .setId("driverColumn")
                .setWidth(110)
                .setEditorBinding(grid.getEditor().getBinder().forField(driverCombo).bind(
                        (ValueProvider<Call, Integer>) Call::getDriverId,
                        (Setter<Call, Integer>) (call, integer) -> {
                            if(driverCombo.getValue()==null) {
                                call.setDriverID(0);
                            }
                            else {
                                call.setDriverID(driverCombo.getValue());
                            }
                            callRepository.updateCall(call);
                            grid.setItems(callList());
                        }
                )).setExpandRatio(1).setResizable(true);
        driverColumn.setHidable(true);
        driverColumn.setHidden(false);

        //ord
        NumberField ord = new NumberField();
        ord.setMinValue(0);
        FilterGrid.Column<Call, Integer> ordColumn = grid.addColumn(Call::getOrder)
                .setEditorBinding(grid.getEditor().getBinder().forField(ord).bind(
                        (ValueProvider<Call, String>) call -> String.valueOf(call.getOrder()),
                        (Setter<Call, String>) (call, String) -> {
                            if (String.isEmpty()) {
                                call.setOrder(0);
                            }
                            else if (String.matches("\\d+")) {
                                call.setOrder(Integer.parseInt(String));
                            }
                            callRepository.updateCall(call);
                            grid.setItems(callList());
                        }
                ));
        ordColumn.setId("ordColumn").setWidth(60).setResizable(true);
        ordColumn.setStyleGenerator(call -> UIcomponents.boldNumberStyle(call.getOrder()));
        ordColumn.setHidable(true);
        ordColumn.setHidden(false);

        //meeting
        CheckBox meeting = new CheckBox();
        FilterGrid.Column meetingColumn =
                grid.addComponentColumn((ValueProvider<Call, Component>) call ->
                        UIcomponents.checkBox(call.isMeeting(),true));
        meetingColumn.setId("meetingColumn").setExpandRatio(1).setResizable(true).setWidth(60);
        meetingColumn.setEditorBinding(grid.getEditor().getBinder().forField(meeting).bind(
                (ValueProvider<Call, Boolean>) Call::isMeeting,
                (Setter<Call, Boolean>) (call, Boolean) -> {
                    call.setMeeting(Boolean);
                    callRepository.updateCall(call);
                }));
        meetingColumn.setHidable(true);
        meetingColumn.setHidden(true);

        //date2
        DateField date2 = UIcomponents.dateField();
        FilterGrid.Column<Call, LocalDate> date2Column = grid.addColumn(
                Call::getDate2,UIcomponents.dateRenderer())
                .setId("date2Column").setWidth(110).setSortable(true)
                .setEditorBinding(grid.getEditor().getBinder().forField(date2).bind(
                        (ValueProvider<Call, LocalDate>) Call::getDate2,
                        (Setter<Call, LocalDate>) (call, LocalDate) -> {
                            call.setDate2(LocalDate);
                            callRepository.updateCall(call);
                            grid.setItems(callList());
                        }
                )).setExpandRatio(1).setResizable(true);
        date2.addFocusListener(focusEvent -> date2.setValue(LocalDate.now().plusDays(defaultDaysAhead)));
        date2Column.setStyleGenerator(call -> UIcomponents.boldDateStyle(call.getDate2()));
        date2Column.setHidable(true);
        date2Column.setHidden(false);

        //date1
        DateField date1 = UIcomponents.dateField();
        FilterGrid.Column<Call, LocalDate> date1Column = grid.addColumn(
                Call::getDate1,UIcomponents.dateRenderer())
                .setId("date1Column").setWidth(110).setSortable(true)
                .setEditorBinding(grid.getEditor().getBinder().forField(date1).bind(
                        (ValueProvider<Call, LocalDate>) Call::getDate1,
                        (Setter<Call, LocalDate>) (call, LocalDate) -> {
                            call.setDate1(LocalDate);
                            callRepository.updateCall(call);
                        }
                )).setExpandRatio(1).setResizable(true);
        date1.addFocusListener(focusEvent -> date1.setValue(LocalDate.now().plusDays(defaultDaysAhead)));
        date1Column.setStyleGenerator(call -> UIcomponents.regularDateStyle(call.getDate1()));
        date1Column.setHidable(true);
        date1Column.setHidden(true);

        //startdate
        DateField startDate = UIcomponents.dateField();
        FilterGrid.Column<Call, LocalDate> startDateColumn = grid.addColumn(
                Call::getStartDate,UIcomponents.dateRenderer())
                .setId("startDateColumn").setWidth(110).setSortable(true)
                .setEditorBinding(grid.getEditor().getBinder().forField(startDate).bind(
                        (ValueProvider<Call, LocalDate>) Call::getStartDate,
                        (Setter<Call, LocalDate>) (call, LocalDate) -> {
                            call.setStartDate(LocalDate);
                            callRepository.updateCall(call);
                        }
                )).setExpandRatio(1).setResizable(true);
        startDateColumn.setStyleGenerator(call -> UIcomponents.regularDateStyle(call.getStartDate()));
        startDateColumn.setHidable(true);
        startDateColumn.setHidden(false);

        //phone
        FilterGrid.Column<Call, String> phoneColumn = grid.addColumn(call -> {
            if(call.getSiteId()==0) return "";
                else return siteRepository.getSiteById(call.getSiteId()).getPhone();})
            .setId("phoneColumn").setWidth(110);
        phoneColumn.setHidable(true);
        phoneColumn.setHidden(true);

        //contact
        FilterGrid.Column<Call, String> contactColumn = grid.addColumn(call -> {
            if(call.getSiteId()==0) return "";
            else return siteRepository.getSiteById(call.getSiteId()).getContact();})
                .setId("contactColumn").setWidth(110);
        contactColumn.setHidable(true);
        contactColumn.setHidden(true);

        //address
        FilterGrid.Column<Call, String> addressColumn = grid.addColumn(call -> {
            if(call.getSiteId()==0) return "";
            else return siteRepository.getSiteById(call.getSiteId()).getAddress();})
                .setId("addressColumn").setWidth(110);
        addressColumn.setHidable(true);
        addressColumn.setHidden(true);

        //area
        FilterGrid.Column<Call, String> areaColumn = grid.addColumn(call ->{
            if(call.getSiteId()==0) return "";
            else return
                    generalRepository.getNameById(siteRepository.getSiteById(call.getSiteId()).getAreaId(),"area");
        })
                .setId("areaColumn")
                .setWidth(110).setExpandRatio(1).setResizable(true);
        areaColumn.setHidable(true);
        areaColumn.setHidden(true);

        //site
        ComboBox<Integer> siteCombo = new UIcomponents().siteComboBox(generalRepository,110,30);
        siteCombo.setEmptySelectionAllowed(true);
        FilterGrid.Column<Call, String> siteColumn = grid.addColumn(call ->
                generalRepository.getNameById(call.getSiteId(),"site"))
                .setId("siteColumn")
                .setWidth(150)
                .setEditorBinding(grid.getEditor().getBinder().forField(siteCombo).bind(
                        (ValueProvider<Call, Integer>) Call::getSiteId,
                        (Setter<Call, Integer>) (call, integer) -> {
                            if(siteCombo.getValue()==null) {
                                call.setSiteId(0);
                            }
                            else {
                                call.setSiteId(siteCombo.getValue());
                            }
                            callRepository.updateCall(call);
                            grid.setItems(callList());
                        }
                )).setExpandRatio(1).setResizable(true);
        siteColumn.setHidable(true);
        siteColumn.setHidden(false);

        //sites per customer
        grid.addItemClickListener(event -> {
            if(event.getItem().getCustomerId()>0) {
                siteCombo.setItems(siteRepository.getActiveIdByCustomer(event.getItem().getCustomerId()));
                siteCombo.setValue(event.getItem().getSiteId());
            }
            else
                siteCombo.setItems(new ArrayList<>());
        });

        //here
        CheckBox here = new CheckBox();
        FilterGrid.Column hereColumn =
                grid.addComponentColumn((ValueProvider<Call, Component>) call ->
                        UIcomponents.checkBox(call.isHere(),true));
        hereColumn.setId("hereColumn").setExpandRatio(1).setResizable(true).setWidth(60);
        hereColumn.setEditorBinding(grid.getEditor().getBinder().forField(here).bind(
                (ValueProvider<Call, Boolean>) Call::isHere,
                (Setter<Call, Boolean>) (call, Boolean) -> {
                    if(Boolean&&(!call.isHere())) {
                        call.setDriverID(0);
                        call.setDate2(Call.nullDate);
                    }
                    call.setHere(Boolean);
                    callRepository.updateCall(call);
                    grid.setItems(callList());
                }));
        hereColumn.setHidable(true);
        hereColumn.setHidden(false);

        //cartype
        ComboBox<Integer> carCombo = new UIcomponents().carComboBox(generalRepository,200,30);
        carCombo.setEmptySelectionAllowed(true);
        FilterGrid.Column<Call, String> carColumn = grid.addColumn(call ->
                generalRepository.getNameById(call.getCarTypeId(),"cartype"))
                .setId("carColumn")
                .setWidth(200)
                .setEditorBinding(grid.getEditor().getBinder().forField(carCombo).bind(
                        (ValueProvider<Call, Integer>) Call::getCarTypeId,
                        (Setter<Call, Integer>) (call, integer) -> {
                            if(carCombo.getValue()==null) {
                                call.setCarTypeId(0);
                            }
                            else {
                                call.setCarTypeId(carCombo.getValue());
                            }
                            callRepository.updateCall(call);
                            grid.setItems(callList());
                        }
                )).setExpandRatio(1).setResizable(true);
        carColumn.setHidable(true);
        carColumn.setHidden(false);

        //calltype
        ComboBox<Integer> callTypeCombo = new UIcomponents().callTypeComboBox(generalRepository,150,30);
        callTypeCombo.setEmptySelectionAllowed(true);
        FilterGrid.Column<Call, String> callTypeColumn = grid.addColumn(call ->
                generalRepository.getNameById(call.getCallTypeId(),"calltype"))
                .setId("callTypeColumn")
                .setWidth(150)
                .setEditorBinding(grid.getEditor().getBinder().forField(callTypeCombo).bind(
                        (ValueProvider<Call, Integer>) Call::getCallTypeId,
                        (Setter<Call, Integer>) (call, integer) -> {
                            if(callTypeCombo.getValue()==null) {
                                call.setCallTypeId(0);
                            }
                            else {
                                call.setCallTypeId(callTypeCombo.getValue());
                            }
                            callRepository.updateCall(call);
                            grid.setItems(callList());
                        }
                )).setExpandRatio(1).setResizable(true);
        callTypeColumn.setHidable(true);
        callTypeColumn.setHidden(false);

        //customer
        ComboBox<Integer> customerCombo = new UIcomponents().customerComboBox(generalRepository,120,30);
        customerCombo.setEmptySelectionAllowed(false);
        FilterGrid.Column<Call, String> customerColumn = grid.addColumn(call ->
                generalRepository.getNameById(call.getCustomerId(),"cust"))
                .setId("customerColumn")
                .setMinimumWidth(70)
                .setEditorBinding(grid.getEditor().getBinder().forField(customerCombo).bind(
                        (ValueProvider<Call, Integer>) Call::getCustomerId,
                        (Setter<Call, Integer>) (call, integer) -> {
                            if(customerCombo.getValue()==null) {
                                call.setCustomerId(0);
                            }
                            else {
                                call.setCustomerId(customerCombo.getValue());
                            }
                            callRepository.updateCall(call);
                            grid.setItems(callList());
                        }
                )).setExpandRatio(1).setResizable(true);

        //id
        FilterGrid.Column<Call, Integer> idColumn = grid.addColumn(Call::getId).setId("idColumn")
                .setWidth(80).setResizable(true);
        idColumn.setHidable(true);
        idColumn.setHidden(true);

        grid.getEditor().setEnabled(true);

        //headers
        grid.getDefaultHeaderRow().getCell("editColumn").setText("עריכה");
        grid.getDefaultHeaderRow().getCell("meetingColumn").setText("תואם");
        grid.getDefaultHeaderRow().getCell("hereColumn").setText("כאן");
        grid.getDefaultHeaderRow().getCell("carColumn").setText("כלי");
        grid.getDefaultHeaderRow().getCell("setColumn").setText("שיבוץ");
        grid.getDefaultHeaderRow().getCell("notesColumn").setText("הערות");
        grid.getDefaultHeaderRow().getCell("descrColumn").setText("תיאור");
        grid.getDefaultHeaderRow().getCell("doneColumn").setText("בוצע");
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
        filterId = UIcomponents.textField(30);
        filterId.addFocusListener(focusEvent -> filterId.setValue(""));
        idColumn.setFilter(filterId, InMemoryFilter.StringComparator.containsIgnoreCase());
        filterId.setWidth("95%");

        TextField filterOrd = UIcomponents.textField(30);
        ordColumn.setFilter(filterOrd, InMemoryFilter.StringComparator.containsIgnoreCase());
        filterOrd.setWidth("95%");

        filterCustomer = new UIcomponents().customerComboBox(generalRepository,120,30);
        customerColumn.setFilter((filterCustomer),
                (cValue, fValue) -> fValue == null || generalRepository.getNameById((Integer)fValue,"cust").equals(cValue));
        filterCustomer.setWidth("95%");
        //filterCustomer.setTabIndex(9);

        ComboBox filterDriver = new UIcomponents().driverComboBox(generalRepository,100,30);
        filterDriver.setWidth("95%");
        driverColumn.setFilter((filterDriver),
                (cValue, fValue) -> fValue == null || generalRepository.getNameById((Integer)fValue,"driver").equals(cValue));

        ComboBox filterCar = new UIcomponents().carComboBox(generalRepository,200,30);
        filterCar.setWidth("95%");
        carColumn.setFilter((filterCar),
                (cValue, fValue) -> fValue == null || generalRepository.getNameById((Integer)fValue,"cartype").equals(cValue));
        //filterCar.setTabIndex(11);

        ComboBox filterCallType = new UIcomponents().callTypeComboBox(generalRepository,150,30);
        filterCallType.setWidth("95%");
        callTypeColumn.setFilter((filterCallType),
                (cValue, fValue) -> fValue == null || generalRepository.getNameById((Integer)fValue,"calltype").equals(cValue));
        //filterCallType.setTabIndex(10);

        ComboBox filterArea = new UIcomponents().areaComboBox(generalRepository,120,30);
        filterArea.setWidth("95%");
        areaColumn.setFilter((filterArea),
                (cValue, fValue) -> fValue == null || generalRepository.getNameById((Integer)fValue,"area").equals(cValue));

        TextField filterSite = UIcomponents.textField(30);
        siteColumn.setFilter(filterSite, InMemoryFilter.StringComparator.containsIgnoreCase());
        filterSite.setWidth("95%");
        //filterArea.setTabIndex(13);

        TextField filterAddress = UIcomponents.textField(30);
        addressColumn.setFilter(filterAddress, InMemoryFilter.StringComparator.containsIgnoreCase());
        filterAddress.setWidth("95%");
        TextField filterPhone = UIcomponents.textField(30);
        phoneColumn.setFilter(filterPhone, InMemoryFilter.StringComparator.containsIgnoreCase());
        filterPhone.setWidth("95%");
        TextField filterContact = UIcomponents.textField(30);
        contactColumn.setFilter(filterContact, InMemoryFilter.StringComparator.containsIgnoreCase());
        filterContact.setWidth("95%");

        //forSorting
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
        grid.sort(sortColumn,SortDirection.ASCENDING);

        DateField filterDate1 = UIcomponents.dateField(30);
        filterDate1.addContextClickListener(contextClickEvent -> filterDate1.setValue(null));
        date1Column.setFilter(filterDate1, (v, fv) -> fv == null || v.isEqual(fv));
        filterDate1.setWidth("95%");
        DateField filterDate2 = UIcomponents.dateField(30);
        filterDate2.addContextClickListener(contextClickEvent -> filterDate2.setValue(null));
        date2Column.setFilter(filterDate2, (v, fv) -> fv == null || v.isEqual(fv));
        filterDate2.setWidth("95%");
        DateField filterStartDate = UIcomponents.dateField(30);
        filterStartDate.addContextClickListener(contextClickEvent -> filterStartDate.setValue(null));
        startDateColumn.setFilter(filterStartDate, (v, fv) -> fv == null || v.isEqual(fv));
        filterStartDate.setWidth("95%");
        DateField filterEndDate = UIcomponents.dateField(30);
        filterEndDate.addContextClickListener(contextClickEvent -> filterEndDate.setValue(null));
        endDateColumn.setFilter(filterEndDate, (v, fv) -> fv == null || v.isEqual(fv));
        filterEndDate.setWidth("95%");

        //filter done
        CheckBox filterDone = new CheckBox();
        filterDone.setValue(filterDoneActive);
        switch (selectCall.getValue().getEng()) {
            case "open":
                doneColumn.setFilter(UIcomponents.BooleanValueProvider(),
                        filterDone, UIcomponents.BooleanPredicate());
                break;
            default:
                doneColumn.setFilter(UIcomponents.BooleanValueProvider(),
                        filterDone, UIcomponents.BooleanPredicateWithShowAll());
        }

        //filter here
        CheckBox filterHere = new CheckBox();
        hereColumn.setFilter(UIcomponents.BooleanValueProvider(),
                filterHere,  UIcomponents.BooleanPredicateWithShowAll());
        //filterHere.setTabIndex(12);

        //filter meeting
        CheckBox filterMeeting = new CheckBox();
        meetingColumn.setFilter(UIcomponents.BooleanValueProvider(),
                filterMeeting, UIcomponents.BooleanPredicateWithShowAll());

        //populate grid
        dataGrid=grid;

        //notes and description
        grid.setDetailsGenerator((DetailsGenerator<Call>) call -> {
            TextArea bigDescr = UIcomponents.textArea("תיאור","100%","55");
            bigDescr.setValue(call.getDescription());
            bigDescr.addValueChangeListener(valueChangeEvent -> {
                call.setDescription(bigDescr.getValue());
                callRepository.updateCall(call);
            });
            TextArea bigNotes =  UIcomponents.textArea("הערות","100%","55");
            bigNotes.setValue(call.getNotes());
            bigNotes.addValueChangeListener(valueChangeEvent -> {
                call.setNotes(bigNotes.getValue());
                callRepository.updateCall(call);
            });
            VerticalLayout layout = new VerticalLayout(bigDescr,bigNotes);
            layout.setComponentAlignment(bigNotes,Alignment.MIDDLE_CENTER);
            layout.setSpacing(false);
            layout.setMargin(false);
            return layout;
        });

        //style
        grid.setStyleGenerator((StyleGenerator<Call>) UIcomponents::callStyle);
        dataGrid.setWidth("100%");
        grid.focus();

        //customer selection
        customerSelection();

        addComponentsAndExpand(dataGrid);
        setComponentAlignment(dataGrid, Alignment.TOP_CENTER);
    }

    private void firstLayout() {

        GridLayout formLayout = new GridLayout(4,2);
        formLayout.setWidth("95%");
        formLayout.setSpacing(true);

        Button selectButton = UIcomponents.searchButton();
        formLayout.addComponent(selectButton,2,0);
        formLayout.setComponentAlignment(selectButton,Alignment.MIDDLE_RIGHT);

        selectCall.setEmptySelectionAllowed(false);
        selectCall.setEnabled(true);
        selectCall.setHeight(selectButton.getHeight(),selectButton.getHeightUnits());
        selectCall.setWidth("500");
        selectCall.setItems(options());
        selectCall.setValue(open);
        selectCall.setItemCaptionGenerator(Options::getHeb);
        selectCall.addValueChangeListener(ValueChangeEvent -> {
            if (selectCall.getValue()!=null) {
                removeComponent(dataGrid);
                addGrid();
            }
        });
        formLayout.addComponent(selectCall,3,0);
        formLayout.setComponentAlignment(selectCall,Alignment.MIDDLE_LEFT);

        newDate = UIcomponents.dateField(150,30);
        newDate.setValue(LocalDate.now().plusDays(1));
        formLayout.addComponent(newDate,0,0);
        formLayout.setComponentAlignment(newDate,Alignment.MIDDLE_LEFT);

        Button print = UIcomponents.truckButton();
        print.addClickListener(clickEvent ->
                Page.getCurrent()
                        .open("/print#"+newDate.getValue().format(UIcomponents.dateFormatter),
                                "_blank",
                                getUI().getPage().getBrowserWindowWidth(),
                                getUI().getPage().getBrowserWindowHeight(),
                                BorderStyle.NONE));
        formLayout.addComponent(print,1,0,1,0);
        formLayout.setComponentAlignment(print,Alignment.MIDDLE_LEFT);

        Button refresh = UIcomponents.refreshButton();
        refresh.addClickListener(clickEvent ->
                Page.getCurrent().reload());
        formLayout.addComponent(refresh,1,1,1,1);
        formLayout.setComponentAlignment(refresh,Alignment.MIDDLE_LEFT);

        newDriver = new UIcomponents().driverComboBox(generalRepository,150,30);
        formLayout.addComponent(newDriver,0,1);
        formLayout.setComponentAlignment(newDriver,Alignment.MIDDLE_LEFT);

        addButton = UIcomponents.addButton();
        formLayout.addComponent(addButton,2,1);
        formLayout.setComponentAlignment(addButton,Alignment.MIDDLE_RIGHT);

        selectedCustomer.setHeight(addButton.getHeight(),addButton.getHeightUnits());
        selectedCustomer.setWidth("500");
        selectedCustomer.addValueChangeListener(valueChangeEvent -> showSelectedCustomer());
        selectedCustomer.addFocusListener(focusEvent -> {
            addButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        });
        selectedCustomer.addBlurListener(event -> {
            addButton.removeClickShortcut();
        });
        formLayout.addComponent(selectedCustomer,3,1);
        formLayout.setComponentAlignment(selectedCustomer,Alignment.MIDDLE_LEFT);

        addComponent(formLayout);

        addButton.addClickListener(click -> {
            if (selectedCustomer.getValue()!=null) {
                long newId=
                    callRepository.insertCall((Integer) selectedCustomer.getValue(),LocalDate.now());
                selectCall.setValue(open);
                removeComponent(dataGrid);
                addGrid();
                filterId.setValue(String.valueOf(newId));
                getUI().getPage().getCurrent().open("/editcall#"+String.valueOf(newId),"_new3",
                        700,750,
                        BorderStyle.NONE);
            }});

        selectCall.focus();
        selectCall.setTabIndex(1);
        selectButton.setTabIndex(2);
        selectedCustomer.setTabIndex(3);
        addButton.setTabIndex(4);
        newDate.setTabIndex(5);
        newDriver.setTabIndex(6);
        print.setTabIndex(7);
        refresh.setTabIndex(8);
    }

    private void customerSelection () {
        parametersMap.get("customer");
        String customerParameter = parametersMap.get("customer");
        List<Integer> customerList = generalRepository.getActiveId("cust");
        if((customerParameter!=null)&&(customerParameter.matches("\\d+")))
            if (customerList.contains(Integer.parseInt(customerParameter))) {
                selectedCustomer.setValue(Integer.parseInt(customerParameter));
                filterCustomer.setValue(Integer.parseInt(customerParameter));
            } else {
                selectedCustomer.setValue(0);
            }
    }

    private void showSelectedCustomer() {
        if ((selectedCustomer.getValue()!=null)&&!(selectedCustomer.getValue().toString().equals("0"))) {
            addButton.setEnabled(true);
            filterCustomer.setValue(selectedCustomer.getValue());
            filterId.setValue("");
        }
        else {
            addButton.setEnabled(false);
            filterCustomer.setValue(null);
            filterId.setValue("");
        }
    }

}
