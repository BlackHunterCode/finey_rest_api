# Componentes Core

Este documento descreve os principais componentes do pacote `core` da Hunter Wallet REST API, explicando seu propósito, funcionamento e como utilizá-los corretamente em seu desenvolvimento.

## BaseController

### Descrição

O `BaseController` é uma classe abstrata que fornece funcionalidades comuns para todos os controladores REST da aplicação. Ele implementa operações CRUD padrão e métodos para padronização de respostas HTTP.

### Características

- Classe genérica que aceita dois tipos: `<T>` para o tipo do payload/DTO e `<ID>` para o tipo do identificador
- Métodos para padronização de respostas (ok, created, noContent)
- Endpoints CRUD abstratos que devem ser implementados pelas classes filhas
- Endpoints opcionais para verificação de existência e contagem de recursos

### Como Utilizar

Para criar um novo controlador, estenda a classe `BaseController` e implemente os métodos abstratos:

```java
@RestController
@RequestMapping("/api/v1/users")
public class UserController extends BaseController<UserDTO, Long> {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ResponseEntity<ApiResponse<UserDTO>> findById(@PathVariable("id") Long id) {
        UserDTO user = userService.findById(id);
        return ok(user);
    }

    @Override
    public ResponseEntity<ApiResponse<Iterable<UserDTO>>> findAll() {
        List<UserDTO> users = userService.findAll();
        return ok(users);
    }

    // Implemente os demais métodos abstratos...
}
```

### Métodos de Resposta

O `BaseController` fornece três métodos principais para padronização de respostas:

1. **ok(data)**: Retorna uma resposta com status 200 (OK) e os dados solicitados.
2. **created(data)**: Retorna uma resposta com status 201 (Created) e os dados do recurso criado.
3. **noContent()**: Retorna uma resposta com status 204 (No Content) para operações bem-sucedidas que não retornam dados.

## ApiResponse

### Descrição

O `ApiResponse` é um DTO genérico utilizado para padronizar todas as respostas da API. Ele encapsula o status da operação, o código HTTP e os dados da resposta.

### Estrutura

```java
public class ApiResponse<T> {
    private String status;
    private int statusCode;
    private T data;
}
```

### Como Utilizar

O `ApiResponse` é utilizado automaticamente pelos métodos do `BaseController`. Ao implementar novos endpoints, utilize os métodos de resposta do controlador base:

```java
@GetMapping("/active")
public ResponseEntity<ApiResponse<List<UserDTO>>> findActiveUsers() {
    List<UserDTO> activeUsers = userService.findActiveUsers();
    return ok(activeUsers);
}
```

## Tratamento de Exceções

### BaseExceptionHandler

A classe `BaseExceptionHandler` fornece métodos para criar respostas de erro padronizadas para diferentes tipos de exceções.

#### Métodos Principais

- **badRequest(message, request)**: Cria uma resposta com status 400 (Bad Request).
- **notFound(message, request)**: Cria uma resposta com status 404 (Not Found).
- **unauthorized(message, request)**: Cria uma resposta com status 401 (Unauthorized).
- **forbidden(message, request)**: Cria uma resposta com status 403 (Forbidden).
- **internalServerError(message, request)**: Cria uma resposta com status 500 (Internal Server Error).

### GlobalExceptionHandler

O `GlobalExceptionHandler` estende o `BaseExceptionHandler` e implementa tratadores específicos para diferentes tipos de exceções que podem ocorrer na aplicação.

#### Exceções Tratadas

- **BusinessException**: Exceções de negócio genéricas.
- **EntityNotFoundException**: Quando uma entidade não é encontrada.
- **MethodArgumentNotValidException**: Erros de validação de argumentos.
- **ConstraintViolationException**: Violações de restrições de validação.
- **DataIntegrityViolationException**: Violações de integridade de dados.
- E muitas outras...

### Como Utilizar

Para lançar exceções que serão tratadas adequadamente pelo `GlobalExceptionHandler`:

```java
@Service
public class UserServiceImpl implements UserService {

    @Override
    public UserDTO findById(Long id) {
        return userRepository.findById(id)
            .map(userMapper::toDto)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    @Override
    public void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException("Email already in use");
        }
    }
}
```

### Criando Exceções Personalizadas

Para criar uma exceção específica de domínio, estenda a classe `BusinessException`:

```java
public class UserAccountCreationException extends BusinessException {
    public UserAccountCreationException(String message) {
        super(message);
    }
}
```

## DTOs Comuns

O pacote `core.dto` contém DTOs que são utilizados em toda a aplicação:

### ApiResponse

Já descrito anteriormente, é o DTO padrão para todas as respostas da API.

### PageResponse

Um DTO para encapsular respostas paginadas, contendo:
- Conteúdo da página atual
- Número total de elementos
- Número total de páginas
- Informações de paginação (tamanho da página, número da página, etc.)

## Boas Práticas

1. **Sempre utilize os métodos do BaseController** para criar respostas HTTP, garantindo a padronização.
2. **Lance exceções apropriadas** para diferentes situações de erro, permitindo que o `GlobalExceptionHandler` as trate adequadamente.
3. **Crie exceções específicas de domínio** quando necessário, estendendo `BusinessException`.
4. **Utilize o ApiResponse** para todas as respostas da API, mesmo em endpoints personalizados.
5. **Documente adequadamente** seus controladores e serviços, seguindo o padrão estabelecido.

---

© 2025 Black Hunter - Todos os Direitos Reservados.
