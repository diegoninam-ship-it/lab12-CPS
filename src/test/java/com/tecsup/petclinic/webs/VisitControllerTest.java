package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.tecsup.petclinic.dtos.VisitDTO;
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
public class VisitControllerTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testFindAllVisits() throws Exception {

        final int ID_FIRST_RECORD = 1;

        this.mockMvc.perform(get("/visits"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$[0].id", is(ID_FIRST_RECORD)));
    }

    @Test
    public void testFindVisitOK() throws Exception {

        int PET_ID = 7;
        int VET_ID = 2;
        String VISIT_DATE = "2010-03-04";
        String DESCRIPTION = "rabies shot";

        this.mockMvc.perform(get("/visits/1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.petId", is(PET_ID)))
                .andExpect(jsonPath("$.vetId", is(VET_ID)))
                .andExpect(jsonPath("$.visitDate", is(VISIT_DATE)))
                .andExpect(jsonPath("$.description", is(DESCRIPTION)));
    }

    @Test
    public void testFindVisitKO() throws Exception {

        mockMvc.perform(get("/visits/666"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateVisit() throws Exception {

        int PET_ID = 1;
        int VET_ID = 1;
        String VISIT_DATE = "2024-06-01";
        String DESCRIPTION = "general checkup";
        double COST = 65.00;

        VisitDTO newVisit = VisitDTO.builder()
                .petId(PET_ID)
                .vetId(VET_ID)
                .visitDate(VISIT_DATE)
                .description(DESCRIPTION)
                .cost(COST)
                .build();

        this.mockMvc.perform(post("/visits")
                        .content(om.writeValueAsString(newVisit))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.petId", is(PET_ID)))
                .andExpect(jsonPath("$.vetId", is(VET_ID)))
                .andExpect(jsonPath("$.visitDate", is(VISIT_DATE)))
                .andExpect(jsonPath("$.description", is(DESCRIPTION)));
    }

    @Test
    public void testDeleteVisit() throws Exception {

        VisitDTO newVisit = VisitDTO.builder()
                .petId(2)
                .vetId(1)
                .visitDate("2024-07-10")
                .description("vaccine")
                .cost(50.00)
                .build();

        ResultActions mvcActions = mockMvc.perform(post("/visits")
                        .content(om.writeValueAsString(newVisit))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        String response = mvcActions.andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.parse(response).read("$.id");

        mockMvc.perform(delete("/visits/" + id))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteVisitKO() throws Exception {

        mockMvc.perform(delete("/visits/1000"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateVisit() throws Exception {

        VisitDTO newVisit = VisitDTO.builder()
                .petId(3)
                .vetId(2)
                .visitDate("2024-08-01")
                .description("initial exam")
                .cost(80.00)
                .build();

        // CREATE
        ResultActions mvcActions = mockMvc.perform(post("/visits")
                        .content(om.writeValueAsString(newVisit))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        String response = mvcActions.andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.parse(response).read("$.id");

        // UPDATE
        VisitDTO upVisit = VisitDTO.builder()
                .id(id)
                .petId(3)
                .vetId(3)
                .visitDate("2024-09-15")
                .description("follow-up exam")
                .cost(95.00)
                .build();

        mockMvc.perform(put("/visits/" + id)
                        .content(om.writeValueAsString(upVisit))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // FIND to verify update
        mockMvc.perform(get("/visits/" + id))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.petId", is(3)))
                .andExpect(jsonPath("$.vetId", is(3)))
                .andExpect(jsonPath("$.visitDate", is("2024-09-15")))
                .andExpect(jsonPath("$.description", is("follow-up exam")));

        // DELETE
        mockMvc.perform(delete("/visits/" + id))
                .andExpect(status().isOk());
    }
}
