package com.uma.example.springuma.integration;


import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Calendar;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uma.example.springuma.model.Imagen;
import com.uma.example.springuma.model.Medico;
import com.uma.example.springuma.model.Paciente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import com.uma.example.springuma.model.Informe;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InformeControllerWebTestClienteIT {

    @LocalServerPort
    private Integer port;

    private WebTestClient client;

    private Imagen imagen;
    private Paciente paciente;
    private Medico medico;
    private Informe informe;
    ObjectMapper objectMapper = new ObjectMapper();

    // After dependency injection
    @PostConstruct
    public void init() {
        client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port)
                .responseTimeout(Duration.ofMillis(30000)).build();

        medico = new Medico("11111111A", "Mari", "trauma");
        medico.setId(1);

        paciente = new Paciente("Rosa", 30, "trauma", "84392821B", medico);
        paciente.setId(1);

        imagen = new Imagen();
        imagen.setId(1);
        imagen.setNombre("img");
        imagen.setPaciente(paciente);
        imagen.setFecha(Calendar.getInstance());
        imagen.setFile_content(null);

        informe = new Informe();
        informe.setId(1);
        informe.setImagen(imagen);
    }

    public void crearMedicoYPaciente(Medico medico, Paciente paciente) {
        client.post().uri("/medico")
                .body(Mono.just(medico), Medico.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody().returnResult();

        client.post().uri("/paciente")
                .body(Mono.just(paciente), Paciente.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody().returnResult();
    }

    @Test
    @DisplayName("Creo un informe correctamente")
    public void create_informeAllCorrect_returnTrue() throws IOException {
        crearMedicoYPaciente(medico, paciente);

        File uploadFile = new File("src/test/resources/healthy.png");

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("image", new FileSystemResource(uploadFile));
        builder.part("paciente", paciente);

        FluxExchangeResult<String> responseBody = client.post()
                .uri("/imagen")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                .expectStatus().is2xxSuccessful().returnResult(String.class);


        FluxExchangeResult<String> prediction = client.get()
                .uri("/imagen/predict/1")
                .exchange()
                .expectStatus().is2xxSuccessful().returnResult(String.class);

        String result = prediction.getResponseBody().blockFirst();
        JsonNode jsonNode = objectMapper.readTree(prediction.getResponseBodyContent());
        String pre = jsonNode.get("prediction").asText();

        informe.setPrediccion(pre);
        informe.setContenido("No se");

        client.post().uri("/informe")
                .body(Mono.just(informe), Informe.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody().returnResult();
    }


    @Test
    @DisplayName("Elimino un informe correctamente")
    public void delente_informeAllCorrect_returnTrue() throws IOException {
        crearMedicoYPaciente(medico, paciente);

        File uploadFile = new File("src/test/resources/healthy.png");

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("image", new FileSystemResource(uploadFile));
        builder.part("paciente", paciente);

        FluxExchangeResult<String> responseBody = client.post()
                .uri("/imagen")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                .expectStatus().is2xxSuccessful().returnResult(String.class);


        FluxExchangeResult<String> prediction = client.get()
                .uri("/imagen/predict/1")
                .exchange()
                .expectStatus().is2xxSuccessful().returnResult(String.class);

        String result = prediction.getResponseBody().blockFirst();
        JsonNode jsonNode = objectMapper.readTree(prediction.getResponseBodyContent());
        String pre = jsonNode.get("prediction").asText();

        informe.setPrediccion(pre);
        informe.setContenido("No se");

        client.post().uri("/informe")
                .body(Mono.just(informe), Informe.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody().returnResult();

        client.delete().uri("/informe/1")
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

}