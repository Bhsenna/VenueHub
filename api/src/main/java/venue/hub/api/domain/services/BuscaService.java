package venue.hub.api.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import venue.hub.api.domain.dtos.busca.BuscaResponseDTO;
import venue.hub.api.domain.dtos.mapper.BuscaMapper;
import venue.hub.api.domain.entities.Venue;
import venue.hub.api.domain.repositories.VenueRepository;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BuscaService {
    @Autowired
    VenueRepository venueRepository;

    @Autowired
    BuscaMapper buscaMapper;

    public Page<BuscaResponseDTO> getResults(Specification<Venue> spec, Pageable paginacao) {
        List<Venue> venues = venueRepository.findAll(spec);
        List<BuscaResponseDTO> venueDTOs = venues.stream().map(buscaMapper::toDTO).collect(Collectors.toList());

        Sort sort = paginacao.getSort();
        if (sort.isSorted()) {
            Comparator<BuscaResponseDTO> comparator = getBuscaResponseDTOComparator(sort);

            if (comparator != null) {
                venueDTOs.sort(comparator);
            }
        }

        int start = (int) paginacao.getOffset();
        int end = Math.min(start + paginacao.getPageSize(), venueDTOs.size());
        List<BuscaResponseDTO> pagedList = venueDTOs.subList(start, end);

        return new PageImpl<>(pagedList, paginacao, venueDTOs.size());

    }

    private static Comparator<BuscaResponseDTO> getBuscaResponseDTOComparator(Sort sort) {
        Comparator<BuscaResponseDTO> comparator = null;

        for (Sort.Order order : sort) {
            Comparator<BuscaResponseDTO> currentComparator = Comparator.comparing(dto -> {
                try {
                    Field field = BuscaResponseDTO.class.getDeclaredField(order.getProperty());
                    field.setAccessible(true);
                    return (Comparable) field.get(dto);
                } catch (Exception e) {
                    return null;
                }
            }, Comparator.nullsLast(Comparator.naturalOrder()));

            if (order.isDescending()) {
                currentComparator = currentComparator.reversed();
            }

            comparator = comparator == null ? currentComparator : comparator.thenComparing(currentComparator);
        }
        return comparator;
    }
}
