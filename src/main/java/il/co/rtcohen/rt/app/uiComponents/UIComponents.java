package il.co.rtcohen.rt.app.uiComponents;

import com.vaadin.data.ValueProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.SerializableBiPredicate;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import il.co.rtcohen.rt.app.uiComponents.fields.CustomDateField;
import il.co.rtcohen.rt.dal.repositories.GeneralRepository;
import org.vaadin.addons.filteringgrid.filters.InMemoryFilter;
import org.vaadin.ui.NumberField;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Deprecated
// TODO: Rewrite
public class UIComponents {

    final static public DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Deprecated
    public ComboBox<Integer> driverComboBox(GeneralRepository generalRepository, int w, int h) {
        List<Integer> driverList = generalRepository.getActiveId("driver");
        ItemCaptionGenerator<Integer> caption =(ItemCaptionGenerator<Integer>) item -> {
            if (item==0) return "";
            return generalRepository.getNameById(item,"driver"); };
        return comboBox(driverList,caption,w,h);
    }

    @Deprecated
    public ComboBox<Integer> callTypeComboBox(GeneralRepository generalRepository, int w, int h) {
        List<Integer> callTypeList = generalRepository.getActiveId("calltype");
        ItemCaptionGenerator<Integer> caption =(ItemCaptionGenerator<Integer>) item -> {
            if (item==0) return "";
            return generalRepository.getNameById(item,"calltype"); };
        return comboBox(callTypeList,caption,w,h);
    }

    @Deprecated
    public ComboBox<Integer> carComboBox(GeneralRepository generalRepository, int w, int h) {
        List<Integer> carList = generalRepository.getActiveId("carType");
        ItemCaptionGenerator<Integer> caption =(ItemCaptionGenerator<Integer>) item -> {
            if (item==0) return "";
            return generalRepository.getNameById(item,"carType"); };
        return comboBox(carList,caption,w,h);
    }

    @Deprecated
    public ComboBox<Integer> customerComboBox(GeneralRepository generalRepository, int w, int h) {
        List<Integer> customers = generalRepository.getActiveId("cust");
        ItemCaptionGenerator<Integer> custCaption =(ItemCaptionGenerator<Integer>) item -> {
            if (item==0) return "";
            return generalRepository.getNameById(item,"cust"); };
        return comboBox(customers,custCaption,w,h);
    }

    @Deprecated
    public ComboBox<Integer> areaComboBox(GeneralRepository generalRepository, int w, int h) {
        List<Integer> areaList = generalRepository.getActiveId("area");
        ItemCaptionGenerator<Integer> areaCaption =(ItemCaptionGenerator<Integer>) item -> {
            if (item==0) return "";
            return generalRepository.getNameById(item,"area"); };
        return UIComponents.comboBox(areaList,areaCaption,w,h);
    }

    @Deprecated
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

    @Deprecated
    public static CheckBox checkBox(Boolean value, Boolean isReadOnly){
        CheckBox checkBox = checkBox(value);
        checkBox.setReadOnly(isReadOnly);
        return checkBox;
    }

    @Deprecated
    public static CheckBox checkBox(Boolean value) {
        CheckBox checkBox = new CheckBox();
        checkBox.setValue(value);
        return checkBox;
    }

    @Deprecated
    public static Label label(String value, String style) {
        Label label = UIComponents.label(style);
        label.setValue(value);
        return label;
    }

    @Deprecated
    public static Label label(String style) {
        Label label = new Label("");
        label.addStyleName(style);
        return label;
    }

    @Deprecated
    public static Button printButton() {
        return bigButton(VaadinIcons.PRINT);
    }

    @Deprecated
    public static Button bigButton(VaadinIcons icon) {
        return button(icon,ValoTheme.BUTTON_PRIMARY);
    }

    @Deprecated
    private static Button button(VaadinIcons icon,String style) {
        Button button = new Button("");
        button.addStyleName(style);
        button.setIcon(icon);
        return button;
    }

    @Deprecated
    public static TextField textField(String w, String h) {
        TextField textField = new TextField();
        textField.setHeight(h);
        textField.setWidth(w);
        return textField;
    }

    @Deprecated
    public static NumberField numberField(String w, String h) {
        NumberField numberField = new NumberField();
        numberField.setHeight(h);
        numberField.setWidth(w);
        return numberField;
    }

    @Deprecated
    public static CustomDateField dateField() {
        CustomDateField dateField = new CustomDateField();
        dateField.setDateFormat("dd/MM/yy");
        return dateField;
    }

    @Deprecated
    public static CustomDateField dateField(int w, int h) {
        return dateField(String.valueOf(w),String.valueOf(h));
    }

    @Deprecated
    public static CustomDateField dateField(String w, String h) {
        CustomDateField dateField = dateField();
        dateField.setHeight(h);
        dateField.setWidth(w);
        return dateField;
    }

    @Deprecated
    public static ValueProvider<Component, Boolean> BooleanValueProvider() {
        return component -> {
            CheckBox checkBox =(CheckBox) component;
            return checkBox.getValue();
        };
    }

    @Deprecated
    public static SerializableBiPredicate<Boolean,Boolean> BooleanPredicateWithShowAll() {
        return(SerializableBiPredicate<Boolean, Boolean>)(aBoolean, aBoolean2) -> {
            if (!aBoolean2)
                return true;
            else return
                    aBoolean.equals(true);
        };
    }

    @Deprecated
    public static SerializableBiPredicate<LocalDate,LocalDate> dateFilter() {
        return((v, fv) -> fv == null || v.isEqual(fv));
    }

    @Deprecated
    public static SerializableBiPredicate<String,String> stringFilter() {
        return(InMemoryFilter.StringComparator.containsIgnoreCase());
    }

    @Deprecated
    public static SerializableBiPredicate<Integer, String> integerFilter() {
        return(InMemoryFilter.StringComparator.containsIgnoreCase());
    }

}
