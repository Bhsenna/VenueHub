package venue.hub.api.domain.dtos.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import venue.hub.api.domain.enums.Estado;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressRequestDTO {

    @NotNull @Pattern(regexp = "\\d{8}")
    private String cep;
    @NotBlank
    private String logradouro;
    @NotNull @Positive
    private Integer numero;
    @NotBlank
    private String complemento;
    @NotBlank
    private String bairro;
    @NotBlank
    private String cidade;
    @NotNull
    private Estado estado;
    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;

}
