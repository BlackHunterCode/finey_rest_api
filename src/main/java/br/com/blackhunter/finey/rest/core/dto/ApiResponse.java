/*
 * @(#)ApiResponse.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

/**
 * Classe protegida - Alterações somente por CODEOWNERS.
 */

package br.com.blackhunter.finey.rest.core.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Classe <code>ApiResponse</code>
 * <p>Classe genérica que padroniza as respostas da API.</p>
 * <p>Todas as respostas da API seguem este formato, contendo um status,
 * código de status HTTP e os dados da resposta.</p>
 * 
 * @param <T> Tipo de dado que será retornado no campo data
 * @author Black Hunter
 * @since 2025
 */
@Data
@NoArgsConstructor
@Builder
public class ApiResponse<T> {
    /**
     * Status da resposta (ex: "SUCCESS", "ERROR", "VALIDATION_ERROR")
     */
    private String status;
    
    /**
     * Código de status HTTP (ex: 200, 400, 404, 500)
     */
    private int statusCode;
    
    /**
     * Dados da resposta, pode ser qualquer tipo de objeto
     */
    private T data;

    private UUID traceId;

    public ApiResponse(String status, int statusCode, T data) {
        this.status = status;
        this.statusCode = statusCode;
        this.data = data;
        this.traceId = null;
    }

    public ApiResponse(String status, int statusCode, T data, UUID traceId) {
        this.status = status;
        this.statusCode = statusCode;
        this.data = data;
        this.traceId = traceId;
    }
}
