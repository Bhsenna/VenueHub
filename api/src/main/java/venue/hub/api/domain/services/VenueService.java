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
import venue.hub.api.infra.exceptions.VenueNotFound;


@Service
public class VenueService {

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private VenueMapper venueMapper;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private EventMapper eventMapper;

    @Autowired
    private AuthenticationService authenticationService;

    @Transactional
    public VenueResponseDTO createVenue(VenueRequestDTO venueRequestDTO) {
        Venue venue = venueMapper.toEntity(venueRequestDTO);
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

    private Page<VenueResponseDTO> getAllVenues(Pageable paginacao) {
        return venueRepository.findAllByAtivoTrue(paginacao)
                .map(venueMapper::toDTO);
    }


    public VenueResponseDTO getVenueById(Long id) {
        var venue = findById(id);

        return venueMapper.toDTO(venue);
    }

    public VenueResponseDTO updateVenue(Long id, VenueUpdateDTO updateDTO) {
        var venue = findById(id);
        venue.update(updateDTO);

        addressRepository.save(venue.getAddress());

        return venueMapper.toDTO(venue);
    }

    public void deleteVenue(Long id) {
        Venue venue = findById(id);
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
                .orElseThrow(() -> new VenueNotFound(HttpStatus.NOT_FOUND, "Local não encontrado com o id: " + id));
    }
}
