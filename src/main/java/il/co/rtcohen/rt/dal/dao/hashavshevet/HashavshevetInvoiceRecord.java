package il.co.rtcohen.rt.dal.dao.hashavshevet;

import il.co.rtcohen.rt.dal.dao.interfaces.AbstractType;
import il.co.rtcohen.rt.dal.dao.interfaces.Cloneable;
import il.co.rtcohen.rt.utils.Date;

public class HashavshevetInvoiceRecord extends AbstractType implements Cloneable<HashavshevetInvoiceRecord> {
    public final int documentRowID;
    public final int documentID;
    public final float amount;
    public final String itemName;
    public final int invoiceNum;
    public final Date invoiceDate;

    public HashavshevetInvoiceRecord(
            int documentRowID, int documentID,
            float amount, String itemName, int invoiceNum, Date invoiceDate) {
        this.documentRowID = documentRowID;
        this.documentID = documentID;
        this.amount = amount;
        this.itemName = itemName;
        this.invoiceNum = invoiceNum;
        this.invoiceDate = invoiceDate;
    }

    public HashavshevetInvoiceRecord(HashavshevetInvoiceRecord other) {
        super(other);
        this.documentRowID = other.documentRowID;
        this.documentID = other.documentID;
        this.amount = other.amount;
        this.itemName = other.itemName;
        this.invoiceNum = other.invoiceNum;
        this.invoiceDate = other.invoiceDate;
    }

    @Override
    public HashavshevetInvoiceRecord cloneObject() {
        return new HashavshevetInvoiceRecord(this);
    }

    @Override
    public String getObjectName() {
        return this.getClass().getName();
    }

    public int getDocumentID() {
        return documentID;
    }

    public int getDocumentRowID() {
        return documentRowID;
    }

    public float getAmount() {
        return this.amount;
    }

    public String getItemName() {
        return this.itemName;
    }
}
