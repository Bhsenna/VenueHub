package venue.hub.api.infra.exceptions;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class CustomException extends RuntimeException{
    HttpStatus httpStatus;
    String message;

    public CustomException(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}