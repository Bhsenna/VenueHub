package venue.hub.api.domain.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.bind.annotation.*;
import venue.hub.api.domain.dtos.page.PageResponse;
import venue.hub.api.domain.dtos.user.UserResponseDTO;
import venue.hub.api.domain.dtos.user.UserUpdateDTO;
import venue.hub.api.domain.services.UserService;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@EnableWebSecurity
public class UserController {

    @Autowired
    UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<PageResponse<UserResponseDTO>> getAllUsers(
            @PageableDefault(size = 10, sort = {"id"}) Pageable paginacao
    ) {
        var userPage = userService.getAllUsers(paginacao);
        List<UserResponseDTO> users = userPage.getContent();

        return ResponseEntity.ok(
                PageResponse.<UserResponseDTO>builder()
                        .totalPages(userPage.getTotalPages())
                        .totalElements(userPage.getTotalElements())
                        .currentPageData(users)
                        .build()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        UserResponseDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateDTO updateDTO
    ) {
        UserResponseDTO updatedUser = userService.updateUser(id, updateDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}