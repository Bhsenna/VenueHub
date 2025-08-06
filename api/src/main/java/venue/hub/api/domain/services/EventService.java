package venue.hub.api.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import venue.hub.api.domain.dtos.event.EventRequestDTO;
import venue.hub.api.domain.dtos.event.EventResponseDTO;
import venue.hub.api.domain.dtos.mapper.EventMapper;
import venue.hub.api.domain.entities.Event;
import venue.hub.api.domain.repositories.EventRepository;
import venue.hub.api.infra.exceptions.EventNotFoundException;

import java.time.LocalDate;

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

    public Page<EventResponseDTO> getAllEvents(Pageable paginacao) {
        return eventRepository.findAllByDataFimAfter(LocalDate.now(), paginacao)
                .map(eventMapper::toDTO);
    }

    public EventResponseDTO getEventById(Long id) {
        var event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Evento não encontrado com o id: " + id, HttpStatus.NOT_FOUND));
        return eventMapper.toDTO(event);
    }
}
