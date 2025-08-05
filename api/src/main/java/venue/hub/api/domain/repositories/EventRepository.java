package venue.hub.api.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import venue.hub.api.domain.entities.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
}
