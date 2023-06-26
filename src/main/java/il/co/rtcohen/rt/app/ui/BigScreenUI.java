package il.co.rtcohen.rt.app.ui;

import il.co.rtcohen.rt.app.LanguageSettings;
import il.co.rtcohen.rt.app.uiComponents.CustomLabel;
import il.co.rtcohen.rt.app.uiComponents.StyleSettings;
import il.co.rtcohen.rt.dal.dao.Area;
import il.co.rtcohen.rt.dal.dao.Call;
import il.co.rtcohen.rt.dal.repositories.AreaRepository;
import il.co.rtcohen.rt.dal.repositories.CallRepository;
import il.co.rtcohen.rt.dal.repositories.UsersRepository;
import il.co.rtcohen.rt.utils.Date;
import il.co.rtcohen.rt.utils.NullPointerExceptionWrapper;

import com.vaadin.event.UIEvents;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.BorderStyle;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

// TODO: Refactor!!!

@SpringComponent
@SpringUI(path="/bigScreen")
public class BigScreenUI extends AbstractUI<HorizontalLayout> {

    private final CallRepository callRepository;

    private final Integer intervalTime;
    private final Integer rowHeight;
    private final AreaRepository areaRepository;
    private final Integer rowsPerColumn;

    @Autowired
    private BigScreenUI(ErrorHandler errorHandler,
                        UsersRepository usersRepository,
                        AreaRepository areaRepository,
                        CallRepository callRepository,
                        @Value("${settings.bigScreen.interval}") Integer intervalTime,
                        @Value("${settings.bigScreen.rowHeight}") Integer rowHeight,
                        @Value("${settings.bigScreen.rowsPerColumn}") Integer rowsPerColumn) {
        super(errorHandler, usersRepository);
        this.areaRepository = areaRepository;
        this.callRepository = callRepository;
        this.intervalTime = intervalTime;
        this.rowHeight = rowHeight;
        this.rowsPerColumn = rowsPerColumn;
    }

    @Override
    protected void setupLayout() {
        layout = new HorizontalLayout();
        layout.setDefaultComponentAlignment(Alignment.TOP_RIGHT);
        layout.setWidth("100%");
        layout.setHeight("100%");
        layout.addStyleName("custom-margins");
        layout.setMargin(new MarginInfo(false,true,true,true));
        loadData();
        setContent(layout);
        //auto refresh
        UI.getCurrent().setPollInterval(intervalTime);
        addPollListener((UIEvents.PollListener) event -> Page.getCurrent().reload());
    }

    private void loadData() {
        List<Area> areas = areaRepository.getItems(true);
        areas.sort(Comparator.comparingInt(area -> -area.getDisplayOrder()));
        String condition = getPage().getUriFragment();
        if (condition.equals("here")) {
            areas.removeIf(area -> !area.isHere());
        } else {
            areas.add(0, null);
            if (condition.equals("out")) {
                areas.removeIf(Area::isHere);
            }
        }
        for (Area area : areas) {
            List<Call> list = ((null != area && area.isHere())
                    ? callRepository.getCallsCurrentlyInTheGarage()
                    : callRepository.getOpenCallsInArea(area)
            );
            if (null != area || !list.isEmpty()) {
                layout.addComponent(getCallsGrid(list, (null == area ? "?" : area.getName())));
            }
        }
    }

