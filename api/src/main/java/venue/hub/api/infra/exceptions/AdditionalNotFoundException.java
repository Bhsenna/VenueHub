package venue.hub.api.infra.exceptions;

import org.springframework.http.HttpStatus;

public class AdditionalNotFoundException extends CustomException {
    public AdditionalNotFoundException(String message, HttpStatus httpStatus) {
        super(httpStatus, message);
    }
}