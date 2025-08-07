package venue.hub.api.domain.dtos.venue;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import venue.hub.api.domain.dtos.address.AddressUpdateDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VenueUpdateDTO {

    private String nome;
    private Integer capacidade;
    private String descricao;
    private String telefone;
    private Double valor;
    private AddressUpdateDTO address;
    private Long userId;
}
