package venue.hub.api.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import venue.hub.api.domain.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
