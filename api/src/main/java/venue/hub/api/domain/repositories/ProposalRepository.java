package venue.hub.api.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import venue.hub.api.domain.entities.Proposal;

public interface ProposalRepository extends JpaRepository<Proposal, Long> {
}
