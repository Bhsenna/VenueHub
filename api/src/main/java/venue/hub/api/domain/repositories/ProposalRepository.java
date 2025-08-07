package venue.hub.api.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import venue.hub.api.domain.entities.Proposal;
import venue.hub.api.domain.entities.Venue;

import java.time.LocalDate;
import java.time.LocalTime;

public interface ProposalRepository extends JpaRepository<Proposal, Long> {

    @Query(value = """
            SELECT count(p.id) > 0
            FROM Proposal p
            JOIN p.event e
            WHERE
            p.venue = :venue
            AND
            p.status IN (ACEITO, CONCLUIDO)
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
}
