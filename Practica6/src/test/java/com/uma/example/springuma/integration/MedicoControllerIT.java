/*
 * @author: Miguel Galdeano Rodríguez
 * @author: Pablo León Vázquez
 */
package com.uma.example.springuma.integration;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebFlux;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.uma.example.springuma.integration.base.AbstractIntegration;
import com.uma.example.springuma.model.Medico;
import com.uma.example.springuma.model.Paciente;

@AutoConfigureMockMvc
class MedicoControllerIT extends AbstractIntegration {

    Medico medico;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

     @BeforeEach
    public void init(){
        medico = new Medico();
        medico.setDni("123");
        medico.setId(1);
        medico.setNombre("Alberto");

    }

    @Test
    @DisplayName("Cuando intento crear un medico, se crea exitosamente")
	void createMedico_returnTrue() throws Exception {
        // crea un medico
        this.mockMvc.perform(post("/medico")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(medico)))
        .andExpect(status().isCreated())
        .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("Cuando intento obtener un medico que no existe, se lanza una excepcion")
    void getMedico_emptyDB_returnException() throws Exception {
        this.mockMvc.perform(get("/medico/2"))
                .andExpect(status().isInternalServerError()); // comprueba que el codigo es 500
    }

    @Test
    @DisplayName("Cuando intento obtener un medico que si existe, lo retorna correctamente")
	void getMedico_exists_returnTrue() throws Exception {

        // crea un medico
        this.mockMvc.perform(post("/medico")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(medico)))
        .andExpect(status().isCreated())
        .andExpect(status().is2xxSuccessful());

        // obtiene el medico
		this.mockMvc.perform(get("/medico/1"))
        .andDo(print())
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.id").value(medico.getId())) // comprueba que el id es igual al de la persona creada
        .andExpect(jsonPath("$.nombre").value(medico.getNombre())) // comprueba que el nombre es igual al de la persona creada
        .andExpect(jsonPath("$.especialidad").value(medico.getEspecialidad())); // comprueba que la especialidad es igual a la de la persona creada
    }

    @Test
    @DisplayName("Cuando modifico algun campo de algun medico y lo guardo, se actualiza correctamente")
	void getMedico_modifyAttributes_returnTrue() throws Exception {
        // crea el medico
        this.mockMvc.perform(post("/medico")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(medico)))
        .andExpect(status().is2xxSuccessful());

        String nuevoDNI="126";
        medico.setDni(nuevoDNI);

        // le cambio el dni
        this.mockMvc.perform(put("/medico")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(medico)))
        .andExpect(status().isNoContent());
        

        // obtiene el listado de personas
        this.mockMvc.perform(get("/medico/1"))
        .andDo(print())
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.id").value(medico.getId())) // comprueba que el id es igual al de la persona creada
        .andExpect(jsonPath("$.dni").value(nuevoDNI)) // comprueba que el id es igual al de la persona creada
        .andExpect(jsonPath("$.nombre").value(medico.getNombre())) // comprueba que el nombre es igual al de la persona creada
        .andExpect(jsonPath("$.especialidad").value(medico.getEspecialidad())); // comprueba que la especialidad es igual a la de la persona creada
    }

    @Test
    @DisplayName("Cuando elimino un medico, se elimina correctamente")
	void deleteMedico_exists_returnTrue() throws Exception {
        // crea el medico
        this.mockMvc.perform(post("/medico")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(medico)))
        .andExpect(status().is2xxSuccessful());

        // elimino el medico
        this.mockMvc.perform(delete("/medico/1")
        .contentType("application/json"))
        .andExpect(status().is2xxSuccessful());

        this.mockMvc.perform(get("/medico/1"))
        .andExpect(status().isInternalServerError()); // comprueba que el codigo es 500
    }

}