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
import venue.hub.api.domain.dtos.venue.VenueUpdateDTO;
<<<<<<< HEAD
import venue.hub.api.domain.dtos.venueadditional.VenueAdditionalRequestDTO;
=======
>>>>>>> b5e1c8f (criando endpoints atualizar e deletar Venue)
import venue.hub.api.domain.services.VenueService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/venues")
public class VenueController {

    @Autowired
    private VenueService venueService;

    @PostMapping("/create")
<<<<<<< HEAD
    public ResponseEntity<VenueResponseDTO> createVenue(@RequestBody @Valid VenueRequestDTO requestDTO, UriComponentsBuilder uriBuilder) {
=======
    public ResponseEntity<VenueResponseDTO> createVenue(@RequestBody @Valid VenueRequestDTO requestDTO, UriComponentsBuilder uriBuilder){
>>>>>>> b5e1c8f (criando endpoints atualizar e deletar Venue)
        var venue = venueService.createVenue(requestDTO);
        var uri = uriBuilder.path("/api/v1/venues/{id}").buildAndExpand(venue.getId()).toUri();

        return ResponseEntity.created(uri).body(venue);
    }

    @GetMapping("/all")
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

    @PutMapping("/update/{id}")
    public ResponseEntity<VenueResponseDTO> updateVenue(
            @PathVariable Long id,
            @RequestBody @Valid VenueUpdateDTO updateDTO) {
        VenueResponseDTO updatedVenue = venueService.updateVenue(id, updateDTO);
        return ResponseEntity.ok(updatedVenue);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteVenue(@PathVariable Long id) {
        venueService.deleteVenue(id);
        return ResponseEntity.noContent().build();
    }

<<<<<<< HEAD
    @PutMapping("/{venueId}/additionals")
    public ResponseEntity<VenueResponseDTO> updateVenueAdditionals(
            @PathVariable Long venueId,
            @RequestBody List<VenueAdditionalRequestDTO> dto) {

        VenueResponseDTO response = venueService.updateVenueAdditionals(venueId, dto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{venueId}/additionals/remove")
    public ResponseEntity<Void> removeAdditionalFromVenue(
            @PathVariable Long venueId,
            @RequestParam(name = "ids") List<Long> additionalIds) {

        venueService.removeAdditionalFromVenue(venueId, additionalIds);
        return ResponseEntity.noContent().build();
    }
=======

>>>>>>> b5e1c8f (criando endpoints atualizar e deletar Venue)

}
