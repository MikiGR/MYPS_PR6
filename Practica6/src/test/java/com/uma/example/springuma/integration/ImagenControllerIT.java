package com.uma.example.springuma.integration;


import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.time.Duration;

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

import com.uma.example.springuma.model.Imagen;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PersonaControllerWebTestClientIT {

    @LocalServerPort
    private Integer port;

    private WebTestClient client;

    private Imagen imagen;
/* 
    // After dependency injection
    @PostConstruct
    public void init() {
        client = WebTestClient.bindToServer().baseUrl("http://localhost:"+port)
        .responseTimeout(Duration.ofMillis(30000)).build();

        persona = new Persona();
        persona.setNombre("Alumno"); 
        persona.setDni("122");
        persona.setEdad(16);
        persona.setId(1);
    }

    @Test
    @DisplayName("Returns the empty list of personas initially")
    public void getPersonas_ReturnPersonasEmpty() {
        client.get().uri("/personas")
            .accept(MediaType.APPLICATION_JSON)
            .exchange() // hace la peticion
            .expectStatus().isOk() // comprueba que el codigo es OK
            .expectHeader().valueEquals("Content-Type", "application/json") // comprueba que el content type es json
            .expectBody().jsonPath("$", hasSize(0)); // comprueba que la respuesta tenga un array con tamanyo 0
    }

    @Test
    @DisplayName("Create a persona and get it correctly")
    public void createPersonaPost_isObtainedWithGet() {

        // crea una persona
        client.post().uri("/persona")
            .body(Mono.just(persona), Persona.class)
            .exchange()
            .expectStatus().isCreated()
            .expectBody().returnResult();
        
        // obtiene la persona con ID 1
        FluxExchangeResult<Persona> result = client.get().uri("/persona/1")
            .exchange()
            .expectStatus().isOk().returnResult(Persona.class); // comprueba que la respuesta es de tipo persona

        Persona personaObtained = result.getResponseBody().blockFirst(); // Obtiene el objeto persona en concreto

        assertEquals(persona, personaObtained);
    }

    @Test
    @DisplayName("Submit a logo to persona/logo controller should return a valid response containing the name of the file")
	void submitValidLogo_shouldRespondValidResponse() throws Exception {

        // crea una persona
        client.post().uri("/persona")
        .body(Mono.just(persona), Persona.class)
        .exchange()
        .expectStatus().isCreated()
        .expectBody().returnResult();
        
        File uploadFile = new File("./src/test/resources/uma.png");

        
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("logo", new FileSystemResource(uploadFile));

        // Envia el logo para la persona en concreto 1
        FluxExchangeResult<String> responseBody = client.post()
        .uri("/persona/1/logo")
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .body(BodyInserters.fromMultipartData(builder.build()))
        .exchange()
        .expectStatus().is2xxSuccessful().returnResult(String.class);
        
        String result = responseBody.getResponseBody().blockFirst();

        assertEquals("Received uma.png", result);
    }
    */
}