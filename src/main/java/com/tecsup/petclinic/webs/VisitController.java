package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.VisitDTO;
import com.tecsup.petclinic.exceptions.VisitNotFoundException;
import com.tecsup.petclinic.services.VisitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class VisitController {

    private final VisitService visitService;

    public VisitController(VisitService visitService) {
        this.visitService = visitService;
    }

    @GetMapping(value = "/visits")
    public ResponseEntity<List<VisitDTO>> findAllVisits() {
        List<VisitDTO> visits = visitService.findAll();
        log.info("visits: " + visits);
        return ResponseEntity.ok(visits);
    }

    @PostMapping(value = "/visits")
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<VisitDTO> create(@RequestBody VisitDTO visitDTO) {
        VisitDTO created = visitService.create(visitDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping(value = "/visits/{id}")
    ResponseEntity<VisitDTO> findById(@PathVariable Integer id) {
        try {
            VisitDTO visitDTO = visitService.findById(id);
            return ResponseEntity.ok(visitDTO);
        } catch (VisitNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping(value = "/visits/{id}")
    ResponseEntity<VisitDTO> update(@RequestBody VisitDTO visitDTO, @PathVariable Integer id) {
        try {
            VisitDTO updated = visitService.findById(id);
            updated.setPetId(visitDTO.getPetId());
            updated.setVetId(visitDTO.getVetId());
            updated.setVisitDate(visitDTO.getVisitDate());
            updated.setDescription(visitDTO.getDescription());
            updated.setCost(visitDTO.getCost());
            visitService.update(updated);
            return ResponseEntity.ok(updated);
        } catch (VisitNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(value = "/visits/{id}")
    ResponseEntity<String> delete(@PathVariable Integer id) {
        try {
            visitService.delete(id);
            return ResponseEntity.ok("Delete ID: " + id);
        } catch (VisitNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
