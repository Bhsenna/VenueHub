package venue.hub.api.domain.validators.user;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;
import venue.hub.api.domain.dtos.user.UserRequestDTO;

@Component
public class ValidatorSenhaSegura implements UserValidator {
    @Override
    public void validate(UserRequestDTO requestDTO) {
        var regexDigit = "(?=.*[0-9])";
        var regexLower = "(?=.*[a-z])";
        var regexUpper = "(?=.*[A-Z])";
        var regexSpecialChara = "(?=.*[^a-zA-Z0-9\\s])";
        var regexNoWhitespace = "(?!.*\\s)";
        var regexMinLength = ".{8,}";

        var regex = "^" + regexDigit + regexLower + regexUpper + regexSpecialChara + regexNoWhitespace + regexMinLength + "$";

        if(!requestDTO.getSenha().matches(regex)) {
            throw new ValidationException("Senha insegura, cumpra todos os requisitos para registrar");
        }
    }
}
