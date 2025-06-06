package tech.inovasoft.inevolving.ms.dashboard.unit;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.inovasoft.inevolving.ms.dashboard.service.client.category.CategoryServiceClient;
import tech.inovasoft.inevolving.ms.dashboard.service.client.objective.ObjectiveServiceClient;
import tech.inovasoft.inevolving.ms.dashboard.service.client.task.TaskServiceClient;

@ExtendWith(MockitoExtension.class)
public class DashboardServiceTest {

        @Mock
        private ObjectiveServiceClient objectivesServiceClient;

        @Mock
        private TaskServiceClient taskServiceClient;

        @Mock
        private CategoryServiceClient categoryServiceClient;
}
