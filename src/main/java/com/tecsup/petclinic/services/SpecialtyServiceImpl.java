package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.SpecialtyDTO;
import com.tecsup.petclinic.entities.Specialty;
import com.tecsup.petclinic.exceptions.SpecialtyNotFoundException;
import com.tecsup.petclinic.mapper.SpecialtyMapper;
import com.tecsup.petclinic.repositories.SpecialtyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository specialtyRepository;
    private final SpecialtyMapper specialtyMapper;

    public SpecialtyServiceImpl(SpecialtyRepository specialtyRepository, SpecialtyMapper specialtyMapper) {
        this.specialtyRepository = specialtyRepository;
        this.specialtyMapper = specialtyMapper;
    }

    @Override
    public SpecialtyDTO create(SpecialtyDTO specialtyDTO) {
        Specialty saved = specialtyRepository.save(specialtyMapper.mapToEntity(specialtyDTO));
        return specialtyMapper.mapToDto(saved);
    }

    @Override
    public SpecialtyDTO update(SpecialtyDTO specialtyDTO) {
        Specialty saved = specialtyRepository.save(specialtyMapper.mapToEntity(specialtyDTO));
        return specialtyMapper.mapToDto(saved);
    }

    @Override
    public void delete(Integer id) throws SpecialtyNotFoundException {
        SpecialtyDTO specialty = findById(id);
        specialtyRepository.delete(specialtyMapper.mapToEntity(specialty));
    }

    @Override
    public SpecialtyDTO findById(Integer id) throws SpecialtyNotFoundException {
        Optional<Specialty> specialty = specialtyRepository.findById(id);
        if (!specialty.isPresent())
            throw new SpecialtyNotFoundException("Record not found...!");
        return specialtyMapper.mapToDto(specialty.get());
    }

    @Override
    public List<SpecialtyDTO> findByName(String name) {
        return specialtyRepository.findByName(name)
                .stream()
                .map(specialtyMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpecialtyDTO> findAll() {
        return specialtyRepository.findAll()
                .stream()
                .map(specialtyMapper::mapToDto)
                .collect(Collectors.toList());
    }
}
