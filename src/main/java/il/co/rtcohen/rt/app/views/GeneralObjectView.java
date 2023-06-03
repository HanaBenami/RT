package il.co.rtcohen.rt.app.views;

import com.vaadin.data.ValueProvider;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.Setter;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import il.co.rtcohen.rt.app.LanguageSettings;
import il.co.rtcohen.rt.app.UIComponents;
import il.co.rtcohen.rt.dal.dao.GeneralObject;
import il.co.rtcohen.rt.dal.repositories.GeneralRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.addons.filteringgrid.FilterGrid;

import java.util.Arrays;
import java.util.Map;

@Deprecated
@SpringView(name = GeneralTypeView.VIEW_NAME)
public class GeneralTypeView extends AbstractDataViewSingleObject<GeneralObject> {

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
            addGrid();
            setTabIndexes();
        }
    }

    private void addActiveColumn() {
        FilterGrid.Column<GeneralObject, Component> activeColumn =
                grid.addComponentColumn((ValueProvider<GeneralObject, Component>) generalType ->
                        UIComponents.checkBox(generalType.isActive(), true));
        activeColumn.setId("activeColumn").setExpandRatio(1).setResizable(false).setWidth(70).setSortable(false);
        activeColumn.setEditorBinding(grid.getEditor().getBinder().forField(new CheckBox()).bind(
                (ValueProvider<GeneralObject, Boolean>) GeneralObject::isActive,
                (Setter<GeneralObject, Boolean>) (generalType, Boolean) -> {
                    generalType.setActive(Boolean);
                    generalRepository.update(generalType);
                }));
        grid.getDefaultHeaderRow().getCell("activeColumn").setText(LanguageSettings.getLocaleString("active"));
        CheckBox filterActive = UIComponents.checkBox(true);
        activeColumn.setFilter(UIComponents.BooleanValueProvider(),
                filterActive, UIComponents.BooleanPredicate());
    }

    private void addNameColumn() {
        FilterGrid.Column<GeneralObject, String> nameColumn =
                grid.addColumn(GeneralObject::getName).setId("nameColumn")
                        .setEditorComponent(new TextField(), (generalType, String) -> {
                            generalType.setName(String);
                            generalRepository.update(generalType);
                        })
                        .setExpandRatio(1).setResizable(false).setMinimumWidth(230);
        grid.getDefaultHeaderRow().getCell("nameColumn").setText(LanguageSettings.getLocaleString("name"));
        TextField filterName = UIComponents.textField(30);
        nameColumn.setFilter(filterName, UIComponents.stringFilter());
        filterName.setWidth("95%");
    }

    private void addIdColumn() {
        FilterGrid.Column<GeneralObject, Integer> idColumn = grid.addColumn(GeneralObject::getId).setId("idColumn")
                .setWidth(70).setResizable(false);
        grid.getEditor().setEnabled(true);
        grid.getDefaultHeaderRow().getCell("idColumn").setText(LanguageSettings.getLocaleString("id"));
        TextField filterId = UIComponents.textField(30);
        idColumn.setFilter(filterId, UIComponents.integerFilter());
        filterId.setWidth("95%");
    }

    @Override
    void addColumns() {
        addActiveColumn();
        addNameColumn();
        addIdColumn();
    }

    @Override
    void addGrid() {
        initGrid("v-align-right");
        grid.setItems(generalRepository.getNames(table));
        addColumns();
        grid.sort("nameColumn");
        grid.setWidthUndefined();
        addComponentsAndExpand(grid);
    }

    private String getTitle() {
        switch (table) {
            case "calltype": {return LanguageSettings.getLocaleString("callTypeTitle");}
            case "custtype": {return LanguageSettings.getLocaleString("customerTypeTitle");}
            case "cartype": {return LanguageSettings.getLocaleString("carTypeTitle");}
            case "driver": {return LanguageSettings.getLocaleString("driversTitle");}
            case "users": {return LanguageSettings.getLocaleString("usersTitle");}
            default: {table=""; return LanguageSettings.getLocaleString("tableNameError");}
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
    }

    @Override
    void setTabIndexes() {
        newName.setTabIndex(1);
        grid.setTabIndex(2);
    }

    private void addNewValue() {
        if (!newName.getValue().isEmpty()) {
            generalRepository.insertName(newName.getValue(), table);
            newName.setValue("");
            newName.focus();
            grid.setItems(generalRepository.getNames(table));
        }
    }
}
