package venue.hub.api.domain.dtos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import venue.hub.api.domain.dtos.busca.BuscaResponseDTO;
import venue.hub.api.domain.entities.Address;
import venue.hub.api.domain.entities.User;
import venue.hub.api.domain.entities.Venue;
import venue.hub.api.domain.services.AuthenticationService;

@Mapper(componentModel = "spring", uses = {VenueAdditionalMapper.class})
public abstract class BuscaMapper {
    @Autowired
    private AuthenticationService authenticationService;

    @Mapping(target = "distancia", expression = "java(calculaDistancia(venue.getAddress()))")
    public abstract BuscaResponseDTO toDTO(Venue venue);

    double calculaDistancia(Address address) {
        User user = authenticationService.getAuthenticatedUser();
        var venueLatitude = address.getLatitude();
        var venueLongitude = address.getLongitude();

        var userLatitude = user.getAddress().getLatitude();
        var userLongitude = user.getAddress().getLongitude();

        return distanciaHaversine(venueLatitude, venueLongitude, userLatitude, userLongitude);
    }

    double distanciaHaversine(double lat1, double lon1, double lat2, double lon2) {
        int R = 6371; // Raio da Terra em km
        double lat1Rad = deg2rad(lat1);
        double lat2Rad = deg2rad(lat2);
        double deltaLat = deg2rad(lat2 - lat1);
        double deltaLon = deg2rad(lon2 - lon1);

        double a = Math.pow(Math.sin(deltaLat / 2), 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.pow(Math.sin(deltaLon / 2), 2);
        double c = Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (2 * R) * c;
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
}
