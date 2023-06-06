package il.co.rtcohen.rt.app.grids;

import com.vaadin.data.ValueProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Setter;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.grid.ScrollDestination;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.Editor;
import il.co.rtcohen.rt.app.LanguageSettings;
import il.co.rtcohen.rt.app.UIComponents;
import il.co.rtcohen.rt.app.UiComponents.CustomComboBox;
import il.co.rtcohen.rt.dal.dao.AbstractType;
import il.co.rtcohen.rt.dal.dao.GeneralObject;
import il.co.rtcohen.rt.dal.repositories.AbstractRepository;
import il.co.rtcohen.rt.dal.repositories.GeneralObjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.addons.filteringgrid.FilterGrid;
import org.vaadin.ui.NumberField;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

abstract public class AbstractFilterGrid<T extends AbstractType> extends FilterGrid<T> {
    private AbstractRepository<T> mainRepository;
    private Supplier<T> newItemSupplier;
    private VerticalLayout verticalLayout;
    private String titleKey;
    protected String title;
    private String errorMessage;
    private String warningMessage;
    private Predicate<T> itemsFilterPredicate;
    private static final Logger logger = LoggerFactory.getLogger(AbstractFilterGrid.class);

    private int itemsCounter;
    private List<T> gridItems;
    private TextField filterIdField;
    private String idFieldId = "idColumn";

    public AbstractFilterGrid(AbstractRepository<T> mainRepository, Supplier<T> newItemSupplier, String titleKey,
                              Predicate<T> itemsFilterPredicate) {
        super();
        this.setGridRepository(mainRepository);
        this.setNewItemSupplier(newItemSupplier);
        this.setTitleKey(titleKey);
        this.setItemsFilterPredicate(itemsFilterPredicate);
        this.setSaveAction();
    }

    protected void initGrid() {
        this.setTitle();
        this.populateGrid();
        this.addColumns();
        this.sort();
        this.setStyle();
    }

    abstract protected void addColumns();

    protected void sort() {
        this.sort("nameColumn", SortDirection.ASCENDING);
    }

    protected void setStyle() {
        this.addStyleName("SMALL-TEXT");
        this.setStyleGenerator((StyleGenerator<T>) T -> {
            if (null == T.getId()) {
                return "yellow";
            } else {
                return null;
            }
        });
    }

    @Deprecated
    public AbstractFilterGrid() {
        super();
    }

    private void setGridRepository(AbstractRepository<T> repository) {
        this.mainRepository = repository;
    }

    private void setNewItemSupplier(Supplier<T> newItemSupplier) {
        this.newItemSupplier = newItemSupplier;
    }

    private void setTitleKey(String titleKey) {
        this.titleKey = titleKey;
    }

    protected void setTitle() {
        this.title = (null == this.titleKey ? "" : LanguageSettings.getLocaleString(this.titleKey));
    }

    protected void setErrorMessage(String errorMessageKey) {
        this.errorMessage = keyToText(errorMessageKey);
    }

    protected void setWarningMessage(String errorMessageKey) {
        this.warningMessage = keyToText(errorMessageKey);
    }
    
    private String keyToText(String key) {
        return  (null == key ? null : LanguageSettings.getLocaleString(key));
    }

    protected void changeErrorMessage() {

    }

    private void setItemsFilterPredicate(Predicate<T> itemsFilterPredicate) {
        this.itemsFilterPredicate = itemsFilterPredicate;
    }

    public T getCurrentItem() {
        Set<T> selected = this.getSelectedItems();
        if (1 == selected.size()) {
            for (T t : selected) {
                return t;   // The first item will be returned
            }
        }
        return null;
    }

    public int getItemsCounter() {
        return itemsCounter;
    }

    private void setSaveAction() {
        Editor<T> editor = this.getEditor();
        editor.setEnabled(true);
        editor.addSaveListener(listener -> {
            T currentItem = listener.getBean();
            if (currentItem.isItemValid()) {
                logger.info("Going to update item in the grid (" + this.titleKey + ", id=" + currentItem.getId() + ")");
                this.mainRepository.updateItem(currentItem);
                this.getDataProvider().refreshItem(currentItem);
                Notification.show(currentItem + " " + LanguageSettings.getLocaleString("wasEdited"));
            } else {
                Notification.show(currentItem + " " + LanguageSettings.getLocaleString("invalidAndCannotBeSaved"),
                        Notification.Type.ERROR_MESSAGE);
            }
        });
        editor.setSaveCaption(LanguageSettings.getLocaleString("save"));
        editor.setCancelCaption(LanguageSettings.getLocaleString("cancel"));
    }

