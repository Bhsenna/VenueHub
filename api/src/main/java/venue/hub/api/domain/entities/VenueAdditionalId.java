package venue.hub.api.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VenueAdditionalId implements Serializable {
    @Column(name = "venue_id")
    private Long venueId;
    
    @Column(name = "additional_id")
    private Long additionalId;
}
