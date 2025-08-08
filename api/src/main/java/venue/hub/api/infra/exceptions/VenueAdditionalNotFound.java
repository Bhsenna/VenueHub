package venue.hub.api.infra.exceptions;

import org.springframework.http.HttpStatus;

public class VenueAdditionalNotFound extends CustomException {
    public VenueAdditionalNotFound(String message, HttpStatus httpStatus) {
        super(httpStatus, message);
    }
}
