# Documentação Completa da Arquitetura Finey REST API
## Visão Geral
A aplicação Finey REST API é uma API financeira desenvolvida em Spring Boot que segue princípios de Domain-Driven Design (DDD) e Clean Architecture . O sistema é responsável por gerenciar dados financeiros de usuários, integrações bancárias, análises financeiras e projeções de saldo.

## Estrutura de Pacotes e Organização
### Estrutura Principal
```
br.com.blackhunter.finey.rest/
├── auth/                    # 
Módulo de Autenticação
│   ├── controller/          # 
Controllers de autenticação
│   ├── service/             # 
Serviços de autenticação
│   └── util/                # 
Utilitários (JWT, Criptografia)
├── client/                  # 
Módulo de Cliente
│   ├── controller/          # 
Controllers de cliente
│   ├── dto/                 # 
DTOs de cliente
│   ├── entity/              # 
Entidades de cliente
│   └── service/             # 
Serviços de cliente
├── config/                  # 
Configurações da Aplicação
│   ├── filter/              # 
Filtros de segurança
│   └── SecurityConfig.java  # 
Configuração de segurança
├── core/                    # 
Núcleo da Aplicação
│   ├── dto/                 # 
DTOs compartilhados
│   ├── exception/           # 
Exceções customizadas
│   ├── handler/             # 
Tratamento global de exceções
│   ├── trace/               # 
Rastreamento de erros
│   └── util/                # 
Utilitários gerais
├── finance/                 # 
Módulo Financeiro (Domínio 
Principal)
│   ├── analysis/            # 
Análises financeiras
│   │   ├── controller/      # 
Controllers de análise
│   │   ├── dto/             # 
DTOs de análise
│   │   └── service/         # 
Serviços de análise
│   ├── calc/                # 
Serviços de cálculo
│   │   └── service/         # 
Serviços especializados em cálculos
│   └── transaction/         # 
Transações financeiras
│       ├── entity/          # 
Entidades de transação
│       └── enums/           # 
Enumerações de transação
├── infrastructure/          # 
Infraestrutura
│   └── database/            # 
Configurações de banco
├── integrations/            # 
Integrações Externas
│   ├── financial_integrator/ # 
Integrador financeiro
│   └── pluggy/              # 
Integração com Pluggy
├── screens_mobile/          # 
Telas Mobile
│   ├── controller/          # 
Controllers para mobile
│   ├── dto/                 # 
DTOs para mobile
│   └── service/             # 
Serviços para mobile
├── useraccount/             # 
Módulo de Conta de Usuário
│   ├── entity/              # 
Entidades de usuário
│   ├── enums/               # 
Enumerações de usuário
│   ├── exception/           # 
Exceções de usuário
│   └── service/             # 
Serviços de usuário
└── util/                    # 
Utilitários Globais
```
## Padrões Arquiteturais
### 1. Domain-Driven Design (DDD) Agregados e Entidades
- UserAccountEntity : Agregado raiz do domínio de usuário
- TransactionEntity : Entidade de transação financeira
- PluggyItemEntity : Entidade de integração bancária Bounded Contexts
- Finance : Contexto financeiro (transações, análises, cálculos)
- UserAccount : Contexto de usuário (contas, perfis)
- Auth : Contexto de autenticação (JWT, segurança)
- Integrations : Contexto de integrações externas
### 2. Clean Architecture Camadas
1. 1.
   Controllers (Interface Layer): Recebem requisições HTTP
2. 2.
   Services (Application Layer): Lógica de aplicação
3. 3.
   Entities (Domain Layer): Regras de negócio
4. 4.
   Infrastructure (Infrastructure Layer): Banco de dados, integrações
### 3. Padrões de Design Service Layer Pattern
- AnalysisService : Orquestra análises financeiras
- FinancialSummaryCalcService : Cálculos de resumo financeiro
- BalanceProjectionCalcService : Projeções de saldo
- ExpensesCategoriesCalcService : Categorização de despesas Repository Pattern
- Implementado através do Spring Data JPA
- Entidades mapeadas com anotações JPA Strategy Pattern
- FinancialIntegratorManager : Gerencia diferentes integradores
- FinancialIntegrator : Interface para integrações financeiras
## Tratamento de Erros e Exception Handling
### Estrutura de Exception Handling GlobalExceptionHandler
```
@RestControllerAdvice
public class 
GlobalExceptionHandler extends 
BaseExceptionHandler {
    // Tratamento centralizado de 
    exceções
}
``` Tipos de Exceções Tratadas
1. 1.
   BusinessException : Exceções de negócio
