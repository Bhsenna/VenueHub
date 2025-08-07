package venue.hub.api.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import venue.hub.api.domain.dtos.mapper.ProposalMapper;
import venue.hub.api.domain.dtos.proposal.ProposalRequestDTO;
import venue.hub.api.domain.dtos.proposal.ProposalResponseDTO;
import venue.hub.api.domain.entities.Proposal;
import venue.hub.api.domain.repositories.ProposalRepository;
import venue.hub.api.domain.validators.proposal.ProposalValidator;
import venue.hub.api.infra.exceptions.ProposalNotFoundException;
import venue.hub.api.infra.exceptions.UserNotFoundException;

import java.util.List;

@Service
public class ProposalService {

    @Autowired
    ProposalRepository proposalRepository;

    @Autowired
    ProposalMapper proposalMapper;

    @Autowired
    List<ProposalValidator> proposalValidators;

    public ProposalResponseDTO createProposal(ProposalRequestDTO requestDTO) {
        proposalValidators.forEach(v -> v.validate(requestDTO));

        Proposal proposal = proposalMapper.toEntity(requestDTO);

        proposalRepository.save(proposal);

        return proposalMapper.toDTO(proposal);
    }

    public Proposal findById(Long id) {
        return proposalRepository.findById(id)
                .orElseThrow(() -> new ProposalNotFoundException("Proposta não encontrada com o id: " + id, HttpStatus.NOT_FOUND));
    }
}
