package venue.hub.api.infra.exceptions;

import org.springframework.http.HttpStatus;

public class AdditionalAlreadyAddedException extends CustomException {
    public AdditionalAlreadyAddedException(String message, HttpStatus httpStatus) {
        super(httpStatus, message);
    }
}
