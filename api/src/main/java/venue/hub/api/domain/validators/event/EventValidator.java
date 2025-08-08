package venue.hub.api.domain.validators.event;

import venue.hub.api.domain.dtos.event.EventRequestDTO;

public interface EventValidator {
    void validate(EventRequestDTO requestDTO);
}
