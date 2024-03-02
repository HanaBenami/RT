package il.co.rtcohen.rt.dal.repositories.hashavshevet;

import il.co.rtcohen.rt.dal.dao.hashavshevet.HashavshevetInvoiceRecord;
import il.co.rtcohen.rt.dal.repositories.interfaces.AbstractTypeRepository;
import il.co.rtcohen.rt.utils.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

@Repository
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class HashavshevetInvoiceRepository
        extends AbstractTypeRepository<HashavshevetInvoiceRecord> {
    static protected final String DB_TABLE_NAME = "v_hash_invoices_data";
    static protected final String DB_COLUMN_DocumentRowID = HashavshevetAbstractRepository.DB_COLUMN_DocumentRowID;
    static protected final String DB_COLUMN_DocumentID = HashavshevetAbstractRepository.DB_COLUMN_DocumentID;
    static protected final String DB_COLUMN_Amount = HashavshevetAbstractRepository.DB_COLUMN_Amount;
    static protected final String DB_COLUMN_ItemName = HashavshevetAbstractRepository.DB_COLUMN_ItemName;
    static protected final String DB_COLUMN_InvoiceNum = HashavshevetAbstractRepository.DB_COLUMN_InvoiceNum;
    static protected final String DB_COLUMN_InvoiceDate = HashavshevetAbstractRepository.DB_COLUMN_InvoiceDate;

    @Autowired
    protected HashavshevetInvoiceRepository(DataSource dataSource) {
        super(dataSource, DB_TABLE_NAME, DB_TABLE_NAME,
                new String[] {
                        DB_COLUMN_DocumentRowID,
                        DB_COLUMN_DocumentID,
                        DB_COLUMN_Amount,
                        DB_COLUMN_ItemName,
                        DB_COLUMN_InvoiceNum,
                        DB_COLUMN_InvoiceDate
                });
        this.setCacheable(false);
    }

    @Override
    protected int updateItemDetailsInStatement(PreparedStatement preparedStatement,
            HashavshevetInvoiceRecord hashavshevetInvoiceRecord) throws SQLException {
        int fieldsCounter = 1;
        preparedStatement.setInt(fieldsCounter, hashavshevetInvoiceRecord.documentRowID);
        fieldsCounter++;
        preparedStatement.setInt(fieldsCounter, hashavshevetInvoiceRecord.documentID);
        fieldsCounter++;
        preparedStatement.setFloat(fieldsCounter, hashavshevetInvoiceRecord.amount);
        fieldsCounter++;
        preparedStatement.setString(fieldsCounter, hashavshevetInvoiceRecord.itemName);
        fieldsCounter++;
        preparedStatement.setInt(fieldsCounter, hashavshevetInvoiceRecord.invoiceNum);
        fieldsCounter++;
        preparedStatement.setString(fieldsCounter, hashavshevetInvoiceRecord.invoiceDate.toString());
        fieldsCounter++;
        return fieldsCounter;
    }

    protected HashavshevetInvoiceRecord getItemFromResultSet(ResultSet rs) throws SQLException {
        return new HashavshevetInvoiceRecord(
                rs.getInt(DB_COLUMN_DocumentRowID),
                rs.getInt(DB_COLUMN_DocumentID),
                rs.getFloat(DB_COLUMN_Amount),
                rs.getString(DB_COLUMN_ItemName),
                rs.getInt(DB_COLUMN_InvoiceNum),
                new Date(rs.getString(DB_COLUMN_InvoiceDate)));
    }

    protected void sort(List<HashavshevetInvoiceRecord> list) {
        list.sort(Comparator.comparingInt(HashavshevetInvoiceRecord::getDocumentID)
                .thenComparing(HashavshevetInvoiceRecord::getDocumentRowID));
    }

    @Override
    public List<HashavshevetInvoiceRecord> getItems() {
        List<HashavshevetInvoiceRecord> list = super.getItems();
        this.sort(list);
        return list;
    }

    public List<HashavshevetInvoiceRecord> getItemsByInvoiceNum(Integer invoiceNum) {
        List<HashavshevetInvoiceRecord> list = super.getItems(
                DB_COLUMN_InvoiceNum + "='" + invoiceNum.toString() + "'");
        this.sort(list);
        return list;
    }
}
