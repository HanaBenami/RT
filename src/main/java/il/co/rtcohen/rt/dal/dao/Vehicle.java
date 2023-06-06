package il.co.rtcohen.rt.dal.dao;

import il.co.rtcohen.rt.dal.repositories.VehicleTypeRepository;

import java.time.LocalDate;

public class Vehicle extends GeneralObject {
    static {
        setDbTableName("vehicle");
        setObjectName("vehicle");
    }

    private Integer siteId;
    private Integer vehicleTypeId;
    private String model = "";
    private String series = "";
    private int zama = 0;
    private int license = 0;
    private int engineHours = 0;
    private LocalDate lastUpdate;

    public Vehicle() {
        super();
    }

    public Vehicle(Integer id, String name, boolean active, Integer siteId, Integer VehicleTypeId, String model,
                   String series, Integer zama, Integer license, Integer engineHours, LocalDate lastUpdate) {
        super(id, name, active);
        this.siteId = siteId;
        this.vehicleTypeId = VehicleTypeId;
        this.model = model;
        this.series = series;
        this.zama = zama;
        this.license = license;
        this.engineHours = engineHours;
        this.lastUpdate = lastUpdate;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getVehicleTypeId() {
        return vehicleTypeId;
    }

    public void setVehicleTypeId(Integer VehicleTypeId) {
        this.vehicleTypeId = VehicleTypeId;
    }

    public GeneralObject getVehicleType(VehicleTypeRepository vehicleTypeRepository) {
        return vehicleTypeRepository.getItem(this.getVehicleTypeId());
    }


    public void setVehicleType(GeneralObject generalObject) {
        this.vehicleTypeId = generalObject.getId();
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public Integer getZama() {
        return zama;
    }

    public void setZama(Integer zama) {
        this.zama = zama;
    }

    public Integer getLicense() {
        return license;
    }

    public void setLicense(Integer license) {
        this.license = license;
    }

    public Integer getEngineHours() {
        return engineHours;
    }

    public void setEngineHours(Integer engineHours) {
        this.engineHours = engineHours;
    }

    public LocalDate getLastUpdate() {
        return this.lastUpdate;
    }

    public void setLastUpdate() {
        setLastUpdate(LocalDate.now());
    }

    public void setLastUpdate(LocalDate lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public boolean isItemValid() {
        return (null != this.getVehicleTypeId()) && (null != this.getSiteId());
    }
}
