package org.lucky0111.pettalkmcpserver.domain.dto.match;

import org.lucky0111.pettalkmcpserver.domain.common.ServiceType;

public record UserApplyRequestDTO(
        String trainerName,
        ServiceType serviceType,
        String petType,
        String petBreed,
        Integer petMonthAge,
        String content,
        String imageUrl
) {
}
