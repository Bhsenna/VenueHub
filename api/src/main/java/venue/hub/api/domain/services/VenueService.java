package venue.hub.api.domain.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import venue.hub.api.domain.dtos.event.EventResponseDTO;
import venue.hub.api.domain.dtos.mapper.EventMapper;
import venue.hub.api.domain.dtos.mapper.VenueMapper;
import venue.hub.api.domain.dtos.venue.VenueRequestDTO;
import venue.hub.api.domain.dtos.venue.VenueResponseDTO;
import venue.hub.api.domain.dtos.venue.VenueUpdateDTO;
import venue.hub.api.domain.dtos.venueadditional.VenueAdditionalRemoveDTO;
import venue.hub.api.domain.dtos.venueadditional.VenueAdditionalRequestDTO;
import venue.hub.api.domain.entities.*;
import venue.hub.api.domain.enums.UserRole;
import venue.hub.api.domain.repositories.AddressRepository;
import venue.hub.api.domain.repositories.VenueAdditionalRepository;
import venue.hub.api.domain.repositories.VenueRepository;
import venue.hub.api.domain.specification.VenueSpecification;
import venue.hub.api.domain.validators.address.AddressValidator;
import venue.hub.api.infra.exceptions.VenueAdditionalNotFound;
import venue.hub.api.infra.exceptions.VenueNotFoundException;

import java.util.ArrayList;
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
    AdditionalService additionalService;

    @Autowired
    VenueAdditionalRepository venueAdditionalRepository;

    @Autowired
    AuthenticationService authenticationService;

    @Transactional
    public VenueResponseDTO createVenue(VenueRequestDTO requestDTO) {

        addressValidators.forEach(v -> v.validate(requestDTO.getAddress()));

        Venue venue = venueMapper.toEntity(requestDTO);

        User user = authenticationService.getAuthenticatedUser();
        venue.setUser(user);

        addressRepository.save(venue.getAddress());
        venueRepository.save(venue);

        if (requestDTO.getAdditionals() != null) {
            List<VenueAdditional> additionalList = new ArrayList<>();
            for (VenueAdditionalRequestDTO additionalDTO : requestDTO.getAdditionals()) {
                Additional additional = additionalService.findById(additionalDTO.getAdditionalId());

                VenueAdditional venueAdditional = createVenueAdditional(venue, additional, additionalDTO.getValor());

                additionalList.add(venueAdditional);
            }
            venue.setAdditionals(additionalList);
        }

        return venueMapper.toDTO(venue);
    }

    public Page<VenueResponseDTO> getAll(Pageable paginacao) {
        return venueRepository.findAll(paginacao)
                .map(venueMapper::toDTO);
    }

    public Page<VenueResponseDTO> getAllVenues(Specification<Venue> spec, Pageable paginacao) {
        User user = authenticationService.getAuthenticatedUser();

        switch (user.getRole()) {
            case OWNER -> spec = spec.and(VenueSpecification.comOwner(user));
            case ADMIN -> spec = spec.and(VenueSpecification.comOwner(null));
            default -> {
                return null;
            }
        }

        return venueRepository.findAll(spec, paginacao)
                .map(venueMapper::toDTO);
    }

    public VenueResponseDTO getVenueById(Long id) {
        var venue = this.findById(id);

        return venueMapper.toDTO(venue);
    }

    @Transactional
    public VenueResponseDTO updateVenue(Long id, VenueUpdateDTO updateDTO) {
        var venue = this.findById(id);
        var user = authenticationService.getAuthenticatedUser();

        if (!venue.getUser().equals(user) && user.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Usuário autenticado não é dono da Venue (" + venue.getUser().getLogin() + ")");
        }

        venue.update(updateDTO);

        venueRepository.save(venue);
        addressRepository.save(venue.getAddress());

        return venueMapper.toDTO(venue);
    }

    public void deleteVenue(Long id) {
        Venue venue = this.findById(id);
        var user = authenticationService.getAuthenticatedUser();

        if (!venue.getUser().equals(user) && user.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Usuário autenticado não é dono da Venue (" + venue.getUser().getLogin() + ")");
        }

        venue.delete();
        venueRepository.save(venue);
    }

    public Page<EventResponseDTO> getEvents(Long venueId, Pageable paginacao) {
        return venueRepository.findEvents(venueId, paginacao)
                .map(eventMapper::toDTO);
    }

    public Page<EventResponseDTO> getEventsByMonthAndYear(Long venueId, int month, int year, Pageable paginacao) {
        return venueRepository.findConfirmedEventsByVenueAndDate(venueId, month, year, paginacao)
                .map(eventMapper::toDTO);
    }

    public Venue findById(Long id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new VenueNotFoundException(HttpStatus.NOT_FOUND, "Local não encontrado com o id: " + id));
    }

    public VenueResponseDTO addAdditionalsToVenue(Long venueId, List<VenueAdditionalRequestDTO> additionals) {
        Venue venue = this.findById(venueId);
        var user = authenticationService.getAuthenticatedUser();

        if (!venue.getUser().equals(user) && user.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Usuário autenticado não é dono da Venue (" + venue.getUser().getLogin() + ")");
        }

        List<VenueAdditional> venueAdditionals = new ArrayList<>();
        for (VenueAdditionalRequestDTO additionalDTO : additionals) {
            Additional additional = additionalService.findById(additionalDTO.getAdditionalId());

            VenueAdditional venueAdditional = createVenueAdditional(venue, additional, additionalDTO.getValor());

            venueAdditionals.add(venueAdditional);
        }

        venueAdditionalRepository.saveAll(venueAdditionals);

        return venueMapper.toDTO(venue);
    }

    private VenueAdditional createVenueAdditional(Venue venue, Additional additional, Double valor) {
        VenueAdditional venueAdditional = new VenueAdditional();
        venueAdditional.setId(new VenueAdditionalId(venue.getId(), additional.getId()));
        venueAdditional.setVenue(venue);
        venueAdditional.setAdditional(additional);
        venueAdditional.setValor(valor);
        return venueAdditional;
    }

    public VenueResponseDTO removeAdditionalsFromVenue(Long venueId, List<VenueAdditionalRemoveDTO> additionals) {
        Venue venue = this.findById(venueId);
        var user = authenticationService.getAuthenticatedUser();

        if (!venue.getUser().equals(user) && user.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Usuário autenticado não é dono da Venue (" + venue.getUser().getLogin() + ")");
        }

        List<VenueAdditional> toRemove = new ArrayList<>();
        for (VenueAdditionalRemoveDTO additionalDTO : additionals) {
            VenueAdditionalId venueAdditionalId = new VenueAdditionalId(venue.getId(), additionalDTO.getAdditionalId());
            VenueAdditional venueAdditional = venueAdditionalRepository.findById(venueAdditionalId)
                    .orElseThrow(() -> new VenueAdditionalNotFound("Adicional não encontrado com o id: " + additionalDTO.getAdditionalId(), HttpStatus.NOT_FOUND));
            toRemove.add(venueAdditional);
        }
        venueAdditionalRepository.deleteAll(toRemove);
        venue.getAdditionals().removeAll(toRemove);

        return venueMapper.toDTO(venue);
    }
}