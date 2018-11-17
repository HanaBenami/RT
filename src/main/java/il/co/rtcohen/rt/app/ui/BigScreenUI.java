package il.co.rtcohen.rt.app.ui;

import com.vaadin.event.UIEvents;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import il.co.rtcohen.rt.app.LanguageSettings;
import il.co.rtcohen.rt.app.UIComponents;
import il.co.rtcohen.rt.dal.dao.Call;
import il.co.rtcohen.rt.dal.repositories.AreaRepository;
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
    private AreaRepository areaRepository;

    @Autowired
    private BigScreenUI(ErrorHandler errorHandler, CallRepository callRepository, GeneralRepository generalRepository, AreaRepository areaRepository, @Value("${settings.bigScreenInterval}") Integer intervalTime) {
        super(errorHandler,callRepository,generalRepository);
        this.areaRepository=areaRepository;
        this.intervalTime=intervalTime;
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
            areas.addAll(areaRepository.getOutAreaId());
        if (!condition.equals("out"))
            areas.addAll(areaRepository.getHereAreaId());

        //sort by display order
        areas.sort((o1, o2) -> {
            if (areaRepository.getAreaById(o1).getDisplayOrder() >
                    areaRepository.getAreaById(o2).getDisplayOrder())
                return -1 ;
            else if (areaRepository.getAreaById(o1).getDisplayOrder().equals(areaRepository.getAreaById(o2).getDisplayOrder()))
                return 0 ;
            else
                return 1;
        });

        //add data per area
        for (Integer area : areas) {
            layout.addComponent(initAreaLayout(area));
        }
    }

    private VerticalLayout initAreaLayout(int area) {
        VerticalLayout areaLayout = new VerticalLayout();
        areaLayout.setDefaultComponentAlignment(Alignment.TOP_RIGHT);
        Label areaTitle = UIComponents.label(generalRepository.getNameById(area,"area"),"LABEL");
        areaLayout.addComponent(areaTitle);
        List<Call> list = callRepository.getOpenCallsPerArea(area);
        Label dateTitle;
        LocalDate nullDate = Call.nullDate;
        for (int i=0;i<list.size();i++)
            if((i==0)||(!(list.get(i).getDate2().equals(list.get(i-1).getDate2())))) {
                dateTitle = UIComponents.label("LABEL-BIGSCREEN");
                if (list.get(i).getDate2().equals(nullDate)) {
                    if((areaTitle.getValue().equals(LanguageSettings.getLocaleString("garage"))))
                        dateTitle.setValue(LanguageSettings.getLocaleString("currentlyHere"));
                    else
                        dateTitle.setValue(LanguageSettings.getLocaleString("notInSchedule"));
                } else {
                    dateTitle.setValue(list.get(i).getDate2().format(dateFormatter));
                }
                areaLayout.addComponent(dateTitle);
                areaLayout.addComponent(addDataPerDate(list,list.get(i).getDate2()));
            }
        areaLayout.addStyleName("custom-margins");
        return areaLayout;
    }

    private Grid addDataPerDate(List<Call> list, LocalDate date) {
        Grid<Call> grid = new Grid<>();
        grid.setWidth("100%");
        List<Call> dateList = new ArrayList<>();
        for (Call call : list)
            if (call.getDate2().equals(date))
                dateList.add(call);
        grid.setItems(dateList);
        grid.setHeaderVisible(false);
        grid.setHeightByRows(dateList.size());
        grid.addStyleName("bigscreen");
        grid.addComponentColumn(this::createCallLabel);
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
            dataString+="</p>";
            return dataString;
    }

}
