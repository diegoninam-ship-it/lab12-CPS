package com.tecsup.petclinic.mapper;

import com.tecsup.petclinic.dtos.VisitDTO;
import com.tecsup.petclinic.entities.Visit;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface VisitMapper {

    VisitMapper INSTANCE = Mappers.getMapper(VisitMapper.class);

    Visit mapToEntity(VisitDTO visitDTO);

    VisitDTO mapToDto(Visit visit);

    List<VisitDTO> mapToDtoList(List<Visit> visits);

    List<Visit> mapToEntityList(List<VisitDTO> visitDTOs);
}
