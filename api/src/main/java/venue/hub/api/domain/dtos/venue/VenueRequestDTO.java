package venue.hub.api.domain.dtos.venue;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import venue.hub.api.domain.dtos.address.AddressRequestDTO;
import venue.hub.api.domain.dtos.venueadditional.VenueAdditionalRequestDTO;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VenueRequestDTO {

    @NotNull
    private String nome;

    @NotNull
    @Positive
    private Integer capacidade;
    private String descricao;

    @NotNull
    @Pattern(regexp = "^\\(?[1-9]{2}\\)? ?(?:[2-8]|9[1-9])[0-9]{3}-?[0-9]{4}$")
    private String telefone;

    @Positive
    private double valor;

    @NotNull
    private AddressRequestDTO address;

    @NotNull
    private Long userId;

    private List<VenueAdditionalRequestDTO> additionals;
}
