package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.tecsup.petclinic.dtos.SpecialtyDTO;
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
public class SpecialtyControllerTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testFindAllSpecialties() throws Exception {

        final int ID_FIRST_RECORD = 1;

        this.mockMvc.perform(get("/specialties"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$[0].id", is(ID_FIRST_RECORD)));
    }

    @Test
    public void testFindSpecialtyOK() throws Exception {

        String SPECIALTY_NAME = "radiology";
        String SPECIALTY_OFFICE = "Farewell";
        int OPEN_HOUR = 8;
        int CLOSE_HOUR = 18;

        this.mockMvc.perform(get("/specialties/1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(SPECIALTY_NAME)))
                .andExpect(jsonPath("$.office", is(SPECIALTY_OFFICE)))
                .andExpect(jsonPath("$.openHour", is(OPEN_HOUR)))
                .andExpect(jsonPath("$.closeHour", is(CLOSE_HOUR)));
    }

    @Test
    public void testFindSpecialtyKO() throws Exception {

        mockMvc.perform(get("/specialties/666"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateSpecialty() throws Exception {

        String SPECIALTY_NAME = "cardiology";
        String SPECIALTY_OFFICE = "TestOffice";
        int OPEN_HOUR = 9;
        int CLOSE_HOUR = 17;

        SpecialtyDTO newSpecialty = SpecialtyDTO.builder()
                .name(SPECIALTY_NAME)
                .office(SPECIALTY_OFFICE)
                .openHour(OPEN_HOUR)
                .closeHour(CLOSE_HOUR)
                .build();

        this.mockMvc.perform(post("/specialties")
                        .content(om.writeValueAsString(newSpecialty))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(SPECIALTY_NAME)))
                .andExpect(jsonPath("$.office", is(SPECIALTY_OFFICE)))
                .andExpect(jsonPath("$.openHour", is(OPEN_HOUR)))
                .andExpect(jsonPath("$.closeHour", is(CLOSE_HOUR)));
    }

    @Test
    public void testDeleteSpecialty() throws Exception {

        SpecialtyDTO newSpecialty = SpecialtyDTO.builder()
                .name("deleteSpecialty")
                .office("OfficeDelete")
                .openHour(8)
                .closeHour(16)
                .build();

        ResultActions mvcActions = mockMvc.perform(post("/specialties")
                        .content(om.writeValueAsString(newSpecialty))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        String response = mvcActions.andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.parse(response).read("$.id");

        mockMvc.perform(delete("/specialties/" + id))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteSpecialtyKO() throws Exception {

        mockMvc.perform(delete("/specialties/1000"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateSpecialty() throws Exception {

        SpecialtyDTO newSpecialty = SpecialtyDTO.builder()
                .name("neurology")
                .office("OfficeA")
                .openHour(8)
                .closeHour(14)
                .build();

        // CREATE
        ResultActions mvcActions = mockMvc.perform(post("/specialties")
                        .content(om.writeValueAsString(newSpecialty))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        String response = mvcActions.andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.parse(response).read("$.id");

        // UPDATE
        SpecialtyDTO upSpecialty = SpecialtyDTO.builder()
                .id(id)
                .name("neurology-updated")
                .office("OfficeB")
                .openHour(9)
                .closeHour(18)
                .build();

        mockMvc.perform(put("/specialties/" + id)
                        .content(om.writeValueAsString(upSpecialty))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // FIND to verify update
        mockMvc.perform(get("/specialties/" + id))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.name", is("neurology-updated")))
                .andExpect(jsonPath("$.office", is("OfficeB")))
                .andExpect(jsonPath("$.openHour", is(9)))
                .andExpect(jsonPath("$.closeHour", is(18)));

        // DELETE
        mockMvc.perform(delete("/specialties/" + id))
                .andExpect(status().isOk());
    }
}
