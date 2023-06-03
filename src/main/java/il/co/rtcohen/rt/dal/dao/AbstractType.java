package il.co.rtcohen.rt.dal.dao;

import il.co.rtcohen.rt.app.LanguageSettings;

abstract public class AbstractType {
    private static String dbTableName;
    private static String objectName;

    private Integer id;

    public AbstractType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(long id) {
        this.id = (int) id;
    }

    public boolean isItemValid() {
        return true;
    }

    public static void setDbTableName(String dbTableName) {
        AbstractType.dbTableName = dbTableName;
    }

    public static String getDbTableName() {
        return dbTableName;
    }

    public static void setObjectName(String objectName) {
        AbstractType.objectName = objectName;
    }

    public static String getObjectName() {
        return objectName;
    }

    @Override
    public String toString() {
        return (null == getId() ? LanguageSettings.getLocaleString("the") : "")
                + LanguageSettings.getLocaleString((null == getObjectName() ? "item" : getObjectName()))
                + (null == getId() ? "" : (" #" + getId()));
    }
}
