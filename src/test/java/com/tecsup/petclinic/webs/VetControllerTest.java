package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.tecsup.petclinic.dtos.VetDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 *
 */
@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class VetControllerTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    /**
     * Verifica que GET /vets retorna status 200 y que el primer registro
     * corresponde al vet con id=1 (James Carter) según data-mysql.sql
     */
    @Test
    public void testFindAllVets() throws Exception {

        final int ID_FIRST_RECORD = 1;

        this.mockMvc.perform(get("/vets"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$[0].id", is(ID_FIRST_RECORD)));
    }

    /**
     * Verifica que GET /vets/1 retorna correctamente los datos
     * del vet James Carter (id=1 en data-mysql.sql)
     */
    @Test
    public void testFindVetOK() throws Exception {

        String VET_FIRST_NAME = "James";
        String VET_LAST_NAME  = "Carter";

        this.mockMvc.perform(get("/vets/1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",        is(1)))
                .andExpect(jsonPath("$.firstName", is(VET_FIRST_NAME)))
                .andExpect(jsonPath("$.lastName",  is(VET_LAST_NAME)));
    }

    /**
     * Verifica que GET /vets/666 retorna 404 cuando el vet no existe
     */
    @Test
    public void testFindVetKO() throws Exception {

        mockMvc.perform(get("/vets/666"))
                .andExpect(status().isNotFound());
    }

    /**
     * Verifica que POST /vets crea un nuevo vet correctamente
     * y retorna 201 con los datos persistidos
     */
    @Test
    public void testCreateVet() throws Exception {

        String VET_FIRST_NAME = "Laura";
        String VET_LAST_NAME  = "Sanchez";

        VetDTO newVetDTO = VetDTO.builder()
                .firstName(VET_FIRST_NAME)
                .lastName(VET_LAST_NAME)
                .build();

        this.mockMvc.perform(post("/vets")
                        .content(om.writeValueAsString(newVetDTO))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is(VET_FIRST_NAME)))
                .andExpect(jsonPath("$.lastName",  is(VET_LAST_NAME)));
    }

    /**
     * Verifica que DELETE /vets/{id} elimina un vet creado previamente
     * Patrón: CREATE → extraer id → DELETE → verificar 200
     */
    @Test
    public void testDeleteVet() throws Exception {

        String VET_FIRST_NAME = "Carlos";
        String VET_LAST_NAME  = "Perez";

        VetDTO newVetDTO = VetDTO.builder()
                .firstName(VET_FIRST_NAME)
                .lastName(VET_LAST_NAME)
                .build();

        ResultActions mvcActions = mockMvc.perform(post("/vets")
                        .content(om.writeValueAsString(newVetDTO))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        String response = mvcActions.andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.parse(response).read("$.id");

        mockMvc.perform(delete("/vets/" + id))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que DELETE /vets/1000 retorna 404 cuando el id no existe
     */
    @Test
    public void testDeleteVetKO() throws Exception {

        mockMvc.perform(delete("/vets/" + "1000"))
                .andExpect(status().isNotFound());
    }

    /**
     * Verifica el ciclo completo: CREATE → UPDATE → FIND → DELETE
     * para garantizar la consistencia de datos tras cada operación
     */
    @Test
    public void testUpdateVet() throws Exception {

        String VET_FIRST_NAME    = "Ana";
        String VET_LAST_NAME     = "Lopez";

        String UP_VET_FIRST_NAME = "Ana Maria";
        String UP_VET_LAST_NAME  = "Lopez Torres";

        VetDTO newVetDTO = VetDTO.builder()
                .firstName(VET_FIRST_NAME)
                .lastName(VET_LAST_NAME)
                .build();

        // CREATE
        ResultActions mvcActions = mockMvc.perform(post("/vets")
                        .content(om.writeValueAsString(newVetDTO))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        String response = mvcActions.andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.parse(response).read("$.id");

        // UPDATE
        VetDTO upVetDTO = VetDTO.builder()
                .id(id)
                .firstName(UP_VET_FIRST_NAME)
                .lastName(UP_VET_LAST_NAME)
                .build();

        mockMvc.perform(put("/vets/" + id)
                        .content(om.writeValueAsString(upVetDTO))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // FIND — validar que los cambios se persistieron
        mockMvc.perform(get("/vets/" + id))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",        is(id)))
                .andExpect(jsonPath("$.firstName", is(UP_VET_FIRST_NAME)))
                .andExpect(jsonPath("$.lastName",  is(UP_VET_LAST_NAME)));

        // DELETE — limpieza de la BD de test
        mockMvc.perform(delete("/vets/" + id))
                .andExpect(status().isOk());
    }
}
