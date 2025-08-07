package venue.hub.api.domain.validators.proposal;

import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import venue.hub.api.domain.dtos.proposal.ProposalRequestDTO;
import venue.hub.api.domain.entities.Proposal;
import venue.hub.api.domain.repositories.ProposalRepository;

import java.util.Optional;

@Component
public class ValidatorVenueDisponivel implements ProposalValidator {
    @Autowired
    ProposalRepository proposalRepository;

    @Override
    public void validate(ProposalRequestDTO requestDTO) {
        var venue = requestDTO.getVenue();
        var event = requestDTO.getEvent();
        var dataInicio = event.getDataInicio();
        var dataFim = event.getDataFim();
        var horaInicio = event.getHoraInicio();
        var horaFim = event.getHoraFim();
        boolean ocupado = proposalRepository.findOcupado(venue, dataInicio, dataFim, horaInicio, horaFim);

        if (ocupado) {
            throw new ValidationException("Venue selecionada já tem outro evento confirmado nessa Data/Hora");
        }
    }
}
