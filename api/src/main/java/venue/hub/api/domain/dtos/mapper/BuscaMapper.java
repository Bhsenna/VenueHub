package venue.hub.api.domain.dtos.mapper;

import org.mapstruct.Mapper;
import venue.hub.api.domain.dtos.busca.BuscaResponseDTO;
import venue.hub.api.domain.entities.Venue;

@Mapper(componentModel = "spring", uses = {VenueAdditionalMapper.class})
public interface BuscaMapper {

    BuscaResponseDTO toDTO(Venue venue);

}
