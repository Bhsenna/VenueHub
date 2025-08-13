package venue.hub.api.domain.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import venue.hub.api.domain.dtos.busca.BuscaRequestDTO;
import venue.hub.api.domain.dtos.busca.BuscaResponseDTO;
import venue.hub.api.domain.dtos.page.PageResponse;
import venue.hub.api.domain.services.BuscaService;

import java.util.List;

@RestController
@RequestMapping("api/v1/busca")
@EnableWebSecurity
public class BuscaController {

    @Autowired
    BuscaService buscaService;

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping
    public ResponseEntity<PageResponse<BuscaResponseDTO>> busca(
            @RequestBody @Valid BuscaRequestDTO requestDTO,
            @PageableDefault(size = 10, sort = {"id"}) Pageable paginacao
    ) {
        var buscaPage = buscaService.getResults(requestDTO, paginacao);
        List<BuscaResponseDTO> resultados = buscaPage.getContent();

        return ResponseEntity.ok(
                PageResponse.<BuscaResponseDTO>builder()
                        .totalPages(buscaPage.getTotalPages())
                        .totalElements(buscaPage.getTotalElements())
                        .currentPageData(resultados)
                        .build()
        );
    }

}
