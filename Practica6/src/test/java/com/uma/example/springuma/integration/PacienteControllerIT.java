/*
 * @author: Miguel Galdeano Rodríguez
 * @author: Pablo León Vázquez
 */
package com.uma.example.springuma.integration;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
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

    Medico medico;
    Paciente paciente;

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
        medico.setEspecialidad("Traumatología");

        paciente = new Paciente();
        paciente.setDni("321");
        paciente.setId(1);
        paciente.setNombre("Juanjo");
        paciente.setMedico(medico);
    }

    private void crearMedico(Medico medico) throws Exception {
        this.mockMvc.perform(post("/medico")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(medico)))
                .andExpect(status().isCreated())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("Crea un paciente de manera exitosa")
    void createPaciente_returnTrue() throws Exception {
        // Creo el medico
        this.crearMedico(this.medico);

        // Creo el paciente
        this.mockMvc.perform(post("/paciente")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(paciente)))
                .andExpect(status().isCreated())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("Cuando intento obtener un paciente que si existe, lo retorna correctamente")
	void getPaciente_exists_returnTrue() throws Exception {
        // Creo el medico
        this.crearMedico(this.medico);

        // Creo el paciente
        this.mockMvc.perform(post("/paciente")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(paciente)))
                .andExpect(status().isCreated())
                .andExpect(status().is2xxSuccessful());

        // obtiene el paciente
		this.mockMvc.perform(get("/paciente/1"))
        .andDo(print())
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.id").value(paciente.getId())) // comprueba que el id es igual al de la persona creada
        .andExpect(jsonPath("$.nombre").value(paciente.getNombre())) // comprueba que el nombre es igual al de la persona creada
        .andExpect(jsonPath("$.edad").value(paciente.getEdad())); // comprueba que la edad es igual a la de la persona creada
    }

    @Test
    @DisplayName("Cuando elimino un paciente, se elimina correctamente")
    void deletePaciente_returnTrue() throws Exception {
       // Creo el medico
       this.crearMedico(this.medico);

       // Creo el paciente
       this.mockMvc.perform(post("/paciente")
               .contentType("application/json")
               .content(objectMapper.writeValueAsString(paciente)))
               .andExpect(status().isCreated())
               .andExpect(status().is2xxSuccessful());

       this.mockMvc.perform(delete("/paciente/1")
        .contentType("application/json"))
        .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("Cuando modifico algun campo de un paciente y lo guardo, se actualiza correctamente")
	void getPaciente_modifyAttributes_returnTrue() throws Exception {
        // crea el medico
       this.crearMedico(this.medico);

        String nuevoDNI="126";
        String nuevoNombre="Jose Carlos";
        paciente.setDni(nuevoDNI);
        paciente.setNombre(nuevoNombre);

        // le cambio el dni
        this.mockMvc.perform(put("/paciente")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(paciente)))
        .andExpect(status().is2xxSuccessful());
        

        // obtiene el listado de personas
        this.mockMvc.perform(get("/paciente/1"))
        .andDo(print())
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.id").value(paciente.getId())) // comprueba que el id es igual al de la persona creada
        .andExpect(jsonPath("$.dni").value(nuevoDNI)) // comprueba que el id es igual al de la persona creada
        .andExpect(jsonPath("$.nombre").value(nuevoNombre)) // comprueba que el nombre es igual al de la persona creada
        .andExpect(jsonPath("$.edad").value(paciente.getEdad())); // comprueba que la edad es igual a la de la persona creada
    }

    @Test
    @DisplayName("Cuando cambio el médico de un paciente y lo guardo, se actualiza correctamente")
	void getPaciente_medicoModified_returnTrue() throws Exception {
        // crea el medico
       this.crearMedico(this.medico);

        // creo el paciente
        this.mockMvc.perform(post("/paciente")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(paciente)))
        .andExpect(status().is2xxSuccessful());
        

        // obtiene el paciente que acabamos de crear
        this.mockMvc.perform(get("/paciente/1"))
        .andDo(print())
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.id").value(paciente.getId())) // comprueba que el id es igual al del paciente creado
        .andExpect(jsonPath("$.medico.id").value(medico.getId())); // comprueba que el id es igual al del medico creado

        //creo el nuevo médico
        Medico nuevoMedico = new Medico();
        nuevoMedico.setDni("124");
        nuevoMedico.setId(2);
        nuevoMedico.setNombre("Julio");
        nuevoMedico.setEspecialidad("Cardiología");

        //Modifico el médico del paciente
        this.paciente.setMedico(nuevoMedico);

        //Subo el nuevo médico 
        this.crearMedico(nuevoMedico);

        //subo el paciente con el nuevo medico
        this.mockMvc.perform(put("/paciente")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(this.paciente)))
        .andExpect(status().isNoContent());

        // Obtengo de nuevo el paciente para ver si se ha modificado el médico
        this.mockMvc.perform(get("/paciente/1"))
        .andDo(print())
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.id").value(paciente.getId())) // comprueba que el id es igual al del paciente cread
        .andExpect(jsonPath("$.medico.id").value(nuevoMedico.getId())); // comprueba que el id es igual al del medico creado

    }


}