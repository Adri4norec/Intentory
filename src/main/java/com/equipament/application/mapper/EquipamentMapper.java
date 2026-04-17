package com.equipament.application.mapper;

import com.equipament.application.dto.EquipamentRequest;
import com.equipament.application.dto.EquipamentResponse;
import com.equipament.domain.model.Equipament;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface EquipamentMapper {

    @Mapping(target = "proprietary", ignore = true)
    @Mapping(target = "imageUrls", ignore = true)
    Equipament toEntity(EquipamentRequest request);

    @Mapping(source = "proprietary.name", target = "proprietaryName")

    @Mapping(source = "status.statusType.name", target = "statusName")
    EquipamentResponse toResponse(Equipament equipment);

    List<EquipamentResponse> toResponseList(List<Equipament> equipments);
}