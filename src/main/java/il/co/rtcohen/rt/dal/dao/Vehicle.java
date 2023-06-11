package il.co.rtcohen.rt.dal.dao;

import il.co.rtcohen.rt.app.LanguageSettings;
import il.co.rtcohen.rt.dal.dao.interfaces.AbstractTypeWithNameAndActiveFields;
import il.co.rtcohen.rt.dal.dao.interfaces.BindRepository;
import il.co.rtcohen.rt.utils.Date;

import java.time.LocalDate;

public class Vehicle extends AbstractTypeWithNameAndActiveFields implements BindRepository<Vehicle> {
    static {
        setObjectName("vehicle");
    }

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

    public Vehicle(Integer id, String name, boolean active, Site site, VehicleType vehicleType, String model,
                   String series, Integer zama, Integer license, Integer engineHours, Date lastUpdate) {
        super(id, name, active);
        this.site = site;
        this.vehicleType = vehicleType;
        this.model = model;
        this.series = series;
        this.zama = zama;
        this.license = license;
        this.engineHours = engineHours;
        this.lastUpdate = lastUpdate;
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
        if (!isItemValid()) {
            return super.toString();
        } else {
            return (getVehicleType().getName() + " (" + LanguageSettings.getLocaleString("site") + ": " + getSite().getName() + ")");
        }
    }
}
