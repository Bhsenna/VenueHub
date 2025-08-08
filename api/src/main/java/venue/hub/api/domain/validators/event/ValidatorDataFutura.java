package venue.hub.api.domain.validators.event;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;
import venue.hub.api.domain.dtos.event.EventRequestDTO;

import java.time.LocalDate;

@Component
public class ValidatorDataFutura implements EventValidator {
    @Override
    public void validate(EventRequestDTO requestDTO) {
        var dataInicio = requestDTO.getDataInicio();
        var dataLimite = LocalDate.now();

        if (dataInicio.isBefore(dataLimite)) {
            throw new ValidationException("Evento agendado no passado");
        }
    }
}