    public void populateGrid() {
        populateGrid(0);
    }

    private void populateGrid(int numOfEmptyLines) {
        gridItems = mainRepository.getItems();
        if (null != this.itemsFilterPredicate) {
            gridItems.removeIf(itemsFilterPredicate);
        }
        if (0 < numOfEmptyLines) {
            assert null != newItemSupplier;
            for (int i = 0; i < numOfEmptyLines; ++i) {
                gridItems.add(0, this.newItemSupplier.get());
            }
        }
        this.setItems(gridItems);
        this.itemsCounter = gridItems.size();
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
        FilterGrid.Column<T, Component> column = this.addComponentColumn(componentProvider);
        column.setId(id).setExpandRatio(1).setWidth(50).setResizable(true).setSortable(false).setHidable(true);
        column.setEditorBinding(this.getEditor().getBinder().forField(new CheckBox()).bind(valueProvider, setter));
        if (null != defaultFilter) {
            CheckBox filterCheckBox = new CheckBox();
            filterCheckBox.setValue(defaultFilter);
            column.setFilter(UIComponents.BooleanValueProvider(), filterCheckBox, UIComponents.BooleanPredicate());
        }
        this.getDefaultHeaderRow().getCell(id).setText(LanguageSettings.getLocaleString(label));
    }

