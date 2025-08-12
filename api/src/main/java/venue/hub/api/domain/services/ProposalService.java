package venue.hub.api.domain.services;

import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import venue.hub.api.domain.dtos.mapper.ProposalMapper;
import venue.hub.api.domain.dtos.proposal.ProposalRequestDTO;
import venue.hub.api.domain.dtos.proposal.ProposalResponseDTO;
import venue.hub.api.domain.dtos.proposal.ProposalUpdateDTO;
import venue.hub.api.domain.entities.Proposal;
import venue.hub.api.domain.entities.User;
import venue.hub.api.domain.enums.Status;
import venue.hub.api.domain.enums.UserRole;
import venue.hub.api.domain.repositories.ProposalRepository;
import venue.hub.api.domain.specification.ProposalSpecification;
import venue.hub.api.domain.validators.proposal.ProposalValidator;
import venue.hub.api.infra.exceptions.ProposalNotFoundException;
import venue.hub.api.infra.exceptions.UserNotFoundException;

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
        User user = authenticationService.getAuthenticatedUser();

        switch (user.getRole()) {
            case OWNER -> {
                spec = spec.and(ProposalSpecification.comOwner(user));
            }
            case CLIENT -> {
                spec = spec.and(ProposalSpecification.comClient(user));
            }
            case ADMIN -> {
            }
            default -> {
                return null;
            }
        }

        return proposalRepository.findAll(spec, paginacao)
                .map(proposalMapper::toDTO);
    }

    public ProposalResponseDTO getProposalById(Long id) {
        var proposal = this.findById(id);
        return proposalMapper.toDTO(proposal);
    }

    public Page<ProposalResponseDTO> getProposalsByVenueId(Long id, Pageable paginacao) {
        return proposalRepository.findByVenueId(id, paginacao)
                .map(proposalMapper::toDTO);
    }
    public Page<ProposalResponseDTO> getProposalsByEventId(Long id, Pageable paginacao) {
        return proposalRepository.findByEventId(id, paginacao)
                .map(proposalMapper::toDTO);
    }


    public Page<ProposalResponseDTO> getProposalsByVenueIdAndStatus(Long id, Status status, Pageable paginacao) {
        return proposalRepository.findByVenueIdAndStatus(id, status, paginacao)
                .map(proposalMapper::toDTO);
    }

    public List<ProposalResponseDTO> getProposalsByEventIdAndStatus(Long id, Status status) {
        return proposalRepository.findByEventIdAndStatus(id, status).stream()
                .map(proposalMapper::toDTO)
                .toList();
    }


    @Transactional
    public ProposalResponseDTO updateProposal(Long id, ProposalUpdateDTO updateDTO) {
        var proposal = this.findById(id);

        proposal.update(updateDTO);

        proposalRepository.save(proposal);

        return proposalMapper.toDTO(proposal);
    }

    @Transactional
    public ProposalResponseDTO deleteProposal(Long id) {
        Proposal proposal = this.findById(id);
        proposal.setStatus(Status.CANCELADO);

        return proposalMapper.toDTO(proposal);
    }

    public Proposal findById(Long id) {
        return proposalRepository.findById(id)
                .orElseThrow(() -> new ProposalNotFoundException(HttpStatus.NOT_FOUND, "Proposta não encontrada com o id: " + id));
    }

    @Transactional
    public ProposalResponseDTO aceitaProposal(Long id) {
        Proposal proposal = this.findById(id);
        User dono = proposal.getVenue().getUser();
        User user = authenticationService.getAuthenticatedUser();

        if (!dono.equals(user) && user.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Usuário autenticado não é dono da Venue (" + dono.getLogin() + ")");
        }
        if (proposal.getStatus() != Status.PENDENTE) {
            throw new ValidationException("Tentando aceitar proposta que não está pendente (" + proposal.getStatus() + ")");
        }

        proposal.setStatus(Status.ACEITO);

        return proposalMapper.toDTO(proposal);
    }

    @Transactional
    public ProposalResponseDTO recusaProposal(Long id) {
        Proposal proposal = this.findById(id);
        User dono = proposal.getVenue().getUser();
        User user = authenticationService.getAuthenticatedUser();

        if (!dono.equals(user) && user.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Usuário autenticado não é dono da Venue (" + dono.getLogin() + ")");
        }
        if (proposal.getStatus() != Status.PENDENTE) {
            throw new ValidationException("Tentando recusar proposta que não está pendente (" + proposal.getStatus() + ")");
        }

        proposal.setStatus(Status.RECUSADO);

        return proposalMapper.toDTO(proposal);
    }

    @Transactional
    public ProposalResponseDTO confirmaProposal(Long id) {
        Proposal proposal = this.findById(id);
        User client = proposal.getEvent().getUser();
        User user = authenticationService.getAuthenticatedUser();

        if (!client.equals(user) && user.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Usuário autenticado não é organizador do Evento (" + client.getLogin() + ")");
        }
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
