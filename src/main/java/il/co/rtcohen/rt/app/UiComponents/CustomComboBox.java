package il.co.rtcohen.rt.app.UiComponents;

import com.vaadin.ui.ComboBox;
import il.co.rtcohen.rt.app.GeneralErrorHandler;
import il.co.rtcohen.rt.dal.dao.*;
import il.co.rtcohen.rt.dal.repositories.VehicleTypeRepository;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class CustomComboBox<T extends Nameable & BindRepository> extends ComboBox<T> {
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

    public static CustomComboBox<VehicleType> vehiclesTypeComboBox(VehicleTypeRepository vehicleTypeRepository) {
        return new CustomComboBox<>(
                vehicleTypeRepository.getItems(),
                () -> {
                    VehicleType newItem = new VehicleType();
                    newItem.setBindRepository(vehicleTypeRepository);
                    return newItem;
                },
             130,
                30,
                true);
    }

    private static Logger getLogger() {
        return Logger.getLogger(GeneralErrorHandler.class.getName());
    }
}
