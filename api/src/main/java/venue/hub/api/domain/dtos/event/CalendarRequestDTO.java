package venue.hub.api.domain.dtos.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import venue.hub.api.domain.entities.Venue;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalendarRequestDTO {

    private Venue venue;

    private int month;

    private int year;

}
