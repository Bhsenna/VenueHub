package venue.hub.api.domain.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import venue.hub.api.domain.entities.Event;
import venue.hub.api.domain.entities.User;
import venue.hub.api.domain.entities.Venue;

public interface VenueRepository extends JpaRepository<Venue, Long> {

    Page<Venue> findAll(Specification<Venue> spec, Pageable paginacao);

    Page<Venue> findByUser(User user, Pageable paginacao);

    @Query("""
            SELECT e FROM Event e
            JOIN Proposal p ON p.event = e
            JOIN Venue v ON p.venue = v
            WHERE p.status IN (ACEITO, CONFIRMADO)
            AND v.id = :venueId
            AND :month between MONTH(e.dataInicio) AND MONTH(e.dataFim)
            AND :year between YEAR(e.dataInicio) AND YEAR(e.dataFim)
            """)
    Page<Event> findConfirmedEventsByVenueAndDate(
            @Param("venueId") Long venueId,
            @Param("month") int month,
            @Param("year") int year,
            Pageable pageable
    );


    @Query(value = """
            SELECT e FROM Event e
            JOIN Proposal p ON p.event = e
            JOIN Venue v ON p.venue = v
            WHERE p.venue.id = :venueId
            """)
    Page<Event> findEvents(
            @Param("venueId") Long venueId,
            Pageable paginacao);

}
