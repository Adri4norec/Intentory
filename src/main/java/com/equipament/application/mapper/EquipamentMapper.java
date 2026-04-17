package com.equipment.application.mapper;

import com.equipment.application.dto.EquipamentRequest;
import com.equipment.application.dto.EquipmentResponse;
import com.equipment.domain.model.Equipament;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EquipamentMapper {

    Equipament toEntity(EquipamentRequest request);

    EquipmentResponse toResponse(Equipament equipment);

    List<EquipmentResponse> toResponseList(List<Equipament> equipments);
}