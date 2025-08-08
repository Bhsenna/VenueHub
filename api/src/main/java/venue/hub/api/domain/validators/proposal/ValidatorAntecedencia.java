package venue.hub.api.domain.validators.proposal;

import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import venue.hub.api.domain.dtos.proposal.ProposalRequestDTO;
import venue.hub.api.domain.repositories.EventRepository;

import java.time.LocalDate;

@Component
public class ValidatorAntecedencia implements ProposalValidator {
    @Autowired
    EventRepository eventRepository;

    @Override
    public void validate(ProposalRequestDTO requestDTO) {
        var event = eventRepository.getReferenceById(requestDTO.getEventId());
        var dataInicio = event.getDataInicio();
        var dataLimite = LocalDate.now().plusMonths(1);

        if (dataInicio.isBefore(dataLimite)) {
            throw new ValidationException("Proposta feita com menos de 1 mês de antecedência");
        }
    }
}
