package venue.hub.api.domain.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import venue.hub.api.domain.entities.Venue;

public interface VenueRepository extends JpaRepository<Venue, Long> {
    Page<Venue> findAllByAtivoTrue(Pageable paginacao);
}
