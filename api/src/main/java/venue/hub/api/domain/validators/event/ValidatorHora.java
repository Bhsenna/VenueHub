package venue.hub.api.domain.validators.event;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;
import venue.hub.api.domain.dtos.event.EventRequestDTO;

@Component
public class ValidatorHora implements EventValidator {
    @Override
    public void validate(EventRequestDTO requestDTO) {
        var dataInicio = requestDTO.getDataInicio();
        var dataFim = requestDTO.getDataFim();

        var horaInicio = requestDTO.getHoraInicio();
        var horaFim = requestDTO.getHoraFim();

        if (horaInicio.isAfter(horaFim) && dataInicio.isEqual(dataFim)) {
            throw new ValidationException("Hora Início vem depois de Hora Fim");
        }
    }
}
