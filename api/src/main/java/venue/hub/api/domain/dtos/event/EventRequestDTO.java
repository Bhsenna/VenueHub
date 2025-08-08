package venue.hub.api.domain.dtos.event;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import venue.hub.api.domain.dtos.additional.AdditionalRequestDTO;
import venue.hub.api.domain.enums.TipoEvento;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestDTO {
    @NotNull
    private TipoEvento tipoEvento;
    @NotNull
    @Positive
    private Integer qtPessoas;
    @NotNull
    private LocalDate dataInicio;
    @NotNull
    private LocalDate dataFim;
    @NotNull
    private LocalTime horaInicio;
    @NotNull
    private LocalTime horaFim;

    @NotNull
    @Positive
    private Long userId;

    private List<AdditionalRequestDTO> additionals;
}
