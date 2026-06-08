package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.OwnerDTO;
import com.tecsup.petclinic.entities.Owner;
import com.tecsup.petclinic.exceptions.OwnerNotFoundException;

import java.util.List;

public interface OwnerService {

    /**
     * @param ownerDTO
     * @return
     */
    OwnerDTO create(OwnerDTO ownerDTO);

    /**
     * @param ownerDTO
     * @return
     */
    OwnerDTO update(OwnerDTO ownerDTO);

    /**
     * @param id
     * @throws OwnerNotFoundException
     */
    void delete(Integer id) throws OwnerNotFoundException;

    /**
     * @param id
     * @return
     * @throws OwnerNotFoundException
     */
    OwnerDTO findById(Integer id) throws OwnerNotFoundException;

    /**
     * @param lastName
     * @return
     */
    List<Owner> findByLastName(String lastName);

    /**
     * @param city
     * @return
     */
    List<Owner> findByCity(String city);

    /**
     * @return
     */
    List<Owner> findAll();
}
