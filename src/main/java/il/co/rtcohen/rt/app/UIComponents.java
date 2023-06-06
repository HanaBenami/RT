package il.co.rtcohen.rt.app;

import com.vaadin.data.ValueProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.SerializableBiPredicate;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.LocalDateRenderer;
import com.vaadin.ui.themes.ValoTheme;
import il.co.rtcohen.rt.dal.dao.AbstractType;
import il.co.rtcohen.rt.dal.dao.Call;
import il.co.rtcohen.rt.dal.dao.GeneralObject;
import il.co.rtcohen.rt.dal.dao.Nameable;
import il.co.rtcohen.rt.dal.repositories.GeneralObjectRepository;
import il.co.rtcohen.rt.dal.repositories.GeneralRepository;
import org.vaadin.addons.filteringgrid.filters.InMemoryFilter;
import org.vaadin.ui.NumberField;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UIComponents {

    final static public DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static String callStyle(Call call) {
        if (call.getDate2().equals(LocalDate.now()))
            return "green";
        if (call.getDate2().equals(LocalDate.now().plusDays(1)))
            return "yellow";
        if ((call.getDate2().equals(Call.nullDate))&&(call.getStartDate().isBefore(LocalDate.now().minusDays(6))))
            return "darkred";
        if ((call.getDate2().equals(Call.nullDate))&&(call.getStartDate().isBefore(LocalDate.now().minusDays(2))))
            return "red";
        return null;
    }

    public static String regularDateStyle(LocalDate localDate) {
        if (Call.nullDate.equals(localDate))
            return "null";
        else
            return null ;
    }

    public static String boldDateStyle(LocalDate localDate) {
        if (Call.nullDate.equals(localDate))
            return "null";
        else
            return "bold" ;
    }

    public static String boldNumberStyle(Integer n) {
        if (n==0)
            return "null";
        else
            return "bold" ;
    }

    public static LocalDateRenderer dateRenderer() {
        return new LocalDateRenderer("dd/MM/yy");
    }

    public ComboBox<Integer> driverComboBox(GeneralRepository generalRepository, int w, int h) {
        List<Integer> driverList = generalRepository.getActiveId("driver");
        ItemCaptionGenerator<Integer> caption =(ItemCaptionGenerator<Integer>) item -> {
            if (item==0) return "";
            return generalRepository.getNameById(item,"driver"); };
        return comboBox(driverList,caption,w,h);
    }

    public ComboBox<Integer> callTypeComboBox(GeneralRepository generalRepository, int w, int h) {
        List<Integer> callTypeList = generalRepository.getActiveId("calltype");
        ItemCaptionGenerator<Integer> caption =(ItemCaptionGenerator<Integer>) item -> {
            if (item==0) return "";
            return generalRepository.getNameById(item,"calltype"); };
        return comboBox(callTypeList,caption,w,h);
    }

    public ComboBox<Integer> carComboBox(GeneralRepository generalRepository, int w, int h) {
        List<Integer> carList = generalRepository.getActiveId("cartype");
        ItemCaptionGenerator<Integer> caption =(ItemCaptionGenerator<Integer>) item -> {
            if (item==0) return "";
            return generalRepository.getNameById(item,"cartype"); };
        return comboBox(carList,caption,w,h);
    }

    public ComboBox<Integer> customerComboBox(GeneralRepository generalRepository, int w, int h) {
        List<Integer> customers = generalRepository.getActiveId("cust");
        ItemCaptionGenerator<Integer> custCaption =(ItemCaptionGenerator<Integer>) item -> {
            if (item==0) return "";
            return generalRepository.getNameById(item,"cust"); };
        return comboBox(customers,custCaption,w,h);
    }

    public ComboBox<Integer> custTypeComboBox(GeneralRepository generalRepository, int w, int h) {
        List<Integer> custTypeList = generalRepository.getActiveId("custType");
        ItemCaptionGenerator<Integer> custTypeCaption =(ItemCaptionGenerator<Integer>) item -> {
            if (item==0) return "";
            return generalRepository.getNameById(item,"custType");
        };
        return UIComponents.comboBox(custTypeList,custTypeCaption,w,h);
    }

    public ComboBox<Integer> generalObjectComboBox(GeneralObjectRepository generalObjectRepository, String dbTableName, int w, int h) {
        generalObjectRepository.setDbTableName(dbTableName);
        List<Integer> list = generalObjectRepository.getItems(true).stream()
                .map(GeneralObject::getId).collect(Collectors.toList());
        ItemCaptionGenerator<Integer> caption = (ItemCaptionGenerator<Integer>) item ->
                (0 == item ? "" : generalObjectRepository.getItem(item).getName());
        return UIComponents.comboBox(list, caption, w, h);
    }

    public ComboBox<Integer> siteComboBox(GeneralRepository generalRepository, int w, int h) {
        ItemCaptionGenerator<Integer> siteCaption =(ItemCaptionGenerator<Integer>) item -> {
            if (item==0) return "";
            return generalRepository.getNameById(item,"site");
        };
        return comboBox(new ArrayList<>(),siteCaption,w,h);
    }

    public ComboBox<Integer> custSiteComboBox(GeneralRepository generalRepository, int w, int h, int custId) {
        List<Integer> siteList = generalRepository.getIds("site", false, "custId=" + custId);
        ItemCaptionGenerator<Integer> siteCaption =(ItemCaptionGenerator<Integer>) item -> {
            if (item==0)
                return "";
            return generalRepository.getNameById(item,"site", "custId=" + custId);
        };
        return comboBox(siteList, siteCaption, w, h);
    }

    public ComboBox<Integer> areaComboBox(GeneralRepository generalRepository, int w, int h) {
        List<Integer> areaList = generalRepository.getActiveId("area");
        ItemCaptionGenerator<Integer> areaCaption =(ItemCaptionGenerator<Integer>) item -> {
            if (item==0) return "";
            return generalRepository.getNameById(item,"area"); };
        return UIComponents.comboBox(areaList,areaCaption,w,h);
    }

    public ComboBox<Integer> userComboBox(GeneralRepository generalRepository, int w, int h) {
        ItemCaptionGenerator<Integer> usersCaption =(ItemCaptionGenerator<Integer>) item -> {
            return(0 == item ? "" : generalRepository.getNameById(item,"users"));
        };
        return comboBox(new ArrayList<>(),usersCaption,w,h);
    }

    public ComboBox<Integer> emptyComboBox(int w, int h) {
        List<Integer> emptyList = new ArrayList<>();
        ItemCaptionGenerator<Integer> caption =(ItemCaptionGenerator<Integer>) item -> "";
        return UIComponents.comboBox(emptyList, caption, w, h);
    }

    private static ComboBox<Integer> comboBox(List<Integer> items,
                                              ItemCaptionGenerator<Integer> caption,
                                              Integer w, Integer h) {
        ComboBox<Integer> comboBox = new ComboBox<>();
        comboBox.setItems(items);
        comboBox.setItemCaptionGenerator(caption);
        comboBox.setHeight(String.valueOf(h));
        comboBox.setWidth(String.valueOf(w));
        return comboBox;
    }

    public static CheckBox checkBox(Boolean value, String caption, Boolean isReadOnly){
        CheckBox checkBox = checkBox(value);
        checkBox.setReadOnly(isReadOnly);
        checkBox.setCaption(caption);
        return checkBox;
    }

    public static CheckBox checkBox(Boolean value, String caption){
        CheckBox checkBox = checkBox(value);
        checkBox.setCaption(caption);
        return checkBox;
    }

    public static CheckBox checkBox(Boolean value, Boolean isReadOnly){
        CheckBox checkBox = checkBox(value);
        checkBox.setReadOnly(isReadOnly);
        return checkBox;
    }

    public static CheckBox checkBox(Boolean value) {
        CheckBox checkBox = new CheckBox();
        checkBox.setValue(value);
        return checkBox;
    }

    public static Label header(String title) {
        return UIComponents.label(title,"LABEL LABEL-CENTER");
    }

    public static Label smallHeader(String title) {
        return UIComponents.label(title,"LABEL-SMALL-HEADER");
    }

    public static Label errorMessage(String errorMessage) {
        Label label = smallHeader(errorMessage);
        label.setStyleName("LABEL-ERROR");
        return label;
    }

    public static Label label(String value, String style) {
        Label label = UIComponents.label(style);
        label.setValue(value);
        return label;
    }

    public static Label label(String style) {
        Label label = new Label("");
        label.addStyleName(style);
        return label;
    }

    public HorizontalLayout titleHorizontalLayout(String title, Button button) {
        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setDefaultComponentAlignment(Alignment.TOP_CENTER);
        titleLayout.addComponent(button);
        titleLayout.addComponent(UIComponents.header(title));
        titleLayout.setWidth("70%");
        return titleLayout;
    }

    public static Button editButton() {
        return gridSmallButton(VaadinIcons.EDIT);
    }

    public static Button gridSmallButton(VaadinIcons icon) {
        return button(icon,"noBorderButton");
    }

    public static Button addButton() {
        return bigButton(VaadinIcons.PLUS);
    }

    public static Button printButton() {
        return bigButton(VaadinIcons.PRINT);
    }

    public static Button trashButton() {
        return bigButton(VaadinIcons.TRASH);
    }

    public static Button closeButton() {
        return bigButton(VaadinIcons.CLOSE);
    }

    public static Button searchButton() {
        return bigButton(VaadinIcons.SEARCH);
    }

    public static Button refreshButton() {
        return bigButton(VaadinIcons.REFRESH);
    }

    public static Button showHideButton(Layout layout) {
        Button showHideButton = UIComponents.bigButton(VaadinIcons.EYE_SLASH);
        showHideButton.addClickListener(clickEvent -> {
            layout.setVisible(!layout.isVisible());
            showHideButton.setIcon(VaadinIcons.EYE);
        });
        return showHideButton;
    }

    public static Button truckButton() {
        return bigButton(VaadinIcons.TRUCK);
    }

    public static Button bigButton(VaadinIcons icon) {
        return button(icon,ValoTheme.BUTTON_PRIMARY);
    }

    private static Button button(VaadinIcons icon,String style) {
        Button button = new Button("");
        button.addStyleName(style);
        button.setIcon(icon);
        return button;
    }

    public static TextField textField(String value, Boolean enabled, int w, int h) {
        TextField textField = textField(enabled,w,h);
        textField.setValue(value);
        return textField;
    }

    public static TextField textField(Boolean enabled, int w, int h) {
        TextField textField = textField(String.valueOf(w),String.valueOf(h));
        textField.setEnabled(enabled);
        return textField;
    }


    public static TextField textField(String w, String h) {
        TextField textField = new TextField();
        textField.setHeight(h);
        textField.setWidth(w);
        return textField;
    }

    public static TextField textField(int h) {
        return textField(String.valueOf(h));
    }

    private static TextField textField(String h) {
        TextField textField = new TextField();
        textField.setHeight(h);
        return textField;
    }

    public static TextArea textArea(String caption,String w,String h) {
        TextArea textArea = new TextArea(caption);
        textArea.setStyleName("v-textarea");
        textArea.setWidth(w);
        textArea.setHeight(h);
        return textArea;
    }

    public static TextArea textArea(int w, int h) {
        return textArea("", Integer.toString(w), Integer.toString(h));
    }

    public static NumberField numberField(String w, String h) {
        NumberField numberField = new NumberField();
        numberField.setHeight(h);
        numberField.setWidth(w);
        return numberField;
    }

    public static DateField dateField() {
        DateField dateField = new DateField();
        dateField.setDateFormat("dd/MM/yy");
        return dateField;
    }

    public static DateField dateField(int w, int h) {
        return dateField(String.valueOf(w),String.valueOf(h));
    }

    public static DateField dateField(String w, String h) {
        DateField dateField = dateField();
        dateField.setHeight(h);
        dateField.setWidth(w);
        return dateField;
    }

    public static DateField dateField(int h) {
        return dateField(String.valueOf(h));
    }

    private static DateField dateField(String h) {
        DateField dateField = dateField();
        dateField.setHeight(h);
        return dateField;
    }

    public static ValueProvider<Component, Boolean> BooleanValueProvider() {
        return(ValueProvider<Component, Boolean>) component -> {
            CheckBox checkBox =(CheckBox) component;
            return checkBox.getValue();
        };
    }

    public static SerializableBiPredicate<Boolean,Boolean> BooleanPredicate() {
        return(SerializableBiPredicate<Boolean, Boolean>) Boolean::equals;
    }

    public static SerializableBiPredicate<Boolean,Boolean> BooleanPredicateWithShowAll() {
        return(SerializableBiPredicate<Boolean, Boolean>)(aBoolean, aBoolean2) -> {
            if (!aBoolean2)
                return true;
            else return
                    aBoolean.equals(true);
        };
    }

    public static SerializableBiPredicate<LocalDate,LocalDate> dateFilter() {
        return((v, fv) -> fv == null || v.isEqual(fv));
    }

    public static SerializableBiPredicate<String,String> stringFilter() {
        return(InMemoryFilter.StringComparator.containsIgnoreCase());
    }

    public static SerializableBiPredicate<Integer, String> integerFilter() {
        return(InMemoryFilter.StringComparator.containsIgnoreCase());
    }

}
