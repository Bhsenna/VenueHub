package venue.hub.api.domain.dtos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import venue.hub.api.domain.dtos.proposal.ProposalRequestDTO;
import venue.hub.api.domain.dtos.proposal.ProposalResponseDTO;
import venue.hub.api.domain.entities.Proposal;
import venue.hub.api.domain.services.EventService;
import venue.hub.api.domain.services.VenueService;

import java.util.List;

@Mapper(componentModel = "spring", uses = {EventMapper.class, EventService.class, VenueMapper.class, VenueService.class})
public interface ProposalMapper {

    ProposalResponseDTO toDTO(Proposal proposal);
    List<ProposalResponseDTO> toDTO(List<Proposal> proposal);

    @Mapping(source = "eventId", target = "event")
    @Mapping(source = "venueId", target = "venue")
    Proposal toEntity(ProposalRequestDTO proposalRequestDTO);
    List<Proposal> toEntity(List<ProposalRequestDTO> proposalRequestDTO);

}
