package venue.hub.api.domain.dtos.additional;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdditionalRequestDTO {

    @NotBlank
    private String nome;

}