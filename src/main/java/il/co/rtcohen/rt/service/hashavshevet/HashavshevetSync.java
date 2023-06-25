package il.co.rtcohen.rt.service.hashavshevet;

import il.co.rtcohen.rt.dal.dao.hashavshevet.HashavshevetDataRecord;
import il.co.rtcohen.rt.dal.repositories.*;
import il.co.rtcohen.rt.dal.repositories.hashavshevet.*;
import il.co.rtcohen.rt.service.cities.IsraelCities;

import il.co.rtcohen.rt.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class HashavshevetSync {
    static Lock lock = new ReentrantLock();

    @Autowired private HashavshevetRepositoryFullData hashavshevetRepositoryFullData;
    @Autowired private HashavshevetRepositoryDataAlreadyMerged hashavshevetRepositoryDataAlreadyMerged;
    @Autowired private HashavshevetRepositoryDataDiff hashavshevetRepositoryDataDiff;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private SiteRepository siteRepository;
    @Autowired private CityRepository cityRepository;
    @Autowired private ContactRepository contactRepository;
    @Autowired private VehicleRepository vehicleRepository;
    @Autowired private VehicleTypeRepository vehicleTypeRepository;
    @Autowired private IsraelCities israelCities;

    @Value("${settings.hashSync.executeScheduleSync}")
    boolean executeScheduleSync;

    @Value("${settings.hashSync.syncNewCustomers}")
    boolean syncNewCustomers;

    @Value("${settings.hashSync.syncOnlyDiff}")
    boolean syncOnlyDiff;

    @Value("${settings.hashSync.syncOnlyListedCustomers}")
    boolean syncOnlyListedCustomers;

    @Value("${settings.hashSync.hashavshevetCustomerIdsToSync}")
    int[] hashavshevetCustomerIdsToSync;

    public void syncData() {
        if (executeScheduleSync) {
            if (syncOnlyListedCustomers) {
                for (int hashavshevetCustomerId : hashavshevetCustomerIdsToSync) {
                    syncData(hashavshevetCustomerId, true);
                }
            } else {
                syncData(null, true);
            }
        }
    }

    public void syncData(Integer hashavshevetCustomerId, boolean waitForLock) throws CannotAcquireLockException {
        if (waitForLock) {
            lock.lock();
        } else {
            boolean locked = lock.tryLock();
            if (!locked) {
                String msg = "Cannot start another hashavshevet sync - Previous sync is still in progress";
                Logger.getLogger(this).info(msg);
                throw new CannotAcquireLockException(msg);
            }
        }

        try {
            Logger.getLogger(this).info("Going to start sync: syncOnlyDiff=" + syncOnlyDiff + ", hashavshevetCustomerId=" + hashavshevetCustomerId);
            HashavshevetAbstractRepository hashavshevetDataRepository = (syncOnlyDiff ? hashavshevetRepositoryDataDiff : hashavshevetRepositoryFullData);
            List<HashavshevetDataRecord> hashavshevetDataRecords = (
                    null == hashavshevetCustomerId
                            ? hashavshevetDataRepository.getItems()
                            : hashavshevetDataRepository.getItemsByHashKey(hashavshevetCustomerId)
            );
            Logger.getLogger(this).info(hashavshevetDataRecords.size() + " records were found and going to be synced");
            for (HashavshevetDataRecord hashavshevetDataRecord : hashavshevetDataRecords) {
                HashavshevetSyncSingleRecord hashavshevetSyncSingleRecord = new HashavshevetSyncSingleRecord(hashavshevetDataRecord, israelCities);
                hashavshevetSyncSingleRecord.syncData(customerRepository, siteRepository, cityRepository, contactRepository, vehicleRepository, vehicleTypeRepository, syncNewCustomers);
                hashavshevetRepositoryDataAlreadyMerged.insertItem(hashavshevetDataRecord);
            }
        } finally {
            lock.unlock();
        }
    }
}
