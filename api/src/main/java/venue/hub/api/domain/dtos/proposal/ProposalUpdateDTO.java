package venue.hub.api.domain.dtos.proposal;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProposalUpdateDTO {

    @Positive
    Double valor;

}
