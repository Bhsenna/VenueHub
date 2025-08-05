package venue.hub.api.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class VenueAdditionalId implements Serializable {
    @Column(name = "venue_id")
    private Long venueId;
    
    @Column(name = "additional_id")
    private Long additionalId;
}
