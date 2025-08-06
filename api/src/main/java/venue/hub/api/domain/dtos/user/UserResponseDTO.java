package venue.hub.api.domain.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import venue.hub.api.domain.entities.Address;
import venue.hub.api.domain.enums.UserRole;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String nome;
    private String sobrenome;
    private String login;
    private String senha;
    private UserRole role;
    private Address address;

    private boolean ativo;
}