    // Usage:
    //        this.addDateColumn(
    //            (ValueProvider<Vehicle, LocalDate>) Vehicle::getLastUpdate,
    //            (Setter<Vehicle, Boolean>) Vehicle::setLastUpdate,
    //            "lastUpdateColumn",
    //            "lastUpdate",
    //            false
    //        );
    public void addDateColumn(ValueProvider<T, LocalDate> valueProvider,
                              Setter<T, LocalDate> setter,
                              String id, String label,
                              boolean editable) {
        FilterGrid.Column<T, LocalDate> column = this.addColumn(
                valueProvider,
                UIComponents.dateRenderer()
        );
        column.setEditorBinding(this.getEditor().getBinder().forField(UIComponents.dateField()).bind(valueProvider, setter));
        column.setId(id).setExpandRatio(1).setWidth(130).setResizable(true).setSortable(true).setEditable(editable).setHidable(true);
        DateField filterField = UIComponents.dateField(30);
        filterField.setWidth("95%");
        column.setFilter(filterField, UIComponents.dateFilter());
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
    public Column<T, String> addTextColumn(ValueProvider<T, String> valueProvider,
                                           Setter<T, String> setter,
                                           int width, String id, String label) {
        FilterGrid.Column<T, String> column = this.addColumn(valueProvider);
        column.setId(id).setExpandRatio(1).setResizable(true).setMinimumWidth(width).setHidable(true);
        TextField textField = new TextField();
        textField.setWidth(2 * width, Unit.PIXELS);
        column.setEditorComponent(textField, setter);

        TextField filterField = UIComponents.textField(30);
        filterField.setWidth("95%");
        column.setFilter(filterField, UIComponents.stringFilter());

        this.getDefaultHeaderRow().getCell(id).setText(LanguageSettings.getLocaleString(label));
        return column;
    }

    // Usage:
    //    customerGrid.addTextColumn(
    //        Customer::getName,
    //        Customer::setName,
    //        80,
    //        "nameColumn",
    //        "name"
    //    );
    public void addNumericColumn(ValueProvider<T, Integer> valueProvider,
                                 Setter<T, Integer> setter,
                                 int width, String id, String label) {
        FilterGrid.Column<T, Integer> column = this.addColumn(valueProvider);
        column.setId(id).setExpandRatio(1).setResizable(true).setWidth(width).setHidable(true);
        if (null != setter) {
            TextField numericField = UIComponents.textField(30);
            column.setEditorBinding(this.getEditor().getBinder().forField(numericField).bind(
                    t -> valueProvider.apply(t).toString(),
                    (Setter<T, String>) (t, s) -> setter.accept(t, Integer.parseInt(s))
            ));
        }

        TextField filterField = UIComponents.textField(30);
        filterField.setWidth("95%");
        column.setFilter(filterField, UIComponents.integerFilter());
        if (idFieldId.equals(id)) {
            filterIdField = filterField;
        }

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

        FilterGrid.Column<T, String> column = this.addColumn(stringValueProvider).setId(id);
        column.setEditorBinding(this.getEditor().getBinder().forField(comboBox).bind(idValueProvider, setter));
        column.setWidth(width).setExpandRatio(1).setResizable(true).setHidable(true);

        ComboBox<Integer> filterComboBox = new UIComponents().generalObjectComboBox(generalObjectRepository, dbTableName, 2 * width,30);
        filterComboBox.setWidth("95%");
        column.setFilter((filterComboBox),
                (cValue, fValue) -> fValue == null || stringValueProviderById.apply(fValue).equals(cValue));

        this.getDefaultHeaderRow().getCell(id).setText(LanguageSettings.getLocaleString(label));
    }
    public void addComboBoxColumn(CustomComboBox selectionComboBox,
                                  CustomComboBox filterComboBox,
                                  ValueProvider<T, String> stringValueProvider,
                                  ValueProvider<Integer, String> stringValueProviderById,
                                  ValueProvider<T, GeneralObject> valueProvider,
                                  Setter<T, GeneralObject> setter,
                                  int width, String id, String label) {
        FilterGrid.Column<T, String> column = this.addColumn(stringValueProvider).setId(id);
        column.setEditorBinding(this.getEditor().getBinder().forField(selectionComboBox).bind(valueProvider, setter));
        column.setWidth(width).setExpandRatio(1).setResizable(true).setHidable(true);

        filterComboBox.setWidth("95%");
//        column.setFilter((filterComboBox),
//                (cValue, fValue) -> fValue == null || stringValueProviderById.apply(fValue).equals(cValue));

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
    public FilterGrid.Column<T, Component> addComponentColumn(ValueProvider<T, Component> componentProvider,
                                   int width, String id, String label) {
        FilterGrid.Column<T, Component> column = this.addComponentColumn(componentProvider);
        column.setId(id).setExpandRatio(1).setResizable(true).setWidth(width).setSortable(false).setHidable(true);
        this.getDefaultHeaderRow().getCell(id).setText(LanguageSettings.getLocaleString(label));
        return column;
    }

    protected void addIdColumn() {
        this.addNumericColumn(
                T::getId,
                null,
                70,
                idFieldId,
                "id"
        );
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

    public VerticalLayout getVerticalLayout() {
        return this.getVerticalLayout(false, false);
    }

    public VerticalLayout getVerticalLayout(boolean resetLayout, boolean withTitle) {
        if (null == this.verticalLayout || resetLayout) {
            initVerticalLayout(withTitle);
        }
        return this.verticalLayout;
    }

    private void initVerticalLayout(boolean withTitle) {
        this.verticalLayout = new VerticalLayout();
        this.verticalLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        this.verticalLayout.setStyleName("scrollable");
        this.verticalLayout.addStyleName("custom-grid-margins");
        this.verticalLayout.setWidth("100%");
        this.setWidth("100%");
        if (withTitle) {
            this.verticalLayout.addComponent(UIComponents.smallHeader(this.title));
        }
        this.changeErrorMessage();
        if (null != errorMessage) {
            this.verticalLayout.addComponent(UIComponents.errorMessage(this.errorMessage));
        } else {
            if (null != warningMessage) {
                this.verticalLayout.addComponent(UIComponents.errorMessage(this.warningMessage));
            }
            this.verticalLayout.addComponentsAndExpand(this);
            this.verticalLayout.addComponent(this.emptyLinesLayout());
        }
    }

    private HorizontalLayout emptyLinesLayout() {
        HorizontalLayout newLinesLayout = new HorizontalLayout();
        newLinesLayout.setWidth(this.getWidth(), this.getWidthUnits());
        newLinesLayout.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);

        Label before = new Label(LanguageSettings.getLocaleString("add"));
        before.setStyleName("SMALL-TEXT");

        NumberField numOfNewLinesFields = new NumberField();
        numOfNewLinesFields.setDecimalPrecision(0);
        numOfNewLinesFields.setDecimalAllowed(false);
        numOfNewLinesFields.setMinValue(1);
        numOfNewLinesFields.setValue("1");
        numOfNewLinesFields.setWidth(50, Unit.PIXELS);

        Label after = new Label(LanguageSettings.getLocaleString("emptyLines"));
        after.setStyleName("SMALL-TEXT");

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

    public void setSelected(int selectedItemId) {
        if (0 != selectedItemId) {
            if (null != filterIdField) {
                filterIdField.setValue(String.valueOf(selectedItemId));
            }
            T item = mainRepository.getItem(selectedItemId);
            if (null != item){
                this.getSelectionModel().select(item);
                this.scrollTo(gridItems.indexOf(item), ScrollDestination.START);
            }
        }
    }
}
