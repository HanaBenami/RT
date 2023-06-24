package il.co.rtcohen.rt.app.uiComponents.fields;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Logger;
import com.vaadin.ui.ComboBox;

import il.co.rtcohen.rt.app.GeneralErrorHandler;
import il.co.rtcohen.rt.dal.dao.*;
import il.co.rtcohen.rt.dal.dao.interfaces.AbstractTypeWithNameAndActiveFields;
import il.co.rtcohen.rt.dal.dao.interfaces.BindRepository;
import il.co.rtcohen.rt.dal.dao.interfaces.Cloneable;
import il.co.rtcohen.rt.dal.dao.interfaces.Nameable;
import il.co.rtcohen.rt.dal.repositories.*;
import il.co.rtcohen.rt.dal.repositories.interfaces.AbstractTypeWithNameAndActiveFieldsRepository;

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
        this.setHeight(COMBO_BOX_HEIGHT);
        this.setWidth(String.valueOf(width));
    }

    public static <T extends AbstractTypeWithNameAndActiveFields & BindRepository<T> & Cloneable<T>> CustomComboBox<T> getComboBox(
                        AbstractTypeWithNameAndActiveFieldsRepository<T> repository,
                        Supplier<T> newItemSupplier, Integer width, boolean allowNewItems
    ) {
        try {
            return new CustomComboBox<>(
                    repository.getItems(true),
                    newItemSupplier,
                    width,
                    allowNewItems
            );
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    public static <T extends AbstractTypeWithNameAndActiveFields & BindRepository<T> & Cloneable<T>> CustomComboBox<T>
            getComboBox(AbstractTypeWithNameAndActiveFieldsRepository<T> repository) {
        try {
            return new CustomComboBox<>(
                    repository.getItems(true),
                    null,
                    130,
                    false);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    public static CustomComboBox<Vehicle> getComboBox(VehicleRepository vehicleRepository, @NotNull Site site) {
        return new CustomComboBox<>(
                vehicleRepository.getItems(site),
                null,
                130,
                false);
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
                 300,
                    true);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    public static CustomComboBox<Site> getComboBox(SiteRepository siteRepository, Customer customer) {
        return new CustomComboBox<>(
                (null == customer ? siteRepository.getItems() : siteRepository.getItems(customer)),
                null,
                130,
                false);
    }

    public static CustomComboBox<Customer> getComboBox(CustomerRepository customerRepository) {
        return getComboBox(customerRepository, null, 100, false);
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
                    false);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    public static CustomComboBox<Area> getComboBox(AreaRepository areaRepository) {
        try {
            return new CustomComboBox<>(
                    areaRepository.getItems(true),
                    () -> {
                        Area newItem = new Area();
                        newItem.setBindRepository(areaRepository);
                        return newItem;
                    },
                    70,
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
                    null,
                    70,
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
                    null,
                    70,
                    false);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    public static CustomComboBox<GarageStatus> getComboBox(GarageStatusRepository garageStatusRepository) {
        try {
            return new CustomComboBox<>(
                    garageStatusRepository.getItems(true),
                    null,
                    70,
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
