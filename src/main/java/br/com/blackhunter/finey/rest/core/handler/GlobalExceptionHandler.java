/**
 * 2025 © Black Hunter - Todos os Direitos Reservados.
 *
 * Classe protegida - Aletrações somente por CODEOWNERS.
 * */

package br.com.blackhunter.finey.rest.core.handler;

import br.com.blackhunter.finey.rest.core.dto.ApiResponse;
import br.com.blackhunter.finey.rest.core.exception.BusinessException;
import br.com.blackhunter.finey.rest.core.trace.service.StackTraceExceptionService;
import br.com.blackhunter.finey.rest.useraccount.exception.UserAccountCreationException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * <p>Classe <code>GlobalExceptionHandler</code>.</p>
 * <p>Classe responsável pelo tratamento global de exceções da aplicação.</p>
 * <p>Estende a classe base {@link BaseExceptionHandler} para utilizar os métodos padronizados de resposta.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends BaseExceptionHandler {

    private final StackTraceExceptionService stackTraceExceptionService;

    public GlobalExceptionHandler(StackTraceExceptionService stackTraceExceptionService) {
        this.stackTraceExceptionService = stackTraceExceptionService;
    }

    /**
     * Trata exceções de negócio (BusinessException).
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<String>> handleBusinessException(BusinessException ex, WebRequest request) {
        // Save the exception and get the trace ID
        UUID traceId = null;
        try {
            traceId = stackTraceExceptionService.saveException(ex);
        } catch (Exception e) {
            // If saving fails, generate a new trace ID as fallback
            traceId = UUID.randomUUID();
            System.err.println("Failed to save exception, using fallback traceId: " + traceId);
            System.err.println("Error details: " + e.getMessage());
        }
        
        return badRequest(ex.getMessage(), request, traceId);
    }

    /**
     * Trata exceções específicas de criação de conta de usuário.
     */
    @ExceptionHandler(UserAccountCreationException.class)
    public ResponseEntity<ApiResponse<String>> handleUserAccountCreationException(UserAccountCreationException ex, WebRequest request) {
        UUID traceId = null;
        try {
            traceId = stackTraceExceptionService.saveException(ex);
        } catch (Exception e) {
            traceId = UUID.randomUUID();
            System.err.println("Failed to save UserAccountCreationException, using fallback traceId: " + traceId);
            System.err.println("Error details: " + e.getMessage());
        }
        
        return badRequest(ex.getMessage(), request, traceId);
    }

    /**
     * Trata exceções de entidade não encontrada.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        UUID traceId = null;
        try {
            traceId = stackTraceExceptionService.saveException(ex);
        } catch (Exception e) {
            traceId = UUID.randomUUID();
            System.err.println("Failed to save EntityNotFoundException, using fallback traceId: " + traceId);
            System.err.println("Error details: " + e.getMessage());
        }
        
        return notFound(ex.getMessage(), request, traceId);
    }

    /**
     * Trata exceções de validação de argumentos de método.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );

        UUID traceId = stackTraceExceptionService.saveException(ex);
        ApiResponse<Map<String, String>> apiResponse = new ApiResponse<>(
            VALIDATION_ERROR, 
            HttpStatus.BAD_REQUEST.value(), 
            errors,
            traceId
        );

        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Trata exceções de violação de restrições.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleConstraintViolationException(
            ConstraintViolationException ex, WebRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> 
            errors.put(violation.getPropertyPath().toString(), violation.getMessage())
        );

        UUID traceId = stackTraceExceptionService.saveException(ex);
        ApiResponse<Map<String, String>> apiResponse = new ApiResponse<>(
            VALIDATION_ERROR, 
            HttpStatus.BAD_REQUEST.value(), 
            errors,
            traceId
        );
        
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Trata exceções de tipo incompatível de argumentos de método.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<String>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        
        String message = String.format("The parameter '%s' with value '%s' could not be converted to type '%s'", 
            ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName());

        UUID traceId = stackTraceExceptionService.saveException(ex);
        return badRequest(message, request, traceId);
    }

    /**
     * Trata exceções de parâmetros obrigatórios ausentes.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<String>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex, WebRequest request) {
        UUID traceId = stackTraceExceptionService.saveException(ex);
        String message = String.format("The parameter '%s' is required", ex.getParameterName());
        return badRequest(message, request, traceId);
    }

    /**
     * Trata exceções de mensagem HTTP não legível.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<String>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex, WebRequest request) {
        UUID traceId = stackTraceExceptionService.saveException(ex);
        return badRequest("Invalid request format", request, traceId);
    }

    /**
     * Trata exceções de violação de integridade de dados.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<String>> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex, WebRequest request) {
        UUID traceId = stackTraceExceptionService.saveException(ex);
        return badRequest("Data integrity violation", request, traceId);
    }

    /**
     * Trata exceções de tamanho máximo de upload excedido.
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<String>> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException ex, WebRequest request) {
        UUID traceId = stackTraceExceptionService.saveException(ex);
        return badRequest("Maximum upload size exceeded", request, traceId);
    }

    /**
     * Trata exceções de handler não encontrado.
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleNoHandlerFoundException(
            NoHandlerFoundException ex, WebRequest request) {
        
        String message = String.format("No handler found for %s %s", 
            ex.getHttpMethod(), ex.getRequestURL());
        UUID traceId = stackTraceExceptionService.saveException(ex);
        return notFound(message, request, traceId);
    }

    /**
     * Trata todas as outras exceções não mapeadas.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleAllUncaughtException(Exception ex, WebRequest request) {
        UUID traceId = stackTraceExceptionService.saveException(ex);
        return internalServerError("An internal server error occurred", request, traceId);
    }
}
