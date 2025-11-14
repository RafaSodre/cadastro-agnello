package com.agnello.cadastro.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.agnello.cadastro.model.ClientEntity;
import com.agnello.cadastro.repository.ClienteRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final ClienteRepository clienteRepository;
    private final NotificationService notificationService;

    public ClientEntity salvarCliente(String nome, String email) {
        // Validar parâmetros
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email não pode ser vazio");
        }

        // Validar formato do email
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Formato de email inválido: " + email);
        }

        // Verificar se já existe um cliente com este email
        if (clienteRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Cliente já cadastrado com o email: " + email);
        }

        // Criar nova entidade
        ClientEntity clientEntity = new ClientEntity();
        clientEntity.setNome(nome.trim());
        clientEntity.setEmail(email.trim().toLowerCase());

        try {
            ClientEntity clienteSalvo = clienteRepository.save(clientEntity);
            log.info("Cliente salvo com sucesso: {} - {}", nome, email);

            // Enviar notificação via serviço externo
            notificationService.enviarNotificaoBemVindo(nome, email);

            return clienteSalvo;
        } catch (Exception e) {
            log.error("Erro ao salvar cliente: {}", e.getMessage());
            throw new IllegalArgumentException("Erro ao salvar cliente: " + e.getMessage());
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }

}
