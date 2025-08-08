package venue.hub.api.domain.validators.proposal;

import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import venue.hub.api.domain.dtos.proposal.ProposalRequestDTO;
import venue.hub.api.domain.repositories.EventRepository;
import venue.hub.api.domain.repositories.ProposalRepository;
import venue.hub.api.domain.repositories.VenueRepository;

@Component
public class ValidatorVenueDisponivel implements ProposalValidator {
    @Autowired
    VenueRepository venueRepository;
    @Autowired
    EventRepository eventRepository;
    @Autowired
    ProposalRepository proposalRepository;

    @Override
    public void validate(ProposalRequestDTO requestDTO) {
        var venue = venueRepository.getReferenceById(requestDTO.getVenueId());
        var event = eventRepository.getReferenceById(requestDTO.getEventId());
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
