package venue.hub.api.infra.exceptions;

import org.springframework.http.HttpStatus;

public class EventNotFoundException extends CustomException {
    public EventNotFoundException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
