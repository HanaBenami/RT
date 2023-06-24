package il.co.rtcohen.rt.app.views;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import il.co.rtcohen.rt.app.uiComponents.CustomLabel;
import il.co.rtcohen.rt.app.uiComponents.fields.CustomButton;
import il.co.rtcohen.rt.service.cities.UpdateSitesCities;
import il.co.rtcohen.rt.service.customers.ExportImportCustomers;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Paths;
import java.util.function.Consumer;

@SpringView(name = ExportImportView.VIEW_NAME)
public class ExportImportView extends AbstractView {

    static final String VIEW_NAME = "ExportImportView";
    GridLayout gridLayout;
    int gridRowsCounter;

    @Autowired
    ExportImportCustomers exportImportCustomers;

    @Autowired
    UpdateSitesCities updateSitesCities;

    @Autowired
    private ExportImportView(ErrorHandler errorHandler) {
        super(errorHandler);
    }

    @PostConstruct
    protected void enter() {
        setDefaultComponentAlignment(Alignment.TOP_LEFT);
        setSizeFull();

        gridLayout = new GridLayout(2, 10);
        gridLayout.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        gridLayout.setSpacing(true);

        addToGrid("Export existing customers list", getDownloadComponent(
                "rt_customers.csv",
                fileName -> exportImportCustomers.exportRtCustomersToCSV(fileName)
        ));


        addToGrid("Export hashavshevet customers list", getDownloadComponent(
        "hashavshevet_customers.csv",
                fileName -> exportImportCustomers.exportHashavshevetCustomersToCSV(fileName)
        ));

        addToGrid("Import customers list", getUploadComponent(
                fileName -> exportImportCustomers.importCustomersCSV(fileName)
        ));

        addToGrid("Update city according to address in all the sites",
                getActionComponent(() -> updateSitesCities.updateCityInAllSites())
        );

        addComponent(gridLayout);
    }

    private void addToGrid(String labelStr, Component component) {
        gridLayout.addComponent(new CustomLabel(labelStr, null), 0, gridRowsCounter);
        gridLayout.addComponent(component, 1, gridRowsCounter);
        gridRowsCounter++;
    }

    private CustomButton getDownloadComponent(String filePath, Consumer<String> fileGeneratorFunction) {
        CustomButton downloadButton = new CustomButton(VaadinIcons.DOWNLOAD, true,
                clickEvent -> fileGeneratorFunction.accept(filePath)
        );
        StreamResource streamResource = createResource(filePath);
        FileDownloader fileDownloader = new FileDownloader(streamResource);
        fileDownloader.extend(downloadButton);
        return downloadButton;
    }

    private Upload getUploadComponent(Consumer<String> fileHandlerFunction) {
        Upload upload = new Upload("Upload File", (Upload.Receiver) (fileName, mimeType) -> {
            try {
                String filePath = Paths.get(Paths.get("").toAbsolutePath().toString(), fileName).toString();
                File file = new File(filePath);
                fileHandlerFunction.accept(filePath);
                return new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        });
        upload.addSucceededListener((Upload.SucceededListener) event -> {
            Notification.show("File uploaded successfully!");
        });
        upload.addFailedListener((Upload.FailedListener) event -> {
            Notification.show("Something went wrong - Please check the logs", Notification.Type.ERROR_MESSAGE);
        });
        return upload;
    }

    private CustomButton getActionComponent(Runnable runnable) {
        return new CustomButton(VaadinIcons.CIRCLE, true,
                clickEvent -> {
                    runnable.run();
                    Notification.show("Done");
                }
        );
    }

    private StreamResource createResource(String filePath) {
        return new StreamResource((StreamResource.StreamSource) () -> {
            try {
                return new FileInputStream(new File(filePath));
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }, filePath);
    }
}
