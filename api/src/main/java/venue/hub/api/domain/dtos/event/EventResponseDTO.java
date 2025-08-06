package venue.hub.api.domain.dtos.event;

import venue.hub.api.domain.entities.Additional;
import venue.hub.api.domain.entities.User;
import venue.hub.api.domain.enums.TipoEvento;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class EventResponseDTO {
    private Long id;

    private TipoEvento tipoEvento;
    private int qtPessoas;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private LocalTime horaInicio;
    private LocalTime horaFim;

    private User user;
    private List<Additional> additionals;
}
