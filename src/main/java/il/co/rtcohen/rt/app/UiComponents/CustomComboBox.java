package il.co.rtcohen.rt.app.UiComponents;

import com.vaadin.ui.ComboBox;
import il.co.rtcohen.rt.app.GeneralErrorHandler;
import il.co.rtcohen.rt.dal.dao.*;
import il.co.rtcohen.rt.dal.repositories.AreasRepository;
import il.co.rtcohen.rt.dal.repositories.CallTypeRepository;
import il.co.rtcohen.rt.dal.repositories.CustomerTypeRepository;
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

    public static CustomComboBox<CustomerType> customerTypeComboBox(CustomerTypeRepository customerTypeRepository) {
        return new CustomComboBox<>(
                customerTypeRepository.getItems(),
                () -> {
                    CustomerType newItem = new CustomerType();
                    newItem.setBindRepository(customerTypeRepository);
                    return newItem;
                },
                70,
                30,
                false);
    }

    public static CustomComboBox<Area> areaComboBox(AreasRepository areasRepository) {
        return new CustomComboBox<>(
                areasRepository.getItems(),
                () -> {
                    Area newItem = new Area();
                    newItem.setBindRepository(areasRepository);
                    return newItem;
                },
                70,
                30,
                false);
    }

    public static CustomComboBox<CallType> callTypeComboBox(CallTypeRepository callTypeRepository) {
        return new CustomComboBox<>(
                callTypeRepository.getItems(),
                () -> {
                    CallType newItem = new CallType();
                    newItem.setBindRepository(callTypeRepository);
                    return newItem;
                },
                70,
                30,
                false);
    }

    private static Logger getLogger() {
        return Logger.getLogger(GeneralErrorHandler.class.getName());
    }
}
