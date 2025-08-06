package venue.hub.api.domain.dtos.venue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import venue.hub.api.domain.entities.Address;
import venue.hub.api.domain.entities.User;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class VenueResponseDTO {

    private Long id;
    private String nome;
    private int capacidade;
    private String descricao;
    private String telefone;
    private double valor;
    private Address address;
    private Long userId;
    private boolean ativo;
}
