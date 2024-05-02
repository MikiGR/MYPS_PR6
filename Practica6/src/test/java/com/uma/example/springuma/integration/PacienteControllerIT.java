package com.uma.example.springuma.integration;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.uma.example.springuma.integration.base.AbstractIntegration;
import com.uma.example.springuma.model.Paciente;
import com.uma.example.springuma.model.Medico;

class PacienteControllerIT extends AbstractIntegration {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Crea un paciente de manera exitosa")
    void savePaciente_returnSuccesfully() throws Exception {
        Medico medico = new Medico("125", "Alberto Canovas", "Traumatología");
        Paciente paciente = new Paciente("Miguel", 22, "Traumatología", "123", medico);

        // Creo el paciente
        this.mockMvc.perform(post("/medico")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(medico)))
                .andExpect(status().isCreated())
                .andExpect(status().is2xxSuccessful());

        // Creo el paciente
        this.mockMvc.perform(post("/paciente")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(paciente)))
                .andExpect(status().isCreated())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("Cuando elimino un paciente, se elimina correctamente")
    void deletePaciente_throwException() throws Exception {
       
    }

}