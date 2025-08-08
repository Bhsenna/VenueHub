package venue.hub.api.domain.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import venue.hub.api.domain.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    UserDetails findByLogin(String login);
    Optional<User> findUserByLogin(String login);

    Page<User> findAllByAtivoTrue(Pageable paginacao);

    boolean existsByLogin(String login);
}
