package com.agnello.cadastro.service;

import com.agnello.cadastro.model.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    @Value("${notification.service.url}")
    private String notificationServiceUrl;

    private final RestTemplate restTemplate;
    private final ApiKeyService apiKeyService;

    public void enviarNotificaoBemVindo(String nome, String email) {
        try {
            NotificationRequest request = new NotificationRequest(nome, email);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-API-KEY", apiKeyService.getValidApiKey());

            HttpEntity<NotificationRequest> entity = new HttpEntity<>(request, headers);

            log.info("Enviando notificação para: {} - URL: {}", email, notificationServiceUrl);

            restTemplate.exchange(
                notificationServiceUrl,
                HttpMethod.POST,
                entity,
                String.class
            );

            log.info("Notificação enviada com sucesso para: {}", email);

        } catch (Exception e) {
            log.error("Erro ao enviar notificação para {}: {}", email, e.getMessage());
            // Não vamos falhar o cadastro por causa da notificação
            // Apenas logamos o erro
        }
    }
}
