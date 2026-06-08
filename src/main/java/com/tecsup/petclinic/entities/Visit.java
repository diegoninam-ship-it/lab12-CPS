package com.tecsup.petclinic.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author jgomezm
 *
 */
@NoArgsConstructor
@Entity(name = "visits")
@Data
public class Visit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "pet_id")
	private Integer petId;

	@Column(name = "vet_id")
	private Integer vetId;

	@Column(name = "visit_date")
	private String visitDate;

	@Column(name = "description")
	private String description;

	@Column(name = "cost")
	private Double cost;

	public Visit(Integer id, Integer petId, Integer vetId, String visitDate, String description, Double cost) {
		this.id = id;
		this.petId = petId;
		this.vetId = vetId;
		this.visitDate = visitDate;
		this.description = description;
		this.cost = cost;
	}
}
