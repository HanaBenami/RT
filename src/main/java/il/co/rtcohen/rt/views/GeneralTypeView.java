package il.co.rtcohen.rt.views;

import com.vaadin.data.ValueProvider;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.Setter;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import il.co.rtcohen.rt.UIComponents;
import il.co.rtcohen.rt.dao.GeneralType;
import il.co.rtcohen.rt.repositories.GeneralRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.addons.filteringgrid.FilterGrid;

import java.util.Arrays;
import java.util.Map;

@SpringView(name = GeneralTypeView.VIEW_NAME)
public class GeneralTypeView extends AbstractDataView<GeneralType> {

    static final String VIEW_NAME = "update";
    private static Logger logger = LoggerFactory.getLogger(GeneralTypeView.class);
    private static String table;
    private TextField newName;

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
            title= getTitle();
            addHeader();
            addNewValueForm();
            addGrid(generalRepository);
        }
    }

    private void addActiveColumn() {
        FilterGrid.Column<GeneralType, Component> activeColumn =
                grid.addComponentColumn((ValueProvider<GeneralType, Component>) generalType ->
                        UIComponents.checkBox(generalType.getActive(), true));
        activeColumn.setId("activeColumn").setExpandRatio(1).setResizable(false).setWidth(70).setSortable(false);
        activeColumn.setEditorBinding(grid.getEditor().getBinder().forField(new CheckBox()).bind(
                (ValueProvider<GeneralType, Boolean>) GeneralType::getActive,
                (Setter<GeneralType, Boolean>) (generalType, Boolean) -> {
                    generalType.setActive(Boolean);
                    generalRepository.update(generalType);
                }));
        grid.getDefaultHeaderRow().getCell("activeColumn").setText("פעיל");
        CheckBox filterActive = UIComponents.checkBox(true);
        activeColumn.setFilter(UIComponents.BooleanValueProvider(),
                filterActive, UIComponents.BooleanPredicate());
    }

    private void addNameColumn() {
        FilterGrid.Column<GeneralType, String> nameColumn =
                grid.addColumn(GeneralType::getName).setId("nameColumn")
                        .setEditorComponent(new TextField(), (generalType, String) -> {
                            generalType.setName(String);
                            generalRepository.update(generalType);
                        })
                        .setExpandRatio(1).setResizable(false).setMinimumWidth(230);
        grid.getDefaultHeaderRow().getCell("nameColumn").setText("שם");
        TextField filterName = UIComponents.textField(30);
        nameColumn.setFilter(filterName, UIComponents.stringFilter());
        filterName.setWidth("95%");
    }

    private void addIdColumn() {
        FilterGrid.Column<GeneralType, Integer> idColumn = grid.addColumn(GeneralType::getId).setId("idColumn")
                .setWidth(70).setResizable(false);
        grid.getEditor().setEnabled(true);
        grid.getDefaultHeaderRow().getCell("idColumn").setText("#");
        TextField filterId = UIComponents.textField(30);
        idColumn.setFilter(filterId, UIComponents.integerFilter());
        filterId.setWidth("95%");
    }

    private void addColumns() {
        addActiveColumn();
        addNameColumn();
        addIdColumn();
    }

    private void addGrid(GeneralRepository repository) {
        initGrid("v-align-right");
        grid.setItems(repository.getNames(table));
        addColumns();
        grid.sort("nameColumn");
        grid.setWidthUndefined();
        //grid.setWidth("370");
        addComponentsAndExpand(grid);
    }

    private String getTitle() {
        switch (table) {
            case "calltype": {return "סוגי קריאות";}
            case "custtype": {return "סוגי לקוחות";}
            case "cartype": {return "סוגי כלים";}
            case "driver": {return "רשימת נהגים";}
            default: {table=""; return "שגיאה בבחירת טבלה";}
        }
    }

    private void addNewValueForm() {
        HorizontalLayout formLayout = new HorizontalLayout();
        formLayout.setWidth("370");
        formLayout.addComponent(addButton);
        newName = super.addNewNameField();
        formLayout.addComponentsAndExpand(newName);
        addComponent(formLayout);
        addButton.addClickListener(click -> addNewValue());
        newName.setTabIndex(1);
        addButton.setTabIndex(2);
    }

    private void addNewValue() {
        if (!newName.getValue().isEmpty()) {
            generalRepository.insertName(newName.getValue(), table);
            newName.setValue("");
            newName.focus();
            removeComponent(grid);
            addGrid(generalRepository);
        }
    }
}
