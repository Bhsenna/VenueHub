package venue.hub.api.domain.dtos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import venue.hub.api.domain.dtos.venueadditional.VenueAdditionalRequestDTO;
import venue.hub.api.domain.dtos.venueadditional.VenueAdditionalResponseDTO;
import venue.hub.api.domain.entities.VenueAdditional;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VenueAdditionalMapper {

    @Mapping(source = "additional.nome", target = "nome")
    @Mapping(source = "id.additionalId", target = "additionalId")
    VenueAdditionalResponseDTO toDTO(VenueAdditional venueAdditional);
    List<VenueAdditionalResponseDTO> toDTO(List<VenueAdditional> venueAdditionals);

    @Mapping(source = "additionalId", target = "id.additionalId")
    VenueAdditional toEntity(VenueAdditionalRequestDTO venueAdditionalResponseDTO);
    List<VenueAdditional> toEntity(List<VenueAdditionalRequestDTO> venueAdditionalResponseDTO);

}
