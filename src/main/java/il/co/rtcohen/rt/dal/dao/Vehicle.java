package il.co.rtcohen.rt.dal.dao;

import il.co.rtcohen.rt.app.LanguageSettings;
import il.co.rtcohen.rt.dal.dao.interfaces.AbstractTypeSyncedWithHashavshevet;
import il.co.rtcohen.rt.dal.dao.interfaces.BindRepository;
import il.co.rtcohen.rt.dal.dao.interfaces.Cloneable;
import il.co.rtcohen.rt.utils.Date;

import java.time.LocalDate;

public class Vehicle extends AbstractTypeSyncedWithHashavshevet implements BindRepository<Vehicle>, Cloneable<Vehicle> {
    private Site site;
    private VehicleType vehicleType;
    private String model = "";
    private String series = "";
    private int zama = 0;
    private int license = 0;
    private int engineHours = 0;
    private Date lastUpdate;

    public Vehicle() {
        super();
    }

    public Vehicle(Integer id, String name, boolean active, int hashavshevetFirstDocId, Site site, VehicleType vehicleType, String model,
                   String series, Integer zama, Integer license, Integer engineHours, Date lastUpdate) {
        super(id, name, active, hashavshevetFirstDocId);
        this.site = site;
        this.vehicleType = vehicleType;
        this.model = model;
        this.series = series;
        this.zama = zama;
        this.license = license;
        this.engineHours = engineHours;
        this.lastUpdate = lastUpdate;
    }

    public Vehicle(Vehicle other) {
        super(other);
        this.site = other.site;
        this.vehicleType = other.vehicleType;
        this.model = other.model;
        this.series = other.series;
        this.zama = other.zama;
        this.license = other.license;
        this.engineHours = other.engineHours;
        this.lastUpdate = other.lastUpdate;
    }

    @Override
    public Vehicle cloneObject() {
        return new Vehicle(this);
    }

    @Override
    public String getObjectName() {
        return "vehicle";
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public VehicleType getVehicleType() {
        return this.vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
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

    public String getFormattedLicense() {
        String license = this.getLicense().toString();
        if (8 == license.length()) {
            license = license.substring(0, 3) + "-" + license.substring(3, 5) + "-" + license.substring(5, 8);
        } else if (7 == license.length()) {
            license = license.substring(0, 2) + "-" + license.substring(2, 5) + "-" + license.substring(5, 7);
        }
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

    public Date getLastUpdate() {
        return this.lastUpdate;
    }

    public void setLastUpdate() {
        setLastUpdate(LocalDate.now());
    }

    public void setLastUpdate(LocalDate lastUpdate) {
        this.lastUpdate = new Date(lastUpdate);
    }

    @Override
    public boolean isItemValid() {
        return (null != this.getVehicleType()) && (null != this.getSite());
    }

    @Override
    public String getName() {
        return (null == getVehicleType() ? "" : this.getVehicleType().getName())
                + " ("
                + LanguageSettings.getLocaleString("site")
                + ": " + (null == getSite() ? "" : this.getSite().getName())
                + ")";
    }
}
