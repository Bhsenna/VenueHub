package venue.hub.api.domain.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import venue.hub.api.domain.dtos.event.EventResponseDTO;
import venue.hub.api.domain.dtos.mapper.EventMapper;
import venue.hub.api.domain.dtos.mapper.VenueMapper;
import venue.hub.api.domain.dtos.venue.VenueRequestDTO;
import venue.hub.api.domain.dtos.venue.VenueResponseDTO;
import venue.hub.api.domain.dtos.venue.VenueUpdateDTO;
import venue.hub.api.domain.entities.User;
import venue.hub.api.domain.entities.Venue;
import venue.hub.api.domain.repositories.AddressRepository;
import venue.hub.api.domain.repositories.VenueRepository;
import venue.hub.api.domain.validators.address.AddressValidator;
import venue.hub.api.infra.exceptions.VenueNotFoundException;

import java.util.List;

@Service
public class VenueService {

    @Autowired
    VenueRepository venueRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    VenueMapper venueMapper;

    @Autowired
    EventMapper eventMapper;

    @Autowired
    List<AddressValidator> addressValidators;

    @Autowired
    AuthenticationService authenticationService;

    @Transactional
    public VenueResponseDTO createVenue(VenueRequestDTO requestDTO){

        addressValidators.forEach(v -> v.validate(requestDTO.getAddress()));

        Venue venue = venueMapper.toEntity(requestDTO);

        addressRepository.save(venue.getAddress());
        venueRepository.save(venue);

        return venueMapper.toDTO(venue);
    }

    public Page<VenueResponseDTO> getVenuesByUser(Pageable paginacao) {
        User user = authenticationService.getAuthenticatedUser();

        switch (user.getRole()) {
            case OWNER -> {
                return venueRepository.findByUser(user, paginacao)
                        .map(venueMapper::toDTO);
            }
            case ADMIN -> {
                return getAllVenues(paginacao);
            }
            default -> {
                return null;
            }
        }

    }

    public Page<VenueResponseDTO> getAllVenues(Pageable paginacao) {
        return venueRepository.findAllByAtivoTrue(paginacao)
                .map(venueMapper::toDTO);
    }

    public VenueResponseDTO getVenueById(Long id) {
        var venue = this.findById(id);

        return venueMapper.toDTO(venue);
    }

    public VenueResponseDTO updateVenue(Long id, VenueUpdateDTO updateDTO) {
        var venue = this.findById(id);
        venue.update(updateDTO);

        addressRepository.save(venue.getAddress());

        return venueMapper.toDTO(venue);
    }

    public void deleteVenue(Long id) {
        Venue venue = this.findById(id);
        venue.setAtivo(false);
        venueRepository.save(venue);
    }

    public Page<EventResponseDTO> getEvents(Long venueId, Pageable paginacao) {
        User user = authenticationService.getAuthenticatedUser();

        return venueRepository.findEvents(venueId, user, paginacao)
                .map(eventMapper::toDTO);
    }

    public Page<EventResponseDTO> getEventsByMonthAndYear(Long venueId, int month, int year, Pageable paginacao) {
        User user = authenticationService.getAuthenticatedUser();
        return venueRepository.findConfirmedEventsByVenueAndDateAndUser(venueId, month, year, user, paginacao)
                .map(eventMapper::toDTO);
    }

    public Venue findById(Long id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new VenueNotFoundException(HttpStatus.NOT_FOUND, "Local não encontrado com o id: " + id));
    }
}
