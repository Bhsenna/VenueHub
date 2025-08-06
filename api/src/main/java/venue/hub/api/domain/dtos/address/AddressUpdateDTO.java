package venue.hub.api.domain.dtos.address;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import venue.hub.api.domain.enums.Estado;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressUpdateDTO {
    private String cep;
    private String logradouro;
    private int numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private Estado estado;
}
