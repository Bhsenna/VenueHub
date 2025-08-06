package venue.hub.api.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import venue.hub.api.domain.dtos.mapper.ProposalMapper;
import venue.hub.api.domain.dtos.proposal.ProposalRequestDTO;
import venue.hub.api.domain.dtos.proposal.ProposalResponseDTO;
import venue.hub.api.domain.entities.Proposal;
import venue.hub.api.domain.repositories.ProposalRepository;

@Service
public class ProposalService {

    @Autowired
    ProposalRepository proposalRepository;

    @Autowired
    ProposalMapper proposalMapper;

    public ProposalResponseDTO createProposal(ProposalRequestDTO requestDTO) {
        Proposal proposal = proposalMapper.toEntity(requestDTO);

        proposalRepository.save(proposal);

        return proposalMapper.toDTO(proposal);
    }
}
