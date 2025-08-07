package venue.hub.api.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import venue.hub.api.domain.entities.VenueAdditional;
import venue.hub.api.domain.entities.VenueAdditionalId;

public interface VenueAdditionalRepository extends JpaRepository<VenueAdditional, VenueAdditionalId> {
}
