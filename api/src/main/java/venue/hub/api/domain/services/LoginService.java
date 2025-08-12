package venue.hub.api.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import venue.hub.api.domain.dtos.mapper.UserMapper;
import venue.hub.api.domain.dtos.user.TokenDTO;
import venue.hub.api.domain.dtos.user.UserLoginDTO;
import venue.hub.api.domain.dtos.user.UserRequestDTO;
import venue.hub.api.domain.dtos.user.UserResponseDTO;
import venue.hub.api.domain.entities.User;
import venue.hub.api.domain.repositories.AddressRepository;
import venue.hub.api.domain.repositories.UserRepository;
import venue.hub.api.infra.exceptions.UserNotFoundException;

@Service
public class LoginService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    public UserResponseDTO createUser(UserRequestDTO requestDTO) {

        if (userRepository.findByLogin(requestDTO.getLogin()) != null) {
            throw new RuntimeException("Usuário já registrado");
        }

        User user = userMapper.toEntity(requestDTO);
        user.setSenha(passwordEncoder.encode(requestDTO.getSenha()));

        addressRepository.save(user.getAddress());
        userRepository.save(user);

        return userMapper.toDTO(user);
    }

    public TokenDTO login(UserLoginDTO dto) {

        User user = userRepository.findUserByLogin(dto.login())
                .orElseThrow(() -> new UserNotFoundException(HttpStatus.NOT_FOUND, "Usuário não encontrado com o login: " + dto.login()));

        if (user == null || !passwordEncoder.matches(dto.senha(), user.getPassword())) {
            throw new UserNotFoundException(HttpStatus.FORBIDDEN, "Credenciais inválidas");
        }

        var authenticationToken = new UsernamePasswordAuthenticationToken(dto.login(), dto.senha());
        var auth = authenticationManager.authenticate(authenticationToken);

        String jwtToken = tokenService.generateToken((User) auth.getPrincipal());

        return new TokenDTO(jwtToken);
    }
}
