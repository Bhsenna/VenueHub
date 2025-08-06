package venue.hub.api.domain.dtos.mapper;

import org.mapstruct.Mapper;
import venue.hub.api.domain.dtos.address.AddressRequestDTO;
import venue.hub.api.domain.dtos.address.AddressResponseDTO;
import venue.hub.api.domain.entities.Address;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressResponseDTO toDTO(Address address);
    Address toEntity(AddressRequestDTO addressRequestDTO);

}
