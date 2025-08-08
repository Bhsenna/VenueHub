package venue.hub.api.domain.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import venue.hub.api.domain.entities.Event;
import venue.hub.api.domain.entities.User;
import venue.hub.api.domain.entities.Venue;

public interface VenueRepository extends JpaRepository<Venue, Long> {
    Page<Venue> findAllByAtivoTrue(Pageable paginacao);

    Page<Venue> findByUser(User user, Pageable paginacao);

    @Query("""
            SELECT e FROM Event e
            JOIN Proposal p ON p.event = e
            JOIN Venue v ON p.venue = v
            WHERE p.status IN (Status.CONFIRMADO, Status.ACEITO)
            AND v.id = :venueId
            AND v.user = :user
            AND MONTH(e.dataInicio) = :month
            AND YEAR(e.dataInicio) = :year
            """)
    Page<Event> findConfirmedEventsByVenueAndDateAndUser(
            @Param("venueId") Long venueId,
            @Param("month") int month,
            @Param("year") int year,
            @Param("user") User user,
            Pageable pageable
    );


    @Query(value = """
            SELECT e FROM Event e
            JOIN Proposal p ON p.event = e
            JOIN Venue v ON p.venue = v
            WHERE p.venue.id = :venueId
            AND v.user = :user
            """)
    Page<Event> findEvents(
            @Param("venueId") Long venueId,
            @Param("user") User user,
            Pageable paginacao);
}
