package com.tecsup.petclinic.repositories;

import com.tecsup.petclinic.entities.PetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad PetType (tabla: types)
 *
 * Al extender JpaRepository y estar anotado con @RepositoryRestResource,
 * Spring Data REST expone automáticamente el endpoint:
 *   GET/POST     → /api/petTypes
 *   GET/PUT/DEL  → /api/petTypes/{id}
 *
 * Ejercicio 5 — Laboratorio 12
 */
@Repository
@RepositoryRestResource(collectionResourceRel = "petTypes", path = "petTypes")
public interface PetTypeRepository extends JpaRepository<PetType, Integer> {
}
