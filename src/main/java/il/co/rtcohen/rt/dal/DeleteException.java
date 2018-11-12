package il.co.rtcohen.rt.dal;

import java.sql.SQLException;

public class DeleteException extends RuntimeException {

    public DeleteException(String message, SQLException e) {
        super(message,e);
    }

}
