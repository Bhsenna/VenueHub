package venue.hub.api.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import venue.hub.api.domain.enums.Status;

import java.time.LocalDateTime;

@Table(name = "proposals")
@Entity(name = "Proposal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Proposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    private Double valor;

    private Status status = Status.PENDENTE;

    private LocalDateTime dataCriacao = LocalDateTime.now();
}
