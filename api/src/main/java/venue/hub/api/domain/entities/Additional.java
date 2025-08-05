package venue.hub.api.domain.entities;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "additionals")
@Entity(name = "Additional")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Additional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

}
