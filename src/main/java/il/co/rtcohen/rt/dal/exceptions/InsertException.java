package il.co.rtcohen.rt.dal.exceptions;

import java.sql.SQLException;

public class InsertException extends RuntimeException {

    public InsertException(String message) {
        super(message);
    }

    public InsertException(String message, SQLException e) {
        super(message,e);
    }

}
