package venue.hub.api.domain.services;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import venue.hub.api.domain.dtos.event.EventRequestDTO;
import venue.hub.api.domain.dtos.event.EventResponseDTO;
import venue.hub.api.domain.dtos.event.EventUpdateDTO;
import venue.hub.api.domain.dtos.mapper.EventMapper;
import venue.hub.api.domain.entities.Event;
import venue.hub.api.domain.entities.User;
import venue.hub.api.domain.repositories.EventRepository;
import venue.hub.api.infra.exceptions.EventNotFoundException;

import java.time.LocalDate;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventMapper eventMapper;

    @Autowired
    private AuthenticationService authenticationService;

    public EventResponseDTO createEvent(EventRequestDTO requestDTO) {
        Event event = eventMapper.toEntity(requestDTO);

        eventRepository.save(event);

        return eventMapper.toDTO(event);
    }

    public Page<EventResponseDTO> getAllEvents(Pageable paginacao) {
        User user = authenticationService.getAuthenticatedUser();

        switch (user.getRole()) {
            case CLIENT -> {
                return eventRepository.findByUser(user, paginacao)
                        .map(eventMapper::toDTO);
            }
            case ADMIN -> {
                return getAllEvents(paginacao);
            }
            default -> {
                return null;
            }
        }

    }

    private Page<EventResponseDTO> findAll(Pageable paginacao) {
        return eventRepository.findAllByDataFimAfter(LocalDate.now(), paginacao)
                .map(eventMapper::toDTO);
    }


    public EventResponseDTO getEventById(Long id) {
        var event = this.findById(id);
        return eventMapper.toDTO(event);
    }

    public EventResponseDTO updateEvent(Long id, @Valid EventUpdateDTO updateDTO) {
        var event = this.findById(id);

        event.update(updateDTO);

        eventRepository.save(event);

        return eventMapper.toDTO(event);
    }

    public void deleteEvent(Long id) {
        var event = this.findById(id);
        eventRepository.delete(event);
    }

    public Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Evento não encontrado com o id: " + id, HttpStatus.NOT_FOUND));
    }
}
