package venue.hub.api.domain.dtos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import venue.hub.api.domain.dtos.venue.VenueRequestDTO;
import venue.hub.api.domain.dtos.venue.VenueResponseDTO;
import venue.hub.api.domain.entities.Venue;
import venue.hub.api.domain.services.UserService;

@Mapper(componentModel = "spring", uses = {AddressMapper.class, UserService.class, UserMapper.class})
public interface VenueMapper {


    VenueResponseDTO toDTO(Venue venue);

    @Mapping(source = "userId", target = "user")
    Venue toEntity(VenueRequestDTO venueRequestDTO);
}
