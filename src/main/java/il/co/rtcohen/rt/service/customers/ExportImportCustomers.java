package il.co.rtcohen.rt.service.customers;

import il.co.rtcohen.rt.dal.dao.Customer;
import il.co.rtcohen.rt.dal.dao.hashavshevet.HashavshevetDataRecord;
import il.co.rtcohen.rt.dal.repositories.*;

import il.co.rtcohen.rt.dal.repositories.hashavshevet.HashavshevetRepositoryFullData;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

@Service
public class ExportImportCustomers {
    @Autowired private CustomerRepository customerRepository;
    @Autowired private HashavshevetRepositoryFullData hashavshevetRepositoryFullData;

    private static final String internalIdHeader = "InternalID";
    private static final String hashavshetetIdHeader = "HashavshetetID";
    private static final String nameHeader = "Name";
    private static final String[] headers = new String[]{internalIdHeader, hashavshetetIdHeader, nameHeader};

    public void exportRtCustomersToCSV(String filePath) {
        getLogger().info("Starting to create customers file: " + filePath);
        List<List<String>> customersData = new ArrayList<>();
        for (Customer customer : customerRepository.getItems()) {
            Integer internalId = customer.getId();
            Integer hashavshetetID = customer.getHashavshevetCustomerId();
            String name = customer.getName();
            customersData.add(Arrays.asList(internalId.toString(), hashavshetetID.toString(), name));
        }
        exportCustomersToCSV(filePath, customersData);
    }

    public void exportHashavshevetCustomersToCSV(String filePath) {
        getLogger().info("Starting to create customers file: " + filePath);
        HashMap<Integer, List<String>> customersData = new HashMap<>();
        for (HashavshevetDataRecord hashavshevetDataRecord : hashavshevetRepositoryFullData.getItems()) {
            Integer hashavshetetID = Integer.parseInt(hashavshevetDataRecord.customerKey);
            String name = hashavshevetDataRecord.customerName;
            Customer customer = customerRepository.getItemByHashKey(hashavshetetID);
            if (null == customer) {
                customer = customerRepository.getItemByName(name);
            }
            Integer internalId = (null == customer ? 0 : customer.getId());
            customersData.put(hashavshetetID, Arrays.asList(internalId.toString(), hashavshetetID.toString(), name));
        }
        exportCustomersToCSV(filePath, new ArrayList<>(customersData.values()));
    }

    private void exportCustomersToCSV(String filePath, List<List<String>> customersData) {
        try {
            FileWriter fileWriter = new FileWriter(filePath);
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder().setHeader(headers).build();
            final CSVPrinter csvPrinter = new CSVPrinter(fileWriter, csvFormat);
            for (List<String> customerData : customersData) {
                csvPrinter.printRecord(customerData);
            }
            getLogger().info("Customers file was created: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            getLogger().info("Failed to create customers file: " + filePath);
        }
    }

    public void importCustomersCSV(String filePath) {
        getLogger().info("Starting to import customers file: " + filePath);
        try {
            Reader fileWriter = new FileReader(filePath);
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder().setHeader(headers).setSkipHeaderRecord(true).build();
            Iterable<CSVRecord> records = csvFormat.parse(fileWriter);
            int errors = 0;
            for (CSVRecord record : records) {
                try {
                    Integer internalId = Integer.parseInt(record.get(internalIdHeader));
                    Integer hashavshetetID = Integer.parseInt(record.get(hashavshetetIdHeader));
                    String name = record.get(nameHeader);
                    Customer customer = (0 == internalId ? new Customer() : customerRepository.getItem(internalId));
                    if (!customer.getName().equals(name) || !customer.getHashavshevetCustomerId().equals(hashavshetetID)) {
                        boolean newCustomer = (0 == internalId);
                        customer.setHashavshevetCustomerId(hashavshetetID);
                        customer.setName(name);
                        customerRepository.updateItem(customer);
                        getLogger().info(customer + " was " + (newCustomer ? "created" : "updated"));
                    }
                } catch (Exception ignored) {
                    getLogger().info("Failed to handel customer data: " + record);
                    errors++;
                }
            }
            if (0 < errors) {
                throw new IOException("Failed to read " + errors + " records");
            }
            getLogger().info("Customers file was imported: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            getLogger().info("Failed to import customers file: " + filePath);
        }
    }

    private Logger getLogger() {
        return Logger.getLogger(this.getClass().getName());
    }
}
