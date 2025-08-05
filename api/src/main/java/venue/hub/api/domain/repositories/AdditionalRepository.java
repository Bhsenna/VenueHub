package venue.hub.api.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import venue.hub.api.domain.entities.Additional;

public interface AdditionalRepository extends JpaRepository<Additional, Long> {
}
