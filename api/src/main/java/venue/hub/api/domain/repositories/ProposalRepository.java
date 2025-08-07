package venue.hub.api.domain.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import venue.hub.api.domain.entities.Proposal;
import venue.hub.api.domain.enums.Status;

public interface ProposalRepository extends JpaRepository<Proposal, Long> {
    Page<Proposal> findAll(Pageable paginacao);

    Page<Proposal> findByVenueId(Long id, Pageable paginacao);

    Page<Proposal> findByVenueIdAndStatus(Long id, Status status, Pageable paginacao);
}
