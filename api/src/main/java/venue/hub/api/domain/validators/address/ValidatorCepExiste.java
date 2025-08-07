package venue.hub.api.domain.validators.address;

import jakarta.validation.ValidationException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import venue.hub.api.domain.dtos.address.AddressRequestDTO;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ValidatorCepExiste implements AddressValidator {
    @Override
    public void validate(AddressRequestDTO requestDTO) {
        String cep = requestDTO.getCep();

        String url = "https://viacep.com.br/ws/" + cep + "/json/";
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assert response.getBody() != null;
        if (response.getBody().toString().contains("erro=true")) {
            throw new ValidationException("CEP " + cep + " não encontrado pela API da ViaCEP");
        }
    }
}
