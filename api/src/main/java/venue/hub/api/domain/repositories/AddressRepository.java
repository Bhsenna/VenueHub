package venue.hub.api.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import venue.hub.api.domain.entities.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
