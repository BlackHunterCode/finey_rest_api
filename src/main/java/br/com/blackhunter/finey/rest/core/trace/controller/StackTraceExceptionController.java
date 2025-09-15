package br.com.blackhunter.finey.rest.core.trace.controller;

import br.com.blackhunter.finey.rest.core.trace.dto.StackTraceExceptionData;
import br.com.blackhunter.finey.rest.core.trace.exception.StackTraceNotFoundException;
import br.com.blackhunter.finey.rest.core.trace.service.StackTraceExceptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 2025 © Black Hunter - Todos os Direitos Reservados.
 *
 * <p>Controlador responsável por gerenciar requisições relacionadas a exceções rastreadas.</p>
 *
 * <p>Classe protegida - Alterações somente por CODEOWNERS.</p>
 *
 * @see StackTraceExceptionService
 * @see StackTraceExceptionData
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/v1/traces")
@RequiredArgsConstructor
public class StackTraceExceptionController {

    private final StackTraceExceptionService stackTraceExceptionService;

    /**
     * Recupera os detalhes de uma exceção rastreada pelo seu ID único.
     *
     * @param traceId O ID único do rastreamento da exceção
     * @return ResponseEntity contendo os detalhes da exceção rastreada ou 404 se não encontrado
     * @since 1.0.0
     */
    @GetMapping("/{traceId}")
    public ResponseEntity<StackTraceExceptionData> getTraceDetails(
            @PathVariable UUID traceId) {
        try {
            log.debug("Buscando detalhes do trace com ID: {}", traceId);
            StackTraceExceptionData traceDetails = stackTraceExceptionService.getExceptionDetails(traceId);
            return ResponseEntity.ok(traceDetails);
        } catch (StackTraceNotFoundException e) {
            log.warn("Trace não encontrado com ID: {}", traceId);
            return ResponseEntity.notFound().build();
        }
    }
}
