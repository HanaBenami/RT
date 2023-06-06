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
import il.co.rtcohen.rt.app.UiComponents.UIComponents;
import il.co.rtcohen.rt.dal.dao.Call;
import il.co.rtcohen.rt.dal.repositories.AreasRepository;
import il.co.rtcohen.rt.dal.repositories.CallRepository;
import il.co.rtcohen.rt.dal.repositories.GeneralRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@SpringComponent
@SpringUI(path="/bigScreen")
public class BigScreenUI extends AbstractUI<HorizontalLayout> {

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM");
    private Integer intervalTime;
    private Integer rowHeight;
    private AreasRepository areasRepository;
    private Integer rowsPerColumn;

    @Autowired
    private BigScreenUI(ErrorHandler errorHandler, CallRepository callRepository,
                        GeneralRepository generalRepository, AreasRepository areasRepository,
                        @Value("${settings.bigScreen.interval}") Integer intervalTime,
                        @Value("${settings.bigScreen.rowHeight}") Integer rowHeight,
                        @Value("${settings.bigScreen.rowsPerColumn}") Integer rowsPerColumn) {
        super(errorHandler,callRepository,generalRepository);
        this.areasRepository=areasRepository;
        this.intervalTime=intervalTime;
        this.rowHeight=rowHeight;
        this.rowsPerColumn=rowsPerColumn;
    }

    @Override
    protected void setupLayout() {
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

    private void loadData() {
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

    private GridLayout initAreaLayout(int area) {
        List<Call> list = callRepository.getOpenCallsPerArea(area);
        int datesCounter = 1;
        for (Call call : list)
            if ((list.indexOf(call)>0)&&(!(call.getDate2().equals(list.get(list.indexOf(call)-1).getDate2()))))

                    datesCounter++;
        int columns = Math.max(1,(int) Math.ceil( (float) (list.size()+datesCounter) / (rowsPerColumn - 1)));
        GridLayout areaLayout = new GridLayout(columns, rowsPerColumn+1);
        areaLayout.setWidth("100%");
        areaLayout.setDefaultComponentAlignment(Alignment.TOP_RIGHT);
        Label areaTitle = UIComponents.label(generalRepository
                .getNameById(area,"area"),"LABEL");
        areaLayout.addComponent(areaTitle,columns-1,0);
        Label dateTitle;
        LocalDate nullDate = Call.nullDate;
        int x = columns-1;
        int y = 1;
        for (Call call : list){
            if ((list.indexOf(call)==0) || (y==1) || (y==rowsPerColumn) ||
                    (!(call.getDate2().equals(list.get(list.indexOf(call)-1).getDate2())))) {
                dateTitle = UIComponents.label("LABEL-BIGSCREEN");
                if (call.getDate2().equals(nullDate)) {
                    if ((areaTitle.getValue().equals(LanguageSettings.getLocaleString("garage"))))
                        dateTitle.setValue(LanguageSettings.getLocaleString("currentlyHere"));
                    else
                        dateTitle.setValue(LanguageSettings.getLocaleString("notInSchedule"));
                } else {
                    dateTitle.setValue(call.getDate2().format(dateFormatter));
                }
                if (y+2>rowsPerColumn) {
                    y=1;
                    x--;
                }
                areaLayout.addComponent(dateTitle,x,y);
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
                Page.getCurrent().open(UIPaths.EDITCALL.getPath() + call.getId(),
                        "_new3",750,770, BorderStyle.NONE));
        grid.addStyleName("custom-margins");
        grid.setStyleGenerator((StyleGenerator<Call>) UIComponents::callStyle);
        return grid;
    }

    private Label createCallLabel(Call call) {
        Label data = new Label(getCallData(call), ContentMode.HTML);
        data.setStyleName("LABEL-SMALL");
        data.setWidth("100%");
        return data;
    }

    private String getCallData(Call call){
            String dataString =
                    "<div align=right dir=\"rtl\"><b>"
                            +(call.getStartDate().format(dateFormatter))+"</b> <B>/</B> "
                            +(generalRepository.getNameById(call.getCustomerId(),"cust"));
            if (!(generalRepository.getNameById(call.getSiteId(),"site").equals("")))
                dataString+=" <B>/</B> "+(generalRepository.getNameById(call.getSiteId(),"site"));
            if (!(generalRepository.getNameById(call.getCarTypeId(),"cartype").equals("")))
                dataString+=" <B>/</B> <b><u>"+(generalRepository.getNameById(call.getCarTypeId(),"cartype"))+"</u></b>";
            if (!(call.getDescription().equals("")))
                dataString+=" <B>/</B> "+(call.getDescription());
            dataString+="</div>";
            return dataString;
    }

}
