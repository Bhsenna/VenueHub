package venue.hub.api.domain.controllers;


import jakarta.validation.Valid;
import org.hibernate.query.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import venue.hub.api.domain.dtos.page.PageResponse;
import venue.hub.api.domain.dtos.proposal.ProposalRequestDTO;
import venue.hub.api.domain.dtos.proposal.ProposalResponseDTO;
import venue.hub.api.domain.dtos.proposal.ProposalUpdateDTO;
import venue.hub.api.domain.entities.Proposal;
import venue.hub.api.domain.enums.Status;
import venue.hub.api.domain.services.ProposalService;
import venue.hub.api.domain.specification.ProposalSpecification;

import java.util.List;

@RestController
@RequestMapping("api/v1/proposals")
public class ProposalController {

    @Autowired
    private ProposalService proposalService;

    @PostMapping("/create")
    public ResponseEntity<ProposalResponseDTO> createProposal(
            @RequestBody @Valid ProposalRequestDTO requestDTO,
            UriComponentsBuilder uriBuilder) {

        var response = proposalService.createProposal(requestDTO);
        var uri = uriBuilder.path("/api/v1/users/{id}").buildAndExpand(response.getId()).toUri();

        return ResponseEntity.created(uri).body(response);
    }


    @GetMapping("/all")
    public ResponseEntity<PageResponse<ProposalResponseDTO>> getAllProposals(
            @PageableDefault(size = 10, sort = {"id"}) Pageable paginacao,
            @RequestParam(value = "status", required = false) Status status
    ) {
        Specification<Proposal> spec = Specification.allOf(
                ProposalSpecification.comStatus(status)
        );

        var proposalPage = proposalService.getAllProposals(spec, paginacao);
        List<ProposalResponseDTO> proposals = proposalPage.getContent();

        return ResponseEntity.ok(
                PageResponse.<ProposalResponseDTO>builder()
                        .totalPages(proposalPage.getTotalPages())
                        .totalElements(proposalPage.getTotalElements())
                        .currentPageData(proposals)
                        .build()
        );
    }

    @PatchMapping("/aceita/{id}")
    public ResponseEntity<ProposalResponseDTO> aceitaProposal(@PathVariable Long id) {
        ProposalResponseDTO response = proposalService.aceitaProposal(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/recusa/{id}")
    public ResponseEntity<ProposalResponseDTO> recusaProposal(@PathVariable Long id) {
        ProposalResponseDTO response = proposalService.recusaProposal(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/confirma/{id}")
    public ResponseEntity<ProposalResponseDTO> confirmaProposal(@PathVariable Long id) {
        ProposalResponseDTO response = proposalService.confirmaProposal(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProposalResponseDTO> getProposalById(@PathVariable Long id) {
        ProposalResponseDTO response = proposalService.getProposalById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProposalResponseDTO> updateProposal(
            @PathVariable Long id,
            @RequestBody @Valid ProposalUpdateDTO updateDTO) {
        ProposalResponseDTO response = proposalService.updateProposal(id, updateDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProposalResponseDTO> deleteProposal(@PathVariable Long id) {
        ProposalResponseDTO response = proposalService.deleteProposal(id);
        return ResponseEntity.ok(response);
    }



}
