package il.co.rtcohen.rt.app.grids;

import il.co.rtcohen.rt.app.uiComponents.*;
import il.co.rtcohen.rt.app.uiComponents.columns.CustomComponentColumn;
import il.co.rtcohen.rt.app.uiComponents.columns.CustomIntegerColumn;
import il.co.rtcohen.rt.app.uiComponents.fields.CustomButton;
import il.co.rtcohen.rt.app.uiComponents.fields.CustomIntegerField;
import il.co.rtcohen.rt.dal.dao.interfaces.Cloneable;
import il.co.rtcohen.rt.utils.Logger;
import il.co.rtcohen.rt.app.LanguageSettings;
import il.co.rtcohen.rt.app.ui.UIPaths;
import il.co.rtcohen.rt.dal.dao.interfaces.AbstractType;
import il.co.rtcohen.rt.dal.repositories.interfaces.AbstractTypeRepository;

import com.vaadin.data.ValueProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.BorderStyle;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.Editor;
import com.vaadin.ui.components.grid.HeaderRow;
import org.vaadin.addons.filteringgrid.FilterGrid;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;


abstract public class AbstractTypeFilterGrid<T extends AbstractType & Cloneable<T>> extends FilterGrid<T> {
    private AbstractTypeRepository<T> mainRepository;
    private Supplier<T> newItemSupplier;
    private VerticalLayout verticalLayout;
    private String titleKey;
    protected String title;
    private String errorMessage;
    private String warningMessage;
    private Predicate<T> itemsFilterPredicate;

    private int itemsCounter;
    private List<T> gridItems;
    private String customSortColumnId = null;
    public final String idColumnId = "idColumn";
    public CustomIntegerColumn<T> idColumn = null;
    private boolean emptyLinesAllow = true;
    private int numOfNewLinesInGrid;

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

    public void initGrid(boolean fullSize, int numOfEmptyLines) {
        this.setTitle();
        this.addColumns();
        this.populateGrid(numOfEmptyLines);
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
                Logger.getLogger(this).info("Going to update item in the grid (" + this.titleKey + ", id=" + currentItem.getId() + ")");
                Integer idBefore = currentItem.getId();
                this.mainRepository.updateItem(currentItem);
                Notification.show(currentItem + " " + LanguageSettings.getLocaleString("wasEdited"));
                if (null == idBefore || 0 == idBefore) { // The item won't be recognized, so we must refresh all
                    assert 0 < numOfNewLinesInGrid;
                    this.populateGrid(numOfNewLinesInGrid - 1);
                } else {
                    this.getDataProvider().refreshItem(currentItem);
                }
                this.setSelectedItem(currentItem);
                this.fireEvent(new Grid.ItemClick<T>(this, this.getColumn(idColumnId), currentItem, null, 0));
            } else {
                Notification.show(currentItem + " " + LanguageSettings.getLocaleString("invalidAndCannotBeSaved"),
                        Notification.Type.ERROR_MESSAGE);
            }
        });
        editor.setSaveCaption(LanguageSettings.getLocaleString("save"));
        editor.setCancelCaption(LanguageSettings.getLocaleString("cancel"));
    }

    protected List<T> getItems() {
        return mainRepository.getItems();
    }

    public List<T> getGridItems() {
        return gridItems;
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
        this.numOfNewLinesInGrid = numOfEmptyLines;
        this.setItems(gridItems);
    }

    private void addEmptyLines(CustomIntegerField numOfEmptyLinesFields) {
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

    protected void addIdColumn() {
        this.idColumn = CustomIntegerColumn.addToGrid(
                T::getId,
                null,
                null,
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
        CustomIntegerField numOfNewLinesFields = new CustomIntegerField(
                null, 1, 1, 10,
                false, null,
                "50px");
        Button addButton = new CustomButton(VaadinIcons.PLUS, true, clickEvent -> addEmptyLines(numOfNewLinesFields));
        addButton.setEnabled(true);
        numOfNewLinesFields.addValueChangeListener(listener -> addButton.setEnabled(!numOfNewLinesFields.isEmpty()));

        newLinesLayout.addComponentsAndExpand(new Label());
        newLinesLayout.addComponents(before, numOfNewLinesFields, after, addButton);

        return newLinesLayout;
    }

    public void setFilterToSelectedItem(int selectedItemId) {
        if (0 != selectedItemId) {
            if (null != this.idColumn && null != this.idColumn.getFilterField()) {
                this.idColumn.getFilterField().setValue(String.valueOf(selectedItemId));
            }
        }
    }

    public void setSelectedItem(T selectedItem) {
            this.getSelectionModel().select(selectedItem);
//          this.scrollTo(this.gridItems.indexOf(currentItem)); // TODO: not working during filter
    }

    public void setSelectedItem(Integer selectedItemId, boolean filter) {
        if (null != selectedItemId && 0 != selectedItemId) {
            if (filter) {
                setFilterToSelectedItem(selectedItemId);
            }
            T item = mainRepository.getItem(selectedItemId);
            if (null != item && gridItems.contains(item)) {
                this.setSelectedItem(item);
            }
        }
    }

    public HeaderRow getFilterRow() {
        return this.getHeaderRow(1);
    }

    public void hideFilterRow() {
        this.removeHeaderRow(this.getFilterRow());
    }

    protected void addCallsColumn(ValueProvider<T, Integer> callsCounterProvider, String urlAddition) {
        CustomComponentColumn.addToGrid(
                (ValueProvider<T, Component>) t -> {
                    if (null == t.getId()) {
                        return null;
                    } else {
                        int openCallsCounter = callsCounterProvider.apply(t);
                        Button callsButton = CustomButton.countingIcon(VaadinIcons.BELL_O, VaadinIcons.BELL, VaadinIcons.BELL, openCallsCounter);
                        callsButton.addClickListener(clickEvent -> {
                            String url = UIPaths.CALLS.getPath() + urlAddition + "=" + t.getId();
                            Page.getCurrent().open(
                                    url,
                                    UIPaths.CALLS.getWindowName(),
                                    UIPaths.CALLS.getWindowWidth(),
                                    UIPaths.CALLS.getWindowHeight(),
                                    BorderStyle.NONE
                            );
                        });
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
