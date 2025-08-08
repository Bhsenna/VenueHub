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
import venue.hub.api.domain.dtos.venue.VenueUpdateDTO;
import venue.hub.api.domain.dtos.venueadditional.VenueAdditionalRequestDTO;
import venue.hub.api.domain.entities.Additional;
import venue.hub.api.domain.entities.Venue;
import venue.hub.api.domain.entities.VenueAdditional;
import venue.hub.api.domain.entities.VenueAdditionalId;
import venue.hub.api.domain.repositories.AddressRepository;
import venue.hub.api.domain.repositories.VenueAdditionalRepository;
import venue.hub.api.domain.repositories.VenueRepository;
import venue.hub.api.infra.exceptions.VenueAdditionalNotFound;
import venue.hub.api.infra.exceptions.VenueNotFound;

import java.util.ArrayList;
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
    private AdditionalService addicionalService;

    @Autowired
    private VenueAdditionalRepository venueAdditionalRepository;

    @Transactional
    public VenueResponseDTO createVenue(VenueRequestDTO venueRequestDTO) {
        Venue venue = venueMapper.toEntity(venueRequestDTO);

        addressRepository.save(venue.getAddress());
        venueRepository.save(venue);

        if (venueRequestDTO.getAdditionals() != null) {
            List<VenueAdditional> additionalList = new ArrayList<>();
            for (VenueAdditionalRequestDTO additionalDTO : venueRequestDTO.getAdditionals()) {
                Additional additional = addicionalService.findById(additionalDTO.getAdditionalId());

                VenueAdditional venueAdditional = createVenueAdditional(venue, additional, additionalDTO.getValor());
                new VenueAdditional();

                additionalList.add(venueAdditional);
            }
            venue.setAdditionals(additionalList);
        }
        return venueMapper.toDTO(venue);
    }

    public Page<VenueResponseDTO> getAllVenues(Pageable paginacao) {
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

    public Venue findById(Long id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new VenueNotFound(HttpStatus.NOT_FOUND, "Local não encontrado com o id: " + id));
    }

    public VenueResponseDTO updateVenueAdditionals(Long venueId, List<VenueAdditionalRequestDTO> additionals) {
        Venue venue = findById(venueId);

        List<VenueAdditional> venueAdditionals = new ArrayList<>();
        for (VenueAdditionalRequestDTO additionalDTO : additionals) {
            Additional additional = addicionalService.findById(additionalDTO.getAdditionalId());

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

    public void removeAdditionalFromVenue(Long venueId, List<Long> additionalIds) {
        Venue venue = findById(venueId);

        List<VenueAdditional> toRemove = new ArrayList<>();
        for (Long additionalId : additionalIds) {
            VenueAdditionalId venueAdditionalId = new VenueAdditionalId(venue.getId(), additionalId);
            VenueAdditional venueAdditional = venueAdditionalRepository.findById(venueAdditionalId)
                    .orElseThrow(() -> new VenueAdditionalNotFound("Adicional não encontrado com o id: " + additionalId, HttpStatus.NOT_FOUND));
            toRemove.add(venueAdditional);
        }
        venueAdditionalRepository.deleteAll(toRemove);
        venue.getAdditionals().removeAll(toRemove);
    }
}