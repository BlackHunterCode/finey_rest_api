# Guias Práticos

Este documento fornece instruções passo a passo para tarefas comuns no desenvolvimento da Hunter Wallet REST API.

## Como Criar um Novo Endpoint

### 1. Crie o DTO

```java
/**
 * 2025 © Black Hunter - Todos os Direitos Reservados.
 *
 * Classe protegida - Alterações somente por CODEOWNERS.
 * */

package br.com.blackhunter.hunter_wallet.rest_api.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>Classe <code>TransactionDTO</code>.</p>
 * <p>DTO para transferência de dados de transações.</p>
 * */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private Long id;
    
    @NotNull(message = "Source account ID is required")
    private Long sourceAccountId;
    
    @NotNull(message = "Destination account ID is required")
    private Long destinationAccountId;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    private LocalDateTime timestamp;
    private String status;
}
```

### 2. Crie o Controlador

```java
/**
 * 2025 © Black Hunter - Todos os Direitos Reservados.
 *
 * Classe protegida - Alterações somente por CODEOWNERS.
 * */

package br.com.blackhunter.hunter_wallet.rest_api.transaction.controller;

import br.com.blackhunter.hunter_wallet.rest_api.core.controller.BaseController;
import br.com.blackhunter.hunter_wallet.rest_api.core.dto.ApiResponse;
import br.com.blackhunter.hunter_wallet.rest_api.transaction.dto.TransactionDTO;
import br.com.blackhunter.hunter_wallet.rest_api.transaction.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>Classe <code>TransactionController</code>.</p>
 * <p>Controlador REST para operações relacionadas a transações.</p>
 * <p>Extends: {@link BaseController}</p>
 * */
@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController extends BaseController<TransactionDTO, Long> {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    public ResponseEntity<ApiResponse<TransactionDTO>> findById(@PathVariable("id") Long id) {
        TransactionDTO transaction = transactionService.findById(id);
        return ok(transaction);
    }

    @Override
    public ResponseEntity<ApiResponse<Iterable<TransactionDTO>>> findAll() {
        List<TransactionDTO> transactions = transactionService.findAll();
        return ok(transactions);
    }

    @Override
    public ResponseEntity<ApiResponse<TransactionDTO>> create(@Valid @RequestBody TransactionDTO payload) {
        TransactionDTO createdTransaction = transactionService.create(payload);
        return created(createdTransaction);
    }

    @Override
    public ResponseEntity<ApiResponse<TransactionDTO>> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody TransactionDTO payload) {
        TransactionDTO updatedTransaction = transactionService.update(id, payload);
        return ok(updatedTransaction);
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("id") Long id) {
        transactionService.delete(id);
        return noContent();
    }

    @Override
    public ResponseEntity<ApiResponse<Boolean>> existsById(@PathVariable("id") Long id) {
        boolean exists = transactionService.existsById(id);
        return ok(exists);
    }

    @Override
    public ResponseEntity<ApiResponse<Long>> count() {
        long count = transactionService.count();
        return ok(count);
    }

    /**
     * Busca transações por status.
     *
     * @param status Status das transações a serem buscadas
     * @return ResponseEntity com ApiResponse contendo a lista de transações
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> findByStatus(@PathVariable String status) {
        List<TransactionDTO> transactions = transactionService.findByStatus(status);
        return ok(transactions);
    }
}
```

### 3. Crie o Serviço

```java
/**
 * 2025 © Black Hunter - Todos os Direitos Reservados.
 *
 * Classe protegida - Alterações somente por CODEOWNERS.
 * */

package br.com.blackhunter.hunter_wallet.rest_api.transaction.service;

import br.com.blackhunter.hunter_wallet.rest_api.transaction.dto.TransactionDTO;
import java.util.List;

/**
 * <p>Interface <code>TransactionService</code>.</p>
 * <p>Serviço para operações relacionadas a transações.</p>
 * */
public interface TransactionService {
    TransactionDTO findById(Long id);
    List<TransactionDTO> findAll();
    TransactionDTO create(TransactionDTO transactionDTO);
    TransactionDTO update(Long id, TransactionDTO transactionDTO);
    void delete(Long id);
    boolean existsById(Long id);
    long count();
    List<TransactionDTO> findByStatus(String status);
}
```

