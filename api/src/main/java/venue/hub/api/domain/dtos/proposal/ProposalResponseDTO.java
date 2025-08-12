package venue.hub.api.domain.dtos.proposal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import venue.hub.api.domain.dtos.event.EventResponseDTO;
import venue.hub.api.domain.dtos.venue.VenueResponseDTO;
import venue.hub.api.domain.enums.Status;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProposalResponseDTO {

    private Long id;

    private EventResponseDTO event;
    private VenueResponseDTO venue;
    private Double valor;

    private Status status;
    private LocalDateTime dataCriacao;

}
