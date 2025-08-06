package venue.hub.api.domain.dtos.additional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdditionalResponseDTO {
    private Long id;
    private String nome;
}
