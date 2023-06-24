package il.co.rtcohen.rt.service.cities;

import il.co.rtcohen.rt.dal.dao.City;
import il.co.rtcohen.rt.dal.dao.Site;
import il.co.rtcohen.rt.dal.repositories.CityRepository;
import il.co.rtcohen.rt.dal.repositories.SiteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.logging.Logger;

@Service
public class UpdateSitesCities {
    @Autowired
    SiteRepository siteRepository;

    @Autowired
    CityRepository cityRepository;

    @Autowired
    IsraelCities israelCities;

    public void updateCityInAllSites() {
        for (Site site : siteRepository.getItems()) {
            if (null == site.getCity()) {
                updateSiteCity(site);
            }
        }
    }

    private void updateSiteCity(Site site) {
        assert null == site.getCity();
        City city = israelCities.findCityNameInAddress(site.getAddress());
        if (null != city) {
            if (null == city.getArea() && null != site.getArea()) {
                city.setArea(site.getArea());
                cityRepository.updateItem(city);
            }
            site.setCity(city);
            siteRepository.updateItem(site);
        }
    }

    private static Logger getLogger() {
        return Logger.getLogger(UpdateSitesCities.class.getName());
    }
}
