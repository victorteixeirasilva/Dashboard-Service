package tech.inovasoft.inevolving.ms.dashboard.api;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tech.inovasoft.inevolving.ms.dashboard.api.dto.RequestTaskDTO;
import tech.inovasoft.inevolving.ms.dashboard.service.client.category.dto.StatusObjectiveDTO;
import tech.inovasoft.inevolving.ms.dashboard.service.client.category.dto.request.RequestAddObjectiveToCategoryDTO;
import tech.inovasoft.inevolving.ms.dashboard.service.client.category.dto.request.RequestCategoryDTO;
import tech.inovasoft.inevolving.ms.dashboard.service.client.category.dto.request.RequestCreateObjectiveDTO;

import java.time.LocalDate;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DashboardControllerTest {

    @LocalServerPort
    private int port;

    private static final UUID idUser = UUID.randomUUID();


    @Test
    public void getDashboard_ok() {
        UUID idCategory = addCategory();
        UUID idObjective = addObjective();
        addObjectiveToCategory(idCategory, idObjective);

        for (int i = 1; i <= 5; i++) {
            addTaskToObjective(idObjective);
        }

        for (int i = 1; i <= 2; i++) {
            updateTaskStatusDone(idUser, addTaskToObjective(idObjective));
        }

        for (int i = 1; i <= 2; i++) {
            updateTaskStatusInProgress(idUser, addTaskToObjective(idObjective));
        }

        updateTaskStatusLate(idUser, addTaskToObjective(idObjective));


        RequestSpecification requestSpecification = given()
                .contentType(ContentType.JSON);

        ValidatableResponse response = requestSpecification
                .when()
                .get("http://localhost:"+port+"/ms/dashboard/"+idUser)
                .then();

        response.assertThat()
                .statusCode(200).and()
                .body("idUser", equalTo(idUser.toString())).and()
                .body("categoryDTOList[0].id", equalTo(idCategory.toString())).and()
                .body("categoryDTOList[0].categoryName", equalTo("Category")).and()
                .body("categoryDTOList[0].categoryDescription", equalTo("Description")).and()
                .body("categoryDTOList[0].objectives[0].id", equalTo(idObjective.toString())).and()
                .body("categoryDTOList[0].objectives[0].nameObjective", equalTo("Name Objective")).and()
                .body("categoryDTOList[0].objectives[0].descriptionObjective", equalTo("Description Objective")).and()
                .body("categoryDTOList[0].objectives[0].statusObjective", equalTo(StatusObjectiveDTO.TODO)).and()
                .body("categoryDTOList[0].objectives[0].idUser", equalTo(idUser.toString())).and()
                .body("categoryDTOList[0].objectives[0].totNumberTasks", equalTo(10)).and()
                .body("categoryDTOList[0].objectives[0].numberTasksToDo", equalTo(5)).and()
                .body("categoryDTOList[0].objectives[0].numberTasksDone", equalTo(2)).and()
                .body("categoryDTOList[0].objectives[0].numberTasksInProgress", equalTo(2)).and()
                .body("categoryDTOList[0].objectives[0].numberTasksOverdue", equalTo(1)).and()
                .body("categoryDTOList[0].objectives[0].percentageTasksToDo", equalTo(50)).and()
                .body("categoryDTOList[0].objectives[0].percentageTasksDone", equalTo(20)).and()
                .body("categoryDTOList[0].objectives[0].percentageTasksInProgress", equalTo(20)).and()
                .body("categoryDTOList[0].objectives[0].percentageTasksOverdue", equalTo(10));
    }

    private UUID addTaskToObjective(UUID idObjective) {
        RequestSpecification requestSpecification = given()
                .contentType(ContentType.JSON);

        ValidatableResponse response = requestSpecification
                .body(new RequestTaskDTO(
                        "Name Task",
                        "Description Task",
                        LocalDate.now(),
                        idObjective,
                        idUser
                ))
                .when()
                .post("http://localhost:8085/ms/tasks")
                .then();

        response.assertThat().statusCode(200);

        return UUID.fromString(response.extract().body().jsonPath().get("id"));
    }

    private UUID addObjective() {
        RequestSpecification requestSpecification = given()
                .contentType(ContentType.JSON);

        ValidatableResponse response = requestSpecification
                .body(new RequestCreateObjectiveDTO(
                        "Name Objective",
                        "Description Objective",
                        idUser
                ))
                .when()
                .post("http://localhost:8088/ms/objectives")
                .then();

        response.assertThat().statusCode(200);

        return UUID.fromString(response.extract().body().jsonPath().get("id"));
    }

    private UUID addCategory() {
        RequestSpecification requestSpecification = given()
                .contentType(ContentType.JSON);

        ValidatableResponse response = requestSpecification
                .body(new RequestCategoryDTO("Category", "Description"))
                .when()
                .post("http://localhost:8081/ms/categories/"+idUser)
                .then();

        response.assertThat().statusCode(200);

        return UUID.fromString(response.extract().body().jsonPath().get("id"));
    }

    private void addObjectiveToCategory(UUID idCategory, UUID idObjective) {
        RequestSpecification requestSpecification = given()
                .contentType(ContentType.JSON);

        ValidatableResponse response = requestSpecification
                .body(new RequestAddObjectiveToCategoryDTO(idCategory, idObjective))
                .when()
                .post("http://localhost:8081/ms/categories/objective/"+idUser)
                .then();

        response.assertThat().statusCode(200);
    }

    private void updateTaskStatusInProgress(UUID idUser, UUID idTask) {
        RequestSpecification requestSpecification = given()
                .contentType(ContentType.JSON);

        ValidatableResponse response = requestSpecification
                .when()
                .patch("http://localhost:8085/ms/tasks/status/progress/"+idUser+"/"+idTask)
                .then();

        response.assertThat().statusCode(200);
    }

    private void updateTaskStatusDone(UUID idUser, UUID idTask) {
        RequestSpecification requestSpecification = given()
                .contentType(ContentType.JSON);

        ValidatableResponse response = requestSpecification
                .when()
                .patch("http://localhost:8085/ms/tasks/status/done/"+idUser+"/"+idTask)
                .then();

        response.assertThat().statusCode(200);
    }

    private void updateTaskStatusLate(UUID idUser, UUID idTask) {
        RequestSpecification requestSpecification = given()
                .contentType(ContentType.JSON);

        ValidatableResponse response = requestSpecification
                .when()
                .patch("http://localhost:8085/ms/tasks/status/late/"+idUser+"/"+idTask)
                .then();

        response.assertThat().statusCode(200);
    }

}
