package venue.hub.api.domain.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import venue.hub.api.domain.entities.Event;
import venue.hub.api.domain.entities.User;

import java.time.LocalDate;

public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findAll(Specification<Event> spec, Pageable paginacao);

    Page<Event> findAllByDataFimAfter(LocalDate now, Pageable paginacao);

    Page<Event> findByUser(User user, Pageable paginacao);

}
