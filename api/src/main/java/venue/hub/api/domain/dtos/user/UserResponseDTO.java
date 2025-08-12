package venue.hub.api.domain.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import venue.hub.api.domain.dtos.address.AddressResponseDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {

    private Long id;
    private String nome;
    private String sobrenome;
    private String login;
    private AddressResponseDTO address;

}
