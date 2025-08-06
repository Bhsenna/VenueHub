package venue.hub.api.domain.dtos.venue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import venue.hub.api.domain.entities.Address;


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
    private boolean ativo;
}
