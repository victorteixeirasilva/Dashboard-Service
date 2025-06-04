package tech.inovasoft.inevolving.ms.dashboard.domain.dto.response;

import java.util.List;
import java.util.UUID;

public record ResponseDashbordDTO(
        UUID idUser,
        List<ResponseCategoryDTO> categoryDTOList
) {
}
