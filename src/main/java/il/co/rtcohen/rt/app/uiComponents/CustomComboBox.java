package il.co.rtcohen.rt.app.uiComponents;

import com.vaadin.ui.ComboBox;
import il.co.rtcohen.rt.app.GeneralErrorHandler;
import il.co.rtcohen.rt.dal.dao.*;
import il.co.rtcohen.rt.dal.dao.interfaces.AbstractTypeWithNameAndActiveFields;
import il.co.rtcohen.rt.dal.dao.interfaces.BindRepository;
import il.co.rtcohen.rt.dal.dao.interfaces.Nameable;
import il.co.rtcohen.rt.dal.repositories.*;
import il.co.rtcohen.rt.dal.repositories.interfaces.AbstractTypeWithNameAndActiveFieldsRepository;

import java.sql.SQLException;
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

    public static <T extends AbstractTypeWithNameAndActiveFields & BindRepository<T>> CustomComboBox<T> getComboBox(
                        AbstractTypeWithNameAndActiveFieldsRepository<T> repository,
                        Supplier<T> newItemSupplier, Integer w, Integer h, boolean allowNewItems
    ) {
        try {
            return new CustomComboBox<>(
                    repository.getItems(true),
                    newItemSupplier,
                    w,
                    h,
                    allowNewItems
            );
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    public static CustomComboBox<VehicleType> getComboBox(VehicleTypeRepository vehicleTypeRepository) {
        try {
            return new CustomComboBox<>(
                    vehicleTypeRepository.getItems(true),
                    () -> {
                        VehicleType newItem = new VehicleType();
                        newItem.setBindRepository(vehicleTypeRepository);
                        return newItem;
                    },
                 130,
                    30,
                    true);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    public static CustomComboBox<Site> getComboBox(SiteRepository siteRepository) {
        return getComboBox(siteRepository, null, 100, 30, false);
    }

    public static CustomComboBox<Customer> getComboBox(CustomerRepository customerRepository) {
        return getComboBox(customerRepository, null, 100, 30, false);
    }

    public static CustomComboBox<CustomerType> getComboBox(CustomerTypeRepository customerTypeRepository) {
        try {
            return new CustomComboBox<>(
                    customerTypeRepository.getItems(true),
                    () -> {
                        CustomerType newItem = new CustomerType();
                        newItem.setBindRepository(customerTypeRepository);
                        return newItem;
                    },
                    70,
                    30,
                    false);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    public static CustomComboBox<Area> getComboBox(AreasRepository areasRepository) {
        try {
            return new CustomComboBox<>(
                    areasRepository.getItems(true),
                    () -> {
                        Area newItem = new Area();
                        newItem.setBindRepository(areasRepository);
                        return newItem;
                    },
                    70,
                    30,
                    false);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    public static CustomComboBox<Driver> getComboBox(DriverRepository driverRepository) {
        try {
            return new CustomComboBox<>(
                    driverRepository.getItems(true),
                    () -> {
                        Driver newItem = new Driver();
                        newItem.setBindRepository(driverRepository);
                        return newItem;
                    },
                    70,
                    30,
                    false);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    public static CustomComboBox<CallType> getComboBox(CallTypeRepository callTypeRepository) {
        try {
            return new CustomComboBox<>(
                    callTypeRepository.getItems(true),
                    () -> {
                        CallType newItem = new CallType();
                        newItem.setBindRepository(callTypeRepository);
                        return newItem;
                    },
                    70,
                    30,
                    false);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    private static Logger getLogger() {
        return Logger.getLogger(GeneralErrorHandler.class.getName());
    }
}
