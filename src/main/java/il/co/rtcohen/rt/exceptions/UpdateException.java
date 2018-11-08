package il.co.rtcohen.rt.exceptions;

import java.sql.SQLException;

public class UpdateException extends RuntimeException {

    public UpdateException(String message, SQLException e) {
        super(message,e);
    }

}
