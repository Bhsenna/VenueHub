package venue.hub.api.domain.dtos.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import venue.hub.api.domain.dtos.additional.AdditionalResponseDTO;
import venue.hub.api.domain.dtos.user.UserResponseDTO;
import venue.hub.api.domain.entities.Additional;
import venue.hub.api.domain.entities.User;
import venue.hub.api.domain.enums.TipoEvento;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventResponseDTO {
    private Long id;

    private TipoEvento tipoEvento;
    private int qtPessoas;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private LocalTime horaInicio;
    private LocalTime horaFim;

    private UserResponseDTO user;
    private List<AdditionalResponseDTO> additionals;
}
