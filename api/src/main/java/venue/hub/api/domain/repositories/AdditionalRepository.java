package venue.hub.api.domain.repositories;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import venue.hub.api.domain.entities.Additional;

public interface AdditionalRepository extends JpaRepository<Additional, Long> {
    Additional findByNome(String nome);
}
