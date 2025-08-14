package venue.hub.api.domain.dtos.mapper;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import jakarta.validation.ValidationException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import venue.hub.api.domain.dtos.address.AddressRequestDTO;
import venue.hub.api.domain.dtos.address.AddressResponseDTO;
import venue.hub.api.domain.entities.Address;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressResponseDTO toDTO(Address address);

    @Mapping(target = "coordenada", expression = "java(pegaCoordenada(addressRequestDTO))")
    Address toEntity(AddressRequestDTO addressRequestDTO);

    @Named("pegaCoordenada")
    default Address.Coordenada pegaCoordenada(AddressRequestDTO addressRequestDTO) {
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyAn4UMErlo_KqrAwXnlbPpfvVh679y2Exk")
                .build();

        String address = addressRequestDTO.getLogradouro() + ", " + addressRequestDTO.getNumero() +
                " - " + addressRequestDTO.getBairro() + ", " + addressRequestDTO.getCidade() +
                " - " + addressRequestDTO.getEstado() + ", " + addressRequestDTO.getCep();

        try {
            GeocodingResult[] results = GeocodingApi.geocode(context, address).await();
            if (results.length > 0) {
                LatLng location = results[0].geometry.location;

                return new Address.Coordenada(location.lat, location.lng);
            }
        } catch (Exception e) {
            throw new ValidationException(e.getMessage());
        }
        return new Address.Coordenada(0., 0.);
    }
}
