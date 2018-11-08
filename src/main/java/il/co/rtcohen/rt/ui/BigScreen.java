package il.co.rtcohen.rt.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.event.UIEvents;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import il.co.rtcohen.rt.UIcomponents;
import il.co.rtcohen.rt.dao.Call;
import il.co.rtcohen.rt.repositories.AreaRepository;
import il.co.rtcohen.rt.repositories.CallRepository;
import il.co.rtcohen.rt.repositories.GeneralRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@UIScope
@SpringComponent
@SpringUI(path="/bigscreen")
@Theme("myTheme")
public class BigScreen extends AbstractUI {

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM");
    private HorizontalLayout layout;
    private Integer intervalTime;
    private AreaRepository areaRepository;

    @Autowired
    private BigScreen(ErrorHandler errorHandler,CallRepository callRepository, GeneralRepository generalRepository, AreaRepository areaRepository,@Value("${settings.bigScreenInterval}") Integer intervalTime) {
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
        addData();
        setContent(layout);
        layout.addStyleName("custom-margins");

        //auto refresh
        UI.getCurrent().setPollInterval(intervalTime);
        addPollListener((UIEvents.PollListener) event -> Page.getCurrent().reload());
    }

    private void addData () {
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
            else
                return 1;
        });

        //add data per area
        for (Integer area : areas) {
            layout.addComponent(areaLayout(area));
        }
    }

    private VerticalLayout areaLayout (int area) {
        VerticalLayout areaLayout = new VerticalLayout();
        areaLayout.setDefaultComponentAlignment(Alignment.TOP_RIGHT);
        Label areaTitle = UIcomponents.label(generalRepository.getNameById(area,"area"),"LABEL");
        areaLayout.addComponent(areaTitle);
        List<Call> list = callRepository.getOpenCallsPerArea(area);
        Label dateTitle;
        LocalDate nullDate = Call.nullDate;
        for (int i=0;i<list.size();i++)
            if((i==0)||(!(list.get(i).getDate2().equals(list.get(i-1).getDate2())))) {
                dateTitle = UIcomponents.label("LABEL-BIGSCREEN");
                if (list.get(i).getDate2().equals(nullDate)) {
                    if((areaTitle.getValue().equals("מוסך")))
                        dateTitle.setValue("בטיפול כאן");
                    else
                        dateTitle.setValue("טרם שובצו");
                } else {
                    dateTitle.setValue(list.get(i).getDate2().format(dateFormatter));
                }
                areaLayout.addComponent(dateTitle);
                areaLayout.addComponent(dateLayout(list,list.get(i).getDate2()));
            }
        areaLayout.addStyleName("custom-margins");
        return areaLayout;
    }

    private Grid dateLayout(List<Call> list, LocalDate date) {
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
        Grid.Column<Call, Label> dataColumn = grid.addComponentColumn(call -> {
            Label data = new Label(callData(call), ContentMode.HTML);
            data.setStyleName("LABEL-SMALL");
            data.setWidth("100%");
            return data;
            });

        //style
        grid.addStyleName("custom-margins");
        grid.setStyleGenerator((StyleGenerator<Call>) UIcomponents::callStyle);
        return grid;
    }

    private String callData (Call call){
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
