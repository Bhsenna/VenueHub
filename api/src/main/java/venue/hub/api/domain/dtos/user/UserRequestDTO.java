package venue.hub.api.domain.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import venue.hub.api.domain.dtos.address.AddressRequestDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO {
    private String nome;
    private String sobrenome;
    private String login;
    private String senha;
    private AddressRequestDTO address;
}
