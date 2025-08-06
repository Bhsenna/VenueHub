package venue.hub.api.domain.dtos.page;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageResponse<T> {
    private int totalPages;
    private long totalElements;
    private List<T> currentPageData;
    int currentCount;
}
