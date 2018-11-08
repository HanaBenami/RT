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
    public static final String VIEW_NAME = "update";
    private static Logger logger = LoggerFactory.getLogger(GeneralTypeView.class);
    private static String table;

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

    private void addGrid(GeneralRepository repository) {

        FilterGrid<GeneralType> grid = UIcomponents.myGrid("v-align-right");

        //data
        List<GeneralType> list = repository.getNames(table);
        grid.setItems(list);
        TextField name = new TextField();
        CheckBox active = new CheckBox();

        //columns

        //active
        FilterGrid.Column activeColumn =
        grid.addComponentColumn((ValueProvider<GeneralType, Component>) generalType ->
                UIcomponents.checkBox(generalType.getActive(),true));
        activeColumn.setId("activeColumn").setExpandRatio(1).setResizable(false).setWidth(70);
        activeColumn.setEditorBinding(grid.getEditor().getBinder().forField(active).bind(
                (ValueProvider<GeneralType, Boolean>) GeneralType::getActive,
                (Setter<GeneralType, Boolean>) (generalType, Boolean) -> {
                    generalType.setActive(Boolean);
                    generalRepository.update(generalType);
                }));
        grid.getDefaultHeaderRow().getCell("activeColumn").setText("פעיל");
        CheckBox filterActive = UIcomponents.checkBox(true);
        activeColumn.setFilter(UIcomponents.BooleanValueProvider(),
                filterActive, UIcomponents.BooleanPredicate());

        //name
        FilterGrid.Column<GeneralType, String> nameColumn =
                grid.addColumn(GeneralType::getName).setId("nameColumn")
                .setEditorComponent(name,(generalType,String) -> {
                    generalType.setName(String);
                    generalRepository.update(generalType);
                })
                .setExpandRatio(1).setResizable(false).setWidth(230);
        grid.getDefaultHeaderRow().getCell("nameColumn").setText("שם");
        TextField filterName = UIcomponents.textField(30);
        nameColumn.setFilter(filterName, InMemoryFilter.StringComparator.containsIgnoreCase());
        filterName.setWidth("95%");

        //id
        FilterGrid.Column<GeneralType, Integer> idColumn = grid.addColumn(GeneralType::getId).setId("idColumn")
                .setWidth(70).setResizable(false);
        grid.getEditor().setEnabled(true);
        grid.getDefaultHeaderRow().getCell("idColumn").setText("#");
        TextField filterId = UIcomponents.textField(30);
        idColumn.setFilter(filterId, InMemoryFilter.StringComparator.containsIgnoreCase());
        filterId.setWidth("95%");

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

        Button addButton = UIcomponents.addButton();
        addButton.setEnabled(false);
        formLayout.addComponent(addButton);

        TextField newName = new TextField();
        newName.focus();
        newName.addFocusListener(focusEvent -> {
            addButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        });
        newName.addBlurListener(event -> {
            addButton.removeClickShortcut();
        });
        newName.addValueChangeListener(valueChangeEvent -> {
            if (newName.getValue().isEmpty())
                addButton.setEnabled(false);
            else
                addButton.setEnabled(true);
        });
        formLayout.addComponentsAndExpand(newName);

        addComponent(formLayout);

        addButton.addClickListener(click -> {
            if (!newName.getValue().isEmpty()) {
                generalRepository.insertName(newName.getValue(), table);
                newName.setValue("");
                newName.focus();
                removeComponent(dataGrid);
                addGrid(generalRepository);
            }
        });

        newName.setTabIndex(1);
        addButton.setTabIndex(2);

    }

}
