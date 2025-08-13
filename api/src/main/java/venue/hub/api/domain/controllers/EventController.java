package venue.hub.api.domain.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import venue.hub.api.domain.dtos.event.EventDetailsDTO;
import venue.hub.api.domain.dtos.event.EventRequestDTO;
import venue.hub.api.domain.dtos.event.EventResponseDTO;
import venue.hub.api.domain.dtos.event.EventUpdateDTO;
import venue.hub.api.domain.dtos.eventadditional.EventAdditionalRequestDTO;
import venue.hub.api.domain.dtos.page.PageResponse;
import venue.hub.api.domain.entities.Event;
import venue.hub.api.domain.services.EventService;

import java.util.List;

@RestController
@RequestMapping("api/v1/events")
@EnableWebSecurity
public class EventController {

    @Autowired
    EventService eventService;

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    @PostMapping("/create")
    public ResponseEntity<EventResponseDTO> createEvent(
            @RequestBody @Valid EventRequestDTO requestDTO,
            UriComponentsBuilder uriBuilder
    ) {
        var event = eventService.createEvent(requestDTO);
        var uri = uriBuilder.path("api/v1/evets/{id}").buildAndExpand(event.getId()).toUri();

        return ResponseEntity.created(uri).body(event);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    @GetMapping("/all")
    public ResponseEntity<PageResponse<EventResponseDTO>> getAllEvents(
            @PageableDefault(size = 10, sort = {"id"}) Pageable paginacao
    ) {
        Specification<Event> spec = Specification.allOf(
        );

        var eventPage = eventService.getAllEvents(spec, paginacao);
        List<EventResponseDTO> events = eventPage.getContent();

        return ResponseEntity.ok(
                PageResponse.<EventResponseDTO>builder()
                        .totalPages(eventPage.getTotalPages())
                        .totalElements(eventPage.getTotalElements())
                        .currentPageData(events)
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> getEventById(@PathVariable Long id) {
        EventResponseDTO event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }


    @GetMapping("/{id}/details")
    public ResponseEntity<EventDetailsDTO> getEventByIdDetails(@PathVariable Long id) {
        EventDetailsDTO event = eventService.getEventsDetails(id);
        return ResponseEntity.ok(event);
    }


    @PreAuthorize("hasRole('CLIENT')")
    @PutMapping("/update/{id}")
    public ResponseEntity<EventResponseDTO> updateEvent(
            @PathVariable Long id,
            @RequestBody @Valid EventUpdateDTO updateDTO
    ) {
        EventResponseDTO updatedEvent = eventService.updateEvent(id, updateDTO);
        return ResponseEntity.ok(updatedEvent);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/additionals/{eventId}")
    public ResponseEntity<EventResponseDTO> addAdditionalsToEvent(
            @PathVariable Long eventId,
            @RequestBody List<EventAdditionalRequestDTO> additionals
    ) {
        EventResponseDTO response = eventService.addAdditionalsToEvent(eventId, additionals);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/additionals/{eventId}/remove")
    public ResponseEntity<EventResponseDTO> removeAdditionalsFromEvent(
            @PathVariable Long eventId,
            @RequestBody List<EventAdditionalRequestDTO> additionals
    ) {
        EventResponseDTO response = eventService.removeAdditionalsFromEvent(eventId, additionals);
        return ResponseEntity.ok(response);
    }

}