2. 2.
   UserAccountCreationException : Erros de criação de conta
3. 3.
   EntityNotFoundException : Entidades não encontradas
4. 4.
   MethodArgumentNotValidException : Validação de argumentos
5. 5.
   ConstraintViolationException : Violação de constraints
6. 6.
   DataIntegrityViolationException : Violação de integridade
7. 7.
   MaxUploadSizeExceededException : Tamanho de upload excedido
8. 8.
   Exception : Tratamento genérico BaseExceptionHandler
Classe abstrata que fornece métodos padronizados:

- badRequest() : HTTP 400
- notFound() : HTTP 404
- unauthorized() : HTTP 401
- forbidden() : HTTP 403
- internalServerError() : HTTP 500
### Estrutura de Resposta Padronizada ApiResponse
```
public class ApiResponse<T> {
    private String 
    status;        // "SUCCESS", 
    "ERROR", etc.
    private int 
    statusCode;       // Código 
    HTTP
    private T 
    data;              // Dados da 
    resposta
    private UUID 
    traceId;        // ID de 
    rastreamento
}
``` Tipos de Status
- SUCCESS : Operação bem-sucedida
- ERROR : Erro genérico
- VALIDATION_ERROR : Erro de validação
- NOT_FOUND : Recurso não encontrado
- UNAUTHORIZED : Não autorizado
- FORBIDDEN : Acesso negado
- SERVER_ERROR : Erro interno do servidor
## Segurança
### Configuração de Segurança SecurityConfig
- JWT Authentication : Autenticação baseada em tokens JWT
- OAuth2 Resource Server : Configuração OAuth2
- Filtros Customizados :
  - ClientValidationFilter : Validação de cliente
  - JwtAuthenticationFilter : Autenticação JWT Endpoints Públicos
- /v1/public/** : Endpoints públicos
- /v1/dev/** : Endpoints de desenvolvimento Criptografia
- CryptUtil : Utilitário para criptografia de dados sensíveis
- BCryptPasswordEncoder : Codificação de senhas
- RSA Keys : Chaves públicas e privadas para JWT
### Proteção de Dados
- Dados Financeiros Criptografados : Todos os valores monetários são criptografados
- IDs de Conta Criptografados : IDs bancários são criptografados
- Trace ID : Rastreamento de requisições para auditoria
## Integrações Externas
### Pluggy Integration
- Finalidade : Integração com instituições financeiras
- Configuração : Client ID, Client Secret, Crypt Secret
- Entidades :
  - PluggyItemEntity : Itens de conexão bancária
  - PluggyAccountDataEntity : Dados de conta bancária
### Redis
- Finalidade : Cache e sessões
- Configuração : Host, porta, senha, timeout
## Padrões de Desenvolvimento
### Convenções de Nomenclatura Entidades
- Sufixo Entity : UserAccountEntity , TransactionEntity
- Tabelas com prefixo hw_ : hw_useraccounts , hw_transactions DTOs
- Sufixo Data para dados: IncomeExpenseData , InvestmentData
- Nomes descritivos: FinancialSummary , ExpensesCategories Serviços
- Sufixo Service : AnalysisService , HomeScreenService
- Sufixo CalcService para cálculos: FinancialSummaryCalcService Controllers
- Sufixo Controller : AnalysisController
- Mapeamento por versão: /v1/finance/analysis
### Anotações Utilizadas Spring Framework
- @Service : Serviços de negócio
- @RestController : Controllers REST
- @RequestMapping : Mapeamento de rotas
- @Value : Injeção de propriedades
- @Configuration : Classes de configuração JPA/Hibernate
- @Entity : Entidades JPA
- @Table : Mapeamento de tabelas
- @Id : Chave primária
- @GeneratedValue : Geração automática de valores
- @ManyToOne , @OneToMany , @OneToOne : Relacionamentos
- @JoinColumn : Colunas de junção
- @Enumerated : Enumerações
- @Lob : Large Objects Lombok
- @Data : Getters, setters, toString, equals, hashCode
- @NoArgsConstructor : Construtor sem argumentos
- @AllArgsConstructor : Construtor com todos os argumentos
- @ToString.Exclude : Exclusão do toString Validação
- @Valid : Validação de objetos
- Constraint annotations para validação de campos
### Documentação Javadoc Padrão de Documentação
- Descrição : Funcionalidade do método
- Funcionalidades : Lista de recursos
- Dados retornados : Estrutura de resposta
- Parâmetros : Descrição detalhada
- Throws : Exceções possíveis
- Guias para QA : Instruções de teste Exemplo de Documentação
```
/**
 * Realiza análise financeira e 
 retorna categorias de despesas.
 * 
 * <p><strong>Funcionalidades:</
 strong></p>
 * <ul>
 *   <li>Categorização automática 
 baseada em palavras-chave</li>
 *   <li>Cálculo de percentuais de 
 participação</li>
 * </ul>
 * 
 * @param bankAccountIds lista de 
 IDs das contas (criptografados)
 * @param periodDate período de 
 análise
 * @return categorias de despesas 
 criptografadas
 * @throws RuntimeException se 
 houver erro no cálculo
 */
