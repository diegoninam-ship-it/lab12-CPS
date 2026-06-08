package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.VetDTO;
import com.tecsup.petclinic.entities.Vet;
import com.tecsup.petclinic.exceptions.VetNotFoundException;
import com.tecsup.petclinic.mapper.VetMapper;
import com.tecsup.petclinic.repositories.VetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class VetServiceImpl implements VetService {

    VetRepository vetRepository;
    VetMapper vetMapper;

    public VetServiceImpl (VetRepository vetRepository, VetMapper vetMapper) {
        this.vetRepository = vetRepository;
        this.vetMapper = vetMapper;
    }

    /**
     *
     * @param vetDTO
     * @return
     */
    @Override
    public VetDTO create(VetDTO vetDTO) {

        Vet newVet = vetRepository.save(vetMapper.mapToEntity(vetDTO));

        return vetMapper.mapToDto(newVet);
    }

    /**
     *
     * @param vetDTO
     * @return
     */
    @Override
    public VetDTO update(VetDTO vetDTO) {

        Vet updatedVet = vetRepository.save(vetMapper.mapToEntity(vetDTO));

        return vetMapper.mapToDto(updatedVet);

    }


    /**
     *
     * @param id
     * @throws VetNotFoundException
     */
    @Override
    public void delete(Integer id) throws VetNotFoundException{

        VetDTO vet = findById(id);

        vetRepository.delete(this.vetMapper.mapToEntity(vet));

    }

    /**
     *
     * @param id
     * @return
     */
    @Override
    public VetDTO findById(Integer id) throws VetNotFoundException {

        Optional<Vet> vet = vetRepository.findById(id);

        if ( !vet.isPresent())
            throw new VetNotFoundException("Record not found...!");

        return this.vetMapper.mapToDto(vet.get());
    }

    /**
     *
     * @param lastName
     * @return
     */
    @Override
    public List<Vet> findByLastName(String lastName) {

        List<Vet> vets = vetRepository.findByLastName(lastName);

        vets.forEach(vet -> log.info("" + vet));

        return vets;
    }

    /**
     *
     * @return
     */
    @Override
    public List<Vet> findAll() {
        //
        return vetRepository.findAll();

    }
}
