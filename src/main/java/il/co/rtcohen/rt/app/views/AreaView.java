package il.co.rtcohen.rt.app.views;

import com.vaadin.data.ValueProvider;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.Setter;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import il.co.rtcohen.rt.app.LanguageSettings;
import il.co.rtcohen.rt.app.UIComponents;
import il.co.rtcohen.rt.dal.dao.Area;
import il.co.rtcohen.rt.dal.dao.GeneralType;
import il.co.rtcohen.rt.dal.repositories.AreaRepository;
import il.co.rtcohen.rt.dal.repositories.GeneralRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.addons.filteringgrid.FilterGrid;

import java.util.List;

@SpringView(name = AreaView.VIEW_NAME)
public class AreaView extends AbstractDataView<Area> {
    static final String VIEW_NAME = "area";
    private AreaRepository areaRepository;
    private TextField newName;
    private TextField displayOrder;

    @Autowired
    private AreaView(ErrorHandler errorHandler, AreaRepository areaRepository, GeneralRepository generalRepository) {
        super(errorHandler,generalRepository);
        this.areaRepository=areaRepository;
        this.generalRepository=generalRepository;
    }

    @Override
    public void createView(ViewChangeListener.ViewChangeEvent event) {
        title=LanguageSettings.getLocaleString("areaTitle");
        addHeader();
        addNewAreaFields();
        addGrid();
        setTabIndexes();
    }

    private void addHereColumn() {
        FilterGrid.Column<Area, Component> hereColumn = grid.addComponentColumn((ValueProvider<Area, Component>) area ->
                UIComponents.checkBox(area.getHere(), true));
        hereColumn.setId("hereColumn").setExpandRatio(1).setResizable(false).setWidth(70);
        hereColumn.setEditorBinding(grid.getEditor().getBinder().forField(new CheckBox()).bind(
                (ValueProvider<Area, Boolean>) Area::getHere,
                (Setter<Area, Boolean>) (area, Boolean) -> {
                    area.setHere(Boolean);
                    areaRepository.updateArea(area);
                }));
        hereColumn.setFilter(UIComponents.BooleanValueProvider(),
                UIComponents.checkBox(false), UIComponents.BooleanPredicateWithShowAll());
        grid.getDefaultHeaderRow().getCell("hereColumn").setText(LanguageSettings.getLocaleString("here"));
    }
    private void addActiveColumn() {
        FilterGrid.Column<Area, Component> activeColumn =
                grid.addComponentColumn((ValueProvider<Area, Component>) area ->
                        UIComponents.checkBox(area.getActive(),true));
        activeColumn.setId("activeColumn").setExpandRatio(1).setResizable(false).setWidth(70).setSortable(false);
        activeColumn.setEditorBinding(grid.getEditor().getBinder().forField(new CheckBox()).bind(
                (ValueProvider<Area, Boolean>) GeneralType::getActive,
                (Setter<Area, Boolean>) (area, Boolean) -> {
                    area.setActive(Boolean);
                    generalRepository.update(area);
                }));
        grid.getDefaultHeaderRow().getCell("activeColumn").setText(LanguageSettings.getLocaleString("active"));
        CheckBox filterActive = UIComponents.checkBox(true);
        activeColumn.setFilter(UIComponents.BooleanValueProvider(),
                filterActive, UIComponents.BooleanPredicate());
    }
    private void addDisplayOrderColumn() {
        displayOrder = new TextField();
        FilterGrid.Column<Area, Integer> displayOrderColumn =
                grid.addColumn(Area::getDisplayOrder).setId("displayOrderColumn")
                        .setEditorBinding(grid.getEditor().getBinder().forField(displayOrder).bind(
                                (ValueProvider<Area, String>) area -> String.valueOf(area.getDisplayOrder()),
                                (Setter<Area, String>) this::setDisplayOrder))
                        .setExpandRatio(1).setResizable(false).setWidth(60);
        displayOrderColumn.setStyleGenerator(area -> {
            if(area.getDisplayOrder()==0) return "null"; else return "bold" ;});
        grid.getDefaultHeaderRow().getCell("displayOrderColumn").setText(LanguageSettings.getLocaleString("order"));
        TextField filterDisplay = UIComponents.textField("95%","30");
        displayOrderColumn.setFilter(filterDisplay, UIComponents.integerFilter());
        filterDisplay.setWidth("95%");
    }
    private void setDisplayOrder(Area area, String string) {
        if (string.matches("\\d+")) {
            area.setDisplayOrder(Integer.parseInt(string));
            areaRepository.updateArea(area);
        }
        else
            displayOrder.setValue(area.getDisplayOrder().toString());
    }
    private void addNameColumn() {
        TextField name = new TextField();
        FilterGrid.Column<Area, String> nameColumn =
                grid.addColumn(Area::getName).setId("nameColumn")
                        .setEditorComponent(name, (area, String) -> {
                            if((area.getName().equals(LanguageSettings.getLocaleString("garage")))&&(!name.getValue().equals(LanguageSettings.getLocaleString("garage")))) {
                                Notification.show(LanguageSettings.getLocaleString("garageWrongUpdate"),
                                        "",Notification.Type.ERROR_MESSAGE);
                            } else {
                                area.setName(String);
                                generalRepository.update(area);
                            }
                        })
                        .setExpandRatio(1).setResizable(false).setMinimumWidth(230);
        grid.getDefaultHeaderRow().getCell("nameColumn").setText(LanguageSettings.getLocaleString("name"));
        TextField filterName = UIComponents.textField(30);
        nameColumn.setFilter(filterName, UIComponents.stringFilter());
        filterName.setWidth("95%");
    }
    private void addIdColumn() {
        FilterGrid.Column<Area, Integer> idColumn = grid.addColumn(Area::getId).setId("idColumn")
                .setWidth(70).setResizable(false);
        grid.getDefaultHeaderRow().getCell("idColumn").setText(LanguageSettings.getLocaleString("id"));
        TextField filterId = UIComponents.textField(30);
        idColumn.setFilter(filterId, UIComponents.integerFilter());
        filterId.setWidth("95%");
        idColumn.setHidden(true);
    }

    @Override
    void addColumns() {
        addHereColumn();
        addActiveColumn();
        addDisplayOrderColumn();
        addNameColumn();
        addIdColumn();
    }

    @Override
    void addGrid() {
        initGrid("v-align-right");
        grid.setItems(areaRepository.getAreas());
        addColumns();
        grid.getEditor().setEnabled(true);
        grid.sort("nameColumn");
        grid.setWidthUndefined();
        addComponentsAndExpand(grid);
    }

    private void addNewAreaFields() {
        HorizontalLayout formLayout = new HorizontalLayout();
        formLayout.setWidth("440");
        formLayout.addComponent(addButton);
        newName = super.addNewNameField();
        formLayout.addComponentsAndExpand(newName);
        addComponent(formLayout);
        addButton.addClickListener(click -> addArea());
    }

    @Override
    void setTabIndexes() {
        newName.setTabIndex(1);
        addButton.setTabIndex(2);
        grid.setTabIndex(3);
    }

    private void addArea() {
        if (!newName.getValue().isEmpty()) {
            areaRepository.insertArea(newName.getValue());
            newName.setValue("");
            newName.focus();
            grid.setItems(areaRepository.getAreas());
        }
    }
}
