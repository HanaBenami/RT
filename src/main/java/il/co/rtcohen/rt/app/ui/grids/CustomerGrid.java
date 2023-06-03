package il.co.rtcohen.rt.app.ui.grids;

import com.vaadin.data.ValueProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Setter;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.Editor;
import il.co.rtcohen.rt.app.LanguageSettings;
import il.co.rtcohen.rt.app.UIComponents;
import il.co.rtcohen.rt.dal.dao.AbstractType;
import il.co.rtcohen.rt.dal.repositories.AbstractRepository;
import il.co.rtcohen.rt.dal.repositories.GeneralObjectRepository;
import org.vaadin.addons.filteringgrid.FilterGrid;
import org.vaadin.ui.NumberField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class CustomerFilterGrid<T extends AbstractType> extends FilterGrid<T> {
    private AbstractRepository<T> repository;
    private Supplier<T> newItemSupplier;

    public CustomerFilterGrid(AbstractRepository<T> repository, Supplier<T> newItemSupplier) {
        super();
        this.setGridRepository(repository);
        this.setNewItemSupplier(newItemSupplier);
        this.setSaveAction();
        this.populateGrid();
    }

    @Deprecated
    public CustomerFilterGrid() {
        super();
    }

    private void setGridRepository(AbstractRepository<T> repository) {
        this.repository = repository;
    }

    private void setNewItemSupplier(Supplier<T> newItemSupplier) {
        this.newItemSupplier = newItemSupplier;
    }

    private void setSaveAction() {
        Editor editor = this.getEditor();
        editor.addSaveListener(listener -> {
            T currentItem = (T) listener.getBean();
            if (repository.isItemValid(currentItem)) {
                this.repository.updateItem(currentItem);
                this.getDataProvider().refreshItem(currentItem);
            } else {
                // TODO
            }
        });
        editor.setSaveCaption(LanguageSettings.getLocaleString("save"));
        editor.setCancelCaption(LanguageSettings.getLocaleString("cancel"));
    }

    public void populateGrid() {
        populateGrid(0);
    }

    private void populateGrid(int numOfEmptyLines) {
        List<T> items = repository.getItems();
        if (0 < numOfEmptyLines) {
            assert null != newItemSupplier;
            for (int i = 0; i < numOfEmptyLines; ++i) {
                items.add(0, this.newItemSupplier.get());
            }
        }
        this.setItems(items);
    }

    private void addEmptyLines(NumberField numOfEmptyLinesFields) {
        if (numOfEmptyLinesFields.isEmpty()) {
            numOfEmptyLinesFields.focus();
        } else {
            this.populateGrid(Integer.parseInt(numOfEmptyLinesFields.getValue()));
        }
    }

    // Usage:
    //         customerGrid.addBooleanColumn(
    //                 (ValueProvider<Customer, Component>) Customer -> UIComponents.checkBox(Customer.isActive(),true),
    //            (ValueProvider<Customer, Boolean>) GeneralObject::isActive,
    //            (Setter<Customer, Boolean>) GeneralObject::setActive,
    //            "activeColumn",
    //            "active",
    //            Boolean.TRUE
    //         );
    public void addBooleanColumn(ValueProvider<T, Component> componentProvider,
                                 ValueProvider<T, Boolean> valueProvider,
                                 Setter<T, Boolean> setter,
                                 String id, String label, Boolean defaultFilter) {
        Column<T, Component> column = this.addComponentColumn(componentProvider);
        column.setId(id).setExpandRatio(1).setWidth(70).setResizable(false).setSortable(false);
        column.setEditorBinding(this.getEditor().getBinder().forField(new CheckBox()).bind(valueProvider, setter));
        CheckBox filterCheckBox = new CheckBox();
        if (null != defaultFilter) {
            filterCheckBox.setValue(defaultFilter);
        }
        column.setFilter(UIComponents.BooleanValueProvider(), filterCheckBox, UIComponents.BooleanPredicate());
        this.getDefaultHeaderRow().getCell(id).setText(LanguageSettings.getLocaleString(label));
    }

    // Usage:
    //    customerGrid.addTextColumn(
    //        Customer::getName,
    //        Customer::setName,
    //        230,
    //        "nameColumn",
    //        "name"
    //    );
    public void addTextColumn(ValueProvider<T, String> valueProvider,
                                 Setter<T, String> setter,
                                 int minimumWidth, String id, String label) {
        Column<T, String> column = this.addColumn(valueProvider);
        column.setId(id).setExpandRatio(1).setResizable(false).setMinimumWidth(minimumWidth);
        column.setEditorComponent(new TextField(), setter);

        TextField filterField = UIComponents.textField(30);
        filterField.setWidth("95%");
        column.setFilter(filterField, UIComponents.stringFilter());

        this.getDefaultHeaderRow().getCell(id).setText(LanguageSettings.getLocaleString(label));
    }

    // Usage:
    //    customerGrid.addTextColumn(
    //        Customer::getName,
    //        Customer::setName,
    //        80,
    //        "nameColumn",
    //        "name"
    //    );
    public void addNumricColumn(ValueProvider<T, Integer> valueProvider,
                              Setter<T, Integer> setter,
                              int width, String id, String label) {
        Column<T, Integer> column = this.addColumn(valueProvider);
        column.setId(id).setExpandRatio(1).setResizable(false).setWidth(width);
        if (null != setter) {
//            column.setEditorComponent(new NumberField(), setter); // TODO
        }

        TextField filterField = UIComponents.textField(30);
        filterField.setWidth("95%");
        column.setFilter(filterField, UIComponents.integerFilter());

        this.getDefaultHeaderRow().getCell(id).setText(LanguageSettings.getLocaleString(label));
    }

    // Usage:
    //        customerGrid.addComboBoxColumn(
    //            customerTypeRepository,
    //            "custType",
    //            (ValueProvider<Customer, String>) Customer -> customerTypeRepository.getItem(Customer.getCustomerTypeID()).getName(),
    //                (ValueProvider<Integer, String>) id -> customerTypeRepository.getItem(id).getName(),
    //                (ValueProvider<Customer, Integer>) Customer::getCustomerTypeID,
    //            (Setter<Customer, Integer>) Customer::setCustomerTypeID,
    //            130,
    //            "custTypeColumn",
    //            "custType"
    //        );
    public void addComboBoxColumn(GeneralObjectRepository generalObjectRepository,
                                  String dbTableName,
                                  ValueProvider<T, String> stringValueProvider,
                                  ValueProvider<Integer, String> stringValueProviderById,
                                  ValueProvider<T, Integer> idValueProvider,
                                  Setter<T, Integer> setter,
                                  int width, String id, String label) {
        ComboBox<Integer> comboBox = new UIComponents().generalObjectComboBox(generalObjectRepository, dbTableName, width,30);
        comboBox.setEmptySelectionAllowed(false);

        Column<T, String> column = this.addColumn(stringValueProvider).setId(id);
        column.setEditorBinding(this.getEditor().getBinder().forField(comboBox).bind(idValueProvider, setter));
        column.setWidth(width).setExpandRatio(1).setResizable(false);

        ComboBox<Integer> filterComboBox = new UIComponents().generalObjectComboBox(generalObjectRepository, dbTableName, width,30);
        filterComboBox.setWidth("95%");
        column.setFilter((filterComboBox),
                (cValue, fValue) -> fValue == null || stringValueProviderById.apply(fValue).equals(cValue));

        this.getDefaultHeaderRow().getCell(id).setText(LanguageSettings.getLocaleString(label));
    }

    // Usage:
    //         customerGrid.addComponentColumn(
    //                (ValueProvider<Customer, Component>) Customer -> {
    //                    if (null == Customer.getId()) {
    //                        return null;
    //                    } else {
    //                        int openCallsCounter = callRepository.countActiveCallsByCustomer(Customer.getId());
    //                        Button callsButton = CustomFilterGrid.countingIcon(VaadinIcons.BELL_O, VaadinIcons.BELL, VaadinIcons.BELL, openCallsCounter);
    //                        callsButton.addClickListener(clickEvent ->
    //                                getUI().getNavigator().navigateTo("call/customer=" + Customer.getId()));
    //                        return callsButton;
    //                    }
    //                },
    //                85,
    //                "callsColumn",
    //                "calls"
    //        );
    public void addComponentColumn(ValueProvider<T, Component> componentProvider,
                                   int width, String id, String label) {
        Column<T, Component> column = this.addComponentColumn(componentProvider);
        column.setId(id).setExpandRatio(1).setResizable(false).setWidth(width).setSortable(false);
        this.getDefaultHeaderRow().getCell(id).setText(LanguageSettings.getLocaleString(label));
    }

    public static Button countingIcon(VaadinIcons zeroIcon, VaadinIcons oneIcon, VaadinIcons multipleIcon, int n) {
        Button button = UIComponents.gridSmallButton(zeroIcon);
        if (1 == n) {
            button.setCaption(String.valueOf((n)));
            button.setIcon(oneIcon);
        } else if (1 < n) {
            button.setCaption(String.valueOf((n)));
            button.setIcon(multipleIcon);
        }
        return button;
    }


    public HorizontalLayout addEmptyLinesLayout() {
        HorizontalLayout newLinesLayout = new HorizontalLayout();
        newLinesLayout.setWidth(this.getWidth(), this.getWidthUnits());
        newLinesLayout.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);

        Label before = new Label(LanguageSettings.getLocaleString("add"));

        NumberField numOfNewLinesFields = new NumberField();
        numOfNewLinesFields.setDecimalPrecision(0);
        numOfNewLinesFields.setDecimalAllowed(false);
        numOfNewLinesFields.setMinValue(1);
        numOfNewLinesFields.setValue("1");
        numOfNewLinesFields.setWidth(100, Unit.PIXELS);

        Label after = new Label(LanguageSettings.getLocaleString("emptyLines"));

        Button addButton = UIComponents.addButton();
        addButton.setEnabled(true);
        addButton.addClickListener(clickEvent -> addEmptyLines(numOfNewLinesFields));

        ArrayList<Component> components = new ArrayList<>(Arrays.asList(before, numOfNewLinesFields, after, addButton));
        if (LanguageSettings.isHebrew()) {
            Collections.reverse(components);
        }
        newLinesLayout.addComponents(components.toArray(new Component[0]));
        newLinesLayout.addComponentsAndExpand(new Label());

        return newLinesLayout;
    }
}
