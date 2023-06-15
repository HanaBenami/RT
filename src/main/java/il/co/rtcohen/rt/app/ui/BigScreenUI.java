package il.co.rtcohen.rt.app.ui;

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
import il.co.rtcohen.rt.app.LanguageSettings;
import il.co.rtcohen.rt.app.uiComponents.StyleSettings;
import il.co.rtcohen.rt.app.uiComponents.UIComponents;
import il.co.rtcohen.rt.app.views.CustomerDataView;
import il.co.rtcohen.rt.dal.dao.Area;
import il.co.rtcohen.rt.dal.dao.Call;
import il.co.rtcohen.rt.dal.repositories.UsersRepository;
import il.co.rtcohen.rt.utils.Date;
import il.co.rtcohen.rt.dal.repositories.AreasRepository;
import il.co.rtcohen.rt.dal.repositories.CallRepository;
import il.co.rtcohen.rt.dal.repositories.GeneralRepository;
import il.co.rtcohen.rt.utils.NullPointerExceptionWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// TODO: Refactor!!!

@SpringComponent
@SpringUI(path="/bigScreen")
public class BigScreenUI extends AbstractUI<HorizontalLayout> {
    private static final Logger logger = LoggerFactory.getLogger(CustomerDataView.class);

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM");
    private Integer intervalTime;
    private Integer rowHeight;
    private AreasRepository areasRepository;
    private Integer rowsPerColumn;

    @Autowired
    private BigScreenUI(ErrorHandler errorHandler, CallRepository callRepository,
                        GeneralRepository generalRepository, AreasRepository areasRepository,
                        UsersRepository usersRepository,
                        @Value("${settings.bigScreen.interval}") Integer intervalTime,
                        @Value("${settings.bigScreen.rowHeight}") Integer rowHeight,
                        @Value("${settings.bigScreen.rowsPerColumn}") Integer rowsPerColumn) {
        super(errorHandler,callRepository,generalRepository, usersRepository);
        this.areasRepository=areasRepository;
        this.intervalTime=intervalTime;
        this.rowHeight=rowHeight;
        this.rowsPerColumn=rowsPerColumn;
    }

    @Override
    protected void setupLayout() throws SQLException {
        layout = new HorizontalLayout();
        layout.setDefaultComponentAlignment(Alignment.TOP_RIGHT);
        layout.setWidth("100%");
        layout.setHeight("100%");
        loadData();
        setContent(layout);
        layout.addStyleName("custom-margins");
        //auto refresh
        UI.getCurrent().setPollInterval(intervalTime);
        addPollListener((UIEvents.PollListener) event -> Page.getCurrent().reload());
    }

    private void loadData() throws SQLException {
        List <Integer> areas = new ArrayList<>();
        String condition = getPage().getUriFragment();
        if (!condition.equals("here"))
            areas.addAll(areasRepository.getOutAreaId());
        if (!condition.equals("out"))
            areas.addAll(areasRepository.getHereAreaId());

        //sort by display order
        areas.sort((o1, o2) -> {
            if (areasRepository.getAreaById(o1).getDisplayOrder() >
                    areasRepository.getAreaById(o2).getDisplayOrder())
                return -1 ;
            else if (areasRepository.getAreaById(o1).getDisplayOrder() == areasRepository.getAreaById(o2).getDisplayOrder())
                return 0 ;
            else
                return 1;
        });

        //add data per area
        layout.setMargin(new MarginInfo(false,true,true,true));
        for (Integer area : areas) {
            layout.addComponent(initAreaLayout(area));
        }
    }

    private GridLayout initAreaLayout(int areaId) throws SQLException {
        Area area = areasRepository.getItem(areaId);
        List<Call> list = (area.isHere()
                        ? callRepository.getCallsCurrentlyInTheGarage()
                        : callRepository.getOpenCallsInArea(area)
        );

        int datesCounter = 1;
        for (Call call : list)
            if ((list.indexOf(call)>0)&&(!(call.getCurrentScheduledDate().equals(list.get(list.indexOf(call)-1).getCurrentScheduledDate()))))

                    datesCounter++;
        int columns = Math.max(1,(int) Math.ceil( (float) (list.size()+datesCounter) / (rowsPerColumn - 1)));
        GridLayout areaLayout = new GridLayout(columns, rowsPerColumn+1);
        areaLayout.setWidth("100%");
        areaLayout.setDefaultComponentAlignment(Alignment.TOP_RIGHT);
        Label areaTitle = UIComponents.label(generalRepository
                .getNameById(areaId,"area"),"LABEL");
        areaLayout.addComponent(areaTitle,columns-1,0);
        Label dateTitle;
        LocalDate nullDate = Date.nullDate().getLocalDate();
        int x = columns-1;
        int y = 1;
        // TODO - Fix to avoid GridLayout$OutOfBoundsException, regardless the value of rowsPerColumn in the .yaml file
        for (Call call : list){
            if ((list.indexOf(call)==0) || (y==1) || (y==rowsPerColumn) ||
                    (!(call.getCurrentScheduledDate().equals(list.get(list.indexOf(call)-1).getCurrentScheduledDate())))) {
                dateTitle = UIComponents.label("LABEL-BIGSCREEN");
                if (call.getCurrentScheduledDate() == null) {
                    if ((areaTitle.getValue().equals(LanguageSettings.getLocaleString("garage"))))
                        dateTitle.setValue(LanguageSettings.getLocaleString("currentlyHere"));
                    else
                        dateTitle.setValue(LanguageSettings.getLocaleString("notInSchedule"));
                } else {
                    dateTitle.setValue(call.getCurrentScheduledDate().toString());
                }
                if (y+2>rowsPerColumn) {
                    y=1;
                    x--;
                }
                areaLayout.addComponent(dateTitle, x, y);
                areaLayout.setComponentAlignment(dateTitle,Alignment.BOTTOM_RIGHT);
                y = y + 1;
            }
            if (y+1>rowsPerColumn) {
                y=1;
                x--;
            }
            areaLayout.addComponent(addCall(call),x,y);
            y = y + 1;
        }
        areaLayout.addStyleName("custom-margins");
        return areaLayout;
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
        Label data = new Label(getCallData(call), ContentMode.HTML);
        data.setStyleName("LABEL-SMALL");
        data.setWidth("100%");
        return data;
    }

    private String getCallData(Call call){
            String dataString = "<div align=right dir=\"rtl\"><b>"
                    + NullPointerExceptionWrapper.getWrapper(call, c -> c.getStartDate().toString(), "")
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
