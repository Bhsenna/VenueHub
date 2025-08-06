package venue.hub.api.domain.dtos.mapper;

import org.mapstruct.Mapper;
import venue.hub.api.domain.dtos.event.EventRequestDTO;
import venue.hub.api.domain.dtos.event.EventResponseDTO;
import venue.hub.api.domain.entities.Event;

@Mapper(componentModel = "spring")
public interface EventMapper {

    EventResponseDTO toDTO(Event event);
    Event toEntity(EventRequestDTO eventRequestDTO);

}
