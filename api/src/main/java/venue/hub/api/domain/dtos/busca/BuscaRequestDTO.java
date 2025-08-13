package venue.hub.api.domain.dtos.busca;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import venue.hub.api.domain.enums.Estado;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuscaRequestDTO {
    private String nome;
    private Integer capacidade;
    private Integer tolerancia = 0;

    private Double valorMin;
    private Double valorMax;

    private String bairro;
    private String cidade;
    private Estado estado;

    private List<Long> idAdditionals;
}
