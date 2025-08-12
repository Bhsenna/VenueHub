package venue.hub.api.domain.dtos.mapper;

import org.mapstruct.Mapper;
import venue.hub.api.domain.dtos.user.UserRequestDTO;
import venue.hub.api.domain.dtos.user.UserResponseDTO;
import venue.hub.api.domain.entities.User;

@Mapper(componentModel = "spring", uses = {AddressMapper.class})
public interface UserMapper {

    UserResponseDTO toDTO(User user);

    User toEntity(UserRequestDTO userRequestDTO);

}