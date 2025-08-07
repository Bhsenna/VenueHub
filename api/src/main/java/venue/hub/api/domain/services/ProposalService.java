package venue.hub.api.domain.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import venue.hub.api.domain.dtos.mapper.ProposalMapper;
import venue.hub.api.domain.dtos.proposal.ProposalRequestDTO;
import venue.hub.api.domain.dtos.proposal.ProposalResponseDTO;
import venue.hub.api.domain.dtos.proposal.ProposalUpdateDTO;
import venue.hub.api.domain.entities.Proposal;
import venue.hub.api.domain.repositories.ProposalRepository;
import venue.hub.api.infra.exceptions.ProposalNotFoundException;


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

    public Page<ProposalResponseDTO> getAllProposals(Pageable paginacao) {
        Page<Proposal> proposals = proposalRepository.findAll(paginacao);

        return proposals.map(proposalMapper::toDTO);
    }

    public ProposalResponseDTO getProposalById(Long id) {
        return proposalMapper.toDTO(findById(id));
    }

    @Transactional
    public ProposalResponseDTO updateProposal(Long id, ProposalUpdateDTO updateDTO) {
        Proposal proposal = findById(id);
        if (updateDTO.getValor() != null) {
            proposal.setValor(updateDTO.getValor());
        }

        return proposalMapper.toDTO(proposal);
    }

    public void deleteProposal(Long id) {
        Proposal proposal = findById(id);
        proposalRepository.delete(proposal);
    }

    public Proposal findById(Long id) {
        return proposalRepository.findById(id)
                .orElseThrow(() -> new ProposalNotFoundException("Proposta não encontrada com o id: " + id, HttpStatus.NOT_FOUND));
    }
}
