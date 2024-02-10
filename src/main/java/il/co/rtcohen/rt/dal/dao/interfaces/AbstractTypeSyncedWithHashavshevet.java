package il.co.rtcohen.rt.dal.dao.interfaces;

public class AbstractTypeSyncedWithHashavshevet extends AbstractTypeWithNameAndActiveFields {
    private int hashavshevetFirstDocId;

    public AbstractTypeSyncedWithHashavshevet() {
        super();
        this.hashavshevetFirstDocId = 0;
    }

    public AbstractTypeSyncedWithHashavshevet(Integer id, String name, boolean active, int hashavshevetFirstDocId) {
        super(id, name, active);
        this.hashavshevetFirstDocId = hashavshevetFirstDocId;
    }

    public AbstractTypeSyncedWithHashavshevet(AbstractTypeSyncedWithHashavshevet other) {
        super(other);
        this.hashavshevetFirstDocId = other.hashavshevetFirstDocId;
    }

    public int getHashavshevetFirstDocId() {
        return hashavshevetFirstDocId;
    }

    public void setHashavshevetFirstDocId(int hashavshevetFirstDocId) {
        this.hashavshevetFirstDocId = hashavshevetFirstDocId;
    }

    public boolean wasSyncedWithHashavshevet() {
        return this.getHashavshevetFirstDocId() != 0;
    }
}
