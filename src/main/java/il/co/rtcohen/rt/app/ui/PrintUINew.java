package il.co.rtcohen.rt.app.ui;


import com.vaadin.server.ErrorHandler;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import il.co.rtcohen.rt.dal.repositories.CallRepository;
import il.co.rtcohen.rt.dal.repositories.GeneralRepository;
import il.co.rtcohen.rt.dal.repositories.UsersRepository;

import java.sql.SQLException;
import java.util.HashMap;

// TODO - Not ready. Should replace PrintUI
@SpringComponent
@SpringUI(path="/printtttt")
public class PrintUINew extends AbstractUI<VerticalLayout> {

    static public HashMap<String, AvailableReport> urlFragmentToReport = new HashMap<>();

    PrintUINew(ErrorHandler errorHandler, CallRepository callRepository, GeneralRepository generalRepository, UsersRepository usersRepository) {
        super(errorHandler, callRepository, generalRepository, usersRepository);
    }

    @Override
    protected void setupLayout() throws SQLException {

    }

    private enum AvailableReport {
        VEHICLES_HERE("here"),
        OPEN_CALLS("open"),
        WORK_SCHEDULE("");

        AvailableReport(String urlFragment) {
            urlFragmentToReport.put(urlFragment, this);
        }
    }
}