package venue.hub.api.domain.services;

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
import venue.hub.api.domain.dtos.address.AddressRequestDTO;
import venue.hub.api.domain.dtos.address.AddressResponseDTO;
import venue.hub.api.domain.dtos.event.EventResponseDTO;
import venue.hub.api.domain.dtos.mapper.EventMapper;
import venue.hub.api.domain.dtos.mapper.VenueMapper;
import venue.hub.api.domain.dtos.user.UserResponseDTO;
import venue.hub.api.domain.dtos.venue.VenueRequestDTO;
import venue.hub.api.domain.dtos.venue.VenueResponseDTO;
import venue.hub.api.domain.dtos.venue.VenueUpdateDTO;
import venue.hub.api.domain.dtos.venueadditional.VenueAdditionalRequestDTO;
import venue.hub.api.domain.entities.*;
import venue.hub.api.domain.enums.Estado;
import venue.hub.api.domain.enums.UserRole;
import venue.hub.api.domain.repositories.AddressRepository;
import venue.hub.api.domain.repositories.VenueRepository;
import venue.hub.api.domain.validators.address.AddressValidator;
import venue.hub.api.infra.exceptions.VenueNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class VenueServiceTest {

    @Mock
    private VenueRepository venueRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private VenueMapper venueMapper;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private AdditionalService additionalService;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private AddressValidator addressValidator;

    @InjectMocks
    private VenueService venueService;

    private User ownerUser;
    private User adminUser;
    private Venue venue;
    private Address address;
    private AddressRequestDTO addressRequestDTO;
    private VenueRequestDTO venueRequestDTO;
    private VenueUpdateDTO venueUpdateDTO;
    private VenueResponseDTO venueResponseDTO;
    private Event event;
    private EventResponseDTO eventResponseDTO;

    @Before
    public void setUp() {
        address = new Address(1L, "8889999", "Logradouro", 123, "complemento", "Bairro", "Cidade", Estado.SC, -1.0, -1.0);
        addressRequestDTO = new AddressRequestDTO("8889999", "Logradouro", 123, "complemento", "Bairro", "Cidade", Estado.SC, -1.0, -1.0);
        AddressResponseDTO addressResponseDTO = new AddressResponseDTO(1L, "8889999", "Logradouro", 123, "complemento", "Bairro", "Cidade", Estado.SC, -1.0, -1.0);

        ownerUser = new User(1L, "Nome", "Sobrenome", "login@test.com", "Senha@teste", UserRole.CLIENT, address, true);

        adminUser = new User(2L, "Admin", "Sobrenome", "admin@test.com", "Senha@admin", UserRole.OWNER, address, true);

        venue = new Venue(1L, "Nome", 123, "Descrição", "47 9999-0000", 1000, address, ownerUser, new ArrayList<>(), true);

        venueRequestDTO = new VenueRequestDTO("New Venue", 123, "Description", "99999-999", 2000, addressRequestDTO, null);
        venueUpdateDTO = new VenueUpdateDTO("Updated Venue", 123, "Updated Description", "99999-999", 2000.0, null, ownerUser.getId());
        venueResponseDTO = new VenueResponseDTO(1L, "Nome", 123, "Updated Description", "99999-999", 2000.0, null, addressResponseDTO, new UserResponseDTO(1L, "Nome", "Sobrenome", "login@login", addressResponseDTO), true);

        event = new Event();
        eventResponseDTO = new EventResponseDTO();

        List<AddressValidator> validators = new ArrayList<>();
        validators.add(addressValidator);
        venueService.addressValidators = validators;
    }

    @Test
    @DisplayName("Deve criar uma nova venue com sucesso")
    public void createVenue_shouldCreateNewVenueWhenValidRequest() {
        // Arrange
        when(authenticationService.getAuthenticatedUser()).thenReturn(ownerUser);
        when(venueMapper.toEntity(any(VenueRequestDTO.class))).thenReturn(venue);
        when(venueRepository.save(any(Venue.class))).thenReturn(venue);
        when(addressRepository.save(any(Address.class))).thenReturn(venue.getAddress());
        when(venueMapper.toDTO(any(Venue.class))).thenReturn(venueResponseDTO);

        // Act
        VenueResponseDTO result = venueService.createVenue(venueRequestDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(venue.getId());
        assertThat(result.getNome()).isEqualTo(venue.getNome());

        verify(authenticationService).getAuthenticatedUser();
        verify(venueMapper).toEntity(venueRequestDTO);
        verify(addressRepository).save(any(Address.class));
        verify(venueRepository).save(venue);
        verify(venueMapper).toDTO(venue);
    }

    @Test
    @DisplayName("Deve criar uma nova venue com adicionais com sucesso")
    public void createVenue_shouldCreateNewVenueWithAdditionals_whenValidRequest() {
        // Arrange
        Long additionalId = 2L;
        Additional additional = new Additional(additionalId, "Cozinheiro");
        VenueAdditionalRequestDTO additionalDTO = new VenueAdditionalRequestDTO(additionalId, 1500.0);
        List<VenueAdditionalRequestDTO> additionalListDTO = List.of(additionalDTO);

        VenueRequestDTO venueWithAdditionalsRequestDTO = new VenueRequestDTO(
                "New Venue", 123, "Description", "99999-999", 2000, addressRequestDTO, additionalListDTO
        );

        Venue venueWithAdditionals = new Venue(
                1L, "New Venue", 123, "Description", "99999-999", 2000, address, ownerUser, new ArrayList<>(), true
        );

        VenueAdditional venueAdditional = new VenueAdditional();
        venueAdditional.setVenue(venueWithAdditionals);
        venueAdditional.setAdditional(additional);
        venueAdditional.setValor(1500.0);
        List<VenueAdditional> venueAdditionalsList = List.of(venueAdditional);

        VenueResponseDTO venueWithAdditionalsResponseDTO = new VenueResponseDTO(
                1L, "New Venue", 123, "Description", "99999-999", 2000, null, null, null, true
        );

        when(authenticationService.getAuthenticatedUser()).thenReturn(ownerUser);
        doNothing().when(addressValidator).validate(any());
        when(venueMapper.toEntity(any(VenueRequestDTO.class))).thenReturn(venueWithAdditionals);
        when(additionalService.findById(anyLong())).thenReturn(additional);
        when(addressRepository.save(any(Address.class))).thenReturn(address);
        when(venueRepository.save(any(Venue.class))).thenReturn(venueWithAdditionals);
        when(venueMapper.toDTO(any(Venue.class))).thenReturn(venueWithAdditionalsResponseDTO);

        // Act
        VenueResponseDTO result = venueService.createVenue(venueWithAdditionalsRequestDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getNome()).isEqualTo("New Venue");

        verify(authenticationService).getAuthenticatedUser();
        verify(venueMapper).toEntity(venueWithAdditionalsRequestDTO);
        verify(additionalService).findById(additionalId);
        verify(addressRepository).save(any(Address.class));
        verify(venueRepository).save(any(Venue.class));
        verify(venueMapper).toDTO(any(Venue.class));
    }

    @Test
    @DisplayName("Deve buscar uma venue pelo ID com sucesso (dono)")
    public void getVenueById_shouldFindVenue_whenUserIsOwner() {
        // Arrange
        when(venueRepository.findById(anyLong())).thenReturn(Optional.of(venue));
        when(authenticationService.getAuthenticatedUser()).thenReturn(ownerUser);
        when(venueMapper.toDTO(any(Venue.class))).thenReturn(venueResponseDTO);

        // Act
        VenueResponseDTO result = venueService.getVenueById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(venue.getId());

        verify(venueRepository).findById(1L);
        verify(authenticationService).getAuthenticatedUser();
        verify(venueMapper).toDTO(venue);
    }

    @Test
    @DisplayName("Deve buscar uma venue pelo ID com sucesso (admin)")
    public void getVenueById_shouldFindVenue_whenUserIsAdmin() {
        // Arrange
        when(venueRepository.findById(anyLong())).thenReturn(Optional.of(venue));
        when(authenticationService.getAuthenticatedUser()).thenReturn(ownerUser);
        when(venueMapper.toDTO(any(Venue.class))).thenReturn(venueResponseDTO);

        // Act
        VenueResponseDTO result = venueService.getVenueById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(venue.getId());

        verify(venueRepository).findById(1L);
        verify(authenticationService).getAuthenticatedUser();
        verify(venueMapper).toDTO(venue);
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException ao buscar venue de outro usuário")
    public void getVenueById_shouldThrowAccessDeniedException_whenUserIsNotOwnerOrAdmin() {
        // Arrange
        User anotherUser = new User();
        anotherUser.setId(3L);
        anotherUser.setRole(UserRole.CLIENT);

        when(venueRepository.findById(anyLong())).thenReturn(Optional.of(venue));
        when(authenticationService.getAuthenticatedUser()).thenReturn(anotherUser);

        // Act & Assert
        assertThatThrownBy(() -> venueService.getVenueById(1L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Usuário autenticado não é dono da Venue");

        verify(venueRepository).findById(1L);
        verify(authenticationService).getAuthenticatedUser();
        verify(venueMapper, never()).toDTO(any(Venue.class));
    }

    @Test
    @DisplayName("Deve atualizar uma venue com sucesso (dono)")
    public void updateVenue_shouldUpdateVenue_whenUserIsOwner() {
        // Arrange
        when(venueRepository.findById(anyLong())).thenReturn(Optional.of(venue));
        when(authenticationService.getAuthenticatedUser()).thenReturn(ownerUser);
        when(venueRepository.save(any(Venue.class))).thenReturn(venue);
        when(addressRepository.save(any(Address.class))).thenReturn(venue.getAddress());
        when(venueMapper.toDTO(any(Venue.class))).thenReturn(venueResponseDTO);

        // Act
        VenueResponseDTO result = venueService.updateVenue(1L, venueUpdateDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getNome()).isEqualTo(venueResponseDTO.getNome());

        verify(venueRepository).findById(1L);
        verify(authenticationService).getAuthenticatedUser();
        verify(venueRepository).save(any(Venue.class));
        verify(addressRepository).save(any(Address.class));
        verify(venueMapper).toDTO(venue);
    }

    @Test
    @DisplayName("Deve deletar uma venue com sucesso (dono)")
    public void deleteVenue_shouldDeleteVenue_whenUserIsOwner() {
        // Arrange
        Venue spyVenue = spy(venue);
        when(venueRepository.findById(anyLong())).thenReturn(Optional.of(spyVenue));
        when(authenticationService.getAuthenticatedUser()).thenReturn(ownerUser);

        // Act
        venueService.deleteVenue(1L);

        // Assert
        verify(venueRepository).findById(1L);
        verify(authenticationService).getAuthenticatedUser();
        verify(spyVenue).delete();
        verify(venueRepository).save(spyVenue);
    }

    @Test
    @DisplayName("Deve lançar VenueNotFoundException se a venue não for encontrada")
    public void findById_shouldThrowVenueNotFoundException_whenVenueDoesNotExist() {
        // Arrange
        when(venueRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> venueService.findById(999L))
                .isInstanceOf(VenueNotFoundException.class)
                .hasMessageContaining("Local não encontrado com o id: 999");

        verify(venueRepository).findById(999L);
    }

    @Test
    @DisplayName("Deve buscar eventos de uma venue com sucesso")
    public void getEvents_shouldFindEvents_whenVenueExists() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> eventPage = new PageImpl<>(List.of(event), pageable, 1);
        Page<EventResponseDTO> eventDTOPage = new PageImpl<>(List.of(eventResponseDTO), pageable, 1);

        when(venueRepository.findEvents(anyLong(), any(Pageable.class))).thenReturn(eventPage);
        when(eventMapper.toDTO(any(Event.class))).thenReturn(eventResponseDTO);

        // Act
        Page<EventResponseDTO> result = venueService.getEvents(1L, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(eventResponseDTO);

        verify(venueRepository).findEvents(1L, pageable);
        verify(eventMapper).toDTO(event);
    }

    @Test
    @DisplayName("Deve retornar uma lista de venues para o OWNER autenticado")
    public void getAllVenues_shouldReturnVenuesForAuthenticatedOwner() {
        // Arrange
        Pageable paginacao = PageRequest.of(0, 10);
        Specification<Venue> spec = Specification.allOf();

        User testOwnerUser = new User();
        testOwnerUser.setRole(UserRole.OWNER);
        testOwnerUser.setId(1L);

        Venue venue1 = new Venue(2L, "Venue do Owner", 150, "Descrição", "99999-999", 1000, address, testOwnerUser, new ArrayList<>(), true);
        Venue venue2 = new Venue(3L, "Outra Venue do Owner", 200, "Descrição", "99999-999", 1200, address, testOwnerUser, new ArrayList<>(), true);

        Page<Venue> venuePage = new PageImpl<>(List.of(venue1, venue2), paginacao, 2);

        VenueResponseDTO venueDTO1 = new VenueResponseDTO(2L, "Venue do Owner", 150, "Descrição", "99999-999", 1000.0, null, null, null, true);
        VenueResponseDTO venueDTO2 = new VenueResponseDTO(3L, "Outra Venue do Owner", 200, "Descrição", "99999-999", 1200.0, null, null, null, true);

        when(authenticationService.getAuthenticatedUser()).thenReturn(testOwnerUser);
        when(venueRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(venuePage);
        when(venueMapper.toDTO(venue1)).thenReturn(venueDTO1);
        when(venueMapper.toDTO(venue2)).thenReturn(venueDTO2);

        // Act
        Page<VenueResponseDTO> result = venueService.getAllVenues(spec, paginacao);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).containsExactly(venueDTO1, venueDTO2);

        verify(venueRepository).findAll(any(Specification.class), eq(paginacao));
    }

    @Test
    @DisplayName("Deve retornar uma lista de venues para o ADMIN autenticado")
    public void getAllVenues_shouldReturnAllVenuesForAdmin() {
        // Arrange
        Pageable paginacao = PageRequest.of(0, 10);
        Specification<Venue> spec = Specification.allOf();

        Venue venue1 = new Venue(2L, "Venue do Owner", 150, "Descrição", "99999-999", 1000, address, ownerUser, new ArrayList<>(), true);
        Venue venue2 = new Venue(3L, "Outra Venue", 200, "Descrição", "99999-999", 1200, address, adminUser, new ArrayList<>(), true);

        Page<Venue> venuePage = new PageImpl<>(List.of(venue1, venue2), paginacao, 2);

        VenueResponseDTO venueDTO1 = new VenueResponseDTO(2L, "Venue do Owner", 150, "Descrição", "99999-999", 1000.0, null, null, null, true);
        VenueResponseDTO venueDTO2 = new VenueResponseDTO(3L, "Outra Venue", 200, "Descrição", "99999-999", 1200.0, null, null, null, true);

        when(authenticationService.getAuthenticatedUser()).thenReturn(adminUser);
        when(venueRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(venuePage);
        when(venueMapper.toDTO(venue1)).thenReturn(venueDTO1);
        when(venueMapper.toDTO(venue2)).thenReturn(venueDTO2);

        // Act
        Page<VenueResponseDTO> result = venueService.getAllVenues(spec, paginacao);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).containsExactly(venueDTO1, venueDTO2);

        verify(venueRepository).findAll(any(Specification.class), eq(paginacao));
    }

    @Test
    @DisplayName("Deve retornar null para um USER autenticado, pois ele não tem acesso")
    public void getAllVenues_shouldReturnNullForAuthenticatedUser() {
        // Arrange
        Pageable paginacao = PageRequest.of(0, 10);
        Specification<Venue> spec = Specification.allOf();

        User regularUser = new User();
        regularUser.setRole(UserRole.CLIENT);

        when(authenticationService.getAuthenticatedUser()).thenReturn(regularUser);

        // Act
        Page<VenueResponseDTO> result = venueService.getAllVenues(spec, paginacao);

        // Assert
        assertThat(result).isNull();

        verify(venueRepository, never()).findAll(any(Specification.class), any(Pageable.class));
    }
}