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
import venue.hub.api.domain.dtos.address.AddressRequestDTO;
import venue.hub.api.domain.dtos.address.AddressResponseDTO;
import venue.hub.api.domain.dtos.address.AddressUpdateDTO;
import venue.hub.api.domain.dtos.mapper.UserMapper;
import venue.hub.api.domain.dtos.user.UserRequestDTO;
import venue.hub.api.domain.dtos.user.UserResponseDTO;
import venue.hub.api.domain.dtos.user.UserUpdateDTO;
import venue.hub.api.domain.entities.Address;
import venue.hub.api.domain.entities.User;
import venue.hub.api.domain.enums.Estado;
import venue.hub.api.domain.enums.UserRole;
import venue.hub.api.domain.repositories.AddressRepository;
import venue.hub.api.domain.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private Address address;
    private UserResponseDTO userResponseDTO;
    private AddressResponseDTO addressResponseDTO;

    @Before
    public void setUp() {
        address = new Address(1L, "88760430", "Rua de Cima", 123, null, "Centro", "Florianópolis", Estado.SC, -1.0, -1.0);
        user = new User(1L, "John", "Doe", "johndoe@email.com", "12345", UserRole.ADMIN, address, true);
        addressResponseDTO = new AddressResponseDTO(address.getId(), address.getCep(), address.getLogradouro(), address.getNumero(), address.getComplemento(), address.getBairro(), address.getCidade(), address.getEstado(), address.getLatitude(), address.getLongitude());
        userResponseDTO = new UserResponseDTO(user.getId(), user.getNome(), user.getSobrenome(), user.getLogin(), user.getSenha(), user.getRole(), addressResponseDTO, user.isAtivo());
    }

    @Test
    @DisplayName("Should find all active users and return them as DTOs")
    public void getAllUsers_shouldFindUsersReturnActiveUsers() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(user), pageable, 1);
        Page<UserResponseDTO> userDTOPage = new PageImpl<>(List.of(userResponseDTO), pageable, 1);

        when(userRepository.findAllByAtivoTrue(pageable)).thenReturn(userPage);
        when(userMapper.toDTO(any(User.class))).thenReturn(userResponseDTO);

        // Act
        Page<UserResponseDTO> result = userService.getAllUsers(pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(userResponseDTO);

        verify(userRepository).findAllByAtivoTrue(pageable);
        verify(userMapper).toDTO(user);
    }

    @Test
    @DisplayName("Should find user by ID and return as DTO")
    public void getUserById_shouldFindUserByIdAndReturn() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userMapper.toDTO(any(User.class))).thenReturn(userResponseDTO);

        // Act
        UserResponseDTO result = userService.getUserById(user.getId());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userResponseDTO.getId());

        verify(userRepository).findById(user.getId());
        verify(userMapper).toDTO(user);
    }

    @Test
    @DisplayName("Should find user by ID, call 'update', and return as DTO")
    public void updateUser_shouldFindUserAndUpdate() {
        // Arrange
        AddressUpdateDTO addressUpdateDTO = new AddressUpdateDTO();
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO("Johnny", "Doe", addressUpdateDTO);
        User mockUser = spy(user);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(userMapper.toDTO(any(User.class))).thenReturn(userResponseDTO);

        // Act
        UserResponseDTO result = userService.updateUser(user.getId(), userUpdateDTO);

        // Assert
        assertThat(result).isNotNull();

        verify(userRepository).findById(user.getId());
        verify(userRepository).save(mockUser);
        verify(userMapper).toDTO(mockUser);
        verify(addressRepository).save(user.getAddress());
        verify(mockUser).update(userUpdateDTO);
    }

    @Test
    @DisplayName("Should find user by ID, call 'delete', and save the user")
    public void deleteUser_shouldFindUserAndDelete() {
        // Arrange
        User mockUser = spy(user);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        doNothing().when(mockUser).delete();
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Act
        userService.deleteUser(user.getId());

        // Assert
        verify(userRepository).findById(user.getId());
        verify(mockUser).delete();
        verify(userRepository).save(mockUser);
    }
}