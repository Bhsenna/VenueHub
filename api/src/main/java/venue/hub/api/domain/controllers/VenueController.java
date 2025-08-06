package venue.hub.api.domain.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import venue.hub.api.domain.dtos.venue.VenueRequestDTO;
import venue.hub.api.domain.dtos.venue.VenueResponseDTO;
import venue.hub.api.domain.services.VenueService;

import java.awt.print.Pageable;
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
    public ResponseEntity<List<VenueResponseDTO>> getAllVenues(){
        return ResponseEntity.ok(venueService.getAllVenues());
    }

}
