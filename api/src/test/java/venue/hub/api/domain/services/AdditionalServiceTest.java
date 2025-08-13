package venue.hub.api.domain.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import venue.hub.api.domain.dtos.additional.AdditionalRequestDTO;
import venue.hub.api.domain.dtos.additional.AdditionalResponseDTO;
import venue.hub.api.domain.dtos.additional.AdditionalUpdateDTO;
import venue.hub.api.domain.dtos.mapper.AdditionalMapper;
import venue.hub.api.domain.entities.Additional;
import venue.hub.api.domain.repositories.AdditionalRepository;
import venue.hub.api.infra.exceptions.AdditionalNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AdditionalServiceTest {

    @Mock
    private AdditionalRepository additionalRepository;

    @Mock
    private AdditionalMapper additionalMapper;

    @InjectMocks
    private AdditionalService additionalService;

    private Additional additional;
    private AdditionalRequestDTO additionalRequestDTO;
    private AdditionalUpdateDTO additionalUpdateDTO;
    private AdditionalResponseDTO additionalResponseDTO;

    @Before
    public void setUp() {
        additional = new Additional(1L, "Cozinheiro");
        additionalRequestDTO = new AdditionalRequestDTO("Segurança");
        additionalUpdateDTO = new AdditionalUpdateDTO("Dj");
        additionalResponseDTO = new AdditionalResponseDTO(1L, "Cozinheiro");
    }


    @Test
    @DisplayName("Deve criar um adicional com sucesso")
    public void createAdditional_shouldCreateAdditional_whenValidRequest() {
        // Arrange
        when(additionalMapper.toEntity(any(AdditionalRequestDTO.class))).thenReturn(additional);
        when(additionalRepository.save(any(Additional.class))).thenReturn(additional);
        when(additionalMapper.toDTO(any(Additional.class))).thenReturn(additionalResponseDTO);

        // Act
        AdditionalResponseDTO result = additionalService.createAdditional(additionalRequestDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(additional.getId());
        assertThat(result.getNome()).isEqualTo(additional.getNome());

        verify(additionalMapper).toEntity(additionalRequestDTO);
        verify(additionalRepository).save(additional);
        verify(additionalMapper).toDTO(additional);
    }

    @Test
    @DisplayName("Deve buscar todos os adicionais e retorná-los como DTOs")
    public void getAllAdditionals_shouldFindAllAndReturnAsDTOs() {
        // Arrange
        Pageable paginacao = PageRequest.of(0, 10);
        Page<Additional> additionalPage = new PageImpl<>(List.of(additional), paginacao, 1);
        Page<AdditionalResponseDTO> additionalDTOPage = new PageImpl<>(List.of(additionalResponseDTO), paginacao, 1);

        when(additionalRepository.findAll(paginacao)).thenReturn(additionalPage);
        when(additionalMapper.toDTO(any(Additional.class))).thenReturn(additionalResponseDTO);

        // Act
        Page<AdditionalResponseDTO> result = additionalService.getAllAdditionals(paginacao);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(additionalResponseDTO);

        verify(additionalRepository).findAll(paginacao);
        verify(additionalMapper).toDTO(additional);
    }

    @Test
    @DisplayName("Deve buscar um adicional pelo ID e retornar como DTO")
    public void getAdditionalById_shouldFindAdditionalByIdAndReturnAsDTO() {
        // Arrange
        when(additionalRepository.findById(anyLong())).thenReturn(Optional.of(additional));
        when(additionalMapper.toDTO(any(Additional.class))).thenReturn(additionalResponseDTO);

        // Act
        AdditionalResponseDTO result = additionalService.getAdditionalById(additional.getId());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(additional.getId());

        verify(additionalRepository).findById(additional.getId());
        verify(additionalMapper).toDTO(additional);
    }

    @Test
    @DisplayName("Deve lançar AdditionalNotFoundException se o adicional não for encontrado por ID")
    public void getAdditionalById_shouldThrowException_whenAdditionalNotFound() {
        // Arrange
        when(additionalRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> additionalService.getAdditionalById(99L))
                .isInstanceOf(AdditionalNotFoundException.class)
                .hasMessageContaining("Adicional não encontrado com o id: 99")
                .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND);

        verify(additionalRepository).findById(99L);
        verify(additionalMapper, never()).toDTO(any(Additional.class));
    }

    @Test
    @DisplayName("Deve atualizar um adicional com sucesso")
    public void updateAdditional_shouldUpdateAdditional_whenValidRequest() {
        // Arrange
        when(additionalRepository.findById(anyLong())).thenReturn(Optional.of(additional));
        when(additionalRepository.save(any(Additional.class))).thenReturn(additional);
        when(additionalMapper.toDTO(any(Additional.class))).thenReturn(additionalResponseDTO);

        Additional spiedAdditional = spy(additional);
        when(additionalRepository.findById(anyLong())).thenReturn(Optional.of(spiedAdditional));

        // Act
        AdditionalResponseDTO result = additionalService.updateAdditional(1L, additionalUpdateDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(additional.getId());

        verify(additionalRepository).findById(1L);
        verify(spiedAdditional).update(additionalUpdateDTO);
        verify(additionalRepository).save(spiedAdditional);
        verify(additionalMapper).toDTO(spiedAdditional);
    }

    @Test
    @DisplayName("Deve lançar AdditionalNotFoundException ao tentar atualizar um adicional inexistente")
    public void updateAdditional_shouldThrowException_whenAdditionalNotFound() {
        // Arrange
        when(additionalRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> additionalService.updateAdditional(99L, additionalUpdateDTO))
                .isInstanceOf(AdditionalNotFoundException.class)
                .hasMessageContaining("Adicional não encontrado com o id: 99");

        verify(additionalRepository).findById(99L);
        verify(additionalRepository, never()).save(any(Additional.class));
        verify(additionalMapper, never()).toDTO(any(Additional.class));
    }
}