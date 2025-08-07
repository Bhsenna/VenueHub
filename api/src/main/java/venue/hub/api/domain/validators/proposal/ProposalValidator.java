package venue.hub.api.domain.validators.proposal;

import venue.hub.api.domain.dtos.proposal.ProposalRequestDTO;

public interface ProposalValidator {
    void validate(ProposalRequestDTO requestDTO);
}
