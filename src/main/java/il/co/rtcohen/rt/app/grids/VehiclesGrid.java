package il.co.rtcohen.rt.app.grids;

import il.co.rtcohen.rt.app.ui.UIPaths;
import il.co.rtcohen.rt.app.uiComponents.columns.*;
import il.co.rtcohen.rt.app.uiComponents.fields.CustomButton;
import il.co.rtcohen.rt.app.uiComponents.fields.CustomComboBox;
import il.co.rtcohen.rt.dal.dao.Site;
import il.co.rtcohen.rt.dal.dao.Vehicle;
import il.co.rtcohen.rt.dal.repositories.CallRepository;
import il.co.rtcohen.rt.dal.repositories.SiteRepository;
import il.co.rtcohen.rt.dal.repositories.VehicleRepository;
import il.co.rtcohen.rt.dal.repositories.VehicleTypeRepository;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Component;

public class VehiclesGrid extends AbstractTypeFilterGrid<Vehicle> {
    private final Site site;
    private final SiteRepository siteRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final CallRepository callRepository;

    public VehiclesGrid(Site selectedSite,
            SiteRepository siteRepository,
            VehicleRepository vehicleRepository,
            VehicleTypeRepository vehicleTypeRepository,
            CallRepository callRepository,
            boolean applyDefaultFilters) {
        super(vehicleRepository, () -> {
            Vehicle vehicle = new Vehicle();
            vehicle.setSite(selectedSite);
            return vehicle;
        },
                "vehiclesOfSites",
                vehicle -> null == vehicle.getSite() || !vehicle.getSite().equals(selectedSite), applyDefaultFilters);
        this.site = (null == selectedSite || selectedSite.isDraft() ? null : selectedSite);
        this.siteRepository = siteRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
        this.callRepository = callRepository;
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
        if (null == this.site) {
            errorMessageKey = "noSite";
        } else if (0 == this.getItemsCounter()) {
            warningMessageKey = "noVehiclesToSite";
        }
        this.setErrorMessage(errorMessageKey);
        this.setWarningMessage(warningMessageKey);
    }

    protected void addColumns() {
        addActiveColumn();
        addAddingCallColumn();
        addCallsColumn();
        addLastUpdateColumn();
        addLicenseColumn();
        addZamaColumn();
        addEngineHoursColumn();
        addSeriesColumn();
        addModelColumn();
        addVehicleTypeColumn();
        addSiteColumn();
        addIdColumn();
    }

    private void addActiveColumn() {
        CustomCheckBoxColumn.addToGrid(
                Vehicle::isActive,
                Vehicle::setActive,
                "activeColumn",
                "active",
                Boolean.TRUE,
                this);
    }

    private void addAddingCallColumn() {
        CustomComponentColumn<Vehicle, Component> column = CustomComponentColumn.addToGrid(
                vehicle -> new CustomButton(
                        VaadinIcons.PLUS,
                        false,
                        UIPaths.EDITCALL.getEditCallPath(null, null, vehicle),
                        UIPaths.EDITCALL.getWindowHeight(),
                        UIPaths.EDITCALL.getWindowWidth(),
                        UIPaths.EDITCALL.getWindowName()),
                60,
                "addCallColumn",
                "addCall",
                this);
        column.getColumn().setStyleGenerator(vehicle -> "red");
        column.getColumn().setHidable(false);
        column.getColumn().setHidden(false);
    }

    private void addCallsColumn() {
        addCallsColumn(
                vehicle -> callRepository.getItems(null, null, vehicle, false).size(),
                "vehicle");
    }

    private void addLastUpdateColumn() {
        CustomDateColumn.addToGrid(
                Vehicle::getLastUpdate,
                null,
                null, "lastUpdateColumn",
                "lastUpdate",
                false,
                this);
    }

    private void addEngineHoursColumn() {
        CustomIntegerColumn.addToGrid(
                Vehicle::getEngineHours,
                Vehicle::setEngineHours,
                null, null, 100,
                "engineHoursColumn",
                "engineHours",
                false,
                false,
                true,
                this);
    }

    private void addLicenseColumn() {
        CustomIntegerColumnWithStringValue.addToGrid(
                Vehicle::getFormattedLicense,
                Vehicle::setLicense,
                null, null, 100,
                "licenseColumn",
                "license",
                false,
                true,
                false,
                this);
    }

    private void addZamaColumn() {
        CustomIntegerColumn.addToGrid(
                Vehicle::getZama,
                Vehicle::setZama,
                null, null, 100,
                "zamaColumn",
                "zama",
                false,
                true,
                false,
                this);
    }

    private void addSeriesColumn() {
        CustomTextColumn<Vehicle> column = CustomTextColumn.addToGrid(
                Vehicle::getSeries,
                Vehicle::setSeries,
                false, 100,
                "seriesColumn",
                "series",
                false,
                true,
                false,
                this);
    }

    private void addModelColumn() {
        CustomTextColumn<Vehicle> column = CustomTextColumn.addToGrid(
                Vehicle::getModel,
                Vehicle::setModel,
                false, 100,
                "modelColumn",
                "model",
                false,
                true,
                false,
                this);
    }

    private void addVehicleTypeColumn() {
        CustomComboBoxColumn.addToGrid(
                CustomComboBox.getComboBox(vehicleTypeRepository),
                CustomComboBox.getComboBox(vehicleTypeRepository),
                Vehicle::getVehicleType,
                Vehicle::setVehicleType,
                true,
                300,
                "vehicleTypeColumn",
                "vehicleType",
                this);
    }

    private void addSiteColumn() {
        CustomComboBoxColumn<Site, Vehicle> column = CustomComboBoxColumn.addToGrid(
                CustomComboBox.getComboBox(siteRepository),
                CustomComboBox.getComboBox(siteRepository),
                Vehicle::getSite,
                null,
                true,
                150,
                "siteColumn",
                "site",
                this);
        column.getColumn().setHidable(true);
        column.getColumn().setHidden(true);
    }

    @Override
    protected void sort() {
        super.sort("vehicleTypeColumn", SortDirection.ASCENDING);
    }
}
