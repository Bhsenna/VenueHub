package venue.hub.api.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import venue.hub.api.domain.dtos.additional.AdditionalRequestDTO;
import venue.hub.api.domain.dtos.additional.AdditionalResponseDTO;
import venue.hub.api.domain.dtos.mapper.AdditionalMapper;
import venue.hub.api.domain.entities.Additional;
import venue.hub.api.domain.repositories.AdditionalRepository;

@Service
public class AdditionalService {

    @Autowired
    AdditionalRepository additionalRepository;

    @Autowired
    AdditionalMapper additionalMapper;

    @Transactional
    public AdditionalResponseDTO createAdditional(AdditionalRequestDTO dto) {
        Additional additional = additionalMapper.toEntity(dto);
        additionalRepository.save(additional);

        return additionalMapper.toDTO(additional);
    }

}