### 4. Implemente o Serviço

```java
/**
 * 2025 © Black Hunter - Todos os Direitos Reservados.
 *
 * Classe protegida - Alterações somente por CODEOWNERS.
 * */

package br.com.blackhunter.hunter_wallet.rest_api.transaction.service;

import br.com.blackhunter.hunter_wallet.rest_api.core.exception.BusinessException;
import br.com.blackhunter.hunter_wallet.rest_api.transaction.dto.TransactionDTO;
import br.com.blackhunter.hunter_wallet.rest_api.transaction.entity.TransactionEntity;
import br.com.blackhunter.hunter_wallet.rest_api.transaction.mapper.TransactionMapper;
import br.com.blackhunter.hunter_wallet.rest_api.transaction.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * <p>Classe <code>TransactionServiceImpl</code>.</p>
 * <p>Implementação do serviço para operações relacionadas a transações.</p>
 * <p>Implements: {@link TransactionService}</p>
 * */
@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Autowired
    public TransactionServiceImpl(
            TransactionRepository transactionRepository,
            TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    @Override
    public TransactionDTO findById(Long id) {
        return transactionRepository.findById(id)
                .map(transactionMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found with id: " + id));
    }

    @Override
    public List<TransactionDTO> findAll() {
        return StreamSupport.stream(transactionRepository.findAll().spliterator(), false)
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TransactionDTO create(TransactionDTO transactionDTO) {
        validateTransaction(transactionDTO);
        
        transactionDTO.setTimestamp(LocalDateTime.now());
        transactionDTO.setStatus("PENDING");
        
        TransactionEntity entity = transactionMapper.toEntity(transactionDTO);
        TransactionEntity savedEntity = transactionRepository.save(entity);
        
        return transactionMapper.toDto(savedEntity);
    }

    @Override
    @Transactional
    public TransactionDTO update(Long id, TransactionDTO transactionDTO) {
        if (!transactionRepository.existsById(id)) {
            throw new EntityNotFoundException("Transaction not found with id: " + id);
        }
        
        transactionDTO.setId(id);
        TransactionEntity entity = transactionMapper.toEntity(transactionDTO);
        TransactionEntity updatedEntity = transactionRepository.save(entity);
        
        return transactionMapper.toDto(updatedEntity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new EntityNotFoundException("Transaction not found with id: " + id);
        }
        
        transactionRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return transactionRepository.existsById(id);
    }

    @Override
    public long count() {
        return transactionRepository.count();
    }

    @Override
    public List<TransactionDTO> findByStatus(String status) {
        return transactionRepository.findByStatus(status).stream()
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());
    }
    
    private void validateTransaction(TransactionDTO transactionDTO) {
        if (transactionDTO.getSourceAccountId().equals(transactionDTO.getDestinationAccountId())) {
            throw new BusinessException("Source and destination accounts cannot be the same");
        }
        
        // Adicione outras validações de negócio conforme necessário
    }
}
```

## Como Implementar um Novo Domínio

### 1. Crie a Estrutura de Pacotes

```
br.com.blackhunter.hunter_wallet.rest_api.newdomain
├── controller
├── dto
├── entity
├── exception
├── mapper
├── repository
└── service
```

### 2. Crie os Arquivos package-info.java

Exemplo para o pacote principal:

```java
/**
 * 2025 © Black Hunter - Todos os Direitos Reservados.
 * <p>
 * Este pacote contém as classes relacionadas ao domínio de [novo domínio].
 * <p>
 * Inclui funcionalidades para gerenciar [descrição das funcionalidades].
 *
 * @since 1.0.0
 */
package br.com.blackhunter.hunter_wallet.rest_api.newdomain;
```

### 3. Crie as Exceções Específicas do Domínio

```java
/**
 * 2025 © Black Hunter - Todos os Direitos Reservados.
 *
 * Classe protegida - Alterações somente por CODEOWNERS.
 * */

package br.com.blackhunter.hunter_wallet.rest_api.newdomain.exception;

import br.com.blackhunter.hunter_wallet.rest_api.core.exception.BusinessException;

/**
 * <p>Classe <code>NewDomainException</code>.</p>
 * <p>Exceção específica para operações relacionadas ao novo domínio.</p>
 * <p>Extends: {@link BusinessException}</p>
 * */
public class NewDomainException extends BusinessException {
    public NewDomainException(String message) {
        super(message);
    }
}
```

