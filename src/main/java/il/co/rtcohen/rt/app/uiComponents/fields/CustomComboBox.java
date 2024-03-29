package il.co.rtcohen.rt.app.uiComponents.fields;

import il.co.rtcohen.rt.dal.dao.*;
import il.co.rtcohen.rt.dal.dao.interfaces.AbstractTypeWithNameAndActiveFields;
import il.co.rtcohen.rt.dal.dao.interfaces.BindRepository;
import il.co.rtcohen.rt.dal.dao.interfaces.Cloneable;
import il.co.rtcohen.rt.dal.dao.interfaces.Nameable;
import il.co.rtcohen.rt.dal.repositories.*;
import il.co.rtcohen.rt.dal.repositories.interfaces.AbstractTypeWithNameAndActiveFieldsRepository;
import il.co.rtcohen.rt.utils.Logger;

import com.vaadin.ui.ComboBox;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import static il.co.rtcohen.rt.app.uiComponents.StyleSettings.COMBO_BOX_HEIGHT;

public class CustomComboBox<T extends Nameable & BindRepository<T>> extends ComboBox<T> {
    public CustomComboBox(List<T> items, Supplier<T> newItemSupplier, Integer width, boolean allowNewItems) {
        super();
        this.setItems(items);
        this.setItemCaptionGenerator(Nameable::getName);
        this.setEmptySelectionAllowed(false);
        if (allowNewItems) {
            this.setNewItemProvider(inputString -> {
                Logger.getLogger(this).info("inputString=" + inputString);
                T newItem = newItemSupplier.get();
                newItem.setName(inputString);
                newItem.insertItem();
                items.add(newItem);
                this.setItems(items);
                this.setSelectedItem(newItem);
                return Optional.of(newItem);
            });
        }
        this.setHeight(COMBO_BOX_HEIGHT);
        this.setWidth(String.valueOf(width));
    }

    public static <T extends AbstractTypeWithNameAndActiveFields & BindRepository<T> & Cloneable<T>> CustomComboBox<T> getComboBox(
            AbstractTypeWithNameAndActiveFieldsRepository<T> repository,
            Supplier<T> newItemSupplier, Integer width, boolean allowNewItems) {
        return new CustomComboBox<>(
                repository.getItems(true),
                newItemSupplier,
                width,
                allowNewItems);
    }

    public static <T extends AbstractTypeWithNameAndActiveFields & BindRepository<T> & Cloneable<T>> CustomComboBox<T> getComboBox(
            AbstractTypeWithNameAndActiveFieldsRepository<T> repository) {
        return new CustomComboBox<>(
                repository.getItems(true),
                null,
                130,
                false);
    }

    public static CustomComboBox<Vehicle> getComboBox(VehicleRepository vehicleRepository, @NotNull Site site) {
        List<Vehicle> list = vehicleRepository.getItems(site);
        // .stream().filter(vehicle ->
        // vehicle.wasSyncedWithHashavshevet()).collect(Collectors.toList())
        list.sort(Comparator.comparing(vehicle -> !vehicle.wasSyncedWithHashavshevet()));
        return new CustomComboBox<>(
                list,
                null,
                130,
                false);
    }

    public static CustomComboBox<VehicleType> getComboBox(VehicleTypeRepository vehicleTypeRepository) {
        return new CustomComboBox<>(
                vehicleTypeRepository.getItems(true),
                () -> {
                    VehicleType newItem = new VehicleType();
                    newItem.setBindRepository(vehicleTypeRepository);
                    return newItem;
                },
                300,
                true);
    }

    public static CustomComboBox<Site> getComboBox(SiteRepository siteRepository, Customer customer) {
        List<Site> list = (null == customer ? siteRepository.getItems() : siteRepository.getItems(customer));
        // .stream().filter(site ->
        // site.wasSyncedWithHashavshevet()).collect(Collectors.toList())
        list.sort(Comparator.comparing(site -> !site.wasSyncedWithHashavshevet()));
        return new CustomComboBox<>(
                list,
                null,
                130,
                false);
    }

    public static CustomComboBox<Customer> getComboBox(CustomerRepository customerRepository) {
        return getComboBox(customerRepository, null, 100, false);
    }

    public static CustomComboBox<CustomerType> getComboBox(CustomerTypeRepository customerTypeRepository) {
        return new CustomComboBox<>(
                customerTypeRepository.getItems(true),
                () -> {
                    CustomerType newItem = new CustomerType();
                    newItem.setBindRepository(customerTypeRepository);
                    return newItem;
                },
                70,
                false);
    }

    public static CustomComboBox<Area> getComboBox(AreaRepository areaRepository) {
        return new CustomComboBox<>(
                areaRepository.getItems(true),
                () -> {
                    Area newItem = new Area();
                    newItem.setBindRepository(areaRepository);
                    return newItem;
                },
                70,
                false);
    }

    public static CustomComboBox<Driver> getComboBox(DriverRepository driverRepository) {
        return new CustomComboBox<>(
                driverRepository.getItems(true),
                null,
                70,
                false);
    }

    public static CustomComboBox<CallType> getComboBox(CallTypeRepository callTypeRepository) {
        return new CustomComboBox<>(
                callTypeRepository.getItems(true),
                null,
                70,
                false);
    }

    public static CustomComboBox<GarageStatus> getComboBox(GarageStatusRepository garageStatusRepository) {
        return new CustomComboBox<>(
                garageStatusRepository.getItems(true),
                null,
                70,
                false);
    }
}
