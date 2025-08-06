package venue.hub.api.infra.exceptions;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends CustomException {
    public UserNotFoundException(String message, HttpStatus httpStatus) {
        super(httpStatus, message);
    }
}
