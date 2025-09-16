# Arquitetura e Organização

## Visão Geral da Arquitetura

A Hunter Wallet REST API segue uma arquitetura em camadas baseada nos princípios de Clean Architecture e Domain-Driven Design (DDD). A aplicação é estruturada para promover a separação de responsabilidades, facilitar a manutenção e permitir a evolução independente dos diferentes componentes.

### Camadas Principais

1. **API / Controllers**: Responsável por receber requisições HTTP, validar entradas e delegar o processamento para a camada de serviço.
2. **Serviços**: Contém a lógica de negócio e orquestra operações entre diferentes entidades e repositórios.
3. **Repositórios**: Abstrai o acesso a dados e provê métodos para persistência e recuperação de entidades.
4. **Entidades**: Representa os objetos de domínio com suas regras e comportamentos.
5. **DTOs**: Objetos de transferência de dados utilizados para comunicação entre camadas e com clientes externos.

## Estrutura de Pacotes

A aplicação segue uma estrutura de pacotes organizada por domínio e funcionalidade:

```
br.com.blackhunter.hunter_wallet.rest_api
├── core                    # Componentes compartilhados entre domínios
│   ├── controller         # Classes base para controladores
│   ├── dto                # DTOs compartilhados
│   ├── exception          # Exceções globais
│   └── handler            # Tratamento global de exceções
├── config                  # Configurações da aplicação
├── security                # Configurações e componentes de segurança
└── [domínios]              # Pacotes específicos de cada domínio
    ├── controller         # Controladores REST
    ├── dto                # DTOs específicos do domínio
    ├── entity             # Entidades JPA
    ├── exception          # Exceções específicas do domínio
    ├── mapper             # Conversores entre entidades e DTOs
    ├── repository         # Interfaces de repositório
    └── service            # Serviços com lógica de negócio
```

### Pacote Core

O pacote `core` contém componentes reutilizáveis que são compartilhados entre todos os domínios da aplicação. Estes componentes fornecem funcionalidades comuns e estabelecem padrões para o restante do código.

### Pacotes de Domínio

Cada domínio de negócio possui seu próprio pacote com uma estrutura interna similar, seguindo os princípios de DDD. Isso permite que cada domínio evolua de forma independente, mantendo a coesão e minimizando o acoplamento.

## Fluxo de Requisições

1. **Recebimento da Requisição**: O controlador recebe a requisição HTTP.
2. **Validação**: Os dados de entrada são validados.
3. **Conversão**: DTOs são convertidos para entidades ou objetos de domínio.
4. **Processamento**: O serviço processa a requisição aplicando a lógica de negócio.
5. **Acesso a Dados**: Os repositórios são utilizados para persistir ou recuperar dados.
6. **Resposta**: O resultado é convertido para DTO e retornado como resposta HTTP.

## Tratamento de Erros

A aplicação utiliza um mecanismo centralizado de tratamento de exceções através do `GlobalExceptionHandler`, que captura exceções lançadas durante o processamento das requisições e as converte em respostas HTTP padronizadas.

## Convenções de Nomenclatura

- **Pacotes**: Nomes em minúsculas, seguindo a convenção Java.
- **Classes**: Nomes em PascalCase, sufixados de acordo com sua função (Controller, Service, Repository, etc.).
- **Métodos**: Nomes em camelCase, começando com verbos que indicam ação.
- **Variáveis**: Nomes em camelCase, descritivos e significativos.

## Documentação

Cada componente da aplicação deve ser documentado seguindo o padrão Javadoc, com descrições claras de sua função, parâmetros e comportamento. Os pacotes possuem arquivos `package-info.java` que descrevem seu propósito e conteúdo.

---

© 2025 Black Hunter - Todos os Direitos Reservados.
