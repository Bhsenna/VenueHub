package venue.hub.api.domain.dtos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import venue.hub.api.domain.dtos.event.EventDetailsDTO;
import venue.hub.api.domain.dtos.event.EventRequestDTO;
import venue.hub.api.domain.dtos.event.EventResponseDTO;
import venue.hub.api.domain.entities.Event;
import venue.hub.api.domain.enums.Status;
import venue.hub.api.domain.services.UserService;

@Mapper(componentModel = "spring", uses = {UserService.class, UserMapper.class})
public interface EventMapper {

    EventResponseDTO toDTO(Event event);

    EventDetailsDTO toDTO(EventResponseDTO response, Long venueId, Status status);

    @Mapping(source = "userId", target = "user")
    Event toEntity(EventRequestDTO eventRequestDTO);

}
