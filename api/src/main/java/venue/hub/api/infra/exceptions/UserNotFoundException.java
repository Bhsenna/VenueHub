package venue.hub.api.infra.exceptions;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends CustomException {
    public UserNotFoundException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
