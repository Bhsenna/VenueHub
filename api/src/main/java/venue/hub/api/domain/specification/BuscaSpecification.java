package venue.hub.api.domain.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import venue.hub.api.domain.entities.Venue;
import venue.hub.api.domain.enums.Estado;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class BuscaSpecification {

    public static Specification<Venue> comAtivo(Boolean ativo) {
        return ((root, query, builder) -> {
            if (ativo == null) return null;
            return builder.equal(root.get("ativo"), ativo);
        });
    }
    
    public static Specification<Venue> comNome(String nome) {
        return ((root, query, builder) -> {
            if (nome == null) return null;
            return builder.like(
                    builder.upper(root.get("nome")),
                    "%"+nome.toUpperCase()+"%"
            );
        });
    }

    public static Specification<Venue> comCapacidade(Integer capacidade, Integer tolerancia) {
        return ((root, query, builder) -> {
            if (capacidade == null || tolerancia == null) return null;
            Integer menor = capacidade - tolerancia;
            Integer maior = capacidade + tolerancia;
            return builder.between(root.get("capacidade"), menor, maior);
        });
    }

    public static Specification<Venue> comValorMin(Double valorMin) {
        return ((root, query, builder) -> {
            if (valorMin == null) return null;
            return builder.greaterThanOrEqualTo(root.get("valor"), valorMin);
        });
    }

    public static Specification<Venue> comValorMax(Double valorMax) {
        return ((root, query, builder) -> {
            if (valorMax == null) return null;
            return builder.lessThanOrEqualTo(root.get("valor"), valorMax);
        });
    }

    public static Specification<Venue> comBairro(String bairro) {
        return ((root, query, builder) -> {
            if (bairro == null) return null;
            return builder.like(
                    builder.upper(root.get("address").get("bairro")),
                    "%"+bairro.toUpperCase()+"%"
            );
        });
    }

    public static Specification<Venue> comCidade(String cidade) {
        return ((root, query, builder) -> {
            if (cidade == null) return null;
            return builder.like(
                    builder.upper(root.get("address").get("cidade")),
                    "%"+cidade.toUpperCase()+"%"
            );
        });
    }

    public static Specification<Venue> comEstado(Estado estado) {
        return ((root, query, builder) -> {
            if (estado == null) return null;
            return builder.equal(root.get("address").get("estado"), estado);
        });
    }

    public static Specification<Venue> comAdditional(List<Long> idAdditionals) {
        return ((root, query, builder) -> {
            if (idAdditionals == null || idAdditionals.isEmpty()) return null;

            Join<Object, Object> join = root.join("additionals");

            Predicate predicate = join.get("id").get("additionalId").in(idAdditionals);

            query.groupBy(root.get("id"));
            query.having(builder.equal(builder.countDistinct(join.get("id").get("additionalId")), idAdditionals.size()));

            return predicate;
        });
    }

}
