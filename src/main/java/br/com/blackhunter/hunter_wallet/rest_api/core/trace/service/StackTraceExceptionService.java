package br.com.blackhunter.hunter_wallet.rest_api.core.trace.service;

import br.com.blackhunter.hunter_wallet.rest_api.core.trace.dto.StackTraceExceptionData;
import br.com.blackhunter.hunter_wallet.rest_api.core.trace.entity.StackTraceExceptionEntity;
import br.com.blackhunter.hunter_wallet.rest_api.core.trace.exception.StackTraceNotFoundException;
import br.com.blackhunter.hunter_wallet.rest_api.core.trace.repository.StackTraceExceptionRepository;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.UUID;  // Used in method signatures
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;

import org.springframework.transaction.support.TransactionTemplate;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

/**
 * 2025 Black Hunter - Todos os Direitos Reservados.
 *
 * Classe protegida - Alterações somente por CODEOWNERS.
 */
@Service
public class StackTraceExceptionService {
    private final StackTraceExceptionRepository stackTraceExceptionRepository;
    private final PlatformTransactionManager transactionManager;

    @PersistenceContext
    private EntityManager entityManager;

    public StackTraceExceptionService(
            StackTraceExceptionRepository stackTraceExceptionRepository,
            @Qualifier("errorLoggingTransactionManager") PlatformTransactionManager transactionManager) {
        this.stackTraceExceptionRepository = stackTraceExceptionRepository;
        this.transactionManager = transactionManager;
    }

        /**
     * Salva uma exceção no banco de dados em uma nova transação.
     * Este método usa gerenciamento programático de transações para garantir que funcione mesmo quando
     * a transação de chamada estiver marcada para rollback.
     *
     * @param throwable A exceção a ser registrada
     * @return O ID de rastreamento da exceção salva ou um novo UUID caso ocorra falha ao salvar
     */
    /**
     * Salva uma exceção no banco de dados em uma nova transação.
     * Este método usa gerenciamento programático de transações com propagação REQUIRES_NEW
     * para garantir que funcione mesmo quando a transação de chamada estiver marcada para rollback.
     *
     * @param throwable A exceção a ser registrada
     * @return O ID de rastreamento da exceção salva ou um novo UUID caso ocorra falha ao salvar
     */
    public UUID saveException(Throwable throwable) {
        // Gera um novo ID de rastreamento imediatamente
        final UUID traceId = UUID.randomUUID();
        
        // Cria um novo template de transação com propagação REQUIRES_NEW
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setReadOnly(false);
        
        try {
            // Executa em uma nova transação
            return transactionTemplate.execute(status -> {
                try {
                    // Cria a stack trace como string
                    StringWriter sw = new StringWriter();
                    throwable.printStackTrace(new PrintWriter(sw));
                    String stackTraceStr = sw.toString();
                    
                    // Obtém o nome do método e da classe, se disponível
                    String methodName = null;
                    String className = null;
                    if (throwable.getStackTrace().length > 0) {
                        StackTraceElement element = throwable.getStackTrace()[0];
                        methodName = element.getMethodName();
                        className = element.getClassName();
                    }
                    
                    // Cria uma nova entidade usando o padrão builder sem definir o traceId
                    StackTraceExceptionEntity entity = StackTraceExceptionEntity.builder()
                            .exceptionClass(throwable.getClass().getName())
                            .message(throwable.getMessage())
                            .stackTrace(stackTraceStr)
                            .methodName(methodName)
                            .className(className)
                            .timestamp(LocalDateTime.now())
                            .build();
                    
                    // Salva usando o repositório - permite que o Hibernate gere o ID
                    entity = stackTraceExceptionRepository.saveAndFlush(entity);
                    
                    return entity.getTraceId();
                } catch (Exception e) {
                    // Registra o erro mas não falha a transação principal
                    System.err.println("Error saving exception with ID " + traceId + ": " + e.getMessage());
                    status.setRollbackOnly();
                    return traceId; // Still return the trace ID for reference
                } finally {
                    // Garante que limpamos o contexto de persistência
                    entityManager.clear();
                }
            });
        } catch (Exception e) {
            // Se chegamos aqui, houve um erro com a própria transação
            System.err.println("Transaction failed for exception " + traceId + ": " + e.getMessage());
            return traceId; // Return the trace ID even if we couldn't save the exception
        }
    }

