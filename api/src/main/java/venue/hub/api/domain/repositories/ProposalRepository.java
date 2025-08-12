package venue.hub.api.domain.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import venue.hub.api.domain.entities.Event;
import venue.hub.api.domain.entities.Proposal;
import venue.hub.api.domain.entities.Venue;
import venue.hub.api.domain.enums.Status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ProposalRepository extends JpaRepository<Proposal, Long> {

    Page<Proposal> findAll(Specification<Proposal> spec, Pageable paginacao);

    @Query(value = """
            SELECT count(p.id) > 0
            FROM Proposal p
            JOIN p.event e
            WHERE
            p.venue = :venue
            AND
            p.status IN (ACEITO, CONFIRMADO)
            AND
            e.dataInicio <= :dataFim
            AND
            e.dataFim >= :dataInicio
            AND
            e.horaInicio < :horaFim
            AND
            e.horaFim > :horaInicio
            """)
    boolean findOcupado(Venue venue, LocalDate dataInicio, LocalDate dataFim, LocalTime horaInicio, LocalTime horaFim);

    List<Proposal> findAllByStatusNotIn(List<Status> notStatus);

    List<Proposal> findAllByEventAndStatusNotIn(Event event, List<Status> endStatus);

    Page<Proposal> findByVenueId(Long id, Pageable paginacao);

    Page<Proposal> findByVenueIdAndStatus(Long id, Status status, Pageable paginacao);

}
