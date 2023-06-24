package il.co.rtcohen.rt.service.hashavshevet;

import il.co.rtcohen.rt.dal.dao.hashavshevet.HashavshevetDataRecord;
import il.co.rtcohen.rt.dal.repositories.*;
import il.co.rtcohen.rt.dal.repositories.hashavshevet.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

// TODO: Add logs
// TODO: Tests w/o UI
// TODO: UI for the above X
// TODO: Scheduled tasks X
// TODO: parameters

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

    @Value("${settings.hashSync.executeSync}")
    boolean executeSync;

    @Value("${settings.hashSync.createNewCustomers}")
    boolean createNewCustomers;

    @Value("${settings.hashSync.syncOnlyDiff}")
    boolean syncOnlyDiff;

    @Value("${settings.hashSync.syncOnlyListedCustomers}")
    boolean syncOnlyListedCustomers;

    @Value("${settings.hashSync.hashavshevetCustomerIds}")
    int[] hashavshevetCustomerIds;

    public void syncData() {
        if (executeSync) {
            if (syncOnlyListedCustomers) {
                for (int hashavshevetCustomerId : hashavshevetCustomerIds) {
                    syncData(syncOnlyDiff, hashavshevetCustomerId, true);
                }
            } else {
                syncData(syncOnlyDiff, null, true);
            }
        }
    }

    public void syncData(boolean diffOnly, Integer hashavshevetCustomerId, boolean waitForLock) throws CannotAcquireLockException {
        if (waitForLock) {
            lock.lock();
        } else {
            boolean locked = lock.tryLock();
            if (!locked) {
                String msg = "Cannot start another hashavshevet sync - Previous sync is still in progress";
                getLogger().info(msg);
                throw new CannotAcquireLockException(msg);
            }
        }

        try {
            getLogger().info("Going to start sync: diffOnly=" + diffOnly + ", hashavshevetCustomerId=" + hashavshevetCustomerId);
            HashavshevetAbstractRepository hashavshevetDataRepository = (diffOnly ? hashavshevetRepositoryDataDiff : hashavshevetRepositoryFullData);
            List<HashavshevetDataRecord> hashavshevetDataRecords = (
                    null == hashavshevetCustomerId
                            ? hashavshevetDataRepository.getItems()
                            : hashavshevetDataRepository.getItemsByHashKey(hashavshevetCustomerId)
            );
            getLogger().info(hashavshevetDataRecords.size() + " records were found and going to be synced");
            for (HashavshevetDataRecord hashavshevetDataRecord : hashavshevetDataRecords) {
                HashavshevetSyncSingleRecord hashavshevetSyncSingleRecord = new HashavshevetSyncSingleRecord(hashavshevetDataRecord);
                hashavshevetSyncSingleRecord.syncData(customerRepository, siteRepository, cityRepository, contactRepository, vehicleRepository, vehicleTypeRepository, createNewCustomers);
                hashavshevetRepositoryDataAlreadyMerged.insertItem(hashavshevetDataRecord);
            }
        } finally {
            lock.unlock();
        }
    }

    private Logger getLogger() {
        return Logger.getLogger(this.getClass().getName());
    }
}
