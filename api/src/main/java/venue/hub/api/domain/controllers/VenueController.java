package venue.hub.api.domain.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import venue.hub.api.domain.dtos.page.PageResponse;
import venue.hub.api.domain.dtos.venue.VenueRequestDTO;
import venue.hub.api.domain.dtos.venue.VenueResponseDTO;
import venue.hub.api.domain.services.VenueService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/venues")
public class VenueController {

    @Autowired
    private VenueService venueService;

    @PostMapping
    public ResponseEntity<VenueResponseDTO> createVenue(@RequestBody @Valid VenueRequestDTO requestDTO, UriComponentsBuilder uriBuilder){
        var venue = venueService.createVenue(requestDTO);
        var uri = uriBuilder.path("/api/v1/venues/{id}").buildAndExpand(venue.getId()).toUri();

        return ResponseEntity.created(uri).body(venue);
    }

    @GetMapping
    public ResponseEntity<PageResponse<VenueResponseDTO>> getAllVenues(
            @PageableDefault(size = 10, sort = {"id"}) Pageable paginacao) {
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

    @GetMapping("/{id}")
    public ResponseEntity<VenueResponseDTO> getVenueById(@PathVariable Long id) {
        VenueResponseDTO venue = venueService.getVenueById(id);
        return ResponseEntity.ok(venue);
    }

}
