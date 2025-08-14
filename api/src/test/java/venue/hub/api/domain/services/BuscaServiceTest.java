package venue.hub.api.domain.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import venue.hub.api.domain.dtos.address.AddressResponseDTO;
import venue.hub.api.domain.dtos.busca.BuscaResponseDTO;
import venue.hub.api.domain.dtos.mapper.BuscaMapper;
import venue.hub.api.domain.entities.Address;
import venue.hub.api.domain.entities.User;
import venue.hub.api.domain.entities.Venue;
import venue.hub.api.domain.enums.Estado;
import venue.hub.api.domain.enums.UserRole;
import venue.hub.api.domain.repositories.VenueRepository;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BuscaServiceTest {

    @Mock
    VenueRepository venueRepository;

    @Mock
    BuscaMapper buscaMapper;

    @InjectMocks
    BuscaService buscaService;

    private User ownerUser;
    private User adminUser;
    private Venue venue1;
    private Venue venue2;
    private Address address;
    private BuscaResponseDTO buscaResponseDTO1;
    private BuscaResponseDTO buscaResponseDTO2;

    @Before
    public void setUp() {
        address = new Address(1L, "8889999", "Logradouro", 123, "complemento", "Bairro", "Cidade", Estado.SC, -1.0, -1.0, null);
        AddressResponseDTO addressResponseDTO = new AddressResponseDTO(1L, "8889999", "Logradouro", 123, "complemento", "Bairro", "Cidade", Estado.SC, -1.0, -1.0);

        ownerUser = new User(1L, "Nome", "Sobrenome", "login@test.com", "Senha@teste", UserRole.CLIENT, address, true);

        adminUser = new User(2L, "Admin", "Sobrenome", "admin@test.com", "Senha@admin", UserRole.OWNER, address, true);

        venue1 = new Venue(1L, "Nome 1", 50 , "Descrição", "47 99999-0000", 1000, address, ownerUser, new ArrayList<>(), true);
        venue2 = new Venue(2L, "Nome 2", 100, "Descrição", "47 90000-9999", 2000, address, ownerUser, new ArrayList<>(), true);

        buscaResponseDTO1 = new BuscaResponseDTO(1L, "Nome 1", 50 , "Descrição", "47 99999-0000", 1000, new ArrayList<>(), addressResponseDTO, 10.);
        buscaResponseDTO2 = new BuscaResponseDTO(2L, "Nome 2", 100, "Descrição", "47 90000-9999", 2000, new ArrayList<>(), addressResponseDTO, 50.);
    }

    @Test
    @DisplayName("Deve retornar a lista do DTO de busca de Venue1 e Venue2 por ID")
    public void getResults_shouldReturnByIdAsc() {
        // Arrange
        List<BuscaResponseDTO> venueDTOs = List.of(buscaResponseDTO1, buscaResponseDTO2);

        Specification<Venue> spec = Specification.allOf();

        Sort sort = Sort.by("id").ascending();
        Pageable paginacao = PageRequest.of(0, 10, sort);

        int start = (int) paginacao.getOffset();
        int end = Math.min(start + paginacao.getPageSize(), venueDTOs.size());
        List<BuscaResponseDTO> pagedList = venueDTOs.subList(start, end);
        Page<BuscaResponseDTO> expected = new PageImpl<>(pagedList, paginacao, venueDTOs.size());

        when(venueRepository.findAll(any(Specification.class))).thenReturn(List.of(venue1, venue2));
        when(buscaMapper.toDTO(venue1)).thenReturn(buscaResponseDTO1);
        when(buscaMapper.toDTO(venue2)).thenReturn(buscaResponseDTO2);

        // Act
        Page<BuscaResponseDTO> result = buscaService.getResults(spec, paginacao);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).containsExactly(buscaResponseDTO1, buscaResponseDTO2);
        assertThat(result).isEqualTo(expected);

        verify(venueRepository).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("Deve retornar a lista do DTO de busca de Venue2 e Venue1 por ID")
    public void getResults_shouldReturnByIdDesc() {
        // Arrange
        List<BuscaResponseDTO> venueDTOs = List.of(buscaResponseDTO2, buscaResponseDTO1);

        Specification<Venue> spec = Specification.allOf();

        Sort sort = Sort.by("id").descending();
        Pageable paginacao = PageRequest.of(0, 10, sort);

        int start = (int) paginacao.getOffset();
        int end = Math.min(start + paginacao.getPageSize(), venueDTOs.size());
        List<BuscaResponseDTO> pagedList = venueDTOs.subList(start, end);
        Page<BuscaResponseDTO> expected = new PageImpl<>(pagedList, paginacao, venueDTOs.size());

        when(venueRepository.findAll(any(Specification.class))).thenReturn(List.of(venue1, venue2));
        when(buscaMapper.toDTO(venue1)).thenReturn(buscaResponseDTO1);
        when(buscaMapper.toDTO(venue2)).thenReturn(buscaResponseDTO2);

        // Act
        Page<BuscaResponseDTO> result = buscaService.getResults(spec, paginacao);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).containsExactly(buscaResponseDTO2, buscaResponseDTO1);
        assertThat(result).isEqualTo(expected);

        verify(venueRepository).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("Deve retornar a lista do DTO de busca de Venues sem ordem")
    public void getResults_shouldReturnUnsorted() {
        // Arrange
        List<BuscaResponseDTO> venueDTOs = List.of(buscaResponseDTO1, buscaResponseDTO2);

        Specification<Venue> spec = Specification.allOf();

        Pageable paginacao = PageRequest.of(0, 10);

        int start = (int) paginacao.getOffset();
        int end = Math.min(start + paginacao.getPageSize(), venueDTOs.size());
        List<BuscaResponseDTO> pagedList = venueDTOs.subList(start, end);
        Page<BuscaResponseDTO> expected = new PageImpl<>(pagedList, paginacao, venueDTOs.size());

        when(venueRepository.findAll(any(Specification.class))).thenReturn(List.of(venue1, venue2));
        when(buscaMapper.toDTO(venue1)).thenReturn(buscaResponseDTO1);
        when(buscaMapper.toDTO(venue2)).thenReturn(buscaResponseDTO2);

        // Act
        Page<BuscaResponseDTO> result = buscaService.getResults(spec, paginacao);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).containsExactly(buscaResponseDTO1, buscaResponseDTO2);
        assertThat(result).isEqualTo(expected);

        verify(venueRepository).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("Deve retornar a lista do DTO de busca de Venue1 e Venue2 por Distancia, depois ID")
    public void getResults_shouldReturnByDistanciaAndIdAsc() {
        // Arrange
        List<BuscaResponseDTO> venueDTOs = List.of(buscaResponseDTO1, buscaResponseDTO2);

        Specification<Venue> spec = Specification.allOf();

        Sort sort = Sort.by("distancia", "id").ascending();
        Pageable paginacao = PageRequest.of(0, 10, sort);

        int start = (int) paginacao.getOffset();
        int end = Math.min(start + paginacao.getPageSize(), venueDTOs.size());
        List<BuscaResponseDTO> pagedList = venueDTOs.subList(start, end);
        Page<BuscaResponseDTO> expected = new PageImpl<>(pagedList, paginacao, venueDTOs.size());

        when(venueRepository.findAll(any(Specification.class))).thenReturn(List.of(venue1, venue2));
        when(buscaMapper.toDTO(venue1)).thenReturn(buscaResponseDTO1);
        when(buscaMapper.toDTO(venue2)).thenReturn(buscaResponseDTO2);

        // Act
        Page<BuscaResponseDTO> result = buscaService.getResults(spec, paginacao);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).containsExactly(buscaResponseDTO1, buscaResponseDTO2);
        assertThat(result).isEqualTo(expected);

        verify(venueRepository).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("Deve retornar a lista do DTO de busca de Venue2 e Venue1 por Distancia, depois ID")
    public void getResults_shouldReturnByDistanciaAndIdDesc() {
        // Arrange
        List<BuscaResponseDTO> venueDTOs = List.of(buscaResponseDTO2, buscaResponseDTO1);

        Specification<Venue> spec = Specification.allOf();

        Sort sort = Sort.by("distancia", "id").descending();
        Pageable paginacao = PageRequest.of(0, 10, sort);

        int start = (int) paginacao.getOffset();
        int end = Math.min(start + paginacao.getPageSize(), venueDTOs.size());
        List<BuscaResponseDTO> pagedList = venueDTOs.subList(start, end);
        Page<BuscaResponseDTO> expected = new PageImpl<>(pagedList, paginacao, venueDTOs.size());

        when(venueRepository.findAll(any(Specification.class))).thenReturn(List.of(venue1, venue2));
        when(buscaMapper.toDTO(venue1)).thenReturn(buscaResponseDTO1);
        when(buscaMapper.toDTO(venue2)).thenReturn(buscaResponseDTO2);

        // Act
        Page<BuscaResponseDTO> result = buscaService.getResults(spec, paginacao);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).containsExactly(buscaResponseDTO2, buscaResponseDTO1);
        assertThat(result).isEqualTo(expected);

        verify(venueRepository).findAll(any(Specification.class));
    }

}