package venue.hub.api.domain.entities;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "venue_additionals")
@Entity(name = "VenueAdditional")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class VenueAdditional {

    @EmbeddedId
    private VenueAdditionalId id;

    @ManyToOne
    @MapsId("venueId")
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @ManyToOne
    @MapsId("additionalId")
    @JoinColumn(name = "additional_id", nullable = false)
    private Additional additional;

    private Double valor;
}
