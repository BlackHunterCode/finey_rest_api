package br.com.blackhunter.hunter_wallet.rest_api.core.trace.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class StackTraceExceptionData {
    private UUID traceId;
    private String exceptionClass;
    private String message;
    private LocalDateTime timestamp;
    private List<StackTraceElementData> stackTraceElements;
    private String methodName;
    private String className;

    @Data
    public static class StackTraceElementData {
        private String declaringClass;
        private String methodName;
        private String fileName;
        private int lineNumber;

        public StackTraceElementData(StackTraceElement element) {
            this.declaringClass = element.getClassName();
            this.methodName = element.getMethodName();
            this.fileName = element.getFileName();
            this.lineNumber = element.getLineNumber();
        }
    }

    // Construtor a partir de uma exceção
    public StackTraceExceptionData(Exception exception) {
        this.traceId = UUID.randomUUID();
        this.exceptionClass = exception.getClass().getName();
        this.message = exception.getMessage();
        this.timestamp = LocalDateTime.now();

        if (exception.getStackTrace() != null && exception.getStackTrace().length > 0) {
            this.stackTraceElements = Arrays.stream(exception.getStackTrace())
                    .map(StackTraceElementData::new)
                    .collect(Collectors.toList());
            this.methodName = exception.getStackTrace()[0].getMethodName();
            this.className = exception.getStackTrace()[0].getClassName();
        }
    }

    // Método para formatar o stack trace como string (opcional)
    public String getFormattedStackTrace() {
        if (stackTraceElements == null || stackTraceElements.isEmpty()) {
            return "";
        }

        return stackTraceElements.stream()
                .map(element -> "at " + element.getDeclaringClass() +
                        "." + element.getMethodName() +
                        "(" + element.getFileName() +
                        ":" + element.getLineNumber() + ")")
                .collect(Collectors.joining("\n"));
    }
}