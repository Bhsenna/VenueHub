package venue.hub.api.domain.dtos.mapper;

import org.mapstruct.Mapper;
import venue.hub.api.domain.dtos.additional.AdditionalRequestDTO;
import venue.hub.api.domain.dtos.additional.AdditionalResponseDTO;
import venue.hub.api.domain.entities.Additional;

@Mapper(componentModel = "spring")
public interface AdditionalMapper {

    AdditionalResponseDTO toDTO(Additional additional);

    Additional toEntity(AdditionalRequestDTO additionalRequestDTO);

}
