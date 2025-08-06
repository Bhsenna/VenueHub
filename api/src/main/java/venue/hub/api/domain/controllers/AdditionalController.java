package venue.hub.api.domain.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import venue.hub.api.domain.dtos.additional.AdditionalRequestDTO;
import venue.hub.api.domain.dtos.additional.AdditionalResponseDTO;
import venue.hub.api.domain.services.AdditionalService;

@RestController
@RequestMapping("api/v1/additionals")
public class AdditionalController {

    @Autowired
    private AdditionalService additionalService;

    @PostMapping("/create")
    public ResponseEntity<AdditionalResponseDTO> create(@RequestBody @Valid AdditionalRequestDTO dto) {
        var created = additionalService.createAdditional(dto);

        return ResponseEntity.ok(created);
    }

}
