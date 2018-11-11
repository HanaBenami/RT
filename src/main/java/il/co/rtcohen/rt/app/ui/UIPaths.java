package il.co.rtcohen.rt.app.ui;

public enum UIPaths {

    EDITCALL("/editCall#"),
    EDITSITE("/editSite#"),
    PRINT("/print#"),
    BIGSCREEN("/bigScreen#");

    private final String path;

    UIPaths(String string) {
        path=string;
    }

    public String getPath() {
        return path;
    }

}
