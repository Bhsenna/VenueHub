package venue.hub.api.infra.exceptions;

import org.springframework.http.HttpStatus;

public class VenueNotFoundException extends CustomException {
    public VenueNotFoundException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
