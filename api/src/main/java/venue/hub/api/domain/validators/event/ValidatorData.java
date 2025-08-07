package venue.hub.api.domain.validators.event;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;
import venue.hub.api.domain.dtos.event.EventRequestDTO;

@Component
public class ValidatorData implements EventValidator {
    @Override
    public void validate(EventRequestDTO requestDTO) {
        var dataInicio = requestDTO.getDataInicio();
        var dataFim = requestDTO.getDataFim();

        if (dataInicio.isAfter(dataFim)) {
            throw new ValidationException("Data Início vem depois de Data Fim");
        }
    }
}
