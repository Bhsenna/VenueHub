package venue.hub.api.domain.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import venue.hub.api.domain.dtos.event.EventRequestDTO;
import venue.hub.api.domain.dtos.event.EventResponseDTO;
import venue.hub.api.domain.dtos.page.PageResponse;
import venue.hub.api.domain.services.EventService;

import java.util.List;

@RestController
@RequestMapping("api/v1/events")
public class EventController {

    @Autowired
    EventService eventService;

    @PostMapping("/create")
    public ResponseEntity<EventResponseDTO> createEvent(
            @RequestBody EventRequestDTO requestDTO,
            UriComponentsBuilder uriBuilder
    ) {
        var event = eventService.createEvent(requestDTO);
        var uri = uriBuilder.path("api/v1/evets/{id}").buildAndExpand(event.getId()).toUri();

        return ResponseEntity.created(uri).body(event);
    }

    @GetMapping("/all")
    public ResponseEntity<PageResponse<EventResponseDTO>> getAllEvents(
            @PageableDefault(size = 10, sort = {"id"}) Pageable paginacao
    ) {
        var eventPage = eventService.getAllEvents(paginacao);
        List<EventResponseDTO> events = eventPage.getContent();

        return ResponseEntity.ok(
                PageResponse.<EventResponseDTO>builder()
                        .totalPages(eventPage.getTotalPages())
                        .totalElements(eventPage.getTotalElements())
                        .currentPageData(events)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> getEventById(@PathVariable Long id) {
        EventResponseDTO event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }
}
