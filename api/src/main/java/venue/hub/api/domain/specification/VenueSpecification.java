package venue.hub.api.domain.specification;

import org.springframework.data.jpa.domain.Specification;
import venue.hub.api.domain.entities.User;
import venue.hub.api.domain.entities.Venue;

public class VenueSpecification {

    public static Specification<Venue> comOwner(User user) {
        return ((root, query, builder) -> {
            if (user == null) return null;
            return builder.equal(root.get("user"), user);
        });
    }

    public static Specification<Venue> comAtivo(Boolean ativo) {
        return ((root, query, builder) -> {
            if (ativo == null) return null;
            return builder.equal(root.get("ativo"), ativo);
        });
    }

}
