package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.OwnerDTO;
import com.tecsup.petclinic.exceptions.OwnerNotFoundException;
import com.tecsup.petclinic.mapper.OwnerMapper;
import com.tecsup.petclinic.services.OwnerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class OwnerController {

    private final OwnerService ownerService;
    private final OwnerMapper mapper;

    public OwnerController(OwnerService ownerService, OwnerMapper mapper) {
        this.ownerService = ownerService;
        this.mapper = mapper;
    }

    /**
     * Get all owners
     *
     * @return
     */
    @GetMapping(value = "/owners")
    public ResponseEntity<List<OwnerDTO>> findAllOwners() {

        List<OwnerDTO> ownersDTO = mapper.mapToDtoList(ownerService.findAll());
        log.info("ownersDTO: " + ownersDTO);

        return ResponseEntity.ok(ownersDTO);
    }

    /**
     * Create owner
     *
     * @param ownerDTO
     * @return
     */
    @PostMapping(value = "/owners")
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<OwnerDTO> create(@RequestBody OwnerDTO ownerDTO) {

        OwnerDTO newOwnerDTO = ownerService.create(ownerDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(newOwnerDTO);
    }

    /**
     * Find owner by id
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/owners/{id}")
    ResponseEntity<OwnerDTO> findById(@PathVariable Integer id) {

        OwnerDTO ownerDTO = null;

        try {
            ownerDTO = ownerService.findById(id);

        } catch (OwnerNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ownerDTO);
    }

    /**
     * Update owner
     *
     * @param ownerDTO
     * @param id
     * @return
     */
    @PutMapping(value = "/owners/{id}")
    ResponseEntity<OwnerDTO> update(@RequestBody OwnerDTO ownerDTO, @PathVariable Integer id) {

        OwnerDTO updateOwnerDTO = null;

        try {

            updateOwnerDTO = ownerService.findById(id);

            updateOwnerDTO.setFirstName(ownerDTO.getFirstName());
            updateOwnerDTO.setLastName(ownerDTO.getLastName());
            updateOwnerDTO.setAddress(ownerDTO.getAddress());
            updateOwnerDTO.setCity(ownerDTO.getCity());
            updateOwnerDTO.setTelephone(ownerDTO.getTelephone());

            ownerService.update(updateOwnerDTO);

        } catch (OwnerNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updateOwnerDTO);
    }

    /**
     * Delete owner by id
     *
     * @param id
     */
    @DeleteMapping(value = "/owners/{id}")
    ResponseEntity<String> delete(@PathVariable Integer id) {

        try {
            ownerService.delete(id);
            return ResponseEntity.ok(" Delete ID :" + id);
        } catch (OwnerNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
