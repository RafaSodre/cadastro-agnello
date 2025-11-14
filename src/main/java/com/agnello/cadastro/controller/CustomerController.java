package com.agnello.cadastro.controller;

import com.agnello.cadastro.model.Cliente;
import com.agnello.cadastro.model.ClientEntity;
import com.agnello.cadastro.service.ApiKeyService;
import com.agnello.cadastro.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clientes")
public class CustomerController {

    private final CustomerService customerService;
    private final ApiKeyService apiKeyService;

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody Cliente cliente, @RequestHeader(value = "X-API-KEY", required = false) String apiKey) {
        if (!apiKeyService.isValidApiKey(apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", "API Key inválida ou não fornecida"));
        }

        try {
            ClientEntity clienteSalvo = customerService.salvarCliente(cliente.getNome(), cliente.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(clienteSalvo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro interno do servidor"));
        }
    }

}
