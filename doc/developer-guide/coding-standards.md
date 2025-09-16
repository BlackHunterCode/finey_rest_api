# Padrões de Código

Este documento descreve os padrões de código que devem ser seguidos no desenvolvimento da Hunter Wallet REST API. Aderir a estes padrões é essencial para manter a consistência, legibilidade e manutenibilidade do código.

## Convenções de Nomenclatura

### Pacotes

- Nomes em minúsculas
- Organizados por domínio e funcionalidade
- Exemplo: `br.com.blackhunter.hunter_wallet.rest_api.useraccount.service`

### Classes

- Nomes em PascalCase (primeira letra maiúscula)
- Substantivos que descrevem o propósito da classe
- Sufixos que indicam a função:
  - `Controller` para controladores REST
  - `Service` para serviços
  - `Repository` para repositórios
  - `Entity` para entidades JPA
  - `DTO` para objetos de transferência de dados
  - `Exception` para exceções
  - `Mapper` para conversores entre entidades e DTOs
- Exemplos: `UserAccountController`, `PaymentService`, `TransactionRepository`

### Interfaces

- Nomes em PascalCase
- Geralmente sem prefixo "I"
- Exemplos: `UserService`, `PaymentProcessor`

### Métodos

- Nomes em camelCase (primeira letra minúscula)
- Verbos ou frases verbais que descrevem a ação
- Exemplos: `findById`, `processPayment`, `validateUserCredentials`

### Variáveis

- Nomes em camelCase
- Descritivos e significativos
- Evitar abreviações não óbvias
- Exemplos: `userId`, `paymentAmount`, `transactionDate`

### Constantes

- Nomes em SNAKE_CASE_MAIÚSCULO
- Exemplos: `MAX_RETRY_ATTEMPTS`, `DEFAULT_PAGE_SIZE`

## Documentação

### Cabeçalho de Classe

Todas as classes devem ter um cabeçalho de documentação seguindo este formato:

```java
/**
 * 2025 © Black Hunter - Todos os Direitos Reservados.
 * */

package br.com.blackhunter.hunter_wallet.rest_api.dominio;

/**
 * <p>Classe <code>NomeClasse</code>.</p>
 * <p>Descrição detalhada da classe e seu propósito.</p>
 * <p>Extends: {@link ClassePai}</p>
 * <p>Implements: [{@link Interface1}, {@link Interface2}]</p>
 * */
public class NomeClasse extends ClassePai implements Interface1, Interface2 {
    // Implementação
}
```

### Documentação de Métodos

Todos os métodos públicos e protegidos devem ser documentados:

```java
/**
 * Descrição do que o método faz.
 *
 * @param parametro1 Descrição do parâmetro
 * @param parametro2 Descrição do parâmetro
 * @return Descrição do valor retornado
 * @throws ExcecaoTipo Descrição de quando a exceção é lançada
 */
public TipoRetorno nomeMetodo(TipoParam parametro1, TipoParam parametro2) throws ExcecaoTipo {
    // Implementação
}
```

### Documentação de Pacotes

Cada pacote deve ter um arquivo `package-info.java` que descreve seu propósito:

```java
/**
 * 2025 © Black Hunter - Todos os Direitos Reservados.
 * <p>
 * Este pacote contém [descrição do conteúdo e propósito do pacote].
 * <p>
 * [Informações adicionais sobre o pacote, suas classes e uso].
 *
 * @see br.com.blackhunter.hunter_wallet.rest_api.outropacote
 * @since 1.0.0
 */
package br.com.blackhunter.hunter_wallet.rest_api.pacote;
```

## Organização de Código

### Ordem dos Elementos em uma Classe

1. Campos estáticos
2. Campos de instância
3. Construtores
4. Métodos públicos
5. Métodos protegidos
6. Métodos privados
7. Classes internas

### Comprimento de Linhas e Métodos

- Limite de 100 caracteres por linha
- Métodos devem ser concisos e ter uma única responsabilidade
- Evitar métodos com mais de 30 linhas

## Tratamento de Erros

### Exceções

- Use exceções específicas em vez de genéricas
- Estenda `BusinessException` para exceções de negócio
- Documente as exceções lançadas por cada método
- Forneça mensagens de erro claras e informativas
- Mensagens de erro para usuários finais devem ser em inglês

### Validação

- Use anotações de validação do Bean Validation (JSR 380)
- Valide entradas no início do método
- Implemente validações de negócio nos serviços

## Testes

### Nomenclatura de Testes

- Classes de teste devem ter o sufixo `Test`
- Métodos de teste devem seguir o padrão: `shouldDoSomethingWhenSomethingHappens`

### Estrutura de Testes

- Siga o padrão AAA (Arrange, Act, Assert)
- Use mocks para isolar a unidade sendo testada
- Escreva testes para casos de sucesso e falha

### Cobertura de Testes

- Mínimo de 80% de cobertura de código
- Foco em testar a lógica de negócio
- Teste todos os caminhos de código, incluindo tratamento de erros

## Estilo de Código

### Indentação e Espaçamento

- Use 4 espaços para indentação (não tabs)
- Uma linha em branco entre métodos
- Espaço após palavras-chave e antes de chaves

### Chaves

- Chaves de abertura na mesma linha da declaração
- Chaves de fechamento em uma nova linha

### Comentários

- Evite comentários óbvios
- Use comentários para explicar "por quê", não "o quê"
- Mantenha comentários atualizados com o código

## Versionamento

- Siga o padrão Semantic Versioning (SemVer)
- Documente todas as alterações no CHANGELOG.md
- Use mensagens de commit descritivas

## Idioma

- Comentários de código podem ser em português
- Mensagens de erro para usuários finais devem ser em inglês
- Nomes de variáveis, métodos e classes em inglês

## Ferramentas de Qualidade de Código

- Utilize o SonarQube para análise estática de código
- Configure o checkstyle para verificar o estilo de código
- Use o PMD para detectar problemas comuns

---

© 2025 Black Hunter - Todos os Direitos Reservados.
