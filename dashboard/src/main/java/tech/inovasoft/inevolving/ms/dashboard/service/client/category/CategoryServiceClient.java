package tech.inovasoft.inevolving.ms.dashboard.service.client.category;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tech.inovasoft.inevolving.ms.dashboard.service.client.category.dto.CategoriesDTO;
import tech.inovasoft.inevolving.ms.dashboard.service.client.category.dto.ObjectivesByCategoryDTO;

import java.util.UUID;

@FeignClient(
        name = "objectives-service",
        url = "http://localhost:8081/ms/categories"
)
public interface CategoryServiceClient {

    //TODO: Desenvolver Teste de Integração.
    @GetMapping("/{idUser}")
    ResponseEntity<CategoriesDTO> getCategories(
            @PathVariable("idUser") UUID idUser
    );

    //TODO: Desenvolver Teste de Integração.
    @GetMapping("/{idUser}/{idCategory}")
    ResponseEntity<ObjectivesByCategoryDTO> getObjectivesByCategory(
            @PathVariable("idUser") UUID idUser,
            @PathVariable("idCategory") UUID idCategory
    );

}
