package br.com.blackhunter.hunter_wallet.rest_api.core.trace.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "hw_stack_trace_exceptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StackTraceExceptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID traceId;

    @Version
    private Long version = 0L;

    private String exceptionClass;

    @Lob
    private String stackTrace;

    @Lob
    private String message;

    private LocalDateTime timestamp;

    private String methodName;
    private String className;
}
