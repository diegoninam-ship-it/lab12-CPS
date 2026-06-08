package com.tecsup.petclinic.mapper;

import com.tecsup.petclinic.dtos.SpecialtyDTO;
import com.tecsup.petclinic.entities.Specialty;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface SpecialtyMapper {

    SpecialtyMapper INSTANCE = Mappers.getMapper(SpecialtyMapper.class);

    Specialty mapToEntity(SpecialtyDTO specialtyDTO);

    SpecialtyDTO mapToDto(Specialty specialty);

    List<SpecialtyDTO> mapToDtoList(List<Specialty> specialties);

    List<Specialty> mapToEntityList(List<SpecialtyDTO> specialtyDTOs);
}
