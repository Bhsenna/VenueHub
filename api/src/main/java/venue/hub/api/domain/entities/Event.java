package venue.hub.api.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import venue.hub.api.domain.dtos.event.EventUpdateDTO;
import venue.hub.api.domain.enums.TipoEvento;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Table(name = "events")
@Entity(name = "Event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoEvento tipoEvento;

    private int qtPessoas;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private LocalTime horaInicio;
    private LocalTime horaFim;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "event_additionals",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "additional_id")
    )
    private List<Additional> additionals;

    public void update(EventUpdateDTO updateDTO) {
        if (updateDTO.getTipoEvento() != null) {
            this.tipoEvento = updateDTO.getTipoEvento();
        }
        if (updateDTO.getQtPessoas() != null && updateDTO.getQtPessoas() > 0) {
            this.qtPessoas = updateDTO.getQtPessoas();
        }
        if (updateDTO.getDataInicio() != null) {
            this.dataInicio = updateDTO.getDataInicio();
        }
        if (updateDTO.getDataFim() != null) {
            this.dataFim = updateDTO.getDataFim();
        }
        if (updateDTO.getHoraInicio() != null) {
            this.horaInicio = updateDTO.getHoraInicio();
        }
        if (updateDTO.getHoraFim() != null) {
            this.horaFim = updateDTO.getHoraFim();
        }
    }
}
