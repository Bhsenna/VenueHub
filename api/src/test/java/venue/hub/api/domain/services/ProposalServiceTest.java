package venue.hub.api.domain.services;

import jakarta.validation.ValidationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import venue.hub.api.domain.dtos.address.AddressResponseDTO;
import venue.hub.api.domain.dtos.event.EventResponseDTO;
import venue.hub.api.domain.dtos.mapper.ProposalMapper;
import venue.hub.api.domain.dtos.proposal.ProposalRequestDTO;
import venue.hub.api.domain.dtos.proposal.ProposalResponseDTO;
import venue.hub.api.domain.dtos.proposal.ProposalUpdateDTO;
import venue.hub.api.domain.dtos.user.UserResponseDTO;
import venue.hub.api.domain.dtos.venue.VenueResponseDTO;
import venue.hub.api.domain.entities.*;
import venue.hub.api.domain.enums.Estado;
import venue.hub.api.domain.enums.Status;
import venue.hub.api.domain.enums.TipoEvento;
import venue.hub.api.domain.enums.UserRole;
import venue.hub.api.domain.repositories.ProposalRepository;
import venue.hub.api.domain.validators.proposal.ProposalValidator;
import venue.hub.api.infra.exceptions.ProposalNotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProposalServiceTest {

    @Mock
    private ProposalRepository proposalRepository;

    @Mock
    private ProposalMapper proposalMapper;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private ProposalValidator proposalValidator;

    @InjectMocks
    private ProposalService proposalService;

    private User ownerUser;
    private User clientUser;
    private User adminUser;
    private Event event;
    private Venue venue;
    private Address address;
    private Proposal proposal;
    private ProposalRequestDTO proposalRequestDTO;
    private ProposalUpdateDTO proposalUpdateDTO;
    private ProposalResponseDTO proposalResponseDTO;

    @Before
    public void setUp() {

        address = new Address(1L, "8889999", "Logradouro", 123, "complemento", "Bairro", "Cidade", Estado.SC, -1.0, -1.0, null);
        AddressResponseDTO addressResponseDTO = new AddressResponseDTO(1L, "8889999", "Logradouro", 123, "complemento", "Bairro", "Cidade", Estado.SC, -1.0, -1.0);

        clientUser = new User(2L, "Nome", "Sobrenome", "login@test.com", "Senha@teste", UserRole.CLIENT, address, true);
        UserResponseDTO clientuserResponseDTO = new UserResponseDTO(1L, "Nome", "Sobrenome", "login@test.com", addressResponseDTO);

        ownerUser = new User(1L, "Owner", "Sobrenome", "owner@test.com", "Senha@client", UserRole.OWNER, address, true);
        UserResponseDTO ownerUserResponseDTO = new UserResponseDTO(1L, "Owner", "Sobrenome", "owner@test.com", addressResponseDTO);

        adminUser = new User(3L, "Admin", "Sobrenome", "admin@test.com", "Senha@admin", UserRole.ADMIN, address, true);

        venue = new Venue(1L, "Nome", 123, "Descrição", "47 9999-0000", 1000, address, ownerUser, new ArrayList<>(), true);
        VenueResponseDTO venueResponseDTO = new VenueResponseDTO(1L, "Nome", 123, "Descrição", "47 9999-0000", 1000, new ArrayList<>(), addressResponseDTO, ownerUserResponseDTO, true);

        event = new Event(1L, "Nome", TipoEvento.ANIVERSARIO, 50, LocalDate.of(2025, 8, 13), LocalDate.of(2025, 8, 13), LocalTime.of(12, 30), LocalTime.of(17, 30), clientUser, null);
        EventResponseDTO eventResponseDTO = new EventResponseDTO(1L, "Nome", TipoEvento.ANIVERSARIO, 50, LocalDate.of(2025, 8, 13), LocalDate.of(2025, 8, 13), LocalTime.of(12, 30), LocalTime.of(17, 30), clientuserResponseDTO, null);

        proposal = new Proposal(1L, event, venue, 50.00, Status.PENDENTE, LocalDateTime.now());
        proposalRequestDTO = new ProposalRequestDTO(1L, 1L, 50.00);
        proposalUpdateDTO = new ProposalUpdateDTO(100.00);
        proposalResponseDTO = new ProposalResponseDTO(1L, eventResponseDTO, venueResponseDTO, 50.00, Status.PENDENTE, LocalDateTime.now());


        proposalService.proposalValidators = List.of(proposalValidator);
    }

    @Test
    @DisplayName("Deve criar uma nova proposta com sucesso")
    public void createProposal_shouldCreateNewProposal_whenValidRequest() {
        // Arrange
        when(proposalMapper.toEntity(any(ProposalRequestDTO.class))).thenReturn(proposal);
        when(proposalRepository.save(any(Proposal.class))).thenReturn(proposal);
        when(proposalMapper.toDTO(any(Proposal.class))).thenReturn(proposalResponseDTO);

        // Act
        ProposalResponseDTO result = proposalService.createProposal(proposalRequestDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(proposal.getId());
        assertThat(result.getStatus()).isEqualTo(Status.PENDENTE);

        verify(proposalValidator).validate(proposalRequestDTO);
        verify(proposalMapper).toEntity(proposalRequestDTO);
        verify(proposalRepository).save(proposal);
        verify(proposalMapper).toDTO(proposal);
    }

    @Test
    @DisplayName("Deve retornar propostas do OWNER autenticado")
    public void getAllProposals_shouldReturnProposalsForOwner() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Specification<Proposal> spec = Specification.allOf();


        Proposal proposal1 = new Proposal(1L, event, venue, 50.0, Status.PENDENTE, LocalDateTime.now());
        Proposal proposal2 = new Proposal(2L, event, venue, 100.0, Status.PENDENTE, LocalDateTime.now());
        Page<Proposal> proposalPage = new PageImpl<>(List.of(proposal1, proposal2), pageable, 2);

        when(authenticationService.getAuthenticatedUser()).thenReturn(ownerUser);
        when(proposalRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(proposalPage);
        when(proposalMapper.toDTO(proposal1)).thenReturn(proposalResponseDTO);
        when(proposalMapper.toDTO(proposal2)).thenReturn(proposalResponseDTO);

        // Act
        Page<ProposalResponseDTO> result = proposalService.getAllProposals(spec, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        verify(authenticationService).getAuthenticatedUser();
        verify(proposalRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("Deve retornar propostas do CLIENT autenticado")
    public void getAllProposals_shouldReturnProposalsForClient() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Specification<Proposal> spec = Specification.allOf();

        Page<Proposal> proposalPage = new PageImpl<>(List.of(proposal), pageable, 1);

        when(authenticationService.getAuthenticatedUser()).thenReturn(clientUser);
        when(proposalRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(proposalPage);
        when(proposalMapper.toDTO(proposal)).thenReturn(proposalResponseDTO);

        // Act
        Page<ProposalResponseDTO> result = proposalService.getAllProposals(spec, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(authenticationService).getAuthenticatedUser();
        verify(proposalRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("Deve retornar todas as propostas para ADMIN")
    public void getAllProposals_shouldReturnAllProposalsForAdmin() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Specification<Proposal> spec = Specification.allOf();

        Page<Proposal> proposalPage = new PageImpl<>(List.of(proposal), pageable, 1);

        when(authenticationService.getAuthenticatedUser()).thenReturn(adminUser);
        when(proposalRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(proposalPage);
        when(proposalMapper.toDTO(proposal)).thenReturn(proposalResponseDTO);

        // Act
        Page<ProposalResponseDTO> result = proposalService.getAllProposals(spec, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(authenticationService).getAuthenticatedUser();
        verify(proposalRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("Deve buscar uma proposta pelo ID com sucesso")
    public void getProposalById_shouldReturnProposal_whenExists() {
        // Arrange
        when(proposalRepository.findById(1L)).thenReturn(Optional.of(proposal));
        when(proposalMapper.toDTO(proposal)).thenReturn(proposalResponseDTO);

        // Act
        ProposalResponseDTO result = proposalService.getProposalById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(proposal.getId());
        assertThat(result.getValor()).isEqualTo(proposal.getValor());

        verify(proposalRepository).findById(1L);
        verify(proposalMapper).toDTO(proposal);
    }


    @Test
    @DisplayName("Deve lançar ProposalNotFoundException se a proposta não existir")
    public void getProposalById_shouldThrowProposalNotFoundException_whenDoesNotExist() {
        // Arrange
        when(proposalRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> proposalService.getProposalById(999L))
                .isInstanceOf(ProposalNotFoundException.class)
                .hasMessageContaining("Proposta não encontrada com o id: 999");

        verify(proposalRepository).findById(999L);
        verify(proposalMapper, never()).toDTO((Proposal) any());
    }

    @Test
    @DisplayName("Deve retornar propostas de um venue com sucesso")
    public void getProposalsByVenueId_shouldReturnProposals_whenExist() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Proposal> proposalPage = new PageImpl<>(List.of(proposal), pageable, 1);

        when(proposalRepository.findByVenueId(1L, pageable)).thenReturn(proposalPage);
        when(proposalMapper.toDTO(proposal)).thenReturn(proposalResponseDTO);

        // Act
        Page<ProposalResponseDTO> result = proposalService.getProposalsByVenueId(1L, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(proposalResponseDTO);

        verify(proposalRepository).findByVenueId(1L, pageable);
        verify(proposalMapper).toDTO(proposal);
    }

    @Test
    @DisplayName("Deve retornar propostas de um evento com sucesso")
    public void getProposalsByEventId_shouldReturnProposals_whenExist() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Proposal> proposalPage = new PageImpl<>(List.of(proposal), pageable, 1);

        when(proposalRepository.findByEventId(1L, pageable)).thenReturn(proposalPage);
        when(proposalMapper.toDTO(proposal)).thenReturn(proposalResponseDTO);

        // Act
        Page<ProposalResponseDTO> result = proposalService.getProposalsByEventId(1L, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(proposalResponseDTO);

        verify(proposalRepository).findByEventId(1L, pageable);
        verify(proposalMapper).toDTO(proposal);
    }

    @Test
    @DisplayName("Deve retornar propostas de um venue com status específico com sucesso")
    public void getProposalsByVenueIdAndStatus_shouldReturnProposals_whenExist() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Proposal> proposalPage = new PageImpl<>(List.of(proposal), pageable, 1);

        when(proposalRepository.findByVenueIdAndStatus(1L, Status.PENDENTE, pageable)).thenReturn(proposalPage);
        when(proposalMapper.toDTO(proposal)).thenReturn(proposalResponseDTO);

        // Act
        Page<ProposalResponseDTO> result = proposalService.getProposalsByVenueIdAndStatus(1L, Status.PENDENTE, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(proposalResponseDTO);

        verify(proposalRepository).findByVenueIdAndStatus(1L, Status.PENDENTE, pageable);
        verify(proposalMapper).toDTO(proposal);
    }

    @Test
    @DisplayName("Deve atualizar uma proposta com sucesso")
    public void updateProposal_shouldUpdateProposal_whenValid() {
        // Arrange
        when(proposalRepository.findById(1L)).thenReturn(Optional.of(proposal));
        when(proposalRepository.save(proposal)).thenReturn(proposal);
        when(proposalMapper.toDTO(proposal)).thenReturn(proposalResponseDTO);

        // Act
        ProposalResponseDTO result = proposalService.updateProposal(1L, proposalUpdateDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(proposalResponseDTO);

        verify(proposalRepository).findById(1L);
        verify(proposalRepository).save(proposal);
        verify(proposalMapper).toDTO(proposal);
    }


    @Test
    @DisplayName("Deve cancelar uma proposta com sucesso")
    public void deleteProposal_shouldCancelProposal_whenExists() {
        // Arrange
        when(proposalRepository.findById(1L)).thenReturn(Optional.of(proposal));
        when(proposalMapper.toDTO(proposal)).thenReturn(proposalResponseDTO);

        // Act
        ProposalResponseDTO result = proposalService.deleteProposal(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(proposalResponseDTO);
        assertThat(proposal.getStatus()).isEqualTo(Status.CANCELADO);

        verify(proposalRepository).findById(1L);
        verify(proposalMapper).toDTO(proposal);
    }

    @Test
    @DisplayName("Deve aceitar a proposta com sucesso quando usuário é dono")
    public void aceitaProposal_shouldAcceptProposal_whenUserIsOwner() {
        // Arrange
        when(proposalRepository.findById(1L)).thenReturn(Optional.of(proposal));
        when(authenticationService.getAuthenticatedUser()).thenReturn(ownerUser);
        when(proposalMapper.toDTO(proposal)).thenReturn(proposalResponseDTO);

        // Act
        ProposalResponseDTO result = proposalService.aceitaProposal(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(proposalResponseDTO);
        assertThat(proposal.getStatus()).isEqualTo(Status.ACEITO);

        verify(proposalRepository).findById(1L);
        verify(authenticationService).getAuthenticatedUser();
        verify(proposalMapper).toDTO(proposal);
    }

    @Test
    @DisplayName("Deve aceitar a proposta com sucesso quando usuário é admin")
    public void aceitaProposal_shouldAcceptProposal_whenUserIsAdmin() {
        // Arrange
        when(proposalRepository.findById(1L)).thenReturn(Optional.of(proposal));
        when(authenticationService.getAuthenticatedUser()).thenReturn(adminUser);
        when(proposalMapper.toDTO(proposal)).thenReturn(proposalResponseDTO);

        // Act
        ProposalResponseDTO result = proposalService.aceitaProposal(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(proposalResponseDTO);
        assertThat(proposal.getStatus()).isEqualTo(Status.ACEITO);

        verify(proposalRepository).findById(1L);
        verify(authenticationService).getAuthenticatedUser();
        verify(proposalMapper).toDTO(proposal);
    }


    @Test
    @DisplayName("Deve lançar AccessDeniedException quando usuário não é dono nem admin")
    public void aceitaProposal_shouldThrowAccessDeniedException_whenUserNotOwnerOrAdmin() {
        // Arrange
        User anotherUser = new User();
        anotherUser.setId(99L);
        anotherUser.setRole(UserRole.CLIENT);

        when(proposalRepository.findById(1L)).thenReturn(Optional.of(proposal));
        when(authenticationService.getAuthenticatedUser()).thenReturn(anotherUser);

        // Act & Assert
        assertThatThrownBy(() -> proposalService.aceitaProposal(1L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Usuário autenticado não é dono da Venue");

        verify(proposalRepository).findById(1L);
        verify(authenticationService).getAuthenticatedUser();
        verify(proposalMapper, never()).toDTO((Proposal) any());
    }

    @Test
    @DisplayName("Deve lançar ValidationException quando proposta não estiver pendente")
    public void aceitaProposal_shouldThrowValidationException_whenProposalNotPending() {
        // Arrange
        proposal.setStatus(Status.ACEITO);
        when(proposalRepository.findById(1L)).thenReturn(Optional.of(proposal));
        when(authenticationService.getAuthenticatedUser()).thenReturn(ownerUser);

        // Act & Assert
        assertThatThrownBy(() -> proposalService.aceitaProposal(1L))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Tentando aceitar proposta que não está pendente");

        verify(proposalRepository).findById(1L);
        verify(authenticationService).getAuthenticatedUser();
        verify(proposalMapper, never()).toDTO((Proposal) any());
    }

    @Test
    @DisplayName("Deve recusar a proposta com sucesso quando usuário é dono")
    public void recusaProposal_shouldRefuseProposal_whenUserIsOwner() {
        // Arrange
        when(proposalRepository.findById(1L)).thenReturn(Optional.of(proposal));
        when(authenticationService.getAuthenticatedUser()).thenReturn(ownerUser);
        when(proposalMapper.toDTO(proposal)).thenReturn(proposalResponseDTO);

        // Act
        ProposalResponseDTO result = proposalService.recusaProposal(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(proposalResponseDTO);
        assertThat(proposal.getStatus()).isEqualTo(Status.RECUSADO);

        verify(proposalRepository).findById(1L);
        verify(authenticationService).getAuthenticatedUser();
        verify(proposalMapper).toDTO(proposal);
    }

    @Test
    @DisplayName("Deve recusar a proposta com sucesso quando usuário é admin")
    public void recusaProposal_shouldRefuseProposal_whenUserIsAdmin() {
        // Arrange
        when(proposalRepository.findById(1L)).thenReturn(Optional.of(proposal));
        when(authenticationService.getAuthenticatedUser()).thenReturn(adminUser);
        when(proposalMapper.toDTO(proposal)).thenReturn(proposalResponseDTO);

        // Act
        ProposalResponseDTO result = proposalService.recusaProposal(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(proposalResponseDTO);
        assertThat(proposal.getStatus()).isEqualTo(Status.RECUSADO);

        verify(proposalRepository).findById(1L);
        verify(authenticationService).getAuthenticatedUser();
        verify(proposalMapper).toDTO(proposal);
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException quando usuário não é dono nem admin")
    public void recusaProposal_shouldThrowAccessDeniedException_whenUserNotOwnerOrAdmin() {
        // Arrange
        User anotherUser = new User();
        anotherUser.setId(99L);
        anotherUser.setRole(UserRole.CLIENT);

        when(proposalRepository.findById(1L)).thenReturn(Optional.of(proposal));
        when(authenticationService.getAuthenticatedUser()).thenReturn(anotherUser);

        // Act & Assert
        assertThatThrownBy(() -> proposalService.recusaProposal(1L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Usuário autenticado não é dono da Venue");

        verify(proposalRepository).findById(1L);
        verify(authenticationService).getAuthenticatedUser();
        verify(proposalMapper, never()).toDTO((Proposal) any());
    }

    @Test
    @DisplayName("Deve lançar ValidationException quando proposta não estiver pendente")
    public void recusaProposal_shouldThrowValidationException_whenProposalNotPending() {
        // Arrange
        proposal.setStatus(Status.ACEITO); // status diferente de PENDENTE
        when(proposalRepository.findById(1L)).thenReturn(Optional.of(proposal));
        when(authenticationService.getAuthenticatedUser()).thenReturn(ownerUser);

        // Act & Assert
        assertThatThrownBy(() -> proposalService.recusaProposal(1L))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Tentando recusar proposta que não está pendente");

        verify(proposalRepository).findById(1L);
        verify(authenticationService).getAuthenticatedUser();
        verify(proposalMapper, never()).toDTO((Proposal) any());
    }

    @Test
    @DisplayName("Deve confirmar a proposta com sucesso e cancelar outras pendentes")
    public void confirmaProposal_shouldConfirmProposalAndCancelOtherPending() {
        // Arrange
        proposal.setStatus(Status.ACEITO);
        Proposal otherPending = new Proposal(2L, event, venue, 60.0, Status.PENDENTE, LocalDateTime.now());
        List<Proposal> pendingList = List.of(otherPending);

        when(proposalRepository.findById(1L)).thenReturn(Optional.of(proposal));
        when(authenticationService.getAuthenticatedUser()).thenReturn(clientUser);
        when(proposalRepository.findAllByEventAndStatusNotIn(event, proposalService.endStatus)).thenReturn(pendingList);
        when(proposalMapper.toDTO(proposal)).thenReturn(proposalResponseDTO);

        // Act
        ProposalResponseDTO result = proposalService.confirmaProposal(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(proposalResponseDTO);
        assertThat(proposal.getStatus()).isEqualTo(Status.CONFIRMADO);
        assertThat(otherPending.getStatus()).isEqualTo(Status.CANCELADO);

        verify(proposalRepository).findById(1L);
        verify(authenticationService).getAuthenticatedUser();
        verify(proposalRepository).findAllByEventAndStatusNotIn(event, proposalService.endStatus);
        verify(proposalMapper).toDTO(proposal);
    }


    @Test
    @DisplayName("Deve lançar AccessDeniedException quando usuário não é organizador nem admin")
    public void confirmaProposal_shouldThrowAccessDeniedException_whenUserNotEventOwnerOrAdmin() {
        // Arrange
        User anotherUser = new User();
        anotherUser.setId(99L);
        anotherUser.setRole(UserRole.CLIENT);

        when(proposalRepository.findById(1L)).thenReturn(Optional.of(proposal));
        when(authenticationService.getAuthenticatedUser()).thenReturn(anotherUser);

        // Act & Assert
        assertThatThrownBy(() -> proposalService.confirmaProposal(1L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Usuário autenticado não é organizador do Evento");

        verify(proposalRepository).findById(1L);
        verify(authenticationService).getAuthenticatedUser();
        verify(proposalMapper, never()).toDTO((Proposal) any());
    }

    @Test
    @DisplayName("Deve lançar ValidationException quando status da proposta não for ACEITO")
    public void confirmaProposal_shouldThrowValidationException_whenProposalNotAccepted() {
        // Arrange
        proposal.setStatus(Status.PENDENTE);
        when(proposalRepository.findById(1L)).thenReturn(Optional.of(proposal));
        when(authenticationService.getAuthenticatedUser()).thenReturn(clientUser);

        // Act & Assert
        assertThatThrownBy(() -> proposalService.confirmaProposal(1L))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Tentando confirmar proposta que não está aceita");

        verify(proposalRepository).findById(1L);
        verify(authenticationService).getAuthenticatedUser();
        verify(proposalMapper, never()).toDTO((Proposal) any());
    }

    @Test
    @DisplayName("Deve expirar propostas com mais de 7 dias e retornar os DTOs")
    public void expiraProposal_shouldExpireOldProposals() {
        // Arrange
        Proposal oldProposal = new Proposal(1L, event, venue, 50.0, Status.PENDENTE,
                LocalDateTime.now().minusDays(8));
        Proposal recentProposal = new Proposal(2L, event, venue, 60.0, Status.PENDENTE,
                LocalDateTime.now().minusDays(3));

        List<Proposal> allProposals = List.of(oldProposal, recentProposal);
        List<Proposal> expiredProposals = List.of(oldProposal);

        ProposalResponseDTO oldProposalDTO = new ProposalResponseDTO(1L, null, null, 50.0, Status.EXPIRADO, oldProposal.getDataCriacao());

        when(proposalRepository.findAllByStatusNotIn(proposalService.endStatus)).thenReturn(allProposals);
        when(proposalMapper.toDTO(expiredProposals)).thenReturn(List.of(oldProposalDTO));

        // Act
        List<ProposalResponseDTO> result = proposalService.expiraProposal();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(oldProposalDTO);

        assertThat(oldProposal.getStatus()).isEqualTo(Status.EXPIRADO);
        assertThat(recentProposal.getStatus()).isEqualTo(Status.PENDENTE);

        verify(proposalRepository).findAllByStatusNotIn(proposalService.endStatus);
        verify(proposalMapper).toDTO(expiredProposals);
    }






}
