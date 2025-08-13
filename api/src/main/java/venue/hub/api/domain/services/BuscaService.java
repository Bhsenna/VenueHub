package venue.hub.api.domain.services;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import venue.hub.api.domain.dtos.busca.BuscaRequestDTO;
import venue.hub.api.domain.dtos.busca.BuscaResponseDTO;
import venue.hub.api.domain.dtos.mapper.BuscaMapper;
import venue.hub.api.domain.entities.Venue;
import venue.hub.api.domain.repositories.VenueRepository;
import venue.hub.api.domain.specification.BuscaSpecification;

@Service
public class BuscaService {
    @Autowired
    VenueRepository venueRepository;

    @Autowired
    BuscaMapper buscaMapper;

    public Page<BuscaResponseDTO> getResults(@Valid BuscaRequestDTO requestDTO, Pageable paginacao) {
        Specification<Venue> spec = Specification.allOf(
                BuscaSpecification.comAtivo     (true),
                BuscaSpecification.comNome      (requestDTO.getNome()),
                BuscaSpecification.comCapacidade(requestDTO.getCapacidade(), requestDTO.getTolerancia()),
                BuscaSpecification.comValorMin  (requestDTO.getValorMin()),
                BuscaSpecification.comValorMax  (requestDTO.getValorMax()),
                BuscaSpecification.comBairro    (requestDTO.getBairro()),
                BuscaSpecification.comCidade    (requestDTO.getCidade()),
                BuscaSpecification.comEstado    (requestDTO.getEstado()),
                BuscaSpecification.comAdditional(requestDTO.getIdAdditionals())
        );

        return venueRepository.findAll(spec, paginacao)
                .map(buscaMapper::toDTO);
    }
}
