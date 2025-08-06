package venue.hub.api.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import venue.hub.api.domain.dtos.venue.VenueUpdateDTO;

import java.util.List;

@Table(name = "venues")
@Entity(name = "Venue")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private int capacidade;
    private String descricao;
    private String telefone;
    private double valor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "venue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VenueAdditional> additionals;

    private boolean ativo = true;

    public void update(VenueUpdateDTO updateDTO) {

        if (updateDTO.getNome() != null && !updateDTO.getNome().isBlank()) {
            this.nome = updateDTO.getNome();
        }

        if (updateDTO.getCapacidade() != null && updateDTO.getCapacidade() > 0) {
            this.capacidade = updateDTO.getCapacidade();
        }

        if (updateDTO.getDescricao() != null && !updateDTO.getDescricao().isBlank()) {
            this.descricao = updateDTO.getDescricao();
        }

        if (updateDTO.getTelefone() != null) {
            this.telefone = updateDTO.getTelefone();
        }

        if (updateDTO.getValor() != null) {
            this.valor = updateDTO.getValor();
        }

        if (updateDTO.getAddress() != null) {
            this.address.update(updateDTO.getAddress());
        }
    }
}