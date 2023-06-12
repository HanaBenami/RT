package il.co.rtcohen.rt.app.grids;

import com.vaadin.client.widget.grid.events.GridSelectionAllowedEvent;
import com.vaadin.data.ValueProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.event.selection.SingleSelectionEvent;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Setter;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.Editor;
import il.co.rtcohen.rt.app.LanguageSettings;
import il.co.rtcohen.rt.app.ui.UIPaths;
import il.co.rtcohen.rt.app.uiComponents.CustomButton;
import il.co.rtcohen.rt.app.uiComponents.CustomNumericColumn;
import il.co.rtcohen.rt.app.uiComponents.UIComponents;
import il.co.rtcohen.rt.dal.dao.Customer;
import il.co.rtcohen.rt.dal.dao.interfaces.AbstractType;
import il.co.rtcohen.rt.dal.repositories.CallRepository;
import il.co.rtcohen.rt.dal.repositories.interfaces.AbstractTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.addons.filteringgrid.FilterGrid;
import org.vaadin.ui.NumberField;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

abstract public class AbstractTypeFilterGrid<T extends AbstractType> extends FilterGrid<T> {
    private AbstractTypeRepository<T> mainRepository;
    private Supplier<T> newItemSupplier;
    private VerticalLayout verticalLayout;
    private String titleKey;
    protected String title;
    private String errorMessage;
    private String warningMessage;
    private Predicate<T> itemsFilterPredicate;
    private static final Logger logger = LoggerFactory.getLogger(AbstractTypeFilterGrid.class);

    private int itemsCounter;
    private List<T> gridItems;
    private String customSortColumnId = null;
    private final HashMap<String, TextField> filterFields = new HashMap<>();
    public final String idColumnId = "idColumn";
    private boolean emptyLinesAllow = true;

    public AbstractTypeFilterGrid(
            AbstractTypeRepository<T> mainRepository,
            Supplier<T> newItemSupplier,
            String titleKey,
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
        this.addColumns();
        this.populateGrid();
        this.sort();
        this.setStyle();
    }

    abstract protected void addColumns();

    protected void sort() {
        this.sort((null == getCustomSortColumnId() ? idColumnId : getCustomSortColumnId()), SortDirection.ASCENDING);
    }

    public void setEmptyLinesAllow(boolean emptyLinesAllow) {
        this.emptyLinesAllow = emptyLinesAllow;
    }

    public void setFilterField(String columnId, TextField filterField) {
        this.filterFields.put(columnId, filterField);
    }

    public String getCustomSortColumnId() {
        return customSortColumnId;
    }

