package venue.hub.api.domain.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import venue.hub.api.domain.dtos.address.AddressUpdateDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {

    private String nome;
    private String sobrenome;
    private AddressUpdateDTO address;

}
