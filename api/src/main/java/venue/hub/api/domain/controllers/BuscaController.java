package venue.hub.api.domain.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import venue.hub.api.domain.dtos.busca.BuscaResponseDTO;
import venue.hub.api.domain.dtos.page.PageResponse;
import venue.hub.api.domain.entities.Venue;
import venue.hub.api.domain.enums.Estado;
import venue.hub.api.domain.services.BuscaService;
import venue.hub.api.domain.specification.BuscaSpecification;

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
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "capacidade", required = false) Integer capacidade,
            @RequestParam(value = "tolerancia", required = false) Integer tolerancia,
            @RequestParam(value = "valorMin", required = false) Double valorMin,
            @RequestParam(value = "valorMax", required = false) Double valorMax,
            @RequestParam(value = "bairro", required = false) String bairro,
            @RequestParam(value = "cidade", required = false) String cidade,
            @RequestParam(value = "estado", required = false) Estado estado,
            @RequestParam(value = "idAdditionals", required = false) List<Long> idAdditionals,
            @PageableDefault(size = 10, sort = {"id"}) Pageable paginacao
    ) {
        Specification<Venue> spec = Specification.allOf(
                BuscaSpecification.comAtivo(true),
                BuscaSpecification.comNome(nome),
                BuscaSpecification.comCapacidade(capacidade, tolerancia == null ? 0 : tolerancia),
                BuscaSpecification.comValorMin(valorMin),
                BuscaSpecification.comValorMax(valorMax),
                BuscaSpecification.comBairro(bairro),
                BuscaSpecification.comCidade(cidade),
                BuscaSpecification.comEstado(estado),
                BuscaSpecification.comAdditional(idAdditionals)
        );

        var buscaPage = buscaService.getResults(spec, paginacao);
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
