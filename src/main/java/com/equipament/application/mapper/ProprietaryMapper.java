package com.equipament.application.mapper;

import com.equipament.application.dto.ProprietaryResponse;
import com.equipament.domain.model.Proprietary;
import org.springframework.stereotype.Component;

@Component
public class ProprietaryMapper {

    public ProprietaryResponse toResponse(Proprietary proprietary) {
        if (proprietary == null) {
            return null;
        }

        return new ProprietaryResponse(
                proprietary.getId(),
                proprietary.getName()
        );
    }
}