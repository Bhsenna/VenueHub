package venue.hub.api.domain.services;

import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import venue.hub.api.domain.dtos.mapper.ProposalMapper;
import venue.hub.api.domain.dtos.proposal.ProposalRequestDTO;
import venue.hub.api.domain.dtos.proposal.ProposalResponseDTO;
import venue.hub.api.domain.dtos.proposal.ProposalUpdateDTO;
import venue.hub.api.domain.entities.Proposal;
import venue.hub.api.domain.entities.User;
import venue.hub.api.domain.enums.Status;
import venue.hub.api.domain.repositories.ProposalRepository;
import venue.hub.api.domain.validators.proposal.ProposalValidator;
import venue.hub.api.infra.exceptions.ProposalNotFoundException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ProposalService {

    @Autowired
    private ProposalRepository proposalRepository;

    @Autowired
    private ProposalMapper proposalMapper;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    List<ProposalValidator> proposalValidators;

    final List<Status> endStatus = Arrays.asList(
            Status.RECUSADO,
            Status.EXPIRADO,
            Status.CANCELADO,
            Status.CONFIRMADO
    );

    @Transactional
    public ProposalResponseDTO createProposal(ProposalRequestDTO requestDTO) {
        proposalValidators.forEach(v -> v.validate(requestDTO));

        Proposal proposal = proposalMapper.toEntity(requestDTO);

        proposalRepository.save(proposal);

        return proposalMapper.toDTO(proposal);
    }

    public Page<ProposalResponseDTO> getAllProposals(Specification<Proposal> spec, Pageable paginacao) {
        return proposalRepository.findAll(spec, paginacao)
                .map(proposalMapper::toDTO);
    }

    public Page<ProposalResponseDTO> getAllProposalsByUser(Specification<Proposal> spec, Pageable paginacao) {
        User user = authenticationService.getAuthenticatedUser();

        switch (user.getRole()) {
            case OWNER -> {
                return proposalRepository.findAllByOwner(user, spec, paginacao)
                        .map(proposalMapper::toDTO);
            }
            case CLIENT -> {
                proposalRepository.findAllByClient(user, spec, paginacao)
                        .map(proposalMapper::toDTO);
            }
            case ADMIN -> {
                return getAllProposals(spec, paginacao);
            }
            default -> {
                return null;
            }
        }

        Page<Proposal> proposals = proposalRepository.findAllByClient(user, spec, paginacao);

        return proposals.map(proposalMapper::toDTO);
    }


    public ProposalResponseDTO getProposalById(Long id) {
        return proposalMapper.toDTO(findById(id));
    }

    public Page<ProposalResponseDTO> getProposalsByVenueId(Long id, Pageable paginacao) {
        return proposalRepository.findByVenueId(id, paginacao)
                .map(proposalMapper::toDTO);
    }

    public Page<ProposalResponseDTO> getProposalsByVenueIdAndStatus(Long id, Status status, Pageable paginacao) {
        return proposalRepository.findByVenueIdAndStatus(id, status, paginacao)
                .map(proposalMapper::toDTO);
    }


    @Transactional
    public ProposalResponseDTO updateProposal(Long id, ProposalUpdateDTO updateDTO) {
        Proposal proposal = findById(id);
        if (updateDTO.getValor() != null) {
            proposal.setValor(updateDTO.getValor());
        }

        return proposalMapper.toDTO(proposal);
    }

    @Transactional
    public ProposalResponseDTO deleteProposal(Long id) {
        Proposal proposal = findById(id);
        proposal.setStatus(Status.CANCELADO);

        return proposalMapper.toDTO(proposal);
    }

    public Proposal findById(Long id) {
        return proposalRepository.findById(id)
                .orElseThrow(() -> new ProposalNotFoundException(HttpStatus.NOT_FOUND, "Proposta não encontrada com o id: " + id));
    }

    @Transactional
    public ProposalResponseDTO aceitaProposal(Long id) {
        Proposal proposal = findById(id);

        if (proposal.getStatus() != Status.PENDENTE) {
            throw new ValidationException("Tentando aceitar proposta que não está pendente (" + proposal.getStatus() + ")");
        }

        proposal.setStatus(Status.ACEITO);

        return proposalMapper.toDTO(proposal);
    }

    @Transactional
    public ProposalResponseDTO recusaProposal(Long id) {
        Proposal proposal = findById(id);

        if (proposal.getStatus() != Status.PENDENTE) {
            throw new ValidationException("Tentando recusar proposta que não está pendente (" + proposal.getStatus() + ")");
        }

        proposal.setStatus(Status.RECUSADO);

        return proposalMapper.toDTO(proposal);
    }

    @Transactional
    public ProposalResponseDTO confirmaProposal(Long id) {
        Proposal proposal = findById(id);

        if (proposal.getStatus() != Status.ACEITO) {
            throw new ValidationException("Tentando confirmar proposta que não está aceita (" + proposal.getStatus() + ")");
        }

        proposal.setStatus(Status.CONFIRMADO);

        List<Proposal> listProposal = proposalRepository.findAllByEventAndStatusNotIn(proposal.getEvent(), endStatus);
        for (Proposal proposalPendente : listProposal) {
            proposalPendente.setStatus(Status.CANCELADO);
        }

        return proposalMapper.toDTO(proposal);
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public List<ProposalResponseDTO> expiraProposal() {
        List<Proposal> listProposal = proposalRepository.findAllByStatusNotIn(endStatus);
        LocalDate now = LocalDate.now();
        long daysBetween;

        List<Proposal> proposalsExpiradas = new ArrayList<>();
        for (Proposal proposal : listProposal) {
            daysBetween = ChronoUnit.DAYS.between(proposal.getDataCriacao().toLocalDate(), now);
            if (daysBetween > 7) {
                proposal.setStatus(Status.EXPIRADO);
                proposalsExpiradas.add(proposal);
            }
        }

        return proposalMapper.toDTO(proposalsExpiradas);
    }
}
