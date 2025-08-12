package venue.hub.api.domain.specification;

import org.springframework.data.jpa.domain.Specification;
import venue.hub.api.domain.entities.Event;
import venue.hub.api.domain.entities.User;

public class EventSpecification {

    public static Specification<Event> comClient(User user) {
        return ((root, query, builder) -> {
            if (user == null) return null;
            return builder.equal(root.get("user"), user);
        });
    }

}
