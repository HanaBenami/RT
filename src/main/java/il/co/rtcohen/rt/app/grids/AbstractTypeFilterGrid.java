package il.co.rtcohen.rt.app.grids;

import com.vaadin.data.ValueProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.Editor;
import il.co.rtcohen.rt.app.uiComponents.*;
import il.co.rtcohen.rt.dal.dao.interfaces.Cloneable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.addons.filteringgrid.FilterGrid;
import org.vaadin.ui.NumberField;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

import il.co.rtcohen.rt.app.LanguageSettings;
import il.co.rtcohen.rt.app.ui.UIPaths;
import il.co.rtcohen.rt.dal.dao.interfaces.AbstractType;
import il.co.rtcohen.rt.dal.repositories.interfaces.AbstractTypeRepository;

abstract public class AbstractTypeFilterGrid<T extends AbstractType & Cloneable<T>> extends FilterGrid<T> {
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
            Predicate<T> itemsFilterPredicate
    ) {
        super();
        this.setGridRepository(mainRepository);
        this.setNewItemSupplier(newItemSupplier);
        this.setTitleKey(titleKey);
        this.setItemsFilterPredicate(itemsFilterPredicate);
        this.setSaveAction();
    }

    public void initGrid(boolean fullSize) {
        this.setTitle();
        this.addColumns();
        this.populateGrid();
        this.sort();
        this.setStyle();
        if (fullSize) {
            this.setSizeFull();
        }
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
            this.addEmptyLines(Integer.parseInt(numOfEmptyLinesFields.getValue()));
        }
    }

    protected void addEmptyLines(int numOfEmptyLines) {
        this.populateGrid(numOfEmptyLines);
    }

    protected T getNewItem() {
        return this.newItemSupplier.get();
    }

    protected Column<T, Integer> addIdColumn() {
        return CustomNumericColumn.addToGrid(
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
            this.verticalLayout.addComponent(new CustomLabel(this.title, null, CustomLabel.LabelStyle.TITLE));
        }
        this.changeErrorMessage();
        if (null != errorMessage) {
            this.verticalLayout.addComponent(new CustomLabel(this.errorMessage, null, CustomLabel.LabelStyle.ERROR));
        } else {
            if (null != warningMessage) {
                this.verticalLayout.addComponent(new CustomLabel(this.warningMessage, null, CustomLabel.LabelStyle.ERROR));
            }
            RtlHorizontalLayout additionalLayout = customAdditionalLayout();
            if (null != additionalLayout) {
                this.verticalLayout.addComponent(additionalLayout);
            }
            this.verticalLayout.addComponentsAndExpand(this);
            if (this.emptyLinesAllow) {
                this.verticalLayout.addComponent(this.emptyLinesLayout());
            }
        }
    }

    protected RtlHorizontalLayout customAdditionalLayout() {
        return null;
    }

    private RtlHorizontalLayout emptyLinesLayout() {
        RtlHorizontalLayout newLinesLayout = new RtlHorizontalLayout();

        Label before = new CustomLabel("add", null, CustomLabel.LabelStyle.SMALL_TEXT);
        Label after = new CustomLabel("emptyLines", null, CustomLabel.LabelStyle.SMALL_TEXT);
        CustomNumericField numOfNewLinesFields = new CustomNumericField(
                null, 1, 1, 10,
                null,
                "50px");
        Button addButton = new CustomButton(VaadinIcons.PLUS, true, clickEvent -> addEmptyLines(numOfNewLinesFields));
        addButton.setEnabled(false);
        numOfNewLinesFields.addValueChangeListener(listener -> addButton.setEnabled(!numOfNewLinesFields.isEmpty()));

        newLinesLayout.addComponentsAndExpand(new Label());
        newLinesLayout.addComponents(before, numOfNewLinesFields, after, addButton);

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
        CustomComponentColumn.addToGrid(
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
                "calls",
                this
        );
    }
}
