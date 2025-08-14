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
import venue.hub.api.domain.dtos.event.EventRequestDTO;
import venue.hub.api.domain.dtos.event.EventResponseDTO;
import venue.hub.api.domain.dtos.event.EventUpdateDTO;
import venue.hub.api.domain.dtos.eventadditional.EventAdditionalRequestDTO;
import venue.hub.api.domain.dtos.mapper.EventMapper;
import venue.hub.api.domain.dtos.user.UserResponseDTO;
import venue.hub.api.domain.entities.Additional;
import venue.hub.api.domain.entities.Address;
import venue.hub.api.domain.entities.Event;
import venue.hub.api.domain.entities.User;
import venue.hub.api.domain.enums.Estado;
import venue.hub.api.domain.enums.TipoEvento;
import venue.hub.api.domain.enums.UserRole;
import venue.hub.api.domain.repositories.EventRepository;
import venue.hub.api.domain.validators.event.EventValidator;
import venue.hub.api.infra.exceptions.EventNotFoundException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private AdditionalService additionalService;

    @Mock
    private EventValidator eventValidator;

    @InjectMocks
    private EventService eventService;

    private User clientUser;
    private User adminUser;
    private Address address;
    private AddressRequestDTO addressRequestDTO;
    private EventRequestDTO eventRequestDTO;
    private EventUpdateDTO eventUpdateDTO;
    private EventResponseDTO eventResponseDTO;
    private Event event;

    @Before
    public void setUp() {
        address = new Address(1L, "8889999", "Logradouro", 123, "complemento", "Bairro", "Cidade", Estado.SC, -1.0, -1.0);
        addressRequestDTO = new AddressRequestDTO("8889999", "Logradouro", 123, "complemento", "Bairro", "Cidade", Estado.SC, -1.0, -1.0);
        AddressResponseDTO addressResponseDTO = new AddressResponseDTO(1L, "8889999", "Logradouro", 123, "complemento", "Bairro", "Cidade", Estado.SC, -1.0, -1.0);

        clientUser = new User(1L, "Nome", "Sobrenome", "login@test.com", "Senha@teste", UserRole.CLIENT, address, true);

        adminUser = new User(2L, "Admin", "Sobrenome", "admin@test.com", "Senha@admin", UserRole.ADMIN, address, true);

        event = new Event(1L, "Nome", TipoEvento.ANIVERSARIO, 50, LocalDate.of(2025, 8, 13), LocalDate.of(2025, 8, 13), LocalTime.of(12, 30), LocalTime.of(17, 30), clientUser, null);

        eventRequestDTO = new EventRequestDTO("New Event", TipoEvento.ANIVERSARIO, 50, LocalDate.of(2025, 8, 13), LocalDate.of(2025, 8, 13), LocalTime.of(12, 30), LocalTime.of(17, 30), null);
        eventUpdateDTO = new EventUpdateDTO("Updated Event", TipoEvento.ANIVERSARIO, 50, LocalDate.of(2025, 8, 13), LocalDate.of(2025, 8, 13), LocalTime.of(12, 30), LocalTime.of(17, 30));
        eventResponseDTO = new EventResponseDTO(1L, "Nome", TipoEvento.ANIVERSARIO, 50, LocalDate.of(2025, 8, 13), LocalDate.of(2025, 8, 13), LocalTime.of(12, 30), LocalTime.of(17, 30), new UserResponseDTO(1L, "Nome", "Sobrenome", "login@login", addressResponseDTO), null);

        List<EventValidator> validators = new ArrayList<>();
        validators.add(eventValidator);
        eventService.eventValidators = validators;
    }

    @Test
    @DisplayName("Deve criar um novo evento com sucesso")
    public void createEvent_shouldCreateNewEvent_whenValidRequest() {
        // Arrange
        when(authenticationService.getAuthenticatedUser()).thenReturn(clientUser);
        when(eventMapper.toEntity(any(EventRequestDTO.class))).thenReturn(event);
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(eventMapper.toDTO(any(Event.class))).thenReturn(eventResponseDTO);

        // Act
        EventResponseDTO result = eventService.createEvent(eventRequestDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(event.getId());
        assertThat(result.getNome()).isEqualTo(event.getNome());

        verify(authenticationService).getAuthenticatedUser();
        verify(eventMapper).toEntity(eventRequestDTO);
        verify(eventRepository).save(event);
        verify(eventMapper).toDTO(event);
    }

    @Test
    @DisplayName("Deve criar um novo evento com adicionais com sucesso")
    public void createEvent_shouldCreateNewEventWithAdditionals_whenValidRequest() {
        // Arrange
        Long additionalId = 2L;
        Additional additional = new Additional(additionalId, "Cozinheiro");
        EventAdditionalRequestDTO additionalDTO = new EventAdditionalRequestDTO(additionalId);
        List<EventAdditionalRequestDTO> additionalListDTO = List.of(additionalDTO);

        EventRequestDTO eventWithAdditionalsRequestDTO = new EventRequestDTO(
                "New Event", TipoEvento.ANIVERSARIO, 50, LocalDate.of(2025, 8, 13), LocalDate.of(2025, 8, 13), LocalTime.of(12, 30), LocalTime.of(17, 30), additionalListDTO
        );

        Event eventWithAdditionals = new Event(
                1L, "New Event", TipoEvento.ANIVERSARIO, 50, LocalDate.of(2025, 8, 13), LocalDate.of(2025, 8, 13), LocalTime.of(12, 30), LocalTime.of(17, 30), clientUser, new ArrayList<>()
        );

        EventResponseDTO eventWithAdditionalsResponseDTO = new EventResponseDTO(
                1L, "New Event", TipoEvento.ANIVERSARIO, 50, LocalDate.of(2025, 8, 13), LocalDate.of(2025, 8, 13), LocalTime.of(12, 30), LocalTime.of(17, 30), null, null
        );

        when(authenticationService.getAuthenticatedUser()).thenReturn(clientUser);
        doNothing().when(eventValidator).validate(any());
        when(eventMapper.toEntity(any(EventRequestDTO.class))).thenReturn(eventWithAdditionals);
        when(additionalService.findById(anyLong())).thenReturn(additional);
        when(eventRepository.save(any(Event.class))).thenReturn(eventWithAdditionals);
        when(eventMapper.toDTO(any(Event.class))).thenReturn(eventWithAdditionalsResponseDTO);

        // Act
        EventResponseDTO result = eventService.createEvent(eventWithAdditionalsRequestDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getNome()).isEqualTo("New Event");

        verify(authenticationService).getAuthenticatedUser();
        verify(eventMapper).toEntity(eventWithAdditionalsRequestDTO);
        verify(additionalService).findById(additionalId);
        verify(eventRepository).save(any(Event.class));
        verify(eventMapper).toDTO(any(Event.class));
    }

    @Test
    @DisplayName("Deve buscar um evento pelo ID com sucesso (cliente)")
    public void getEventById_shouldFindEvent_whenUserIsClient() {
        // Arrange
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(authenticationService.getAuthenticatedUser()).thenReturn(clientUser);
        when(eventMapper.toDTO(any(Event.class))).thenReturn(eventResponseDTO);

        // Act
        EventResponseDTO result = eventService.getEventById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(event.getId());

        verify(eventRepository).findById(1L);
        verify(authenticationService).getAuthenticatedUser();
        verify(eventMapper).toDTO(event);
    }

    @Test
    @DisplayName("Deve buscar um evento pelo ID com sucesso (admin)")
    public void getEventById_shouldFindEvent_whenUserIsAdmin() {
        // Arrange
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(authenticationService.getAuthenticatedUser()).thenReturn(adminUser);
        when(eventMapper.toDTO(any(Event.class))).thenReturn(eventResponseDTO);

        // Act
        EventResponseDTO result = eventService.getEventById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(event.getId());

        verify(eventRepository).findById(1L);
        verify(authenticationService).getAuthenticatedUser();
        verify(eventMapper).toDTO(event);
    }

    @Test
    @DisplayName("Deve lancar AccessDeniedException ao buscar event de outro usuário")
    public void getEventById_shouldThrowAccessDeniedException_whenUserIsNotClientOrAdmin() {
        // Arrange
        User anotherUser = new User();
        anotherUser.setId(3L);
        anotherUser.setRole(UserRole.OWNER);

        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(authenticationService.getAuthenticatedUser()).thenReturn(anotherUser);

        // Act & Assert
        assertThatThrownBy(() -> eventService.getEventById(1L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Usuário autenticado não é organizador do Evento");

        verify(eventRepository).findById(1L);
        verify(authenticationService).getAuthenticatedUser();
        verify(eventMapper, never()).toDTO(any(Event.class));
    }

    @Test
    @DisplayName("Deve atualizar um evento com sucesso (client)")
    public void updateEvent_shouldUpdateEvent_whenUserIsClient() {
        // Arrange
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(authenticationService.getAuthenticatedUser()).thenReturn(clientUser);
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(eventMapper.toDTO(any(Event.class))).thenReturn(eventResponseDTO);

        // Act
        EventResponseDTO result = eventService.updateEvent(1L, eventUpdateDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getNome()).isEqualTo(eventResponseDTO.getNome());

        verify(eventRepository).findById(1L);
        verify(authenticationService).getAuthenticatedUser();
        verify(eventRepository).save(any(Event.class));
        verify(eventMapper).toDTO(event);
    }

    @Test
    @DisplayName("Deve deletar um evento com sucesso (client)")
    public void deleteEvent_shouldDeleteEvent_whenUserIsOwner() {
        // Arrange
        Event spyEvent = spy(event);
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(spyEvent));
        when(authenticationService.getAuthenticatedUser()).thenReturn(clientUser);

        // Act
        eventService.deleteEvent(1L);

        // Assert
        verify(eventRepository).findById(1L);
        verify(authenticationService).getAuthenticatedUser();
        verify(eventRepository).delete(spyEvent);
    }

    @Test
    @DisplayName("Deve lançar EventNotFoundException se o evento não for encontrado")
    public void findById_shouldThrowEventNotFoundException_whenEventDoesNotExist() {
        // Arrange
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> eventService.findById(999L))
                .isInstanceOf(EventNotFoundException.class)
                .hasMessageContaining("Evento não encontrado com o id: 999");

        verify(eventRepository).findById(999L);
    }

    @Test
    @DisplayName("Deve retornar uma lista de events para o CLIENT autenticado")
    public void getAllEvents_shouldReturnEventsForAuthenticatedClient() {
        // Arrange
        Pageable paginacao = PageRequest.of(0, 10);
        Specification<Event> spec = Specification.allOf();

        User testClientUser = new User();
        testClientUser.setRole(UserRole.CLIENT);
        testClientUser.setId(1L);

        Event event1 = new Event(2L, "Event do Client", TipoEvento.ANIVERSARIO, 150, LocalDate.of(2025, 8, 13), LocalDate.of(2025, 8, 13), LocalTime.of(12, 30), LocalTime.of(17, 30), testClientUser, new ArrayList<>());
        Event event2 = new Event(3L, "Outro Event do Client", TipoEvento.OUTROS, 50, LocalDate.of(2025, 8, 13), LocalDate.of(2025, 8, 13), LocalTime.of(12, 30), LocalTime.of(17, 30), testClientUser, new ArrayList<>());

        Page<Event> eventPage = new PageImpl<>(List.of(event1, event2), paginacao, 2);

        EventResponseDTO eventDTO1 = new EventResponseDTO(2L, "Event do Client", TipoEvento.ANIVERSARIO, 150, LocalDate.of(2025, 8, 13), LocalDate.of(2025, 8, 13), LocalTime.of(12, 30), LocalTime.of(17, 30), null, null);
        EventResponseDTO eventDTO2 = new EventResponseDTO(3L, "Outro Event do Client", TipoEvento.OUTROS, 50, LocalDate.of(2025, 8, 13), LocalDate.of(2025, 8, 13), LocalTime.of(12, 30), LocalTime.of(17, 30), null, null);

        when(authenticationService.getAuthenticatedUser()).thenReturn(testClientUser);
        when(eventRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(eventPage);
        when(eventMapper.toDTO(event1)).thenReturn(eventDTO1);
        when(eventMapper.toDTO(event2)).thenReturn(eventDTO2);

        // Act
        Page<EventResponseDTO> result = eventService.getAllEvents(spec, paginacao);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).containsExactly(eventDTO1, eventDTO2);

        verify(eventRepository).findAll(any(Specification.class), eq(paginacao));
    }

    @Test
    @DisplayName("Deve retornar uma lista de events para o ADMIN autenticado")
    public void getAllEvents_shouldReturnAllEventsForAdmin() {
        // Arrange
        Pageable paginacao = PageRequest.of(0, 10);
        Specification<Event> spec = Specification.allOf();

        Event event1 = new Event(2L, "Event do Client", TipoEvento.ANIVERSARIO, 150, LocalDate.of(2025, 8, 13), LocalDate.of(2025, 8, 13), LocalTime.of(12, 30), LocalTime.of(17, 30), clientUser, new ArrayList<>());
        Event event2 = new Event(3L, "Outro Event", TipoEvento.OUTROS, 200, LocalDate.of(2025, 8, 13), LocalDate.of(2025, 8, 13), LocalTime.of(12, 30), LocalTime.of(17, 30), adminUser, new ArrayList<>());

        Page<Event> eventPage = new PageImpl<>(List.of(event1, event2), paginacao, 2);

        EventResponseDTO eventDTO1 = new EventResponseDTO(2L, "Event do Client", TipoEvento.ANIVERSARIO, 150, LocalDate.of(2025, 8, 13), LocalDate.of(2025, 8, 13), LocalTime.of(12, 30), LocalTime.of(17, 30), null, null);
        EventResponseDTO eventDTO2 = new EventResponseDTO(3L, "Outro Event do Client", TipoEvento.OUTROS, 50, LocalDate.of(2025, 8, 13), LocalDate.of(2025, 8, 13), LocalTime.of(12, 30), LocalTime.of(17, 30), null, null);

        when(authenticationService.getAuthenticatedUser()).thenReturn(adminUser);
        when(eventRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(eventPage);
        when(eventMapper.toDTO(event1)).thenReturn(eventDTO1);
        when(eventMapper.toDTO(event2)).thenReturn(eventDTO2);

        // Act
        Page<EventResponseDTO> result = eventService.getAllEvents(spec, paginacao);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).containsExactly(eventDTO1, eventDTO2);

        verify(eventRepository).findAll(any(Specification.class), eq(paginacao));
    }

    @Test
    @DisplayName("Deve retornar null para um USER autenticado, pois ele não tem acesso")
    public void getAllEvents_shouldReturnNullForAuthenticatedUser() {
        // Arrange
        Pageable paginacao = PageRequest.of(0, 10);
        Specification<Event> spec = Specification.allOf();

        User regularUser = new User();
        regularUser.setRole(UserRole.OWNER);

        when(authenticationService.getAuthenticatedUser()).thenReturn(regularUser);

        // Act
        Page<EventResponseDTO> result = eventService.getAllEvents(spec, paginacao);

        // Assert
        assertThat(result).isNull();

        verify(eventRepository, never()).findAll(any(Specification.class), eq(paginacao));
    }
}