    private GridLayout getCallsGrid(List<Call> list, String title) {
        // Count dates
        list.sort(Comparator.comparing(Call::getStartDate));
        list.sort(Comparator.comparing(Call::getCurrentScheduledDate));
        int datesCounter = 1;
        for (Call call : list) {
            if ((0 < list.indexOf(call)) && call.getCurrentScheduledDate().equals(list.get(list.indexOf(call) - 1).getCurrentScheduledDate())) {
                datesCounter++;
            }
        }

        // Calculate number of columns needed
        int columns = Math.max(1,(int) Math.ceil( (float) (list.size() + datesCounter) / (rowsPerColumn - 1)));

        // Init grid layout
        GridLayout gridLayout = new GridLayout(columns, rowsPerColumn+1);
        gridLayout.setWidth("100%");
        gridLayout.addStyleName("custom-margins");
        gridLayout.setDefaultComponentAlignment(Alignment.TOP_RIGHT);
        Label areaTitle = new CustomLabel(title, null, false, CustomLabel.LabelStyle.MEDIUM_TITLE);
        gridLayout.addComponent(areaTitle,columns - 1,0);

        // Populate the grid
        int x = columns - 1;
        int y = 1;
        // TODO - Fix to avoid GridLayout$OutOfBoundsException, regardless the value of rowsPerColumn in the .yaml file
        Label dateTitle;
        for (Call call : list) {
            if ((list.indexOf(call)==0) || (y==1) || (y==rowsPerColumn) ||
                    (!(call.getCurrentScheduledDate().equals(list.get(list.indexOf(call)-1).getCurrentScheduledDate())))) {
                dateTitle = new CustomLabel(null, null, false, CustomLabel.LabelStyle.MEDIUM_TEXT);
                if (call.getCurrentScheduledDate() == null || call.getCurrentScheduledDate().equals(Date.nullDate())) {
                    if ((areaTitle.getValue().equals(LanguageSettings.getLocaleString("garage")))) {
                        dateTitle.setValue(LanguageSettings.getLocaleString("currentlyHere"));
                    } else {
                        dateTitle.setValue(LanguageSettings.getLocaleString("notInSchedule"));
                    }
                } else {
                    dateTitle.setValue(call.getCurrentScheduledDate().toShortString());
                }
                if (y+2>rowsPerColumn) {
                    y=1;
                    x--;
                }
                gridLayout.addComponent(dateTitle, x, y);
                gridLayout.setComponentAlignment(dateTitle, Alignment.BOTTOM_RIGHT);
                y = y + 1;
            }
            if (y+1>rowsPerColumn) {
                y=1;
                x--;
            }
            gridLayout.addComponent(addCall(call),x,y);
            y = y + 1;
        }

        return gridLayout;
    }

    private Grid<Call> addCall(Call call) {
        Grid<Call> grid = new Grid<>();
        grid.setWidth("100%");
        grid.setRowHeight(rowHeight);
        grid.setHeightMode(HeightMode.UNDEFINED);
        grid.setItems(call);
        grid.setHeaderVisible(false);
        grid.addStyleName("bigscreen");
        grid.addComponentColumn(this::createCallLabel);
        grid.addContextClickListener(clickEvent ->
                Page.getCurrent().open(UIPaths.EDITCALL.getEditCallPath(call),
                        UIPaths.EDITCALL.getWindowName(), UIPaths.EDITCALL.getWindowWidth(), UIPaths.EDITCALL.getWindowHeight(),
                        BorderStyle.NONE));
        grid.addStyleName("custom-margins");
        grid.setStyleGenerator((StyleGenerator<Call>) StyleSettings::callStyle);
        return grid;
    }

    private Label createCallLabel(Call call) {
        Label label = new CustomLabel(getCallData(call), "100%", false, CustomLabel.LabelStyle.SMALL_TEXT);
        label.setContentMode(ContentMode.HTML);
        return label;
    }

    private String getCallData(Call call){
        String dataString = "<div align=right dir=\"rtl\"><b>"
                + NullPointerExceptionWrapper.getWrapper(call, c -> c.getStartDate().toShortString(), "")
                + "</b> <B>/</B> "
                + NullPointerExceptionWrapper.getWrapper(call, c -> c.getCustomer().getName(), "")
                + " <B>/</B> "
                + NullPointerExceptionWrapper.getWrapper(call, c -> c.getSite().getName(), "")
                + " <B>/</B> <b><u>"
                + NullPointerExceptionWrapper.getWrapper(call, c -> c.getVehicle().getVehicleType().getName(), "")
                + "</u></b>";
        if (!call.getDescription().isEmpty()) {
            dataString += " <B>/</B> " + call.getDescription();
        }
        dataString += "</div>";
        return dataString;
    }
}
