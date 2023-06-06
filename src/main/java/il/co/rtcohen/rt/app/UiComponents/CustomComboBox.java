package il.co.rtcohen.rt.app.UiComponents;

import com.vaadin.ui.ComboBox;
import il.co.rtcohen.rt.app.GeneralErrorHandler;
import il.co.rtcohen.rt.dal.dao.BindRepository;
import il.co.rtcohen.rt.dal.dao.GeneralObject;
import il.co.rtcohen.rt.dal.dao.Nameable;
import il.co.rtcohen.rt.dal.repositories.AbstractRepository;
import il.co.rtcohen.rt.dal.repositories.RepositoryInterface;
import il.co.rtcohen.rt.dal.repositories.VehicleTypeRepository;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class CustomComboBox<T extends Nameable & BindRepository<T>> extends ComboBox<T> {
    public CustomComboBox(List<T> items, Supplier<T> newItemSupplier, Integer w, Integer h, boolean allowNewItems) {
        super();
        this.setItems(items);
        this.setItemCaptionGenerator(Nameable::getName);
        this.setEmptySelectionAllowed(false);
        if (allowNewItems) {
            this.setNewItemProvider(inputString -> {
                getLogger().info("inputString=" + inputString);
                T newItem = newItemSupplier.get();
                newItem.setName(inputString);
                newItem.insertItem();
                items.add(newItem);
                this.setItems(items);
                this.setSelectedItem(newItem);
                return Optional.of(newItem);
            });
        }
        this.setHeight(String.valueOf(h));
        this.setWidth(String.valueOf(w));
    }

    public static CustomComboBox vehiclesTypeComboBox(RepositoryInterface repository) {
        return new CustomComboBox<>(repository.getItems(), () -> {
                GeneralObject newItem = new GeneralObject();
                newItem.setBindRepository(repository);
                return newItem;
            },
         130, 30, true);
    }

    private static Logger getLogger() {
        return Logger.getLogger(GeneralErrorHandler.class.getName());
    }
}
