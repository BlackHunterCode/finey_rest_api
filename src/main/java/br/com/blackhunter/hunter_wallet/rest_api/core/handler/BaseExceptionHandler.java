/**
 * 2025 © Black Hunter - Todos os Direitos Reservados.
 *
 * Classe protegida - Alterações somente por CODEOWNERS.
 */

package br.com.blackhunter.hunter_wallet.rest_api.core.handler;

import br.com.blackhunter.hunter_wallet.rest_api.core.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.util.UUID;

/**
 * <p>Classe <code>BaseExceptionHandler</code>.</p>
 * <p>Classe abstrata base para tratamento de exceções da aplicação.</p>
 * <p>Fornece métodos para criar respostas padronizadas para diferentes tipos de erros.</p>
 */
public abstract class BaseExceptionHandler {

    protected static final String ERROR = "error";
    protected static final String VALIDATION_ERROR = "validation_error";
    protected static final String NOT_FOUND = "not_found";
    protected static final String UNAUTHORIZED = "unauthorized";
    protected static final String FORBIDDEN = "forbidden";
    protected static final String SERVER_ERROR = "server_error";

    /**
     * Cria uma resposta de erro com status HTTP 400 (Bad Request).
     *
     * @param message Mensagem de erro
     * @param request Requisição web
     * @return ResponseEntity com ApiResponse contendo detalhes do erro
     */
    protected ResponseEntity<ApiResponse<String>> badRequest(String message, WebRequest request, UUID traceId) {
        return buildErrorResponse(message, HttpStatus.BAD_REQUEST, traceId);
    }

    /**
     * Cria uma resposta de erro com status HTTP 404 (Not Found).
     *
     * @param message Mensagem de erro
     * @param request Requisição web
     * @return ResponseEntity com ApiResponse contendo detalhes do erro
     */
    protected ResponseEntity<ApiResponse<String>> notFound(String message, WebRequest request, UUID traceId) {
        return buildErrorResponse(message, HttpStatus.NOT_FOUND, traceId);
    }

    /**
     * Cria uma resposta de erro com status HTTP 401 (Unauthorized).
     *
     * @param message Mensagem de erro
     * @param request Requisição web
     * @return ResponseEntity com ApiResponse contendo detalhes do erro
     */
    protected ResponseEntity<ApiResponse<String>> unauthorized(String message, WebRequest request,  UUID traceId) {
        return buildErrorResponse(message, HttpStatus.UNAUTHORIZED, traceId);
    }

    /**
     * Cria uma resposta de erro com status HTTP 403 (Forbidden).
     *
     * @param message Mensagem de erro
     * @param request Requisição web
     * @return ResponseEntity com ApiResponse contendo detalhes do erro
     */
    protected ResponseEntity<ApiResponse<String>> forbidden(String message, WebRequest request,  UUID traceId) {
        return buildErrorResponse(message, HttpStatus.FORBIDDEN, traceId);
    }

    /**
     * Cria uma resposta de erro com status HTTP 500 (Internal Server Error).
     *
     * @param message Mensagem de erro
     * @param request Requisição web
     * @return ResponseEntity com ApiResponse contendo detalhes do erro
     */
    protected ResponseEntity<ApiResponse<String>> internalServerError(String message, WebRequest request,  UUID traceId) {
        return buildErrorResponse(message, HttpStatus.INTERNAL_SERVER_ERROR, traceId);
    }

    /**
     * Método utilitário para construir uma resposta de erro padronizada.
     *
     * @param message Mensagem de erro
     * @param status Status HTTP
     * @return ResponseEntity com ApiResponse contendo detalhes do erro
     */
    private ResponseEntity<ApiResponse<String>> buildErrorResponse(String message, HttpStatus status, UUID traceId) {
        String statusType = getStatusType(status);
        ApiResponse<String> apiResponse = new ApiResponse<>(statusType, status.value(), message, traceId);
        return new ResponseEntity<>(apiResponse, status);
    }

    /**
     * Retorna o tipo de status com base no status HTTP.
     *
     * @param status Status HTTP
     * @return Tipo de status
     */
    private String getStatusType(HttpStatus status) {
        if (status == HttpStatus.NOT_FOUND) {
            return NOT_FOUND;
        } else if (status == HttpStatus.UNAUTHORIZED) {
            return UNAUTHORIZED;
        } else if (status == HttpStatus.FORBIDDEN) {
            return FORBIDDEN;
        } else if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
            return SERVER_ERROR;
        } else if (status == HttpStatus.BAD_REQUEST) {
            return ERROR;
        } else {
            return ERROR;
        }
    }
}