### 4. Implemente as Classes do Domínio

Siga os exemplos anteriores para criar:
- Entidades
- DTOs
- Mapeadores
- Repositórios
- Serviços
- Controladores

## Como Lidar com Validações

### 1. Validação de Bean com Anotações

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must have at least 8 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", 
             message = "Password must contain at least one digit, one lowercase, one uppercase, and one special character")
    private String password;
}
```

### 2. Validação de Negócio nos Serviços

```java
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public UserDTO create(UserDTO userDTO) {
        // Validação de negócio
        validateNewUser(userDTO);
        
        // Processamento
        UserEntity entity = userMapper.toEntity(userDTO);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setActive(true);
        
        UserEntity savedEntity = userRepository.save(entity);
        return userMapper.toDto(savedEntity);
    }
    
    private void validateNewUser(UserDTO userDTO) {
        // Verificar se o email já está em uso
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new BusinessException("Email already in use");
        }
        
        // Outras validações específicas de negócio
        if (userDTO.getName().equalsIgnoreCase("admin") && !isAdminCreationAllowed()) {
            throw new BusinessException("Admin creation is restricted");
        }
    }
    
    private boolean isAdminCreationAllowed() {
        // Lógica para verificar se a criação de admin é permitida
        return false;
    }
}
```

### 3. Tratamento de Erros de Validação

O `GlobalExceptionHandler` já está configurado para tratar erros de validação e retornar respostas apropriadas:

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException ex, WebRequest request) {
    
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error -> 
        errors.put(error.getField(), error.getDefaultMessage())
    );
    
    ApiResponse<Map<String, String>> apiResponse = new ApiResponse<>(
        VALIDATION_ERROR, 
        HttpStatus.BAD_REQUEST.value(), 
        errors
    );
    
    return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
}
```

## Como Escrever Testes

### 1. Teste de Unidade para Serviço

```java
/**
 * 2025 © Black Hunter - Todos os Direitos Reservados.
 *
 * Classe protegida - Alterações somente por CODEOWNERS.
 * */

package br.com.blackhunter.hunter_wallet.rest_api.transaction.service;

import br.com.blackhunter.hunter_wallet.rest_api.core.exception.BusinessException;
import br.com.blackhunter.hunter_wallet.rest_api.transaction.dto.TransactionDTO;
import br.com.blackhunter.hunter_wallet.rest_api.transaction.entity.TransactionEntity;
import br.com.blackhunter.hunter_wallet.rest_api.transaction.mapper.TransactionMapper;
import br.com.blackhunter.hunter_wallet.rest_api.transaction.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * <p>Classe <code>TransactionServiceTest</code>.</p>
 * <p>Testes unitários para o serviço de transações.</p>
 * */
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private TransactionDTO transactionDTO;
    private TransactionEntity transactionEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Arrange - Configuração dos objetos de teste
        transactionDTO = new TransactionDTO();
        transactionDTO.setId(1L);
        transactionDTO.setSourceAccountId(100L);
        transactionDTO.setDestinationAccountId(200L);
        transactionDTO.setAmount(new BigDecimal("500.00"));
        transactionDTO.setStatus("PENDING");
        transactionDTO.setTimestamp(LocalDateTime.now());

        transactionEntity = new TransactionEntity();
        transactionEntity.setId(1L);
        transactionEntity.setSourceAccountId(100L);
        transactionEntity.setDestinationAccountId(200L);
        transactionEntity.setAmount(new BigDecimal("500.00"));
        transactionEntity.setStatus("PENDING");
        transactionEntity.setTimestamp(LocalDateTime.now());
    }

    @Test
    void shouldFindTransactionById() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transactionEntity));
        when(transactionMapper.toDto(transactionEntity)).thenReturn(transactionDTO);

        // Act
        TransactionDTO result = transactionService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(transactionRepository).findById(1L);
        verify(transactionMapper).toDto(transactionEntity);
    }

    @Test
    void shouldThrowExceptionWhenTransactionNotFound() {
        // Arrange
        when(transactionRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            transactionService.findById(999L);
        });
        verify(transactionRepository).findById(999L);
    }

    @Test
    void shouldCreateTransaction() {
        // Arrange
        when(transactionMapper.toEntity(any(TransactionDTO.class))).thenReturn(transactionEntity);
        when(transactionRepository.save(any(TransactionEntity.class))).thenReturn(transactionEntity);
        when(transactionMapper.toDto(transactionEntity)).thenReturn(transactionDTO);

        // Act
        TransactionDTO result = transactionService.create(transactionDTO);

        // Assert
        assertNotNull(result);
        assertEquals("PENDING", result.getStatus());
        verify(transactionRepository).save(any(TransactionEntity.class));
    }

    @Test
    void shouldThrowExceptionWhenSourceAndDestinationAccountsAreSame() {
        // Arrange
        TransactionDTO invalidDTO = new TransactionDTO();
        invalidDTO.setSourceAccountId(100L);
        invalidDTO.setDestinationAccountId(100L);
        invalidDTO.setAmount(new BigDecimal("500.00"));

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            transactionService.create(invalidDTO);
        });
        verify(transactionRepository, never()).save(any(TransactionEntity.class));
    }
}
```

