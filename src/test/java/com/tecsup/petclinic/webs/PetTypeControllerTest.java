package com.tecsup.petclinic.webs;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de Integración — Entidad: PetType (Types)
 * Ejercicio 5 — Laboratorio 12: Construcción y Pruebas de Software
 *
 * Alumno: Estudiante Junior de Ingeniería de Software
 * Paquete: com.tecsup.petclinic.webs
 *
 * Requiere: PetTypeRepository.java en el paquete repositories
 * Endpoint expuesto por Spring Data REST: /api/petTypes
 *
 * Cómo ejecutar:
 *   mvn clean test "-Dspring.profiles.active=h2" "-Dtest=PetTypeControllerTest"
 */
@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class PetTypeControllerTest {

    private static final String API_PETTYPES = "/api/petTypes";

    @Autowired
    private MockMvc mockMvc;

    // -------------------------------------------------------
    // TC01: Listar todos los tipos de mascota
    //       HATEOAS → { "_embedded": { "petTypes": [...] } }
    // -------------------------------------------------------
    @Test
    public void testFindAllPetTypes() throws Exception {

        log.info("TC01 - GET " + API_PETTYPES);

        mockMvc.perform(get(API_PETTYPES)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$._embedded.petTypes", isA(java.util.List.class)))
                .andExpect(jsonPath("$._embedded.petTypes", hasSize(greaterThan(0))))
                .andDo(print());
    }

    // -------------------------------------------------------
    // TC02: Obtener tipo por ID existente
    //       Dataset H2 inicial incluye tipos con IDs 1..6
    // -------------------------------------------------------
    @Test
    public void testFindPetTypeOK() throws Exception {

        int idExistente = 1;
        log.info("TC02 - GET " + API_PETTYPES + "/" + idExistente);

        mockMvc.perform(get(API_PETTYPES + "/" + idExistente)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$._links.self.href",
                        containsString("/api/petTypes/" + idExistente)))
                .andDo(print());
    }

    // -------------------------------------------------------
    // TC03: Obtener tipo por ID inexistente → 404
    // -------------------------------------------------------
    @Test
    public void testFindPetTypeKO() throws Exception {

        int idInexistente = 9999;
        log.info("TC03 - GET " + API_PETTYPES + "/" + idInexistente + " (esperado 404)");

        mockMvc.perform(get(API_PETTYPES + "/" + idInexistente)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    // -------------------------------------------------------
    // TC04: Crear un nuevo tipo → 201 Created
    //       Spring Data REST → body vacío, ID en header Location
    // -------------------------------------------------------
    @Test
    public void testCreatePetType() throws Exception {

        String newTypeJson = "{\"name\": \"Reptil\"}";
        log.info("TC04 - POST " + API_PETTYPES + " body=" + newTypeJson);

        MvcResult result = mockMvc.perform(post(API_PETTYPES)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newTypeJson))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", containsString("/api/petTypes/")))
                .andDo(print())
                .andReturn();

        String location = result.getResponse().getHeader("Location");
        log.info("Tipo creado en: " + location);
    }

    // -------------------------------------------------------
    // TC05: Actualizar un tipo existente → 204 No Content
    // -------------------------------------------------------
    @Test
    public void testUpdatePetType() throws Exception {

        // 1) Crear el tipo
        String createJson = "{\"name\": \"Ave\"}";

        MvcResult created = mockMvc.perform(post(API_PETTYPES)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andReturn();

        String location = created.getResponse().getHeader("Location");
        String id = location.substring(location.lastIndexOf("/") + 1);
        log.info("TC05 - Tipo creado ID=" + id + " → PUT " + API_PETTYPES + "/" + id);

        // 2) Actualizar
        String updateJson = "{\"name\": \"Ave Exótica\"}";

        mockMvc.perform(put(API_PETTYPES + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isNoContent())
                .andDo(print());

        // 3) Verificar el cambio
        mockMvc.perform(get(API_PETTYPES + "/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Ave Exótica")))
                .andDo(print());
    }

    // -------------------------------------------------------
    // TC06: Eliminar un tipo existente → 204 No Content
    // -------------------------------------------------------
    @Test
    public void testDeletePetType() throws Exception {

        // 1) Crear el tipo que vamos a borrar
        String createJson = "{\"name\": \"Insecto\"}";

        MvcResult created = mockMvc.perform(post(API_PETTYPES)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andReturn();

        String location = created.getResponse().getHeader("Location");
        String id = location.substring(location.lastIndexOf("/") + 1);
        log.info("TC06 - DELETE " + API_PETTYPES + "/" + id);

        // 2) Eliminar
        mockMvc.perform(delete(API_PETTYPES + "/" + id))
                .andExpect(status().isNoContent())
                .andDo(print());

        // 3) Confirmar que ya no existe
        mockMvc.perform(get(API_PETTYPES + "/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    // -------------------------------------------------------
    // TC07: Eliminar tipo inexistente → 404
    // -------------------------------------------------------
    @Test
    public void testDeletePetTypeKO() throws Exception {

        int idInexistente = 9999;
        log.info("TC07 - DELETE " + API_PETTYPES + "/" + idInexistente + " (esperado 404)");

        mockMvc.perform(delete(API_PETTYPES + "/" + idInexistente))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}
