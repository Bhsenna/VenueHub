package venue.hub.api.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import venue.hub.api.domain.dtos.mapper.AddressMapper;
import venue.hub.api.domain.dtos.mapper.UserMapper;
import venue.hub.api.domain.dtos.user.UserRequestDTO;
import venue.hub.api.domain.dtos.user.UserResponseDTO;
import venue.hub.api.domain.entities.User;
import venue.hub.api.domain.repositories.AddressRepository;
import venue.hub.api.domain.repositories.UserRepository;

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
        //Address address = addressMapper.toEntity(requestDTO.getAddress());
        User user = userMapper.toEntity(requestDTO);

        addressRepository.save(user.getAddress());
        userRepository.save(user);

        return userMapper.toDTO(user);
    }

    public User findById(Long id) {
        return userRepository.getReferenceById(id);
    }
}
