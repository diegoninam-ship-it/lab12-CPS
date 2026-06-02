package com.tecsup.petclinic.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class VisitDTO {

    private Integer id;

    private Integer petId;

    private Integer vetId;

    private String visitDate;

    private String description;

    private Double cost;
}
