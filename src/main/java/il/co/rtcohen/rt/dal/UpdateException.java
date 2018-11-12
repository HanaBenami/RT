package il.co.rtcohen.rt.dal;

import java.sql.SQLException;

public class UpdateException extends RuntimeException {

    public UpdateException(String message, SQLException e) {
        super(message,e);
    }

}
