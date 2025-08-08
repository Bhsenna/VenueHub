package venue.hub.api.domain.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import venue.hub.api.domain.dtos.user.TokenDTO;
import venue.hub.api.domain.dtos.user.UserLoginDTO;
import venue.hub.api.domain.dtos.user.UserRequestDTO;
import venue.hub.api.domain.dtos.user.UserResponseDTO;
import venue.hub.api.domain.services.LoginService;

@RestController
@RequestMapping("api/v1/auth")
public class AuthenticationController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> createUser(
            @RequestBody @Valid UserRequestDTO requestDTO,
            UriComponentsBuilder uriBuilder
    ) {
        var user = loginService.createUser(requestDTO);
        var uri = uriBuilder.path("/api/v1/users/{id}").buildAndExpand(user.getId()).toUri();

        return ResponseEntity.created(uri).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody UserLoginDTO dto) {
        TokenDTO token = loginService.login(dto);
        return ResponseEntity.ok(token);
    }

}
