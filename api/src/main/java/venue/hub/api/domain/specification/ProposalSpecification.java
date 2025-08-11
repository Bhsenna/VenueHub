package venue.hub.api.domain.specification;

import org.springframework.data.jpa.domain.Specification;
import venue.hub.api.domain.entities.Proposal;
import venue.hub.api.domain.entities.User;
import venue.hub.api.domain.enums.Status;

public class ProposalSpecification {

    public static Specification<Proposal> comStatus(Status status) {
        return ((root, query, builder) -> {
            var stat = status == null ? Status.PENDENTE : status;
            return builder.equal(root.get("status"), stat);
        });
    }

    public static Specification<Proposal> comOwner(User user) {
        return ((root, query, builder) -> {
            if (user == null) return null;
            return builder.equal(root.get("venue").get("user"), user);
        });
    }

    public static Specification<Proposal> comClient(User user) {
        return ((root, query, builder) -> {
            if (user == null) return null;
            return builder.equal(root.get("event").get("user"), user);
        });
    }

}
