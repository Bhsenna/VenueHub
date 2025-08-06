package venue.hub.api.domain.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import venue.hub.api.domain.dtos.additional.AdditionalRequestDTO;
import venue.hub.api.domain.dtos.additional.AdditionalResponseDTO;
import venue.hub.api.domain.dtos.page.PageResponse;
import venue.hub.api.domain.services.AdditionalService;

import java.util.List;

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

    @GetMapping("/all")
    public ResponseEntity<PageResponse<AdditionalResponseDTO>> getAllAdditionals(
            @PageableDefault(size = 10, sort = {"id"}) Pageable paginacao
    ) {
        var additionalPage = additionalService.getAllAdditionals(paginacao);
        List<AdditionalResponseDTO> additionals = additionalPage.getContent();

        return ResponseEntity.ok(
                PageResponse.<AdditionalResponseDTO>builder()
                        .totalPages(additionalPage.getTotalPages())
                        .totalElements(additionalPage.getTotalElements())
                        .currentPageData(additionals)
                        .build()
        );
    }
}
