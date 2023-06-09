package il.co.rtcohen.rt.dal.repositories.exceptions;

import java.sql.SQLException;

public class DeleteException extends RuntimeException {

    public DeleteException(String message, SQLException e) {
        super(message,e);
    }

}
