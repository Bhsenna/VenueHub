package venue.hub.api.domain.dtos.venueadditional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatchVenueAdditionalsRequestDTO {
    private List<VenueAdditionalRequestDTO> additionals;
}

