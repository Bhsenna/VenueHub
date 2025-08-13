package venue.hub.api.domain.dtos.busca;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import venue.hub.api.domain.dtos.address.AddressResponseDTO;
import venue.hub.api.domain.dtos.user.UserResponseDTO;
import venue.hub.api.domain.dtos.venueadditional.VenueAdditionalResponseDTO;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuscaResponseDTO {

    private Long id;
    private String nome;
    private int capacidade;
    private String descricao;
    private String telefone;
    private double valor;
    private List<VenueAdditionalResponseDTO> additionals;
    private AddressResponseDTO address;
    private UserResponseDTO user;
    private boolean ativo;

}
