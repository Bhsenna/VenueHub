package venue.hub.api.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import venue.hub.api.domain.dtos.event.EventRequestDTO;
import venue.hub.api.domain.dtos.event.EventResponseDTO;
import venue.hub.api.domain.dtos.mapper.EventMapper;
import venue.hub.api.domain.entities.Event;
import venue.hub.api.domain.repositories.EventRepository;

@Service
public class EventService {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    EventMapper eventMapper;

    public EventResponseDTO createEvent(EventRequestDTO requestDTO) {
        Event event = eventMapper.toEntity(requestDTO);

        eventRepository.save(event);

        return eventMapper.toDTO(event);
    }
}
