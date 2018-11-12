package il.co.rtcohen.rt.app.views;

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
import il.co.rtcohen.rt.app.UIComponents;
import il.co.rtcohen.rt.dal.dao.Call;
import il.co.rtcohen.rt.dal.dao.Customer;
import il.co.rtcohen.rt.dal.repositories.CallRepository;
import il.co.rtcohen.rt.dal.repositories.GeneralRepository;
import il.co.rtcohen.rt.dal.repositories.SiteRepository;
import il.co.rtcohen.rt.dal.services.CallService;
import il.co.rtcohen.rt.app.ui.UIPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.addons.filteringgrid.FilterGrid;
import org.vaadin.ui.NumberField;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SpringView(name = CallView.VIEW_NAME)
public class CallView extends AbstractDataView<Call> {
    static final String VIEW_NAME = "call";
    private static Logger logger = LoggerFactory.getLogger(CustomerView.class);
    private Integer defaultDaysAhead = 1;
    private TextField filterId;
    private Boolean filterDoneActive = false;
    private ComboBox<Options> selectOption = new ComboBox<>();
    private DateField newDate;
    private ComboBox<Integer> newDriver;
    private ComboBox<Integer> selectCustomer;
    private ComboBox<Integer> filterCustomer;
    private ComboBox<Integer> siteCombo;
    private Map<String, String> parametersMap;
    private Options open;
    private CallRepository callRepository;
    private CallService callService;
    private SiteRepository siteRepository;
    private GridLayout headerLayout;
    private Button selectButton;
    private Button refresh;
    private Button print;

    @Autowired
    private CallView(ErrorHandler errorHandler, CallRepository callRepository, SiteRepository siteRepository, GeneralRepository generalRepository, CallService callService) {
        super(errorHandler, generalRepository);
        this.callRepository = callRepository;
        this.siteRepository = siteRepository;
        this.callService=callService;
    }

    @Override
    public void createView(ViewChangeListener.ViewChangeEvent event) {
        parametersMap = event.getParameterMap();
        logger.info("Parameters map  " + Arrays.toString(parametersMap.entrySet().toArray()));
        selectCustomer = new UIComponents().customerComboBox(generalRepository, 120, 30);
        addHeaderLayout();
        addGrid();
        setTabIndexes();
        selectOption.focus();
        showSelectedCustomer();
    }

