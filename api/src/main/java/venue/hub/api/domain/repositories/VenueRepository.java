package venue.hub.api.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import venue.hub.api.domain.entities.Venue;

public interface VenueRepository extends JpaRepository<Venue, Long> {
}
