package venue.hub.api.domain.dtos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import venue.hub.api.domain.dtos.venue.VenueRequestDTO;
import venue.hub.api.domain.dtos.venue.VenueResponseDTO;
import venue.hub.api.domain.entities.Venue;
import venue.hub.api.domain.services.UserService;

@Mapper(componentModel = "spring", uses = {AddressMapper.class, UserService.class, UserMapper.class, VenueAdditionalMapper.class})
public interface VenueMapper {

    @Mapping(source = "additionals", target = "additionals")
    VenueResponseDTO toDTO(Venue venue);

    @Mapping(target = "additionals", ignore = true)
    Venue toEntity(VenueRequestDTO venueRequestDTO);

}