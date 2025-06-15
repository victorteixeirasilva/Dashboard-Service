package tech.inovasoft.inevolving.ms.dashboard.service.client.category;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tech.inovasoft.inevolving.ms.dashboard.service.client.category.dto.CategoriesDTO;
import tech.inovasoft.inevolving.ms.dashboard.service.client.category.dto.ObjectivesByCategoryDTO;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;


@FeignClient(
        name = "categories-service",
        url = "http://localhost:8081/ms/categories"
)
public interface CategoryServiceClient {

    @GetMapping("/{idUser}")
    ResponseEntity<CategoriesDTO> getCategories(
            @PathVariable("idUser") UUID idUser
    );

    @GetMapping("/{idUser}/{idCategory}")
    ResponseEntity<ObjectivesByCategoryDTO> getObjectivesByCategory(
            @PathVariable("idUser") UUID idUser,
            @PathVariable("idCategory") UUID idCategory
    );

}
