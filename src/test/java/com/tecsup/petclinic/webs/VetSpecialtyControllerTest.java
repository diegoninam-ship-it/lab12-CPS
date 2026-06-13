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
 * Pruebas de Integración — Entidad: VetSpecialty (vet_specialties)
 * Ejercicio 6 — Laboratorio 12: Construcción y Pruebas de Software
 *
 * Alumno: Estudiante Junior de Ingeniería de Software
 * Paquete: com.tecsup.petclinic.webs
 *
 * NOTA TÉCNICA:
 *   Este proyecto usa Spring Data REST (HATEOAS), por lo tanto:
 *   - GET /api/specialties  → responde con { "_embedded": { "specialties": [...] } }
 *   - POST /api/specialties → responde con body vacío, el ID viene en el header Location
 *   - GET /api/specialties/{id} → responde con { "name": "...", "_links": {...} }
 *     (el id NO viene en el body, viene en el link self)
 *
 * Cómo ejecutar:
 *   mvn clean test "-Dspring.profiles.active=h2" "-Dtest=VetSpecialtyControllerTest"
 */
@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class VetSpecialtyControllerTest {

    private static final String API_SPECIALTIES = "/api/specialties";
    private static final String API_VETS        = "/api/vets";

    @Autowired
    private MockMvc mockMvc;

    // -------------------------------------------------------
    // TC01: Listar todas las especialidades
    //       La API retorna HATEOAS → { "_embedded": { "specialties": [...] } }
    // -------------------------------------------------------
    @Test
    public void testFindAllSpecialties() throws Exception {

        log.info("TC01 - GET " + API_SPECIALTIES);

        mockMvc.perform(get(API_SPECIALTIES)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                // HATEOAS envuelve la lista dentro de _embedded.specialties
                .andExpect(jsonPath("$._embedded.specialties", isA(java.util.List.class)))
                .andExpect(jsonPath("$._embedded.specialties", hasSize(greaterThan(0))))
                .andDo(print());
    }

    // -------------------------------------------------------
    // TC02: Obtener especialidad por ID existente
    //       La API retorna el objeto sin campo "id" en el body;
    //       el id viene dentro de _links.self.href
    // -------------------------------------------------------
    @Test
    public void testFindSpecialtyOK() throws Exception {

        int idExistente = 1;
        log.info("TC02 - GET " + API_SPECIALTIES + "/" + idExistente);

        mockMvc.perform(get(API_SPECIALTIES + "/" + idExistente)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                // Verifica que el campo "name" existe (el id está en _links.self.href)
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$._links.self.href",
                        containsString("/api/specialties/" + idExistente)))
                .andDo(print());
    }

    // -------------------------------------------------------
    // TC03: Obtener especialidad por ID inexistente → 404
    // -------------------------------------------------------
    @Test
    public void testFindSpecialtyKO() throws Exception {

        int idInexistente = 9999;
        log.info("TC03 - GET " + API_SPECIALTIES + "/" + idInexistente + " (esperado 404)");

        mockMvc.perform(get(API_SPECIALTIES + "/" + idInexistente)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    // -------------------------------------------------------
    // TC04: Crear una nueva especialidad → 201 Created
    //       Spring Data REST devuelve body vacío; el ID viene
    //       en el header Location: http://localhost/api/specialties/{id}
    // -------------------------------------------------------
    @Test
    public void testCreateSpecialty() throws Exception {

        String newSpecialtyJson = "{\"name\": \"Traumatología\"}";
        log.info("TC04 - POST " + API_SPECIALTIES + " body=" + newSpecialtyJson);

        MvcResult result = mockMvc.perform(post(API_SPECIALTIES)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newSpecialtyJson))
                .andExpect(status().isCreated())
                // El ID viene en el header Location
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", containsString("/api/specialties/")))
                .andDo(print())
                .andReturn();

        String location = result.getResponse().getHeader("Location");
        log.info("Especialidad creada en: " + location);
    }

    // -------------------------------------------------------
    // TC05: Actualizar una especialidad existente → 204 No Content
    //       Extraemos el ID del header Location del POST previo
    // -------------------------------------------------------
    @Test
    public void testUpdateSpecialty() throws Exception {

        // 1) Crear la especialidad
        String createJson = "{\"name\": \"Cardiología\"}";

        MvcResult created = mockMvc.perform(post(API_SPECIALTIES)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andReturn();

        // Extraer el ID del header Location  → "http://localhost/api/specialties/5"
        String location = created.getResponse().getHeader("Location");
        String id = location.substring(location.lastIndexOf("/") + 1);
        log.info("TC05 - Specialty creada ID=" + id + " → PUT " + API_SPECIALTIES + "/" + id);

        // 2) Actualizar
        String updateJson = "{\"name\": \"Cardiología Veterinaria\"}";

        mockMvc.perform(put(API_SPECIALTIES + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isNoContent())
                .andDo(print());

        // 3) Verificar el cambio
        mockMvc.perform(get(API_SPECIALTIES + "/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Cardiología Veterinaria")))
                .andDo(print());
    }

    // -------------------------------------------------------
    // TC06: Eliminar una especialidad existente → 204 No Content
    // -------------------------------------------------------
    @Test
    public void testDeleteSpecialty() throws Exception {

        // 1) Crear la especialidad que vamos a borrar
        String createJson = "{\"name\": \"Neurología\"}";

        MvcResult created = mockMvc.perform(post(API_SPECIALTIES)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andReturn();

        String location = created.getResponse().getHeader("Location");
        String id = location.substring(location.lastIndexOf("/") + 1);
        log.info("TC06 - DELETE " + API_SPECIALTIES + "/" + id);

        // 2) Eliminar
        mockMvc.perform(delete(API_SPECIALTIES + "/" + id))
                .andExpect(status().isNoContent())
                .andDo(print());

        // 3) Confirmar que ya no existe
        mockMvc.perform(get(API_SPECIALTIES + "/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    // -------------------------------------------------------
    // TC07: Eliminar especialidad inexistente → 404
    // -------------------------------------------------------
    @Test
    public void testDeleteSpecialtyKO() throws Exception {

        int idInexistente = 9999;
        log.info("TC07 - DELETE " + API_SPECIALTIES + "/" + idInexistente + " (esperado 404)");

        mockMvc.perform(delete(API_SPECIALTIES + "/" + idInexistente))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    // -------------------------------------------------------
    // TC08: Verificar relación vet_specialties
    //       GET /api/vets/{id}/specialties → lista las especialidades del vet
    //       Esta es la tabla de unión vet_specialties vista desde el Vet
    // -------------------------------------------------------
    @Test
    public void testGetVetSpecialtiesRelation() throws Exception {

        // El dataset H2 inicial tiene vets con specialties ya cargadas
        int vetId = 1;
        log.info("TC08 - GET " + API_VETS + "/" + vetId + "/specialties (relación vet_specialties)");

        mockMvc.perform(get(API_VETS + "/" + vetId + "/specialties")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    // -------------------------------------------------------
    // TC09: Verificar que un Vet tiene el link a specialties
    //       (confirma que la relación vet_specialties está expuesta)
    // -------------------------------------------------------
    @Test
    public void testGetVetHasSpecialtiesLink() throws Exception {

        int vetId = 3;
        log.info("TC09 - GET " + API_VETS + "/" + vetId + " (verificar link specialties)");

        mockMvc.perform(get(API_VETS + "/" + vetId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", notNullValue()))
                .andExpect(jsonPath("$.lastName", notNullValue()))
                // La relación vet_specialties se expone como link HATEOAS
                .andExpect(jsonPath("$._links.specialties.href",
                        containsString("/api/vets/" + vetId + "/specialties")))
                .andDo(print());
    }
}
