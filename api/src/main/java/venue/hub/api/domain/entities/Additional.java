package venue.hub.api.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import venue.hub.api.domain.dtos.additional.AdditionalRequestDTO;

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

    public void update(AdditionalRequestDTO dto) {
        if (dto.getNome() != null && !dto.getNome().isEmpty()) {
            this.nome = dto.getNome();
        }
    }
}
