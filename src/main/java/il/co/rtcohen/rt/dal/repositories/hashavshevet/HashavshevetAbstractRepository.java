package il.co.rtcohen.rt.dal.repositories.hashavshevet;

import il.co.rtcohen.rt.dal.dao.hashavshevet.HashavshevetDataRecord;
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
abstract public class HashavshevetAbstractRepository
        extends AbstractTypeRepository<HashavshevetDataRecord> {
    static protected final String DB_COLUMN_DocumentRowID = "DocumentRowId";
    static protected final String DB_COLUMN_DocumentID = "DocumentID";
    static protected final String DB_COLUMN_DocumentType = "DocumentType";
    static protected final String DB_COLUMN_CustomerKey = "CustomerKey";
    static protected final String DB_COLUMN_CustomerName = "CustomerName";
    static protected final String DB_COLUMN_CustomerAddress = "CustomerAddress";
    static protected final String DB_COLUMN_CustomerCity = "CustomerCity";
    static protected final String DB_COLUMN_CustomerPhones = "CustomerPhones";
    static protected final String DB_COLUMN_SiteAddress = "SiteAddress";
    static protected final String DB_COLUMN_SiteCity = "SiteCity";
    static protected final String DB_COLUMN_contact = "contact";
    static protected final String DB_COLUMN_VehicleSeriesOrLicense = "VehicleSeriesOrLicense";
    static protected final String DB_COLUMN_VehicleModel = "VehicleModel";
    static protected final String DB_COLUMN_VehicleType = "VehicleType";
    static protected final String DB_COLUMN_Amount = "Amount";
    static protected final String DB_COLUMN_ItemName = "ItemName";
    static protected final String DB_COLUMN_InvoiceNum = "InvoiceNum";
    static protected final String DB_COLUMN_InvoiceDate = "InvoiceDate";

    @Autowired
    protected HashavshevetAbstractRepository(DataSource dataSource, String dbTableName) {
        super(dataSource, dbTableName, dbTableName,
                new String[] {
                        DB_COLUMN_DocumentRowID,
                        DB_COLUMN_DocumentID,
                        DB_COLUMN_DocumentType,
                        DB_COLUMN_CustomerKey,
                        DB_COLUMN_CustomerName,
                        DB_COLUMN_CustomerAddress,
                        DB_COLUMN_CustomerCity,
                        DB_COLUMN_CustomerPhones,
                        DB_COLUMN_SiteAddress,
                        DB_COLUMN_SiteCity,
                        DB_COLUMN_contact,
                        DB_COLUMN_VehicleSeriesOrLicense,
                        DB_COLUMN_VehicleModel,
                        DB_COLUMN_VehicleType,
                        DB_COLUMN_Amount,
                        DB_COLUMN_ItemName,
                        DB_COLUMN_InvoiceNum,
                        DB_COLUMN_InvoiceDate
                });
        this.setCacheable(false);
    }

    @Override
    protected int updateItemDetailsInStatement(PreparedStatement preparedStatement,
            HashavshevetDataRecord hashavshevetDataRecord) throws SQLException {
        int fieldsCounter = 1;
        preparedStatement.setInt(fieldsCounter, hashavshevetDataRecord.documentRowID);
        fieldsCounter++;
        preparedStatement.setInt(fieldsCounter, hashavshevetDataRecord.documentID);
        fieldsCounter++;
        preparedStatement.setString(fieldsCounter,
                (hashavshevetDataRecord.documentType == null ? null : hashavshevetDataRecord.documentType.name));
        fieldsCounter++;
        preparedStatement.setString(fieldsCounter, hashavshevetDataRecord.customerKey);
        fieldsCounter++;
        preparedStatement.setString(fieldsCounter, hashavshevetDataRecord.customerName);
        fieldsCounter++;
        preparedStatement.setString(fieldsCounter, hashavshevetDataRecord.customerAddress);
        fieldsCounter++;
        preparedStatement.setString(fieldsCounter, hashavshevetDataRecord.customerCityAndZip);
        fieldsCounter++;
        preparedStatement.setString(fieldsCounter, hashavshevetDataRecord.customerPhonesStr);
        fieldsCounter++;
        preparedStatement.setString(fieldsCounter, hashavshevetDataRecord.siteAddress);
        fieldsCounter++;
        preparedStatement.setString(fieldsCounter, hashavshevetDataRecord.siteCityAndZip);
        fieldsCounter++;
        preparedStatement.setString(fieldsCounter, hashavshevetDataRecord.siteContactNameAndPhones);
        fieldsCounter++;
        preparedStatement.setString(fieldsCounter, hashavshevetDataRecord.vehicleSeriesOrLicense);
        fieldsCounter++;
        preparedStatement.setString(fieldsCounter, hashavshevetDataRecord.vehicleModel);
        fieldsCounter++;
        preparedStatement.setString(fieldsCounter, hashavshevetDataRecord.vehicleType);
        fieldsCounter++;
        preparedStatement.setFloat(fieldsCounter, hashavshevetDataRecord.amount);
        fieldsCounter++;
        preparedStatement.setString(fieldsCounter, hashavshevetDataRecord.itemName);
        fieldsCounter++;
        preparedStatement.setInt(fieldsCounter, hashavshevetDataRecord.invoiceNum);
        fieldsCounter++;
        preparedStatement.setString(fieldsCounter, hashavshevetDataRecord.invoiceDate.toString());
        fieldsCounter++;
        return fieldsCounter;
    }

    protected HashavshevetDataRecord getItemFromResultSet(ResultSet rs) throws SQLException {
        return new HashavshevetDataRecord(
                rs.getInt(DB_COLUMN_DocumentRowID),
                rs.getInt(DB_COLUMN_DocumentID),
                HashavshevetDataRecord.DocumentType.getDocumentType(rs.getString(DB_COLUMN_DocumentType)),
                rs.getString(DB_COLUMN_CustomerKey),
                rs.getString(DB_COLUMN_CustomerName),
                rs.getString(DB_COLUMN_CustomerAddress),
                rs.getString(DB_COLUMN_CustomerCity),
                rs.getString(DB_COLUMN_CustomerPhones),
                rs.getString(DB_COLUMN_SiteAddress),
                rs.getString(DB_COLUMN_SiteCity),
                rs.getString(DB_COLUMN_contact),
                rs.getString(DB_COLUMN_VehicleSeriesOrLicense),
                rs.getString(DB_COLUMN_VehicleModel),
                rs.getString(DB_COLUMN_VehicleType),
                rs.getFloat(DB_COLUMN_Amount),
                rs.getString(DB_COLUMN_ItemName),
                rs.getInt(DB_COLUMN_InvoiceNum),
                new Date(rs.getString(DB_COLUMN_InvoiceDate)));
    }

    @Override
    public List<HashavshevetDataRecord> getItems() {
        List<HashavshevetDataRecord> list = super.getItems();
        list.sort(Comparator.comparingInt(HashavshevetDataRecord::getCustomerKey)
                .thenComparing(HashavshevetDataRecord::getDocumentID));
        return list;
    }

    public List<HashavshevetDataRecord> getItemsByHashKey(Integer hashKey) {
        return super.getItems(DB_COLUMN_CustomerKey + "='" + hashKey.toString() + "'");
    }

    public List<HashavshevetDataRecord> getItemsByCustomerName(String customerName) {
        return super.getItems(DB_COLUMN_CustomerName + " like '" + customerName.replaceAll("'", "_") + "'");
    }
}