    public void setCustomSortColumnId(String customSortColumnId) {
        this.customSortColumnId = customSortColumnId;
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
    public AbstractTypeFilterGrid() {
        super();
    }

    private void setGridRepository(AbstractTypeRepository<T> repository) {
        this.mainRepository = repository;
    }

    private void setNewItemSupplier(Supplier<T> newItemSupplier) {
        this.newItemSupplier = newItemSupplier;
    }

    protected void setTitleKey(String titleKey) {
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
            this.getDataProvider().refreshItem(currentItem);
            this.setSelectedItem(currentItem);
            this.fireEvent(new Grid.ItemClick<T>(this, this.getColumn(idColumnId), currentItem, null, 0));
        });
        editor.setSaveCaption(LanguageSettings.getLocaleString("save"));
        editor.setCancelCaption(LanguageSettings.getLocaleString("cancel"));
    }

    protected List<T> getItems() {
        return mainRepository.getItems();
    }

    public void populateGrid() {
        populateGrid(0);
    }

    private void populateGrid(int numOfEmptyLines) {
        this.gridItems = getItems();
        if (null != this.itemsFilterPredicate) {
            this.gridItems.removeIf(itemsFilterPredicate);
        }
        this.itemsCounter = gridItems.size();
        if (this.gridItems.isEmpty() && (0 == numOfEmptyLines) && emptyLinesAllow) {
            numOfEmptyLines = 1;
        }
        if (0 < numOfEmptyLines) {
            assert null != newItemSupplier;
            for (int i = 0; i < numOfEmptyLines; ++i) {
                gridItems.add(0, this.getNewItem());
            }
        }
        this.setItems(gridItems);
    }

    private void addEmptyLines(NumberField numOfEmptyLinesFields) {
        if (numOfEmptyLinesFields.isEmpty()) {
            numOfEmptyLinesFields.focus();
        } else {
            this.populateGrid(Integer.parseInt(numOfEmptyLinesFields.getValue()));
        }
    }

    protected T getNewItem() {
        return this.newItemSupplier.get();
    }

    @Deprecated
    // TODO: use uiComponents.CustomTextColumn instead
    public Column<T, String> addTextColumn(ValueProvider<T, String> valueProvider,
                                           Setter<T, String> setter,
                                           int width, String id, String label) {
        FilterGrid.Column<T, String> column = this.addColumn(valueProvider);
        column.setId(id).setExpandRatio(1).setResizable(true).setMinimumWidth(width).setHidable(true);
        TextField textField = new TextField();
        textField.setWidth(2 * width, Unit.PIXELS);
        if (null != setter) {
            column.setEditorComponent(textField, setter);
        }
        TextField filterField = UIComponents.textField(30);
        filterField.setWidth("95%");
        column.setFilter(filterField, UIComponents.stringFilter());

        this.getDefaultHeaderRow().getCell(id).setText(LanguageSettings.getLocaleString(label));
        return column;
    }

    // TODO: Move to UiComponents
    public FilterGrid.Column<T, Component> addComponentColumn(ValueProvider<T, Component> componentProvider,
                                   int width, String id, String label) {
        FilterGrid.Column<T, Component> column = this.addComponentColumn(componentProvider);
        column.setId(id).setExpandRatio(1).setResizable(true).setWidth(width).setSortable(false).setHidable(true);
        this.getDefaultHeaderRow().getCell(id).setText(LanguageSettings.getLocaleString(label));
        return column;
    }

    protected void addIdColumn() {
        CustomNumericColumn.addToGrid(
                T::getId,
                null,
                70,
                idColumnId,
                "id",
                false,
                true,
                true,
                this
        );
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
            if (this.emptyLinesAllow) {
                this.verticalLayout.addComponent(this.emptyLinesLayout());
            }
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

    public void setFilterToSelectedItem(int selectedItemId) {
        if (0 != selectedItemId) {
            if (null != filterFields.get(idColumnId)) {
                filterFields.get(idColumnId).setValue(String.valueOf(selectedItemId));
            }
        }
    }

    public void setSelectedItem(T selectedItem) {
            this.getSelectionModel().select(selectedItem);
    }

    public void setSelectedItem(Integer selectedItemId) {
        if (null != selectedItemId && 0 != selectedItemId) {
            setFilterToSelectedItem(selectedItemId);
            T item = mainRepository.getItem(selectedItemId);
            if (null != item && gridItems.contains(item)) {
                this.setSelectedItem(item);
            }
        }
    }

    public void hideFilterRow() {
        // Will work only in the first call
        try {
            this.removeHeaderRow(this.getHeaderRow(1));
        } catch (Exception ignored) {}
    }

    protected void addCallsColumn(ValueProvider<T, Integer> callsCounterProvider, String urlAddition) {
        this.addComponentColumn(
                (ValueProvider<T, Component>) t -> {
                    if (null == t.getId()) {
                        return null;
                    } else {
                        int openCallsCounter = callsCounterProvider.apply(t);
                        Button callsButton = CustomButton.countingIcon(VaadinIcons.BELL_O, VaadinIcons.BELL, VaadinIcons.BELL, openCallsCounter);
                        callsButton.addClickListener(clickEvent ->
                                getUI().getNavigator().navigateTo(UIPaths.CALLS.getPath() + urlAddition + "=" + t.getId()));
                        return callsButton;
                    }
                },
                60,
                "callsColumn",
                "calls"
        );
    }
}
