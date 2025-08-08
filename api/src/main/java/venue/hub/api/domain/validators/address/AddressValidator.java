package venue.hub.api.domain.validators.address;

import venue.hub.api.domain.dtos.address.AddressRequestDTO;

public interface AddressValidator {
    void validate(AddressRequestDTO requestDTO);
}
