package com.notifications.application.mapper;
import com.notifications.application.dto.EmailRequest;
import com.notifications.application.dto.EmailResponse;
import com.notifications.domain.model.EmailNotification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmailMapper {

    EmailNotification toEntity(EmailRequest request);

    EmailResponse toResponse(EmailNotification notification);
}