package venue.hub.api.infra.exceptions;

import org.springframework.http.HttpStatus;

public class ProposalNotFoundException extends CustomException {
    public ProposalNotFoundException(String message, HttpStatus httpStatus) {
        super(httpStatus, message);
    }
}
