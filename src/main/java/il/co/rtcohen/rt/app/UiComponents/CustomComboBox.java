package il.co.rtcohen.rt.app.UiComponents;

import com.vaadin.ui.ComboBox;
import il.co.rtcohen.rt.dal.dao.GeneralObject;
import il.co.rtcohen.rt.dal.dao.Nameable;

import java.util.List;
import java.util.Optional;

public class ComboBox<Nameable> extends ComboBox<Nameable> {

    public ComboBox(List<Nameable> items, Integer w, Integer h) {

    }

    public ComboBox<Nameable> generateComboBox(List<Nameable> items, Integer w, Integer h) {
        ComboBox<Nameable> comboBox = new ComboBox<>();
        comboBox.setItems(items);
        comboBox.setItemCaptionGenerator(Nameable::getName);
        comboBox.setNewItemProvider(inputString -> {
            GeneralObject newItem = new Nameable(inputString);
            items.add(newItem);
            comboBox.setItems(items);
            return Optional.of(newItem);
        });
        comboBox.setHeight(String.valueOf(h));
        comboBox.setWidth(String.valueOf(w));
        return comboBox;
    }
}
