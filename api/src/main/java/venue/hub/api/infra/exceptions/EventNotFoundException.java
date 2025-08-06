package venue.hub.api.infra.exceptions;

import org.springframework.http.HttpStatus;

public class EventNotFoundException extends CustomException {
    public EventNotFoundException(String message, HttpStatus httpStatus) {
        super(httpStatus, message);
    }
}
