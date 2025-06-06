package tech.inovasoft.inevolving.ms.dashboard.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DashboardControllerTest {

    @LocalServerPort
    private int port;

    @Test
    public void getDashboard_ok() {
        //TODO: Desenvolver teste do End-Point
    }

}
