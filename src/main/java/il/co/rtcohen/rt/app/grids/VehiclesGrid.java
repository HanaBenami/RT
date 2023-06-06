package il.co.rtcohen.rt.app.grids;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.Setter;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Component;
import il.co.rtcohen.rt.app.UIComponents;
import il.co.rtcohen.rt.app.UiComponents.CustomComboBox;
import il.co.rtcohen.rt.dal.dao.GeneralObject;
import il.co.rtcohen.rt.dal.dao.Site;
import il.co.rtcohen.rt.dal.dao.Vehicle;
import il.co.rtcohen.rt.dal.repositories.GeneralObjectRepository;
import il.co.rtcohen.rt.dal.repositories.VehicleRepository;
import il.co.rtcohen.rt.dal.repositories.VehicleTypeRepository;
import org.vaadin.addons.filteringgrid.FilterGrid;

import java.time.LocalDate;

public class VehiclesGrid extends AbstractFilterGrid<Vehicle> {
    private final Site site;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final GeneralObjectRepository generalObjectRepository;

    public VehiclesGrid(Site site,
                        VehicleRepository vehicleRepository,
                        GeneralObjectRepository generalObjectRepository,
                        VehicleTypeRepository vehicleTypeRepository) {
        super(vehicleRepository, () -> {
                    Vehicle vehicle = new Vehicle();
                    vehicle.setSiteId(site.getId());
                    return vehicle;
                },
                "vehiclesOfSites",
                vehicle -> null == site || !vehicle.getSiteId().equals(site.getId()));
        this.site = site;
        this.vehicleTypeRepository = vehicleTypeRepository;
        this.generalObjectRepository = generalObjectRepository;
        this.initGrid();
    }

    @Override
    protected void setTitle() {
        super.setTitle();
        if (null != this.site) {
            this.title += " " + site.getName();
        }
    }

    @Override
    protected void changeErrorMessage() {
        String errorMessageKey = null;
        String warningMessageKey = null;
        if (null == site) {
            errorMessageKey = "noSite";
        } else if (0 == this.getItemsCounter()) {
            warningMessageKey = "noVehiclesToSite";
        }
        this.setErrorMessage(errorMessageKey);
        this.setWarningMessage(warningMessageKey);
    }

    protected void addColumns() {
        addActiveColumn();
        addLastUpdateColumn();
        addLicenseColumn();
        addZamaColumn();
        addEngineHoursColumn();
        addSeriesColumn();
        addModelColumn();
        addVehicleTypeColumn();
        addIdColumn();
    }

    private void addActiveColumn() {
        this.addBooleanColumn(
                (ValueProvider<Vehicle, Component>) vehicle -> UIComponents.checkBox(vehicle.isActive(),true),
                (ValueProvider<Vehicle, Boolean>) Vehicle::isActive,
                (Setter<Vehicle, Boolean>) Vehicle::setActive,
                "activeColumn",
                "active",
                Boolean.TRUE
        );
    }

    private void addLastUpdateColumn() {
        this.addDateColumn(
                (ValueProvider<Vehicle, LocalDate>) Vehicle::getLastUpdate,
                (Setter<Vehicle, LocalDate>) Vehicle::setLastUpdate,
                "lastUpdateColumn",
                "lastUpdate",
                false
        );
    }

    private void addEngineHoursColumn() {
        this.addNumericColumn(
                Vehicle::getZama,
                Vehicle::setZama,
                140,
                "engineHoursColumn",
                "engineHours"
        );
    }

    private void addLicenseColumn() {
        this.addNumericColumn(
                Vehicle::getZama,
                Vehicle::setZama,
                140,
                "licenseColumn",
                "license"
        );
    }

    private void addZamaColumn() {
        this.addNumericColumn(
                Vehicle::getZama,
                Vehicle::setZama,
                140,
                "zamaColumn",
                "zama"
        );
    }

    private void addSeriesColumn() {
        this.addTextColumn(
                Vehicle::getSeries,
                Vehicle::setSeries,
                140,
                "seriesColumn",
                "series"
        );
    }

    private void addModelColumn() {
        this.addTextColumn(
                Vehicle::getModel,
                Vehicle::setModel,
                140,
                "modelColumn",
                "model"
        );
    }

    private void addVehicleTypeColumn() {
        this.addComboBoxColumn(
                CustomComboBox.vehiclesTypeComboBox(vehicleTypeRepository),
                CustomComboBox.vehiclesTypeComboBox(vehicleTypeRepository),
                (ValueProvider<Vehicle, String>) vehicle -> {
                    GeneralObject vehicleType = vehicleTypeRepository.getItem(vehicle.getVehicleTypeId());
                    return (null == vehicleType ? "" : vehicleType.getName());
                },
                (ValueProvider<Integer, String>) id -> vehicleTypeRepository.getItem(id).getName(),
                (ValueProvider<Vehicle, GeneralObject>) vehicle -> vehicleTypeRepository.getItem(vehicle.getVehicleTypeId()),
                (Setter<Vehicle, GeneralObject>) Vehicle::setVehicleType,
                230,
                "vehicleTypeColumn",
                "carType"
        );
//        this.addComboBoxColumn(
//                generalObjectRepository,
//                vehicleTypeRepository.getDbTableName(),
//                (ValueProvider<Vehicle, String>) vehicle -> {
//                    GeneralObject vehicleType = vehicleTypeRepository.getItem(vehicle.getVehicleTypeId());
//                    return (null == vehicleType ? "" : vehicleType.getName());
//                },
//                (ValueProvider<Integer, String>) id -> vehicleTypeRepository.getItem(id).getName(),
//                (ValueProvider<Vehicle, Integer>) Vehicle::getVehicleTypeId,
//                (Setter<Vehicle, Integer>) Vehicle::setVehicleTypeId,
//                230,
//                "vehicleTypeColumn",
//                "carType"
//        );
    }

    @Override
    protected void sort() {
        super.sort("vehicleTypeColumn", SortDirection.ASCENDING);
    }
}
