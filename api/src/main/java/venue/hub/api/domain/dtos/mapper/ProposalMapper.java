package venue.hub.api.domain.dtos.mapper;

import org.mapstruct.Mapper;
import venue.hub.api.domain.dtos.proposal.ProposalRequestDTO;
import venue.hub.api.domain.dtos.proposal.ProposalResponseDTO;
import venue.hub.api.domain.entities.Proposal;

@Mapper(componentModel = "spring")
public interface ProposalMapper {

    ProposalResponseDTO toDTO(Proposal proposal);
    Proposal toEntity(ProposalRequestDTO proposalRequestDTO);

}