    public class Options {
        String heb, eng;
        Options(String eng, String heb) {
            this.heb = heb;
            this.eng = eng;
        }
        String getHeb() {
            return heb;
        }
        String getEng() {
            return eng;
        }
    }
    private List<Options> options() {
        List<Options> options = new ArrayList<>();
        Options all = new Options("all", "כל הקריאות");
        open = new Options("open", "קריאות פתוחות");
        Options close = new Options("close", "קריאות סגורות");
        Options yesterday = new Options("yesterday", "אתמול");
        Options today = new Options("today", "היום");
        Options tomorrow = new Options("tomorrow", "מחר");
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
    private List<Call> getCalls() {
        List<Call> list;
        switch (selectOption.getValue().getEng()) {
            case "all":
                filterDoneActive = false;
                list = callRepository.getCalls();
                break;
            case "yesterday":
                filterDoneActive = false;
                list = callRepository.getCalls(LocalDate.now().minusDays(1));
                break;
            case "tomorrow":
                filterDoneActive = false;
                list = callRepository.getCalls(LocalDate.now().plusDays(1));
                break;
            case "plus2":
                filterDoneActive = false;
                list = callRepository.getCalls(LocalDate.now().plusDays(2));
                break;
            case "today":
                filterDoneActive = false;
                list = callRepository.getCalls(LocalDate.now());
                break;
            case "open":
                filterDoneActive = false;
                list = callRepository.getCalls(false);
                break;
            case "close":
                filterDoneActive = true;
                list = callRepository.getCalls(true);
                break;
            default:
                list = callRepository.getCalls();
                break;

        }
        return list;
    }
    private void addSetOrderColumn() {
        FilterGrid.Column<Call, Component> setOrderColumn =
                grid.addComponentColumn((ValueProvider<Call, Component>) call -> {
                            Button setButton = new Button();
                            if ((call.getDate2().equals(Call.nullDate)) || call.getDriverId() == 0) {
                                setButton.setIcon(VaadinIcons.CALENDAR_USER);
                                setButton.addClickListener(clickEvent -> {
                                    if (!(newDriver.isEmpty()))
                                        call.setDriverID(newDriver.getValue());
                                    call.setDate2(newDate.getValue());
                                    callService.updateCall(call);
                                    grid.setItems(getCalls());
                                });
                            } else {
                                setButton.setIcon(VaadinIcons.CLOSE_SMALL);
                                setButton.addClickListener(clickEvent -> {
                                    call.setDriverID(0);
                                    call.setDate2(Call.nullDate);
                                    callService.updateCall(call);
                                    grid.setItems(getCalls());
                                });
                            }
                            setButton.setStyleName("noBorderButton");
                            return setButton;
                        }
                ).setId("setOrderColumn");
        setOrderColumn.setWidth(60).setHidable(true).setHidden(false).setSortable(false);
        grid.getDefaultHeaderRow().getCell("setOrderColumn").setText("שיבוץ");
    }
    private void addSitesColumn() {
        FilterGrid.Column sitesColumn =
                grid.addComponentColumn((ValueProvider<Call, Component>) Call -> {
                    int n=siteRepository.getActiveIdByCustomer(Call.getCustomerId()).size();
                    Button sitesButton = UIComponents.gridSmallButton(VaadinIcons.FROWN_O);
                    sitesButton.addClickListener(clickEvent ->
                            getUI().getNavigator().navigateTo
                                    ("site/customer="+ Call.getCustomerId()));
                    if(n==1) {
                        sitesButton.setCaption(String.valueOf((n)));
                        sitesButton.setIcon(VaadinIcons.HOME_O);
                    }
                    else  if (n>1) {
                        sitesButton.setCaption(String.valueOf((n)));
                        sitesButton.setIcon(VaadinIcons.HOME);
                    }
                    return sitesButton;
                });
        sitesColumn.setId("sitesColumn").setExpandRatio(1).setResizable(false).setWidth(85).setSortable(false);
        grid.getDefaultHeaderRow().getCell("sitesColumn").setText("אתרים");
    }
    private void addEditColumn() {
        FilterGrid.Column editColumn =
                grid.addComponentColumn((ValueProvider<Call, Component>) call -> {
                    Button editButton = UIComponents.editButton();
                    final BrowserWindowOpener opener = new BrowserWindowOpener
                            (new ExternalResource(UIPaths.EDITCALL.getPath() + call.getId()));
                    opener.setFeatures("height=700,width=700,resizable");
                    opener.extend(editButton);
                    return editButton;
                }).setId("editColumn");
        editColumn.setWidth(60).setHidable(true).setHidden(false).setSortable(false);
        grid.getDefaultHeaderRow().getCell("editColumn").setText("עריכה");

    }
    private void addNotesColumn() {
        FilterGrid.Column notesColumn =
                grid.addComponentColumn((ValueProvider<Call, Component>) call -> {
                            Button notesButton = new Button();
                            if (call.getNotes().equals(""))
                                notesButton.setIcon(VaadinIcons.COMMENT_O);
                            else
                                notesButton.setIcon(VaadinIcons.COMMENT);
                            notesButton.setStyleName("noBorderButton");
                            notesButton.addClickListener(clickEvent ->
                                    grid.setDetailsVisible(call, !grid.isDetailsVisible(call)));
                            return notesButton;
                        }
                ).setId("notesColumn");
        notesColumn.setWidth(60).setHidable(true).setHidden(false).setSortable(false);
        grid.getDefaultHeaderRow().getCell("notesColumn").setText("הערות");
    }
    private void addDescriptionColumn() {
        FilterGrid.Column descrColumn =
                grid.addComponentColumn((ValueProvider<Call, Component>) call -> {
                    Button descrButton = new Button();
                    if (call.getDescription().equals(""))
                        descrButton.setIcon(VaadinIcons.COMMENT_O);
                    else
                        descrButton.setIcon(VaadinIcons.COMMENT);
                    descrButton.setStyleName("noBorderButton");
                    descrButton.addClickListener(clickEvent ->
                            grid.setDetailsVisible(call, !grid.isDetailsVisible(call)));
                    return descrButton;
                }).setId("descriptionColumn");
        descrColumn.setWidth(60).setHidable(true).setHidden(false).setSortable(false);
        grid.getDefaultHeaderRow().getCell("descriptionColumn").setText("תיאור");
    }
    private void addDoneColumn() {
        FilterGrid.Column<Call, Component> doneColumn =
                grid.addComponentColumn((ValueProvider<Call, Component>) call ->
                        UIComponents.checkBox(call.isDone(), true));
        doneColumn.setId("doneColumn").setExpandRatio(1).setResizable(true).setWidth(60);
        doneColumn.setHidable(true);
        doneColumn.setHidden(false);
        doneColumn.setSortable(false);
        CheckBox filterDone = new CheckBox();
        filterDone.setValue(filterDoneActive);
        switch (selectOption.getValue().getEng()) {
            case "open":
                doneColumn.setFilter(UIComponents.BooleanValueProvider(),
                        filterDone, UIComponents.BooleanPredicate());
                break;
            default:
                doneColumn.setFilter(UIComponents.BooleanValueProvider(),
                        filterDone, UIComponents.BooleanPredicateWithShowAll());
        }
        grid.getDefaultHeaderRow().getCell("doneColumn").setText("בוצע");
    }
    private void addEndDateColumn() {
        DateField endDate = UIComponents.dateField();
        FilterGrid.Column<Call, LocalDate> endDateColumn = grid.addColumn(
                Call::getEndDate, UIComponents.dateRenderer())
                .setId("endDateColumn").setWidth(130).setSortable(true)
                .setEditorBinding(grid.getEditor().getBinder().forField(endDate).bind(
                        (ValueProvider<Call, LocalDate>) Call::getEndDate,
                        (Setter<Call, LocalDate>) (call, LocalDate) -> {
                            call.setEndDate(LocalDate);
                            callService.updateCall(call);
                        }
                )).setExpandRatio(1).setResizable(true);
        endDate.addFocusListener(focusEvent -> endDate.setValue(LocalDate.now()));
        endDateColumn.setStyleGenerator(call -> UIComponents.regularDateStyle(call.getEndDate()));
        endDateColumn.setHidable(true);
        endDateColumn.setHidden(false);
        grid.getDefaultHeaderRow().getCell("endDateColumn").setText("ת' סגירה");
        DateField filterEndDate = UIComponents.dateField(30);
        filterEndDate.addContextClickListener(contextClickEvent -> filterEndDate.setValue(null));
        endDateColumn.setFilter(filterEndDate, UIComponents.dateFilter());
        filterEndDate.setWidth("95%");
    }
    private void addDriverColumn() {
        ComboBox<Integer> driverCombo = new UIComponents().driverComboBox(generalRepository, 100, 30);
        driverCombo.setEmptySelectionAllowed(true);
        driverCombo.setWidth("95");
        FilterGrid.Column<Call, String> driverColumn = grid.addColumn(call ->
                generalRepository.getNameById(call.getDriverId(), "driver"))
                .setId("driverColumn")
                .setWidth(110)
                .setEditorBinding(grid.getEditor().getBinder().forField(driverCombo).bind(
                        (ValueProvider<Call, Integer>) Call::getDriverId,
                        (Setter<Call, Integer>) (call, integer) -> {
                            if (driverCombo.getValue() == null) {
                                call.setDriverID(0);
                            } else {
                                call.setDriverID(driverCombo.getValue());
                            }
                            callService.updateCall(call);
                            grid.setItems(getCalls());
                        }
                )).setExpandRatio(1).setResizable(true);
        driverColumn.setHidable(true);
        driverColumn.setHidden(false);
        grid.getDefaultHeaderRow().getCell("driverColumn").setText("נהג");
        ComboBox<Integer> filterDriver = new UIComponents().driverComboBox(generalRepository, 100, 30);
        filterDriver.setWidth("95%");
        driverColumn.setFilter((filterDriver),
                (cValue, fValue) -> fValue == null || generalRepository.getNameById
                        (fValue, "driver").equals(cValue));
    }
    private void addOrderColumn() {
        TextField order = new NumberField();
        FilterGrid.Column<Call, Integer> orderColumn = grid.addColumn(Call::getOrder)
                .setEditorBinding(grid.getEditor().getBinder().forField(order).bind(
                        (ValueProvider<Call, String>) call -> String.valueOf(call.getOrder()),
                        (Setter<Call, String>) (call, String) -> {
                            if (String.isEmpty()) {
                                call.setOrder(0);
                            } else if (String.matches("\\d+")) {
                                call.setOrder(Integer.parseInt(String));
                            }
                            callService.updateCall(call);
                            grid.setItems(getCalls());
                        }
                ));
        orderColumn.setId("orderColumn").setWidth(60).setResizable(true);
        orderColumn.setStyleGenerator(call -> UIComponents.boldNumberStyle(call.getOrder()));
        orderColumn.setHidable(true);
        orderColumn.setHidden(false);
        TextField filterOrder = UIComponents.textField(30);
        orderColumn.setFilter(filterOrder, UIComponents.integerFilter());
        filterOrder.setWidth("95%");
        grid.getDefaultHeaderRow().getCell("orderColumn").setText("סדר");
    }
    private void addMeetingColumn() {
        FilterGrid.Column<Call, Component> meetingColumn =
                grid.addComponentColumn((ValueProvider<Call, Component>) call ->
                        UIComponents.checkBox(call.isMeeting(), true));
        meetingColumn.setId("meetingColumn").setExpandRatio(1).setResizable(true).setWidth(60);
        meetingColumn.setEditorBinding(grid.getEditor().getBinder().forField(new CheckBox()).bind(
                (ValueProvider<Call, Boolean>) Call::isMeeting,
                (Setter<Call, Boolean>) (call, Boolean) -> {
                    call.setMeeting(Boolean);
                    callService.updateCall(call);
                }));
        meetingColumn.setHidable(true);
        meetingColumn.setHidden(true);
        meetingColumn.setFilter(UIComponents.BooleanValueProvider(),
                new CheckBox(), UIComponents.BooleanPredicateWithShowAll());
        grid.getDefaultHeaderRow().getCell("meetingColumn").setText("תואם");
    }
    private void addDate2Column() {
        DateField date2 = UIComponents.dateField();
        FilterGrid.Column<Call, LocalDate> date2Column = grid.addColumn(
                Call::getDate2, UIComponents.dateRenderer())
                .setId("date2Column").setWidth(110).setSortable(true)
                .setEditorBinding(grid.getEditor().getBinder().forField(date2).bind(
                        (ValueProvider<Call, LocalDate>) Call::getDate2,
                        (Setter<Call, LocalDate>) (call, LocalDate) -> {
                            call.setDate2(LocalDate);
                            callService.updateCall(call);
                            grid.setItems(getCalls());
                        }
                )).setExpandRatio(1).setResizable(true);
        date2.addFocusListener(focusEvent -> date2.setValue(LocalDate.now().plusDays(defaultDaysAhead)));
        date2Column.setStyleGenerator(call -> UIComponents.boldDateStyle(call.getDate2()));
        date2Column.setHidable(true);
        date2Column.setHidden(false);
        DateField filterDate2 = UIComponents.dateField(30);
        filterDate2.addContextClickListener(contextClickEvent -> filterDate2.setValue(null));
        date2Column.setFilter(filterDate2, UIComponents.dateFilter());
        filterDate2.setWidth("95%");
        grid.getDefaultHeaderRow().getCell("date2Column").setText("ת' שיבוץ");
    }
    private void addDate1Column() {
        DateField date1 = UIComponents.dateField();
        FilterGrid.Column<Call, LocalDate> date1Column = grid.addColumn(
                Call::getDate1, UIComponents.dateRenderer())
                .setId("date1Column").setWidth(110).setSortable(true)
                .setEditorBinding(grid.getEditor().getBinder().forField(date1).bind(
                        (ValueProvider<Call, LocalDate>) Call::getDate1,
                        (Setter<Call, LocalDate>) (call, LocalDate) -> {
                            call.setDate1(LocalDate);
                            callService.updateCall(call);
                        }
                )).setExpandRatio(1).setResizable(true);
        date1.addFocusListener(focusEvent -> date1.setValue(LocalDate.now().plusDays(defaultDaysAhead)));
        date1Column.setStyleGenerator(call -> UIComponents.regularDateStyle(call.getDate1()));
        date1Column.setHidable(true);
        date1Column.setHidden(true);
        DateField filterDate1 = UIComponents.dateField(30);
        filterDate1.addContextClickListener(contextClickEvent -> filterDate1.setValue(null));
        date1Column.setFilter(filterDate1, UIComponents.dateFilter());
        filterDate1.setWidth("95%");
        grid.getDefaultHeaderRow().getCell("date1Column").setText("ת' מתוכנן");
    }
    private void addStartDateColumn() {
        DateField startDate = UIComponents.dateField();
        FilterGrid.Column<Call, LocalDate> startDateColumn = grid.addColumn(
                Call::getStartDate, UIComponents.dateRenderer())
                .setId("startDateColumn").setWidth(110).setSortable(true)
                .setEditorBinding(grid.getEditor().getBinder().forField(startDate).bind(
                        (ValueProvider<Call, LocalDate>) Call::getStartDate,
                        (Setter<Call, LocalDate>) (call, LocalDate) -> {
                            call.setStartDate(LocalDate);
                            callService.updateCall(call);
                        }
                )).setExpandRatio(1).setResizable(true);
        startDateColumn.setStyleGenerator(call -> UIComponents.regularDateStyle(call.getStartDate()));
        startDateColumn.setHidable(true);
        startDateColumn.setHidden(false);
        DateField filterStartDate = UIComponents.dateField(30);
        filterStartDate.addContextClickListener(contextClickEvent -> filterStartDate.setValue(null));
        startDateColumn.setFilter(filterStartDate, UIComponents.dateFilter());
        filterStartDate.setWidth("95%");
        grid.getDefaultHeaderRow().getCell("startDateColumn").setText("ת' פתיחה");
    }
    private void addSiteColumn() {
        siteCombo = new UIComponents().siteComboBox(generalRepository, 110, 30);
        siteCombo.setEmptySelectionAllowed(true);
        FilterGrid.Column<Call, String> siteColumn = grid.addColumn(call ->
                generalRepository.getNameById(call.getSiteId(), "site"))
                .setId("siteColumn")
                .setWidth(150)
                .setEditorBinding(grid.getEditor().getBinder().forField(siteCombo).bind(
                        (ValueProvider<Call, Integer>) Call::getSiteId,
                        (Setter<Call, Integer>) (call, integer) -> {
                            if (siteCombo.getValue() == null) {
                                call.setSiteId(0);
                            } else {
                                call.setSiteId(siteCombo.getValue());
                            }
                            callService.updateCall(call);
                            grid.setItems(getCalls());
                        }
                )).setExpandRatio(1).setResizable(true);
        siteColumn.setHidable(true);
        siteColumn.setHidden(false);
        grid.addItemClickListener(this::getSitesPerCustomer);
        TextField filterSite = UIComponents.textField(30);
        siteColumn.setFilter(filterSite, UIComponents.stringFilter());
        filterSite.setWidth("95%");
        grid.getDefaultHeaderRow().getCell("siteColumn").setText("אתר");
    }
    private void getSitesPerCustomer(Grid.ItemClick<Call> event) {
        if (event.getItem().getCustomerId() > 0) {
            siteCombo.setItems(siteRepository.getActiveIdByCustomer(event.getItem().getCustomerId()));
            siteCombo.setValue(event.getItem().getSiteId());
        } else
            siteCombo.setItems(new ArrayList<>());
    }
    private void addPhoneColumn() {
        FilterGrid.Column<Call, String> phoneColumn = grid.addColumn(call -> {
            if (call.getSiteId() == 0) return "";
            else return siteRepository.getSiteById(call.getSiteId()).getPhone();
        }).setId("phoneColumn").setWidth(110);
        phoneColumn.setHidable(true);
        phoneColumn.setHidden(true);
        TextField filterPhone = UIComponents.textField(30);
        phoneColumn.setFilter(filterPhone, UIComponents.stringFilter());
        filterPhone.setWidth("95%");
        grid.getDefaultHeaderRow().getCell("phoneColumn").setText("טלפון");
    }
    private void addContactColumn() {
        FilterGrid.Column<Call, String> contactColumn = grid.addColumn(call -> {
            if (call.getSiteId() == 0) return "";
            else return siteRepository.getSiteById(call.getSiteId()).getContact();
        })
                .setId("contactColumn").setWidth(110);
        contactColumn.setHidable(true);
        contactColumn.setHidden(true);
        TextField filterContact = UIComponents.textField(30);
        contactColumn.setFilter(filterContact, UIComponents.stringFilter());
        filterContact.setWidth("95%");
        grid.getDefaultHeaderRow().getCell("contactColumn").setText("א.קשר");
    }
    private void addAddressColumn() {
        FilterGrid.Column<Call, String> addressColumn = grid.addColumn(call -> {
            if (call.getSiteId() == 0) return "";
            else return siteRepository.getSiteById(call.getSiteId()).getAddress();
        })
                .setId("addressColumn").setWidth(110);
        addressColumn.setHidable(true);
        addressColumn.setHidden(true);
        TextField filterAddress = UIComponents.textField(30);
        addressColumn.setFilter(filterAddress, UIComponents.stringFilter());
        filterAddress.setWidth("95%");
        grid.getDefaultHeaderRow().getCell("addressColumn").setText("כתובת");
    }
    private void addAreaColumn() {
        FilterGrid.Column<Call, String> areaColumn = grid.addColumn(call -> {
            if (call.getSiteId() == 0) return "";
            else
                return generalRepository.getNameById(siteRepository.getSiteById
                        (call.getSiteId()).getAreaId(), "area");
        }).setId("areaColumn").setWidth(110).setExpandRatio(1).setResizable(true);
        areaColumn.setHidable(true);
        areaColumn.setHidden(true);
        ComboBox<Integer> filterArea = new UIComponents().areaComboBox(generalRepository,120,30);
        filterArea.setWidth("95%");
        areaColumn.setFilter((filterArea),
                (cValue, fValue) -> fValue == null || generalRepository.getNameById(fValue,"area").equals(cValue));
        grid.getDefaultHeaderRow().getCell("areaColumn").setText("אזור");
    }
    private void addHereColumn() {
        FilterGrid.Column<Call, Component> hereColumn =
                grid.addComponentColumn((ValueProvider<Call, Component>) call ->
                        UIComponents.checkBox(call.isHere(), true));
        hereColumn.setId("hereColumn").setExpandRatio(1).setResizable(true).setWidth(60);
        hereColumn.setEditorBinding(grid.getEditor().getBinder().forField(new CheckBox()).bind(
                (ValueProvider<Call, Boolean>) Call::isHere,
                (Setter<Call, Boolean>) (call, Boolean) -> {
                    if (Boolean && (!call.isHere())) {
                        call.setDriverID(0);
                        call.setDate2(Call.nullDate);
                    }
                    call.setHere(Boolean);
                    callService.updateCall(call);
                    grid.setItems(getCalls());
                }));
        hereColumn.setHidable(true);
        hereColumn.setHidden(false);
        hereColumn.setFilter(UIComponents.BooleanValueProvider(),
                new CheckBox(), UIComponents.BooleanPredicateWithShowAll());
        grid.getDefaultHeaderRow().getCell("hereColumn").setText("כאן");
    }
    private void addCarTypeColumn() {
        ComboBox<Integer> carCombo = new UIComponents().carComboBox(generalRepository, 200, 30);
        carCombo.setEmptySelectionAllowed(true);
        FilterGrid.Column<Call, String> carColumn = grid.addColumn(call ->
                generalRepository.getNameById(call.getCarTypeId(), "cartype"))
                .setId("carColumn")
                .setWidth(200)
                .setEditorBinding(grid.getEditor().getBinder().forField(carCombo).bind(
                        (ValueProvider<Call, Integer>) Call::getCarTypeId,
                        (Setter<Call, Integer>) (call, integer) -> {
                            if (carCombo.getValue() == null) {
                                call.setCarTypeId(0);
                            } else {
                                call.setCarTypeId(carCombo.getValue());
                            }
                            callService.updateCall(call);
                            grid.setItems(getCalls());
                        }
                )).setExpandRatio(1).setResizable(true);
        carColumn.setHidable(true);
        carColumn.setHidden(false);
        ComboBox<Integer> filterCar = new UIComponents().carComboBox(generalRepository,200,30);
        filterCar.setWidth("95%");
        carColumn.setFilter((filterCar),
                (cValue, fValue) -> fValue == null || generalRepository.getNameById(fValue,"cartype").equals(cValue));
        grid.getDefaultHeaderRow().getCell("carColumn").setText("כלי");
    }
    private void addCallTypeColumn() {
        ComboBox<Integer> callTypeCombo = new UIComponents().callTypeComboBox(generalRepository, 150, 30);
        callTypeCombo.setEmptySelectionAllowed(true);
        FilterGrid.Column<Call, String> callTypeColumn = grid.addColumn(call ->
                generalRepository.getNameById(call.getCallTypeId(), "calltype"))
                .setId("callTypeColumn")
                .setWidth(150)
                .setEditorBinding(grid.getEditor().getBinder().forField(callTypeCombo).bind(
                        (ValueProvider<Call, Integer>) Call::getCallTypeId,
                        (Setter<Call, Integer>) (call, integer) -> {
                            if (callTypeCombo.getValue() == null) {
                                call.setCallTypeId(0);
                            } else {
                                call.setCallTypeId(callTypeCombo.getValue());
                            }
                            callService.updateCall(call);
                            grid.setItems(getCalls());
                        }
                )).setExpandRatio(1).setResizable(true);
        callTypeColumn.setHidable(true);
        callTypeColumn.setHidden(false);
        ComboBox<Integer> filterCallType = new UIComponents().callTypeComboBox(generalRepository,150,30);
        filterCallType.setWidth("95%");
        callTypeColumn.setFilter((filterCallType),
                (cValue, fValue) -> fValue == null || generalRepository.getNameById(fValue,"calltype").equals(cValue));
        grid.getDefaultHeaderRow().getCell("callTypeColumn").setText("סוג");
    }
    private void addCustomerColumn() {
        ComboBox<Integer> customerCombo = new UIComponents().customerComboBox(generalRepository, 120, 30);
        customerCombo.setEmptySelectionAllowed(false);
        FilterGrid.Column<Call, String> customerColumn = grid.addColumn(call ->
                generalRepository.getNameById(call.getCustomerId(), "cust"))
                .setId("customerColumn")
                .setMinimumWidth(70)
                .setEditorBinding(grid.getEditor().getBinder().forField(customerCombo).bind(
                        (ValueProvider<Call, Integer>) Call::getCustomerId,
                        (Setter<Call, Integer>) (call, integer) -> {
                            if (customerCombo.getValue() == null) {
                                call.setCustomerId(0);
                            } else {
                                call.setCustomerId(customerCombo.getValue());
                            }
                            callService.updateCall(call);
                            grid.setItems(getCalls());
                        }
                )).setExpandRatio(1).setResizable(true);
        filterCustomer = new UIComponents().customerComboBox(generalRepository,120,30);
        customerColumn.setFilter((filterCustomer),
                (cValue, fValue) -> fValue == null || generalRepository.getNameById(fValue,"cust").equals(cValue));
        filterCustomer.setWidth("95%");
        grid.getDefaultHeaderRow().getCell("customerColumn").setText("לקוח");
    }
    private void addIdColumn() {
        FilterGrid.Column<Call, Integer> idColumn = grid.addColumn(Call::getId).setId("idColumn")
                .setWidth(80).setResizable(true);
        idColumn.setHidable(true);
        idColumn.setHidden(true);
        filterId = UIComponents.textField(30);
        filterId.addFocusListener(focusEvent -> filterId.setValue(""));
        idColumn.setFilter(filterId, UIComponents.integerFilter());
        filterId.setWidth("95%");
        grid.getDefaultHeaderRow().getCell("idColumn").setText("#");
    }

    @Override
    void addColumns() {
        addSetOrderColumn();
        addSitesColumn();
        addEditColumn();
        addNotesColumn();
        addDescriptionColumn();
        addDoneColumn();
        addEndDateColumn();
        addDriverColumn();
        addOrderColumn();
        addMeetingColumn();
        addDate2Column();
        addDate1Column();
        addStartDateColumn();
        addSiteColumn();
        addPhoneColumn();
        addContactColumn();
        addAddressColumn();
        addAreaColumn();
        addHereColumn();
        addCarTypeColumn();
        addCallTypeColumn();
        addCustomerColumn();
        addIdColumn();
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
        grid.sort(sortColumn,SortDirection.ASCENDING);
    }

    @Override
    void addGrid() {
        initGrid("");
        grid.setItems(getCalls());
        addColumns();
        grid.setDetailsGenerator((DetailsGenerator<Call>) this::getCallDetails);
        grid.setStyleGenerator((StyleGenerator<Call>) UIComponents::callStyle);
        grid.getEditor().setEnabled(true);
        grid.focus();
        sortGrid();
        getSelectedCustomer();
        grid.setWidth("100%");
        addComponentsAndExpand(grid);
        setComponentAlignment(grid, Alignment.TOP_CENTER);
    }

    private VerticalLayout getCallDetails(Call call) {
        TextArea bigDescr = UIComponents.textArea("תיאור","100%","55");
        bigDescr.setValue(call.getDescription());
        bigDescr.addValueChangeListener(valueChangeEvent -> {
            call.setDescription(bigDescr.getValue());
            callService.updateCall(call);
        });
        TextArea bigNotes =  UIComponents.textArea("הערות","100%","55");
        bigNotes.setValue(call.getNotes());
        bigNotes.addValueChangeListener(valueChangeEvent -> {
            call.setNotes(bigNotes.getValue());
            callService.updateCall(call);
        });
        VerticalLayout layout = new VerticalLayout(bigDescr,bigNotes);
        layout.setComponentAlignment(bigNotes,Alignment.MIDDLE_CENTER);
        layout.setSpacing(false);
        layout.setMargin(false);
        return layout;
    }

    private void addHeaderLayout() {
        headerLayout = new GridLayout(4,2);
        headerLayout.setWidth("95%");
        headerLayout.setSpacing(true);
        addSelectOptionButton();
        addOptionSelectionFields();
        addNewDateField();
        addPrintButton();
        addRefreshButton();
        addNewDriverField();
        setAddButton();
        addSelectCustomerComboBox();
        addComponent(headerLayout);
        selectOption.focus();
    }

    @Override
    void setTabIndexes() {
        selectOption.setTabIndex(1);
        selectCustomer.setTabIndex(2);
        newDate.setTabIndex(3);
        newDriver.setTabIndex(4);
        print.setTabIndex(5);
        refresh.setTabIndex(6);
        grid.setTabIndex(7);
    }

    private void addSelectCustomerComboBox() {
        selectCustomer.setHeight(addButton.getHeight(),addButton.getHeightUnits());
        selectCustomer.setWidth("500");
        selectCustomer.addValueChangeListener(valueChangeEvent -> showSelectedCustomer());
        selectCustomer.addFocusListener(focusEvent ->
                addButton.setClickShortcut(ShortcutAction.KeyCode.ENTER));
        selectCustomer.addBlurListener(event -> addButton.removeClickShortcut());
        headerLayout.addComponent(selectCustomer,3,1);
        headerLayout.setComponentAlignment(selectCustomer,Alignment.MIDDLE_LEFT);
    }

    private void addNewDateField() {
        newDate = UIComponents.dateField(150,30);
        newDate.setValue(LocalDate.now().plusDays(1));
        headerLayout.addComponent(newDate,0,0);
        headerLayout.setComponentAlignment(newDate,Alignment.MIDDLE_LEFT);
    }

    private void addNewDriverField() {
        newDriver = new UIComponents().driverComboBox(generalRepository,150,30);
        headerLayout.addComponent(newDriver,0,1);
        headerLayout.setComponentAlignment(newDriver,Alignment.MIDDLE_LEFT);
    }

    private void addSelectOptionButton() {
        selectButton = UIComponents.searchButton();
        headerLayout.addComponent(selectButton,2,0);
        headerLayout.setComponentAlignment(selectButton,Alignment.MIDDLE_RIGHT);
    }

    private void setAddButton() {
        headerLayout.addComponent(addButton,2,1);
        headerLayout.setComponentAlignment(addButton,Alignment.MIDDLE_RIGHT);
        addButton.addClickListener(click -> addCall());
    }

    private void addPrintButton() {
        print = UIComponents.truckButton();
        print.addClickListener(clickEvent -> print());
        headerLayout.addComponent(print,1,0,1,0);
        headerLayout.setComponentAlignment(print,Alignment.MIDDLE_LEFT);
    }

    private void addRefreshButton() {
        refresh = UIComponents.refreshButton();
        refresh.addClickListener(clickEvent -> Page.getCurrent().reload());
        headerLayout.addComponent(refresh,1,1,1,1);
        headerLayout.setComponentAlignment(refresh,Alignment.MIDDLE_LEFT);    }

    private void addOptionSelectionFields() {
        selectOption.setEmptySelectionAllowed(false);
        selectOption.setEnabled(true);
        selectOption.setHeight(selectButton.getHeight(),selectButton.getHeightUnits());
        selectOption.setWidth("500");
        selectOption.setItems(options());
        selectOption.setValue(open);
        selectOption.setItemCaptionGenerator(Options::getHeb);
        selectOption.addValueChangeListener(ValueChangeEvent -> {
            if (selectOption.getValue()!=null) {
                removeComponent(grid);
                addGrid();
            }
        });
        headerLayout.addComponent(selectOption,3,0);
        headerLayout.setComponentAlignment(selectOption,Alignment.MIDDLE_LEFT);
    }

    private void getSelectedCustomer() {
        parametersMap.get("customer");
        String customerParameter = parametersMap.get("customer");
        List<Integer> customerList = generalRepository.getActiveId("cust");
        if((customerParameter!=null)&&(customerParameter.matches("\\d+")))
            if (customerList.contains(Integer.parseInt(customerParameter))) {
                selectCustomer.setValue(Integer.parseInt(customerParameter));
                filterCustomer.setValue(Integer.parseInt(customerParameter));
            } else {
                selectCustomer.setValue(0);
            }
    }

    private void showSelectedCustomer() {
        if ((selectCustomer.getValue()!=null)&&!(selectCustomer.getValue().toString().equals("0"))) {
            addButton.setEnabled(true);
            filterCustomer.setValue(selectCustomer.getValue());
        }
        else {
            addButton.setEnabled(false);
            filterCustomer.setValue(null);
        }
        filterId.setValue("");
    }

    private void addCall() {
        if (selectCustomer.getValue()!=null) {
            long newId=
                    callRepository.insertCall(selectCustomer.getValue(),LocalDate.now());
            selectOption.setValue(open);
            removeComponent(grid);
            addGrid();
            filterId.setValue(String.valueOf(newId));
            Page.getCurrent().open(UIPaths.EDITCALL.getPath()+String.valueOf(newId),"_new3",
                    700,700,BorderStyle.NONE);
        }
    }

    private void print() {
        Page.getCurrent().open(UIPaths.PRINT.getPath()+newDate.getValue().format(UIComponents.dateFormatter),
                        "_blank",
                        getUI().getPage().getBrowserWindowWidth(),
                        getUI().getPage().getBrowserWindowHeight(),
                        BorderStyle.NONE);
    }

}
