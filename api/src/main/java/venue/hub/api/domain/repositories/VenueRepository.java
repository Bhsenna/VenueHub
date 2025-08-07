package venue.hub.api.domain.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import venue.hub.api.domain.entities.Event;
import venue.hub.api.domain.entities.Venue;

public interface VenueRepository extends JpaRepository<Venue, Long> {
    Page<Venue> findAllByAtivoTrue(Pageable paginacao);

    @Query("""
            SELECT e FROM Event e
            JOIN Proposal p ON p.event = e
            WHERE p.status IN (Status.CONFIRMADO, Status.ACEITO)
            AND p.venue.id = :venueId
            AND MONTH(e.dataInicio) = :month
            AND YEAR(e.dataInicio) = :year
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
            WHERE p.venue.id = :venueId
            """)
    Page<Event> findEvents(@Param("venueId") Long venueId, Pageable paginacao);
}