### 2. Teste de Integração para Controlador

```java
/**
 * 2025 © Black Hunter - Todos os Direitos Reservados.
 *
 * Classe protegida - Alterações somente por CODEOWNERS.
 * */

package br.com.blackhunter.hunter_wallet.rest_api.transaction.controller;

import br.com.blackhunter.hunter_wallet.rest_api.transaction.dto.TransactionDTO;
import br.com.blackhunter.hunter_wallet.rest_api.transaction.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * <p>Classe <code>TransactionControllerTest</code>.</p>
 * <p>Testes de integração para o controlador de transações.</p>
 * */
@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    private TransactionDTO transactionDTO;

    @BeforeEach
    void setUp() {
        transactionDTO = new TransactionDTO();
        transactionDTO.setId(1L);
        transactionDTO.setSourceAccountId(100L);
        transactionDTO.setDestinationAccountId(200L);
        transactionDTO.setAmount(new BigDecimal("500.00"));
        transactionDTO.setStatus("PENDING");
        transactionDTO.setTimestamp(LocalDateTime.now());
    }

    @Test
    void shouldCreateTransaction() throws Exception {
        // Arrange
        when(transactionService.create(any(TransactionDTO.class))).thenReturn(transactionDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("created")))
                .andExpect(jsonPath("$.statusCode", is(201)))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.sourceAccountId", is(100)))
                .andExpect(jsonPath("$.data.destinationAccountId", is(200)))
                .andExpect(jsonPath("$.data.amount", is(500.00)))
                .andExpect(jsonPath("$.data.status", is("PENDING")));
    }

    @Test
    void shouldGetTransactionById() throws Exception {
        // Arrange
        when(transactionService.findById(1L)).thenReturn(transactionDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.statusCode", is(200)))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.sourceAccountId", is(100)))
                .andExpect(jsonPath("$.data.destinationAccountId", is(200)));
    }

    @Test
    void shouldGetAllTransactions() throws Exception {
        // Arrange
        List<TransactionDTO> transactions = Arrays.asList(transactionDTO);
        when(transactionService.findAll()).thenReturn(transactions);

        // Act & Assert
        mockMvc.perform(get("/api/v1/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.statusCode", is(200)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id", is(1)));
    }

    @Test
    void shouldUpdateTransaction() throws Exception {
        // Arrange
        when(transactionService.update(eq(1L), any(TransactionDTO.class))).thenReturn(transactionDTO);

        // Act & Assert
        mockMvc.perform(put("/api/v1/transactions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.statusCode", is(200)))
                .andExpect(jsonPath("$.data.id", is(1)));
    }

    @Test
    void shouldDeleteTransaction() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/v1/transactions/1"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.status", is("deleted")))
                .andExpect(jsonPath("$.statusCode", is(204)));
    }
}
```

---

© 2025 Black Hunter - Todos os Direitos Reservados.
