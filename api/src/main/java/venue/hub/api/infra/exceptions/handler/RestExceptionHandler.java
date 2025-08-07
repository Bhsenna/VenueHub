package venue.hub.api.infra.exceptions.handler;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import venue.hub.api.infra.exceptions.CustomException;

import java.util.Map;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, String>> handleCustomException(CustomException customException) {
        return ResponseEntity
                .status(customException.getHttpStatus())
                .body(Map.of("message", customException.getMessage(), "status", customException.getHttpStatus().toString()));
    }
}
