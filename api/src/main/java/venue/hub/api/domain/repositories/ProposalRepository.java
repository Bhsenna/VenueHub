package venue.hub.api.domain.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import venue.hub.api.domain.entities.Proposal;
import venue.hub.api.domain.entities.User;
import venue.hub.api.domain.enums.Status;

public interface ProposalRepository extends JpaRepository<Proposal, Long> {
    Page<Proposal> findAll(Pageable paginacao);

    @Query(value = """
            SELECT p FROM Proposal p
            JOIN Event e ON p.event = e
            WHERE e.user = :user\s
           \s""")
    Page<Proposal> findAllByClient(User user, Pageable paginacao);

    @Query(value = """
            SELECT p FROM Proposal p
            JOIN Venue v ON p.venue = v
            WHERE v.user = :user\s
           \s""")
    Page<Proposal> findAllByOwner(User user, Pageable paginacao);

    Page<Proposal> findByVenueId(Long id, Pageable paginacao);

    Page<Proposal> findByVenueIdAndStatus(Long id, Status status, Pageable paginacao);
}
