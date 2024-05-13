package com.uma.example.springuma.integration;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Calendar;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import com.uma.example.springuma.model.Imagen;

import jakarta.annotation.PostConstruct;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ImagenControllerIT {

    @LocalServerPort
    private Integer port;

    private WebTestClient client;

    private Imagen imagen;
    private Paciente paciente;
    private Medico medico;
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
    @DisplayName("Subimos una imagen de un paciente de manera correcta")
    public void uploadImageTest_() {
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

    }

    @Test
    @DisplayName("Realizamos una predicción a una imagen sana y debe de devolver que es sana")
    public void prediction_isHealthy_returnNotCancer() throws IOException {
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

        assertEquals("Not cancer (label 0),  score: 0.984481368213892", pre);
    }

    @Test
    @DisplayName("Realizamos una prediccion de una imagen con cancer y debe de devolver que tiene cancer")
    public void prediction_notHealthy_returnCancer() throws IOException {
        crearMedicoYPaciente(medico, paciente);

        File uploadFile = new File("src/test/resources/no_healthty.png");

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

        assertEquals("Cancer (label 1), score: 0.6412607431411743", pre);
    }


}