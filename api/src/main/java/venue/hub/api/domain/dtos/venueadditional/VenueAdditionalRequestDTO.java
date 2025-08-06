package venue.hub.api.domain.dtos.venueadditional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VenueAdditionalRequestDTO {
    private Long additionalId;
    private Double valor;
}