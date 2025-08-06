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
import venue.hub.api.domain.repositories.UserRepository;
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


    @Transactional
    public VenueResponseDTO createVenue(VenueRequestDTO venueRequestDTO){
        Venue venue = venueMapper.toEntity(venueRequestDTO);
        addressRepository.save(venue.getAddress());

        venueRepository.save(venue);

        return venueMapper.toDTO(venue);
    }

    public Page<VenueResponseDTO> getAllVenues(Pageable paginacao){
        return venueRepository.findAllByAtivoTrue(paginacao)
                .map(venueMapper::toDTO);
    }

    public VenueResponseDTO getVenueById(Long id){
        var venue = venueRepository.findById(id)
                .orElseThrow(() -> new VenueNotFound(HttpStatus.NOT_FOUND, "Local não encontrado com o id: " + id));

        return venueMapper.toDTO(venue);
    }
}
