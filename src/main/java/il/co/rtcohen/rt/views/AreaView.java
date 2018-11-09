package il.co.rtcohen.rt.views;

import com.vaadin.data.ValueProvider;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.Setter;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import il.co.rtcohen.rt.UIcomponents;
import il.co.rtcohen.rt.dao.Area;
import il.co.rtcohen.rt.dao.GeneralType;
import il.co.rtcohen.rt.repositories.AreaRepository;
import il.co.rtcohen.rt.repositories.GeneralRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.addons.filteringgrid.FilterGrid;
import org.vaadin.ui.NumberField;

import java.util.List;

@SpringView(name = AreaView.VIEW_NAME)
public class AreaView extends AbstractDataView {
    static final String VIEW_NAME = "area";
    private AreaRepository areaRepository;
    private FilterGrid<Area> grid;
    private TextField newName;

    @Autowired
    private AreaView(ErrorHandler errorHandler, AreaRepository areaRepository, GeneralRepository generalRepository) {
        super(errorHandler,generalRepository);
        this.areaRepository=areaRepository;
        this.generalRepository=generalRepository;
    }

    @Override
    public void createView(ViewChangeListener.ViewChangeEvent event) {
        title="רשימת אזורים";
        addHeader();
        addForm();
        addGrid();
    }

    private void hereColumn() {
        FilterGrid.Column hereColumn = grid.addComponentColumn((ValueProvider<Area, Component>) area ->
                UIcomponents.checkBox(area.getHere(), true));
        hereColumn.setId("hereColumn").setExpandRatio(1).setResizable(false).setWidth(70);
        hereColumn.setEditorBinding(grid.getEditor().getBinder().forField(new CheckBox()).bind(
                (ValueProvider<Area, Boolean>) Area::getHere,
                (Setter<Area, Boolean>) (area, Boolean) -> {
                    area.setHere(Boolean);
                    areaRepository.updateArea(area);
                }));
        hereColumn.setFilter(UIcomponents.BooleanValueProvider(),
                UIcomponents.checkBox(false), UIcomponents.BooleanPredicateWithShowAll());
        grid.getDefaultHeaderRow().getCell("hereColumn").setText("כאן");
    }

    private void activeColumn() {
        FilterGrid.Column activeColumn =
                grid.addComponentColumn((ValueProvider<Area, Component>) area ->
                        UIcomponents.checkBox(area.getActive(),true));
        activeColumn.setId("activeColumn").setExpandRatio(1).setResizable(false).setWidth(70);
        activeColumn.setEditorBinding(grid.getEditor().getBinder().forField(new CheckBox()).bind(
                (ValueProvider<Area, Boolean>) GeneralType::getActive,
                (Setter<Area, Boolean>) (area, Boolean) -> {
                    area.setActive(Boolean);
                    generalRepository.update(area);
                }));
        grid.getDefaultHeaderRow().getCell("activeColumn").setText("פעיל");
        CheckBox filterActive = UIcomponents.checkBox(true);
        activeColumn.setFilter(UIcomponents.BooleanValueProvider(),
                filterActive, UIcomponents.BooleanPredicate());
    }

    private void displayOrderColumn() {
        TextField displayOrder = new NumberField();
        FilterGrid.Column<Area, Integer> displayOrderColumn =
                grid.addColumn(Area::getDisplayOrder).setId("displayOrderColumn")
                        .setEditorBinding(grid.getEditor().getBinder().forField(displayOrder).bind(
                                (ValueProvider<Area, String>) area -> {
                                    return String.valueOf(area.getDisplayOrder());},
                                (Setter<Area, String>) (area, String) -> {
                                    if (String.matches("\\d+")) {
                                        area.setDisplayOrder(Integer.parseInt(String));
                                        areaRepository.updateArea(area);
                                    }
                                    else
                                        displayOrder.setValue(
                                                String.valueOf(area.getDisplayOrder()));
                                }
                        ))
                        .setExpandRatio(1).setResizable(false).setWidth(60);
        displayOrderColumn.setStyleGenerator(area -> {
            if(area.getDisplayOrder()==0) return "null"; else return "bold" ;});
        grid.getDefaultHeaderRow().getCell("displayOrderColumn").setText("סדר");
        NumberField filterDisplay = UIcomponents.numberField("95%","30");
        displayOrderColumn.setFilter(filterDisplay, UIcomponents.textFilter());
        filterDisplay.setWidth("95%");
    }

    private void nameColumn() {
        TextField name = new TextField();
        FilterGrid.Column<Area, String> nameColumn =
                grid.addColumn(Area::getName).setId("nameColumn")
                        .setEditorComponent(name, (area, String) -> {
                            if((area.getName().equals("מוסך"))&&(!name.getValue().equals("מוסך"))) {
                                Notification.show("לא ניתן לעדכן את שם המוסך",
                                        "",Notification.Type.ERROR_MESSAGE);
                            } else {
                                area.setName(String);
                                generalRepository.update(area);
                            }
                        })
                        .setExpandRatio(1).setResizable(false).setMinimumWidth(230);
        grid.getDefaultHeaderRow().getCell("nameColumn").setText("שם");
        TextField filterName = UIcomponents.textField(30);
        nameColumn.setFilter(filterName, UIcomponents.stringFilter());
        filterName.setWidth("95%");
    }

    private void idColumn() {
        FilterGrid.Column<Area, Integer> idColumn = grid.addColumn(Area::getId).setId("idColumn")
                .setWidth(70).setResizable(false);
        grid.getDefaultHeaderRow().getCell("idColumn").setText("#");
        TextField filterId = UIcomponents.textField(30);
        idColumn.setFilter(filterId, UIcomponents.textFilter());
        filterId.setWidth("95%");
        idColumn.setHidden(true);
    }

    private void addGrid() {
        grid = UIcomponents.myGrid("v-align-right");
        List<Area> list = areaRepository.getAreas();
        grid.setItems(list);

        hereColumn();
        activeColumn();
        displayOrderColumn();
        nameColumn();
        idColumn();

        grid.getEditor().setEnabled(true);
        grid.sort("nameColumn");
        dataGrid=grid;
        dataGrid.setWidth("440");
        addComponentsAndExpand(dataGrid);
    }

    private void addForm() {
        HorizontalLayout formLayout = new HorizontalLayout();
        formLayout.setWidth("440");
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
            areaRepository.insertArea(newName.getValue());
            newName.setValue("");
            newName.focus();
            removeComponent(dataGrid);
            addGrid();
        }
    }
}
