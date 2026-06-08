package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.tecsup.petclinic.dtos.OwnerDTO;
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

@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class OwnerControllerTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    /**
     * Verifica que GET /owners retorna status 200 y que el primer registro
     * corresponde al owner con id=1 (George Franklin) según data-mysql.sql
     */
    @Test
    public void testFindAllOwners() throws Exception {

        final int ID_FIRST_RECORD = 1;

        this.mockMvc.perform(get("/owners"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$[0].id", is(ID_FIRST_RECORD)));
    }

    /**
     * Verifica que GET /owners/1 retorna correctamente los datos
     * de George Franklin (id=1 en data-mysql.sql)
     */
    @Test
    public void testFindOwnerOK() throws Exception {

        String OWNER_FIRST_NAME = "George";
        String OWNER_LAST_NAME  = "Franklin";
        String OWNER_ADDRESS    = "110 W. Liberty St.";
        String OWNER_CITY       = "Madison";
        String OWNER_TELEPHONE  = "6085551023";

        this.mockMvc.perform(get("/owners/1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",        is(1)))
                .andExpect(jsonPath("$.firstName", is(OWNER_FIRST_NAME)))
                .andExpect(jsonPath("$.lastName",  is(OWNER_LAST_NAME)))
                .andExpect(jsonPath("$.address",   is(OWNER_ADDRESS)))
                .andExpect(jsonPath("$.city",      is(OWNER_CITY)))
                .andExpect(jsonPath("$.telephone", is(OWNER_TELEPHONE)));
    }

    /**
     * Verifica que GET /owners/666 retorna 404 cuando el owner no existe
     */
    @Test
    public void testFindOwnerKO() throws Exception {

        mockMvc.perform(get("/owners/666"))
                .andExpect(status().isNotFound());
    }

    /**
     * Verifica que POST /owners crea un nuevo owner correctamente
     * y retorna 201 con todos los campos persistidos
     */
    @Test
    public void testCreateOwner() throws Exception {

        String OWNER_FIRST_NAME = "Maria";
        String OWNER_LAST_NAME  = "Gomez";
        String OWNER_ADDRESS    = "Av. Larco 123";
        String OWNER_CITY       = "Lima";
        String OWNER_TELEPHONE  = "9876543210";

        OwnerDTO newOwnerDTO = OwnerDTO.builder()
                .firstName(OWNER_FIRST_NAME)
                .lastName(OWNER_LAST_NAME)
                .address(OWNER_ADDRESS)
                .city(OWNER_CITY)
                .telephone(OWNER_TELEPHONE)
                .build();

        this.mockMvc.perform(post("/owners")
                        .content(om.writeValueAsString(newOwnerDTO))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is(OWNER_FIRST_NAME)))
                .andExpect(jsonPath("$.lastName",  is(OWNER_LAST_NAME)))
                .andExpect(jsonPath("$.address",   is(OWNER_ADDRESS)))
                .andExpect(jsonPath("$.city",      is(OWNER_CITY)))
                .andExpect(jsonPath("$.telephone", is(OWNER_TELEPHONE)));
    }

    /**
     * Verifica que DELETE /owners/{id} elimina un owner creado previamente
     * Patrón: CREATE → extraer id → DELETE → verificar 200
     */
    @Test
    public void testDeleteOwner() throws Exception {

        String OWNER_FIRST_NAME = "Pedro";
        String OWNER_LAST_NAME  = "Quispe";
        String OWNER_ADDRESS    = "Jr. Huancayo 456";
        String OWNER_CITY       = "Lima";
        String OWNER_TELEPHONE  = "9112233445";

        OwnerDTO newOwnerDTO = OwnerDTO.builder()
                .firstName(OWNER_FIRST_NAME)
                .lastName(OWNER_LAST_NAME)
                .address(OWNER_ADDRESS)
                .city(OWNER_CITY)
                .telephone(OWNER_TELEPHONE)
                .build();

        ResultActions mvcActions = mockMvc.perform(post("/owners")
                        .content(om.writeValueAsString(newOwnerDTO))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        String response = mvcActions.andReturn().getResponse().getContentAsString();

        // Owner.id es Long — leer como Integer en JsonPath y convertir
        Integer id = JsonPath.parse(response).read("$.id");

        mockMvc.perform(delete("/owners/" + id))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que DELETE /owners/1000 retorna 404 cuando el id no existe
     */
    @Test
    public void testDeleteOwnerKO() throws Exception {

        mockMvc.perform(delete("/owners/" + "1000"))
                .andExpect(status().isNotFound());
    }

    /**
     * Verifica el ciclo completo: CREATE → UPDATE → FIND → DELETE
     * Todos los campos del owner son verificados tras la actualización
     */
    @Test
    public void testUpdateOwner() throws Exception {

        String OWNER_FIRST_NAME    = "Luis";
        String OWNER_LAST_NAME     = "Torres";
        String OWNER_ADDRESS       = "Calle Lima 789";
        String OWNER_CITY          = "Arequipa";
        String OWNER_TELEPHONE     = "9001122334";

        String UP_OWNER_FIRST_NAME = "Luis Alberto";
        String UP_OWNER_LAST_NAME  = "Torres Ruiz";
        String UP_OWNER_ADDRESS    = "Av. Bolognesi 1010";
        String UP_OWNER_CITY       = "Cusco";
        String UP_OWNER_TELEPHONE  = "9009988776";

        OwnerDTO newOwnerDTO = OwnerDTO.builder()
                .firstName(OWNER_FIRST_NAME)
                .lastName(OWNER_LAST_NAME)
                .address(OWNER_ADDRESS)
                .city(OWNER_CITY)
                .telephone(OWNER_TELEPHONE)
                .build();

        // CREATE
        ResultActions mvcActions = mockMvc.perform(post("/owners")
                        .content(om.writeValueAsString(newOwnerDTO))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        String response = mvcActions.andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.parse(response).read("$.id");

        // UPDATE
        OwnerDTO upOwnerDTO = OwnerDTO.builder()
                .id(Integer.valueOf(id))
                .firstName(UP_OWNER_FIRST_NAME)
                .lastName(UP_OWNER_LAST_NAME)
                .address(UP_OWNER_ADDRESS)
                .city(UP_OWNER_CITY)
                .telephone(UP_OWNER_TELEPHONE)
                .build();

        mockMvc.perform(put("/owners/" + id)
                        .content(om.writeValueAsString(upOwnerDTO))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // FIND — validar que los cambios se persistieron correctamente
        mockMvc.perform(get("/owners/" + id))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",        is(id)))
                .andExpect(jsonPath("$.firstName", is(UP_OWNER_FIRST_NAME)))
                .andExpect(jsonPath("$.lastName",  is(UP_OWNER_LAST_NAME)))
                .andExpect(jsonPath("$.address",   is(UP_OWNER_ADDRESS)))
                .andExpect(jsonPath("$.city",      is(UP_OWNER_CITY)))
                .andExpect(jsonPath("$.telephone", is(UP_OWNER_TELEPHONE)));

        // DELETE — limpieza de la BD de test
        mockMvc.perform(delete("/owners/" + id))
                .andExpect(status().isOk());
    }
}
