package il.co.rtcohen.rt.views;

import com.vaadin.data.ValueProvider;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.Setter;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import il.co.rtcohen.rt.UIcomponents;
import il.co.rtcohen.rt.dao.GeneralType;
import il.co.rtcohen.rt.repositories.GeneralRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.addons.filteringgrid.FilterGrid;
import org.vaadin.addons.filteringgrid.filters.InMemoryFilter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SpringView(name = GeneralTypeView.VIEW_NAME)
public class GeneralTypeView extends AbstractDataView {
    static final String VIEW_NAME = "update";
    private static Logger logger = LoggerFactory.getLogger(GeneralTypeView.class);
    private static String table;
    private TextField newName;
    private FilterGrid<GeneralType> grid;

    @Autowired
    private GeneralTypeView(ErrorHandler errorHandler, GeneralRepository generalRepository) {
        super(errorHandler, generalRepository);
    }

    @Override
    public void createView(ViewChangeListener.ViewChangeEvent event) {
        Map<String, String> parametersMap = event.getParameterMap();
        logger.info("Parameters map  " + Arrays.toString(parametersMap.entrySet().toArray()));
        String tableName = parametersMap.get("table");
        if(tableName!=null && !tableName.isEmpty()) {
            table = tableName;
            title=setTitle();
            addHeader();
            addForm();
            addGrid(generalRepository);
        }
    }

    private void activeColumn() {
        FilterGrid.Column activeColumn =
                grid.addComponentColumn((ValueProvider<GeneralType, Component>) generalType ->
                        UIcomponents.checkBox(generalType.getActive(), true));
        activeColumn.setId("activeColumn").setExpandRatio(1).setResizable(false).setWidth(70);
        activeColumn.setEditorBinding(grid.getEditor().getBinder().forField(new CheckBox()).bind(
                (ValueProvider<GeneralType, Boolean>) GeneralType::getActive,
                (Setter<GeneralType, Boolean>) (generalType, Boolean) -> {
                    generalType.setActive(Boolean);
                    generalRepository.update(generalType);
                }));
        grid.getDefaultHeaderRow().getCell("activeColumn").setText("פעיל");
        CheckBox filterActive = UIcomponents.checkBox(true);
        activeColumn.setFilter(UIcomponents.BooleanValueProvider(),
                filterActive, UIcomponents.BooleanPredicate());
    }

    private void nameColumn() {
        FilterGrid.Column<GeneralType, String> nameColumn =
                grid.addColumn(GeneralType::getName).setId("nameColumn")
                        .setEditorComponent(new TextField(), (generalType, String) -> {
                            generalType.setName(String);
                            generalRepository.update(generalType);
                        })
                        .setExpandRatio(1).setResizable(false).setWidth(230);
        grid.getDefaultHeaderRow().getCell("nameColumn").setText("שם");
        TextField filterName = UIcomponents.textField(30);
        nameColumn.setFilter(filterName, UIcomponents.stringFilter());
        filterName.setWidth("95%");
    }

    private void idColumn() {
        FilterGrid.Column<GeneralType, Integer> idColumn = grid.addColumn(GeneralType::getId).setId("idColumn")
                .setWidth(70).setResizable(false);
        grid.getEditor().setEnabled(true);
        grid.getDefaultHeaderRow().getCell("idColumn").setText("#");
        TextField filterId = UIcomponents.textField(30);
        idColumn.setFilter(filterId, UIcomponents.textFilter());
        filterId.setWidth("95%");
    }

    private void addColumns() {
        activeColumn();
        nameColumn();
        idColumn();
    }

    private void addGrid(GeneralRepository repository) {
        grid = UIcomponents.myGrid("v-align-right");
        grid.setItems(repository.getNames(table));
        addColumns();
        grid.sort("nameColumn");
        dataGrid=grid;
        dataGrid.setWidth("370");
        addComponentsAndExpand(dataGrid);
    }

    private String setTitle() {
        switch (table) {
            case "calltype": {return "סוגי קריאות";}
            case "custtype": {return "סוגי לקוחות";}
            case "cartype": {return "סוגי כלים";}
            case "driver": {return "רשימת נהגים";}
            default: {table=""; return "שגיאה בבחירת טבלה";}
        }
    }

    private void addForm() {
        HorizontalLayout formLayout = new HorizontalLayout();
        formLayout.setWidth("370");
        formLayout.addComponent(addButton);
        newName = super.newName();
        formLayout.addComponentsAndExpand(newName);
        addComponent(formLayout);
        addButton.addClickListener(click -> addClick());
        newName.setTabIndex(1);
        addButton.setTabIndex(2);
    }

    private void addClick() {
        if (!newName.getValue().isEmpty()) {
            generalRepository.insertName(newName.getValue(), table);
            newName.setValue("");
            newName.focus();
            removeComponent(dataGrid);
            addGrid(generalRepository);
        }
    }
}
