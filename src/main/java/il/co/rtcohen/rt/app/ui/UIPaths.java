package il.co.rtcohen.rt.app.ui;

import il.co.rtcohen.rt.dal.dao.Call;
import il.co.rtcohen.rt.dal.dao.Customer;
import il.co.rtcohen.rt.dal.dao.Site;
import il.co.rtcohen.rt.dal.dao.Vehicle;

public enum UIPaths {
    EDITCALL("/editCall#", "_blank",1000, 1100),
    CALLS("#!calls/", "_blank", 1500, 1100),
    PRINT("/print#"),
    BIGSCREEN("/bigScreen#");

    private final String path;
    private final String windowName;
    private final int windowWidth;
    private final int windowHeight;

    UIPaths(String path) {
        this(path, null, 0, 0);
    }

    UIPaths(String string, String windowName, int windowWidth, int windowHeight) {
        this.path = string;
        this.windowName = windowName;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
    }

    public String getPath() {
        return path;
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public String getWindowName() {
        return windowName;
    }

    public String getEditCallPath(Call call) {
        return EDITCALL.path + "call=" + call.getId();
    }

    public String getEditCallPath(Customer customer, Site site, Vehicle vehicle) {
        String url = EDITCALL.path;
        if (null != customer) {
            url += "&customer=" + customer.getId();
        }
        if (null != site) {
            url += "&site=" + site.getId();
        }
        if (null != vehicle) {
            url += "&vehicle=" + vehicle.getId();
        }
        assert EDITCALL.path.endsWith("#");
        url = url.replace("#&", "#");
        return url;
    }
}
