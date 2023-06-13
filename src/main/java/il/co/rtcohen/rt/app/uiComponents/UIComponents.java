package il.co.rtcohen.rt.app.uiComponents;

import com.vaadin.data.ValueProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.SerializableBiPredicate;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import il.co.rtcohen.rt.dal.repositories.GeneralRepository;
import org.vaadin.addons.filteringgrid.filters.InMemoryFilter;
import org.vaadin.ui.NumberField;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    public ComboBox<Integer> custTypeComboBox(GeneralRepository generalRepository, int w, int h) {
        List<Integer> custTypeList = generalRepository.getActiveId("custType");
        ItemCaptionGenerator<Integer> custTypeCaption =(ItemCaptionGenerator<Integer>) item -> {
            if (item==0) return "";
            return generalRepository.getNameById(item,"custType");
        };
        return UIComponents.comboBox(custTypeList,custTypeCaption,w,h);
    }

    @Deprecated
    public ComboBox<Integer> siteComboBox(GeneralRepository generalRepository, int w, int h) {
        ItemCaptionGenerator<Integer> siteCaption =(ItemCaptionGenerator<Integer>) item -> {
            if (item==0) return "";
            return generalRepository.getNameById(item,"site");
        };
        return comboBox(new ArrayList<>(),siteCaption,w,h);
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
    public ComboBox<Integer> userComboBox(GeneralRepository generalRepository, int w, int h) {
        ItemCaptionGenerator<Integer> usersCaption =(ItemCaptionGenerator<Integer>) item -> {
            return(0 == item ? "" : generalRepository.getNameById(item,"users"));
        };
        return comboBox(new ArrayList<>(),usersCaption,w,h);
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
    public static CheckBox checkBox(Boolean value, String caption, Boolean isReadOnly){
        CheckBox checkBox = checkBox(value);
        checkBox.setReadOnly(isReadOnly);
        checkBox.setCaption(caption);
        return checkBox;
    }

    @Deprecated
    public static CheckBox checkBox(Boolean value, String caption){
        CheckBox checkBox = checkBox(value);
        checkBox.setCaption(caption);
        return checkBox;
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

    public static Label header(String title) {
        return UIComponents.label(title,"LABEL LABEL-CENTER");
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

    public static Button printButton() {
        return bigButton(VaadinIcons.PRINT);
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


    public static TextField textField(String w, String h) {
        TextField textField = new TextField();
        textField.setHeight(h);
        textField.setWidth(w);
        return textField;
    }

    public static NumberField numberField(String w, String h) {
        NumberField numberField = new NumberField();
        numberField.setHeight(h);
        numberField.setWidth(w);
        return numberField;
    }

    public static CustomDateField dateField() {
        CustomDateField dateField = new CustomDateField();
        dateField.setDateFormat("dd/MM/yy");
        return dateField;
    }

    public static CustomDateField dateField(int w, int h) {
        return dateField(String.valueOf(w),String.valueOf(h));
    }

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