```
## Configurações da Aplicação
### application.properties
```
# Aplicação
spring.application.name=rest-api
spring.profiles.active=dev

# JWT
jwt.private.key=classpath:app.key
jwt.public.key=classpath:app.pub

# Pluggy
hunter.secrets.pluggy.client-id=$
{PLUGGY_CLIENT_ID}
hunter.secrets.pluggy.
client-secret=$
{PLUGGY_CLIENT_SECRET}
hunter.secrets.pluggy.
crypt-secret=${PLUGGY_CRYPT_SECRET}

# Redis
spring.data.redis.host=$
{REDIS_HOST:redis}
spring.data.redis.port=$
{REDIS_PORT:6379}
spring.data.redis.password=$
{REDIS_PASSWORD:}

# Logs
logging.level.root=INFO
logging.level.br.com.blackhunter.
finey=DEBUG
```
### Variáveis de Ambiente
- PLUGGY_CLIENT_ID : ID do cliente Pluggy
- PLUGGY_CLIENT_SECRET : Secret do cliente Pluggy
- PLUGGY_CRYPT_SECRET : Chave de criptografia Pluggy
- REDIS_HOST : Host do Redis
- REDIS_PORT : Porta do Redis
- REDIS_PASSWORD : Senha do Redis
## Funcionalidades Principais
### 1. Análise Financeira
- Resumo Financeiro : Receitas, despesas, investimentos
- Projeção de Saldo : Baseada em histórico de 3 meses
- Categorização de Despesas : 8 categorias automáticas
- Saldo da Carteira : Consolidação de contas bancárias
### 2. Cálculos Financeiros
- Médias Históricas : Análise de padrões financeiros
- Projeções Estatísticas : Algoritmos de previsão
- Taxa de Retorno : Cálculo de rentabilidade
- Percentuais de Participação : Distribuição por categoria
### 3. Segurança de Dados
- Criptografia End-to-End : Todos os dados sensíveis
- Autenticação JWT : Tokens seguros
- Rastreamento de Requisições : Trace IDs únicos
- Validação de Cliente : Filtros de segurança
## Boas Práticas Implementadas
### 1. Código Limpo
- Single Responsibility : Cada classe tem uma responsabilidade
- Dependency Injection : Inversão de dependências
- Immutability : DTOs imutáveis quando possível
- Naming Conventions : Nomenclatura clara e consistente
### 2. Testabilidade
- Documentação Javadoc : Guias para QA
- Separação de Responsabilidades : Facilita testes unitários
- Injeção de Dependências : Permite mocking
- Exception Handling : Tratamento previsível de erros
### 3. Manutenibilidade
- Modularização : Separação por domínios
- Configuração Externa : Properties e variáveis de ambiente
- Logging Estruturado : Logs detalhados para debugging
- Versionamento de API : Endpoints versionados
### 4. Performance
- Cache Redis : Cache de dados frequentes
- Lazy Loading : Carregamento sob demanda
- Paginação : Para grandes volumes de dados
- Otimização de Queries : JPA otimizado
## Considerações para Agentes de IA
### 1. Padrões de Implementação
- Sempre seguir a estrutura de pacotes existente
- Utilizar os padrões de nomenclatura estabelecidos
- Implementar tratamento de exceções adequado
- Documentar métodos com Javadoc detalhado
### 2. Segurança
- Criptografar todos os dados financeiros sensíveis
- Utilizar o CryptUtil para criptografia
- Implementar validação de entrada
- Seguir princípios de autenticação e autorização
### 3. Qualidade de Código
- Seguir princípios SOLID
- Implementar testes unitários
- Utilizar anotações Lombok apropriadas
- Manter consistência com o código existente
### 4. Integração
- Utilizar os serviços de cálculo existentes
- Seguir padrões de injeção de dependência
- Implementar logging adequado
- Utilizar a estrutura de resposta ApiResponse