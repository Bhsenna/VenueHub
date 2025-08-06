package venue.hub.api.domain.services;

import jakarta.transaction.Transactional;
import org.hibernate.query.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import venue.hub.api.domain.dtos.mapper.VenueMapper;
import venue.hub.api.domain.dtos.venue.VenueRequestDTO;
import venue.hub.api.domain.dtos.venue.VenueResponseDTO;
import venue.hub.api.domain.entities.User;
import venue.hub.api.domain.entities.Venue;
import venue.hub.api.domain.repositories.AddressRepository;
import venue.hub.api.domain.repositories.UserRepository;
import venue.hub.api.domain.repositories.VenueRepository;

import java.awt.print.Pageable;
import java.util.List;


@Service
public class VenueService {

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private VenueMapper venueMapper;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public VenueResponseDTO createVenue(VenueRequestDTO venueRequestDTO){
        Venue venue = venueMapper.toEntity(venueRequestDTO);
        User user = userRepository.findById(venueRequestDTO.getUserId())
                        .orElseThrow();

        addressRepository.save(venue.getAddress());

        venueRepository.save(venue);
        venue.setUser(user);

        return venueMapper.toDTO(venue);
    }

    public List<VenueResponseDTO> getAllVenues(){
        return venueRepository.findAll().stream()
                .map(venue -> venueMapper.toDTO(venue))
                .toList();
    }
}
