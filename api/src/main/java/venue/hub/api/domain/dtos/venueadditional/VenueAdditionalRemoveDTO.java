package venue.hub.api.domain.dtos.venueadditional;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VenueAdditionalRemoveDTO {

    @NotNull
    @Positive
    private Long additionalId;

}