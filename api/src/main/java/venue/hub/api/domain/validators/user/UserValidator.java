package venue.hub.api.domain.validators.user;

import venue.hub.api.domain.dtos.user.UserRequestDTO;

public interface UserValidator {
    void validate(UserRequestDTO requestDTO);
}
