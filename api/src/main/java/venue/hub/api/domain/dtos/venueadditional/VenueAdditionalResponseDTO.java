package venue.hub.api.domain.dtos.venueadditional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import venue.hub.api.domain.entities.VenueAdditionalId;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VenueAdditionalResponseDTO {

    private VenueAdditionalId id;
    private String nome;
    private Long valor;

}