package venue.hub.api.infra.exceptions;

import org.springframework.http.HttpStatus;

public class VenueNotFound extends CustomException{
    public VenueNotFound(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
