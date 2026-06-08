package com.tecsup.petclinic.mapper;

import com.tecsup.petclinic.dtos.VetDTO;
import com.tecsup.petclinic.entities.Vet;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValueMappingStrategy =  NullValueMappingStrategy.RETURN_DEFAULT)
public interface VetMapper {

    Vet mapToEntity(VetDTO vetDTO);

    VetDTO mapToDto(Vet vet);

    List<VetDTO> mapToDtoList(List<Vet> vetList);

    List<Vet> mapToEntityList(List<VetDTO> vetDTOList);
}
