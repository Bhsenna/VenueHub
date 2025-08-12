package venue.hub.api.domain.validators.user;

import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import venue.hub.api.domain.dtos.user.UserRequestDTO;
import venue.hub.api.domain.repositories.UserRepository;

@Component
public class ValidatorLoginUnico implements UserValidator {
    @Autowired
    UserRepository userRepository;

    @Override
    public void validate(UserRequestDTO requestDTO) {
        String login = requestDTO.getLogin();

        boolean exists = userRepository.existsByLogin(login);

        if (exists) {
            throw new ValidationException("Login " + login + " já cadastrado");
        }
    }
}
