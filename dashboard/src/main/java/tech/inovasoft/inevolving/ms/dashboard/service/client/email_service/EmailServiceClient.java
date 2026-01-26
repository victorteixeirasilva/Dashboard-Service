package tech.inovasoft.inevolving.ms.dashboard.service.client.email_service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import tech.inovasoft.inevolving.ms.dashboard.service.client.email_service.dto.EmailRequest;


@FeignClient(
        name = "email-service",
        url = "http://email-service:8092/ms/email"
//        url = "${inevolving.uri.ms.dashboard}"
)
public interface EmailServiceClient {

    @PostMapping("/{token}")
    ResponseEntity<String> sendEmail(
            @RequestBody EmailRequest request,
            @PathVariable String token
    );

}
