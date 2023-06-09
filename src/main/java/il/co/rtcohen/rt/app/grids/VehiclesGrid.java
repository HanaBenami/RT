package il.co.rtcohen.rt.app.grids;

import com.vaadin.shared.data.sort.SortDirection;
import il.co.rtcohen.rt.app.uiComponents.*;
import il.co.rtcohen.rt.dal.dao.Site;
import il.co.rtcohen.rt.dal.dao.Vehicle;
import il.co.rtcohen.rt.dal.repositories.VehicleRepository;
import il.co.rtcohen.rt.dal.repositories.VehicleTypeRepository;

public class VehiclesGrid extends AbstractTypeFilterGrid<Vehicle> {
    private final Site site;
    private final VehicleTypeRepository vehicleTypeRepository;

    public VehiclesGrid(Site site,
                        VehicleRepository vehicleRepository,
                        VehicleTypeRepository vehicleTypeRepository) {
        super(vehicleRepository, () -> {
                    Vehicle vehicle = new Vehicle();
                    vehicle.setSite(site);
                    return vehicle;
                },
                "vehiclesOfSites",
                vehicle -> null == site || null == vehicle.getSite() || !vehicle.getSite().equals(site));
        this.site = site;
        this.vehicleTypeRepository = vehicleTypeRepository;
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
        CustomCheckBoxColumn.addToGrid(
                Vehicle::isActive,
                Vehicle::setActive,
                "activeColumn",
                "active",
                Boolean.TRUE,
                this
        );
    }

    private void addLastUpdateColumn() {
        CustomDateColumn.addToGrid(
                Vehicle::getLastUpdate,
                null,
                "lastUpdateColumn",
                "lastUpdate",
                false,
                this
        );
    }

    private void addEngineHoursColumn() {
        CustomNumericColumn.addToGrid(
                Vehicle::getEngineHours,
                Vehicle::setEngineHours,
                140,
                "engineHoursColumn",
                "engineHours",
                false,
                false,
                true,
                this
        );
    }

    private void addLicenseColumn() {
        CustomNumericColumn.addToGrid(
                Vehicle::getLicense,
                Vehicle::setLicense,
                140,
                "licenseColumn",
                "license",
                false,
                true,
                false,
                this
        );
    }

    private void addZamaColumn() {
        CustomNumericColumn.addToGrid(
                Vehicle::getZama,
                Vehicle::setZama,
                140,
                "zamaColumn",
                "zama",
                false,
                true,
                false,
                this
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
        CustomComboBoxColumn.addToGrid(
                CustomComboBox.getComboBox(vehicleTypeRepository),
                CustomComboBox.getComboBox(vehicleTypeRepository),
                Vehicle::getVehicleType,
                Vehicle::setVehicleType,
                230,
                "vehicleTypeColumn",
                "vehicleType",
                this
        );
    }

    @Override
    protected void sort() {
        super.sort("vehicleTypeColumn", SortDirection.ASCENDING);
    }
}
