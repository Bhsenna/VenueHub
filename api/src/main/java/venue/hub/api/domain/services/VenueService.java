package venue.hub.api.domain.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import venue.hub.api.domain.dtos.mapper.VenueMapper;
import venue.hub.api.domain.dtos.venue.VenueRequestDTO;
import venue.hub.api.domain.dtos.venue.VenueResponseDTO;
import venue.hub.api.domain.entities.User;
import venue.hub.api.domain.entities.Venue;
import venue.hub.api.domain.repositories.AddressRepository;
import venue.hub.api.domain.repositories.VenueRepository;
import venue.hub.api.domain.validators.address.AddressValidator;
import venue.hub.api.infra.exceptions.UserNotFoundException;
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
    List<AddressValidator> addressValidators;

    @Transactional
    public VenueResponseDTO createVenue(VenueRequestDTO requestDTO){

        addressValidators.forEach(v -> v.validate(requestDTO.getAddress()));

        Venue venue = venueMapper.toEntity(requestDTO);

        addressRepository.save(venue.getAddress());
        venueRepository.save(venue);

        return venueMapper.toDTO(venue);
    }

    public Page<VenueResponseDTO> getAllVenues(Pageable paginacao){
        return venueRepository.findAllByAtivoTrue(paginacao)
                .map(venueMapper::toDTO);
    }

    public VenueResponseDTO getVenueById(Long id){
        var venue = this.findById(id);
        return venueMapper.toDTO(venue);
    }

    public Venue findById(Long id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new VenueNotFoundException(HttpStatus.NOT_FOUND, "Local não encontrado com o id: " + id));
    }
}
