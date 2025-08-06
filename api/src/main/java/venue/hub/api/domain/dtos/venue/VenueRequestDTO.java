package venue.hub.api.domain.dtos.venue;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import venue.hub.api.domain.dtos.address.AddressRequestDTO;
import venue.hub.api.domain.dtos.user.UserRequestDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VenueRequestDTO {

    private String nome;
    private int capacidade;
    private String descricao;
    private String telefone;
    private double valor;
    private AddressRequestDTO address;
    private Long userId;

}
