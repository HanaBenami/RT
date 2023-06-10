package il.co.rtcohen.rt.app.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;
import java.sql.SQLException;
import java.util.HashMap;

import il.co.rtcohen.rt.app.LanguageSettings;
import il.co.rtcohen.rt.dal.dao.User;
import il.co.rtcohen.rt.dal.repositories.CallRepository;
import il.co.rtcohen.rt.dal.repositories.GeneralRepository;
import il.co.rtcohen.rt.dal.repositories.UsersRepository;

@Theme("myTheme")
public abstract class AbstractUI<T extends Layout> extends UI {
    T layout;
    CallRepository callRepository;
    GeneralRepository generalRepository;
    UsersRepository usersRepository;

    AbstractUI(
            ErrorHandler errorHandler,
            @Deprecated CallRepository callRepository,
            @Deprecated GeneralRepository generalRepository,
            UsersRepository usersRepository
    ) {
        setErrorHandler(errorHandler);
        this.callRepository = callRepository;
        this.generalRepository = generalRepository;
        this.usersRepository = usersRepository;
    }

    protected abstract void setupLayout() throws SQLException;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        try {
            setupLayout();
            getUI().setLocale(LanguageSettings.locale);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Deprecated
    int getSessionUsernameId() {
        return (int) getSession().getAttribute("userid");
    }

    User getSessionUsername() {
        return usersRepository.getItem((int)getSession().getAttribute("userid"));
    }

    protected HashMap<String, String> getParametersMap() {
        HashMap<String, String> parameters = new HashMap<>();
        for (String keyAndValueString : getPage().getUriFragment().split("&")) {
            String[] keyAndValueArray = keyAndValueString.split("=");
            if (2 == keyAndValueArray.length) {
                parameters.put(keyAndValueArray[0], keyAndValueArray[1]);
            }
        }
        return parameters;
    }

    void refreshWindow() {
        JavaScript.getCurrent().execute("location.reload();");
    }

    void closeWindow() {
        JavaScript.getCurrent().execute("setTimeout(function() {self.close();},500);");
    }
}
