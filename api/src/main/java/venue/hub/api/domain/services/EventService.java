package venue.hub.api.domain.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import venue.hub.api.domain.dtos.event.EventRequestDTO;
import venue.hub.api.domain.dtos.event.EventResponseDTO;
import venue.hub.api.domain.dtos.event.EventUpdateDTO;
import venue.hub.api.domain.dtos.eventadditional.EventAdditionalRequestDTO;
import venue.hub.api.domain.dtos.mapper.EventMapper;
import venue.hub.api.domain.entities.Additional;
import venue.hub.api.domain.entities.Event;
import venue.hub.api.domain.entities.User;
import venue.hub.api.domain.enums.UserRole;
import venue.hub.api.domain.repositories.EventRepository;
import venue.hub.api.domain.specification.EventSpecification;
import venue.hub.api.domain.validators.event.EventValidator;
import venue.hub.api.infra.exceptions.AdditionalAlreadyAddedException;
import venue.hub.api.infra.exceptions.AdditionalNotFoundException;
import venue.hub.api.infra.exceptions.EventNotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventService {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    EventMapper eventMapper;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    AdditionalService additionalService;

    @Autowired
    List<EventValidator> eventValidators;

    @Transactional
    public EventResponseDTO createEvent(EventRequestDTO requestDTO) {

        eventValidators.forEach(v -> v.validate(requestDTO));

        Event event = eventMapper.toEntity(requestDTO);

        User user = authenticationService.getAuthenticatedUser();
        event.setUser(user);

        eventRepository.save(event);

        if (requestDTO.getAdditionals() != null) {
            List<Additional> additionals = new ArrayList<>();
            for (EventAdditionalRequestDTO additionalDTO : requestDTO.getAdditionals()) {
                Additional additional = additionalService.findById(additionalDTO.getAdditionalId());

                additionals.add(additional);
            }
            event.setAdditionals(additionals);
        }

        return eventMapper.toDTO(event);
    }

    public Page<EventResponseDTO> getAllEvents(Specification<Event> spec, Pageable paginacao) {
        User user = authenticationService.getAuthenticatedUser();

        switch (user.getRole()) {
            case CLIENT -> spec = spec.and(EventSpecification.comClient(user));
            case ADMIN -> spec = spec.and(EventSpecification.comClient(null));
            default -> {
                return null;
            }
        }

        return eventRepository.findAll(spec, paginacao)
                .map(eventMapper::toDTO);
    }

//    private Page<EventResponseDTO> findAll(Pageable paginacao) {
//        return eventRepository.findAllByDataFimAfter(LocalDate.now(), paginacao)
//                .map(eventMapper::toDTO);
//    }

    public EventResponseDTO getEventById(Long id) {
        var event = this.findById(id);
        var user = authenticationService.getAuthenticatedUser();
        if (!event.getUser().equals(user) && user.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Usuário autenticado não é organizador do Evento (" + event.getUser().getLogin() + ")");
        }

        return eventMapper.toDTO(event);
    }

    @Transactional
    public EventResponseDTO updateEvent(Long id, EventUpdateDTO updateDTO) {
        var event = this.findById(id);
        var user = authenticationService.getAuthenticatedUser();

        if (!event.getUser().equals(user) && user.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Usuário autenticado não é organizador do Evento (" + event.getUser().getLogin() + ")");
        }

        event.update(updateDTO);

        eventRepository.save(event);

        return eventMapper.toDTO(event);
    }

    public void deleteEvent(Long id) {
        var event = this.findById(id);
        var user = authenticationService.getAuthenticatedUser();

        if (!event.getUser().equals(user) && user.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Usuário autenticado não é organizador do Evento (" + event.getUser().getLogin() + ")");
        }
        eventRepository.delete(event);
    }

    public Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(HttpStatus.NOT_FOUND, "Evento não encontrado com o id: " + id));
    }

    public EventResponseDTO addAdditionalsToEvent(Long id, List<EventAdditionalRequestDTO> additionals) {
        var event = this.findById(id);
        var user = authenticationService.getAuthenticatedUser();

        if (!event.getUser().equals(user) && user.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Usuário autenticado não é organizador do Evento (" + event.getUser().getLogin() + ")");
        }

        List<Additional> additionalEntities = new ArrayList<>();
        for (EventAdditionalRequestDTO additionalDto : additionals) {
            Additional additional = additionalService.findById(additionalDto.getAdditionalId());

            if (event.getAdditionals().contains(additional)) {
                throw new AdditionalAlreadyAddedException(
                        "Additional já adicionado ao evento: " + additionalDto.getAdditionalId(), HttpStatus.BAD_REQUEST);
            }
            additionalEntities.add(additional);
        }

        event.getAdditionals().addAll(additionalEntities);
        eventRepository.save(event);

        return eventMapper.toDTO(event);
    }

    public EventResponseDTO removeAdditionalsFromEvent(Long id, List<EventAdditionalRequestDTO> additionals) {
        var event = this.findById(id);
        var user = authenticationService.getAuthenticatedUser();

        if (!event.getUser().equals(user) && user.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Usuário autenticado não é organizador do Evento (" + event.getUser().getLogin() + ")");
        }

        for (EventAdditionalRequestDTO additionalDto : additionals) {
            Additional additional = additionalService.findById(additionalDto.getAdditionalId());

            if (!event.getAdditionals().contains(additional)) {
                throw new AdditionalNotFoundException(
                        "Additional não está associado ao evento: " + additionalDto.getAdditionalId(), HttpStatus.BAD_REQUEST);
            }
            event.getAdditionals().remove(additional);
        }

        eventRepository.save(event);

        return eventMapper.toDTO(event);
    }
}