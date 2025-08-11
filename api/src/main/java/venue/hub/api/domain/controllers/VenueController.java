package venue.hub.api.domain.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import venue.hub.api.domain.dtos.event.EventResponseDTO;
import venue.hub.api.domain.dtos.page.PageResponse;
import venue.hub.api.domain.dtos.venue.VenueRequestDTO;
import venue.hub.api.domain.dtos.venue.VenueResponseDTO;
import venue.hub.api.domain.dtos.venue.VenueUpdateDTO;
import venue.hub.api.domain.services.VenueService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/venues")
@EnableWebSecurity
public class VenueController {

    @Autowired
    private VenueService venueService;

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/create")
    public ResponseEntity<VenueResponseDTO> createVenue(@RequestBody @Valid VenueRequestDTO requestDTO, UriComponentsBuilder uriBuilder){
        var venue = venueService.createVenue(requestDTO);
        var uri = uriBuilder.path("/api/v1/venues/{id}").buildAndExpand(venue.getId()).toUri();

        return ResponseEntity.created(uri).body(venue);
    }

    @GetMapping
    public ResponseEntity<PageResponse<VenueResponseDTO>> getAllVenues(
            @PageableDefault(size = 10, sort = {"id"}) Pageable paginacao
    ) {
        var venuePage = venueService.getAllVenues(paginacao);
        List<VenueResponseDTO> venues = venuePage.getContent();

        return ResponseEntity.ok(
                PageResponse.<VenueResponseDTO>builder()
                        .totalPages(venuePage.getTotalPages())
                        .totalElements(venuePage.getTotalElements())
                        .currentPageData(venues)
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @GetMapping("/all")
    public ResponseEntity<PageResponse<VenueResponseDTO>> getVenuesByOwner(
            @PageableDefault(size = 10, sort = {"id"}) Pageable paginacao) {
        var venuePage = venueService.getVenuesByUser(paginacao);
        List<VenueResponseDTO> venues = venuePage.getContent();

        return ResponseEntity.ok(
                PageResponse.<VenueResponseDTO>builder()
                        .totalPages(venuePage.getTotalPages())
                        .totalElements(venuePage.getTotalElements())
                        .currentPageData(venues)
                        .build()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<VenueResponseDTO> getVenueById(@PathVariable Long id) {
        VenueResponseDTO venue = venueService.getVenueById(id);
        return ResponseEntity.ok(venue);
    }

    @PreAuthorize("hasRole('OWNER'")
    @PutMapping("/update/{id}")
    public ResponseEntity<VenueResponseDTO> updateVenue(
            @PathVariable Long id,
            @RequestBody @Valid VenueUpdateDTO updateDTO) {
        VenueResponseDTO updatedVenue = venueService.updateVenue(id, updateDTO);
        return ResponseEntity.ok(updatedVenue);
    }

    @PreAuthorize("hasRole('OWNER'")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteVenue(@PathVariable Long id) {
        venueService.deleteVenue(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/all-events")
    public ResponseEntity<PageResponse<EventResponseDTO>> getAllEvents(
            @RequestParam Long venueId,
            @PageableDefault(size = 10, sort = {"id"}) Pageable paginacao) {
        var eventPage = venueService.getEvents(venueId, paginacao);
        List<EventResponseDTO> events = eventPage.getContent();

        return ResponseEntity.ok(
                PageResponse.<EventResponseDTO>builder()
                        .totalPages(eventPage.getTotalPages())
                        .totalElements(eventPage.getTotalElements())
                        .currentPageData(events)
                        .build()
        );
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/events")
    public ResponseEntity<PageResponse<EventResponseDTO>> getEventsCalendar(
            @RequestParam Long venueId,
            @RequestParam int month,
            @RequestParam int year,
            @PageableDefault(size = 10, sort = {"id"}) Pageable paginacao) {
        var eventPage = venueService.getEventsByMonthAndYear(venueId, month, year, paginacao);
        List<EventResponseDTO> events = eventPage.getContent();

        return ResponseEntity.ok(
                PageResponse.<EventResponseDTO>builder()
                        .totalPages(eventPage.getTotalPages())
                        .totalElements(eventPage.getTotalElements())
                        .currentPageData(events)
                        .build()
        );
    }



}
