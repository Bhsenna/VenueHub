package venue.hub.api.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import venue.hub.api.domain.dtos.address.AddressUpdateDTO;
import venue.hub.api.domain.enums.Estado;

@Table(name = "addresses")
@Entity(name = "Address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cep;
    private String logradouro;
    private int numero;
    private String complemento;
    private String bairro;
    private String cidade;

    @Enumerated(EnumType.STRING)
    private Estado estado;

    private Double latitude;
    private Double longitude;

    @Transient
    private Coordenada coordenada;

    public Coordenada getCoordenada() {
        if (this.coordenada == null) {
            this.coordenada = new Coordenada(this.latitude, this.longitude);
        }
        return this.coordenada;
    }

    public void setCoordenada(Coordenada coordenada) {
        this.coordenada = coordenada;
        this.latitude = coordenada.getLatitude();
        this.longitude = coordenada.getLongitude();
    }

    @Getter
    @AllArgsConstructor
    public static class Coordenada {
        private Double latitude;
        private Double longitude;
    }

    public void update(AddressUpdateDTO address) {
        if (address.getCep() != null && !address.getCep().isBlank()) {
            this.cep = address.getCep();
        }
        if (address.getLogradouro() != null && !address.getLogradouro().isBlank()) {
            this.logradouro = address.getLogradouro();
        }
        if (address.getNumero() > 0) {
            this.numero = address.getNumero();
        }
        if (address.getBairro() != null && !address.getBairro().isBlank()) {
            this.bairro = address.getBairro();
        }
        if (address.getCidade() != null && !address.getCidade().isBlank()) {
            this.cidade = address.getCidade();
        }
        if (address.getEstado() != null) {
            this.estado = address.getEstado();
        }
    }
}
