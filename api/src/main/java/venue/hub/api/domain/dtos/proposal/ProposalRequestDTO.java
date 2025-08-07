package venue.hub.api.domain.dtos.proposal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import venue.hub.api.domain.entities.Event;
import venue.hub.api.domain.entities.User;
import venue.hub.api.domain.entities.Venue;
import venue.hub.api.domain.enums.Status;
import venue.hub.api.domain.enums.TipoEvento;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProposalRequestDTO {
    private Long eventId;
    private Long venueId;

    @NotNull
    @Positive
    private Double valor;
}
