package venue.hub.api.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import venue.hub.api.domain.dtos.mapper.AddressMapper;
import venue.hub.api.domain.dtos.mapper.UserMapper;
import venue.hub.api.domain.dtos.user.UserRequestDTO;
import venue.hub.api.domain.dtos.user.UserResponseDTO;
import venue.hub.api.domain.dtos.user.UserUpdateDTO;
import venue.hub.api.domain.entities.User;
import venue.hub.api.domain.repositories.AddressRepository;
import venue.hub.api.domain.repositories.UserRepository;
import venue.hub.api.infra.exceptions.UserNotFoundException;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    UserMapper userMapper;

    @Autowired
    AddressMapper addressMapper;

    public UserResponseDTO createUser(UserRequestDTO requestDTO) {
        User user = userMapper.toEntity(requestDTO);

        addressRepository.save(user.getAddress());
        userRepository.save(user);

        return userMapper.toDTO(user);
    }

    public Page<UserResponseDTO> getAllUsers(Pageable paginacao) {
        return userRepository.findAllByAtivoTrue(paginacao)
                .map(userMapper::toDTO);
    }

    public UserResponseDTO getUserById(Long id) {
        var user = this.findById(id);
        return userMapper.toDTO(user);
    }

    public UserResponseDTO updateUser(Long id, UserUpdateDTO updateDTO) {
        var user = this.findById(id);

        user.update(updateDTO);

        addressRepository.save(user.getAddress());
        userRepository.save(user);

        return userMapper.toDTO(user);
    }

    public void deleteUser(Long id) {
        var user = this.findById(id);
        user.delete();
        userRepository.save(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado com o id: " + id, HttpStatus.NOT_FOUND));
    }
}
