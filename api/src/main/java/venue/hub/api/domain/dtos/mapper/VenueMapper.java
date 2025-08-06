package venue.hub.api.domain.dtos.mapper;

import org.mapstruct.Mapper;
import venue.hub.api.domain.dtos.venue.VenueRequestDTO;
import venue.hub.api.domain.dtos.venue.VenueResponseDTO;
import venue.hub.api.domain.entities.Venue;

@Mapper(componentModel = "spring", uses = {AddressMapper.class})
public interface VenueMapper {

    VenueResponseDTO toDTO(Venue venue);
    Venue toEntity(VenueRequestDTO venueRequestDTO);
}
