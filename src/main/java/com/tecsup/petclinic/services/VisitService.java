package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.VisitDTO;
import com.tecsup.petclinic.exceptions.VisitNotFoundException;

import java.util.List;

public interface VisitService {

    VisitDTO create(VisitDTO visitDTO);

    VisitDTO update(VisitDTO visitDTO);

    void delete(Integer id) throws VisitNotFoundException;

    VisitDTO findById(Integer id) throws VisitNotFoundException;

    List<VisitDTO> findByPetId(Integer petId);

    List<VisitDTO> findAll();
}