    /**
     * Recupera os detalhes de uma exceção pelo seu ID de rastreamento.
     *
     * @param traceId O ID do rastreamento a ser recuperado
     * @return Os detalhes da exceção
     * @throws StackTraceNotFoundException se o rastreamento não for encontrado
     * @throws RuntimeException se ocorrer um erro inesperado
     */
    public StackTraceExceptionData getExceptionDetails(UUID traceId) {
        try {
            return stackTraceExceptionRepository.findById(traceId)
                    .map(this::convertToDto)
                    .orElseThrow(() -> new StackTraceNotFoundException("Stack trace not found with id: " + traceId));
        } catch (StackTraceNotFoundException e) {
            // Relança exceções de não encontrado diretamente
            throw e;
        } catch (Exception e) {
            // Registra o erro completo para depuração
            System.err.println("Error retrieving exception details for traceId: " + traceId);
            e.printStackTrace();
            
            // Inclui a mensagem de erro original na resposta
            throw new RuntimeException("Failed to retrieve exception details: " + e.getMessage(), e);
        }
    }

    /**
     * Converte uma StackTraceExceptionEntity para um DTO StackTraceExceptionData.
     * Trata o parsing da string de stack trace para elementos estruturados.
     * 
     * @param entity A entidade a ser convertida
     * @return O DTO convertido com os elementos de stack trace parseados
     * @since 1.0.0
     */

    private StackTraceExceptionData convertToDto(StackTraceExceptionEntity entity) {
        try {
            StackTraceExceptionData data = new StackTraceExceptionData();
            data.setTraceId(entity.getTraceId());
            data.setExceptionClass(entity.getExceptionClass());
            data.setMessage(entity.getMessage());
            data.setTimestamp(entity.getTimestamp());
            data.setMethodName(entity.getMethodName());
            data.setClassName(entity.getClassName());
            
            // Converte a string de stack trace para objetos StackTraceElementData
            if (entity.getStackTrace() != null && !entity.getStackTrace().isEmpty()) {
                String[] lines = entity.getStackTrace().split("\\r?\\n");
                data.setStackTraceElements(
                    Arrays.stream(lines)
                        .filter(line -> line.trim().startsWith("at "))
                        .map(line -> {
                            try {
                                // Format: at package.class.method(File.java:123)
                                line = line.substring(3); // Remove 'at ' prefix
                                int parenIndex = line.indexOf('(');
                                String methodPart = line.substring(0, parenIndex);
                                String locationPart = line.substring(parenIndex + 1, line.length() - 1);
                                
                                int lastDot = methodPart.lastIndexOf('.');
                                String declaringClass = methodPart.substring(0, lastDot);
                                String methodName = methodPart.substring(lastDot + 1);
                                
                                String[] locationParts = locationPart.split(":");
                                String fileName = locationParts[0];
                                int lineNumber = locationParts.length > 1 ? Integer.parseInt(locationParts[1]) : -1;
                                
                                return new StackTraceExceptionData.StackTraceElementData(
                                    new StackTraceElement(declaringClass, methodName, fileName, lineNumber)
                                );
                            } catch (Exception e) {
                                // If parsing fails, return a basic element with the original line
                                return new StackTraceExceptionData.StackTraceElementData(
                                    new StackTraceElement("Unknown", "<parsing failed>", entity.getStackTrace(), -1)
                                );
                            }
                        })
                        .collect(Collectors.toList())
                );
            }
            return data;
        } catch (Exception e) {
            // If anything goes wrong, return a minimal DTO with the error message
            StackTraceExceptionData errorData = new StackTraceExceptionData();
            errorData.setTraceId(entity.getTraceId());
            errorData.setExceptionClass("Error parsing stack trace");
            errorData.setMessage(e.getMessage());
            errorData.setTimestamp(LocalDateTime.now());
            return errorData;
        }
    }

    // Helper methods for stack trace processing
}
