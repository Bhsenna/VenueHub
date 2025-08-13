package venue.hub.api.domain.services;

import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import venue.hub.api.domain.dtos.address.AddressRequestDTO;
import venue.hub.api.domain.dtos.address.AddressResponseDTO;
import venue.hub.api.domain.dtos.mapper.UserMapper;
import venue.hub.api.domain.dtos.user.TokenDTO;
import venue.hub.api.domain.dtos.user.UserLoginDTO;
import venue.hub.api.domain.dtos.user.UserRequestDTO;
import venue.hub.api.domain.dtos.user.UserResponseDTO;
import venue.hub.api.domain.entities.Address;
import venue.hub.api.domain.entities.User;
import venue.hub.api.domain.enums.Estado;
import venue.hub.api.domain.enums.UserRole;
import venue.hub.api.domain.repositories.AddressRepository;
import venue.hub.api.domain.repositories.UserRepository;
import venue.hub.api.domain.validators.address.AddressValidator;
import venue.hub.api.domain.validators.user.UserValidator;
import venue.hub.api.infra.exceptions.UserNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LoginServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @Mock
    private List<AddressValidator> addressValidators;

    @Mock
    private List<UserValidator> userValidators;

    @InjectMocks
    private LoginService loginService;


    private User user;
    private Address address;
    private UserResponseDTO userResponseDTO;
    private AddressResponseDTO addressResponseDTO;

    @Before
    public void setUp() {
        address = new Address(1L, "88760430", "Rua de Cima", 123, null, "Centro", "Florianópolis", Estado.SC, -1.0, -1.0);
        user = new User(1L, "John", "Doe", "johndoe@email.com", "12345", UserRole.ADMIN, address, true);
        addressResponseDTO = new AddressResponseDTO(address.getId(), address.getCep(), address.getLogradouro(), address.getNumero(), address.getComplemento(), address.getBairro(), address.getCidade(), address.getEstado(), address.getLatitude(), address.getLongitude());
        userResponseDTO = new UserResponseDTO(user.getId(), user.getNome(), user.getSobrenome(), user.getLogin(), addressResponseDTO);
    }

    @Test
    public void createUser_shouldCreateUserSuccessfully() {
        // Arrange
        AddressRequestDTO addressRequestDTO = new AddressRequestDTO("88760430", "Rua de Cima", 123, null, "Centro", "Florianópolis", Estado.SC, -1.0, -1.0);
        UserRequestDTO requestDTO = new UserRequestDTO("John", "Doe", "johndoe@email.com", "12345", UserRole.ADMIN, addressRequestDTO);

        UserValidator mockUserValidator1 = mock(UserValidator.class);

        AddressValidator mockAddressValidator = mock(AddressValidator.class);
        UserValidator mockUserValidator = mock(UserValidator.class);

        when(userMapper.toEntity(requestDTO)).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(userResponseDTO);
        when(passwordEncoder.encode(requestDTO.getSenha())).thenReturn("hashed_password");

        // Act
        UserResponseDTO result = loginService.register(requestDTO);

        // Assert
        assertNotNull(result);

        verify(addressRepository).save(user.getAddress());
        verify(userRepository).save(user);
        verify(userMapper).toEntity(requestDTO);
        verify(userMapper).toDTO(user);
        verify(passwordEncoder).encode(requestDTO.getSenha());
    }

    @Test
    public void login_shouldUserExistsAndLogin() {
        // Arrange
        UserLoginDTO userLoginDTO = new UserLoginDTO("johndoe@email.com", "12345");
        when(userRepository.findUserByLogin(userLoginDTO.login())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userLoginDTO.senha(), user.getPassword())).thenReturn(true);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userLoginDTO.login(), userLoginDTO.senha());
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);

        String expectedToken = "mocked-jwt-token";
        when(tokenService.generateToken(user)).thenReturn(expectedToken);

        // Act
        TokenDTO result = loginService.login(userLoginDTO);

        // Assert
        assertNotNull(result);
        assertEquals(expectedToken, result.token());

        verify(userRepository).findUserByLogin(userLoginDTO.login());
        verify(passwordEncoder).matches(userLoginDTO.senha(), user.getPassword());
        verify(authenticationManager).authenticate(authenticationToken);
        verify(tokenService).generateToken(user);
    }

    @Test
    public void login_shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        UserLoginDTO userLoginDTO = new UserLoginDTO("nonexistent@email.com", "password");
        when(userRepository.findUserByLogin(userLoginDTO.login())).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> loginService.login(userLoginDTO));
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        assertEquals("Usuário não encontrado com o login: " + userLoginDTO.login(), exception.getMessage());

        verify(userRepository).findUserByLogin(userLoginDTO.login());
        verifyNoMoreInteractions(passwordEncoder, authenticationManager, tokenService);
    }

    @Test
    public void login_shouldThrowExceptionWhenPasswordIsInvalid() {
        // Arrange
        UserLoginDTO userLoginDTO = new UserLoginDTO("johndoe@email.com", "wrongpassword");
        when(userRepository.findUserByLogin(userLoginDTO.login())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userLoginDTO.senha(), user.getPassword())).thenReturn(false);

        // Act
        Throwable ex = catchThrowable(() -> loginService.login(userLoginDTO));

        // Assert
        Assertions.assertThat(ex).isInstanceOf(UserNotFoundException.class)
                .hasMessage("Credenciais inválidas")
                .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.FORBIDDEN);

        verify(userRepository).findUserByLogin(userLoginDTO.login());
        verify(passwordEncoder).matches(userLoginDTO.senha(), user.getPassword());
        verifyNoMoreInteractions(authenticationManager, tokenService);
    }
}