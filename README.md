Análise da Arquitetura Atual

    Classes que Atuam como Portas de Entrada:

        PasswordLoginHandler 
        RequestMagicLinkHandler
        VerifyMagicLinkHandler
        RegisterUserHandler        
	    ListUsersHandler

Portas de Saída

    Interfaces:
        UserRepository
        MagicLinkRepository
        PasswordHasher
        TokenService
        MailSender


Adaptadores
    Adaptadores de Entrada:
        Classes: AuthController e UserController

    Adaptadores de Saída:
        Persistência: JpaUserRepository e JpaMagicLinkRepository
        Segurança: BcryptPasswordHasher e JwtTokenService
        E-mail: LogMailSender



As entidades de domínio User e MagicLink possuem anotações do JPA. Isso acopla o modelo de domínio diretamente a uma tecnologia de persistência. Os Handlers estão lançando ResponseStatusException, que é uma exceção específica do Spring Web. Isso faz com que a lógica de aplicação conheça detalhes do protocolo HTTP e do framework. Os Controllers dependem diretamente das classes concretas dos Handlers. Não há uma interface que formalize o contrato do caso de uso.

Questões extras:

1. O objetivo central da Arquitetura Hexagonal é:
A) Padronizar camadas de repositório com JPA
B) Centralizar decisões no banco de dados
C) Manter o domínio independente de infraestrutura
D) Reduzir o número de testes de integração

Resposta: C

 

2. Em “Ports & Adapters”, Input Ports representam:
A) Interfaces para recursos externos (DB, mensageria)
B) Interfaces que expõem casos de uso do aplicativo
C) Controllers REST
D) Repositórios de banco de dados

Resposta: B

3. Output Ports são:
A) Interfaces usadas pelo domínio/aplicação para falar com o “mundo externo”
B) Controllers que recebem requisições
C) Entidades JPA
D) Serviços de domínio com regras de negócio

Resposta: A
 

4. Onde não devem aparecer anotações de framework (ex.: @Entity, @RestController)?
A) Adaptadores
B) Domínio
C) Camada Web
D) Persistência

Resposta: B
 

5. Trocar REST por gRPC sem tocar na regra de negócio é possível porque:
A) As entidades do domínio expõem ResponseEntity
B) Controllers chamam diretamente repositórios
C) A UI é um adaptador de entrada plugado em uma porta de entrada
D) O domínio conhece o protocolo gRPC

Resposta: C
 

6. Qual é um anti-padrão em Hexagonal?
A) Portas com linguagem do negócio
B) Domínio anêmico e serviços de aplicação gordos com toda a regra
C) Adaptadores restritos a I/O e mapeamento
D) Separar Input e Output Ports

Resposta: B
 

7. Qual benefício direto da Hexagonal?
A) Menos código cerimonial
B) Substituir infraestrutura com menor impacto no domínio
C) Reduzir número de interfaces
D) Eliminar testes E2E

Resposta: B
 

8. Marque os itens como verdadeiro ou falso, justificando brevemente sua resposta:

[F] Domínio pode depender de JPA desde que use apenas @Entity.

[V] Input Ports expõem casos de uso; Output Ports modelam dependências externas.

[F] Adaptadores conhecem detalhes do domínio e podem validar regras complexas.

[V] Controllers devem falar com o caso de uso (input port), não diretamente com repositórios.

[F] Trocar o banco (JPA → JDBC) deve exigir mudanças extensas no domínio.

 

9. Explique a diferença entre Port e Adapter

Port: É a especificação (uma interface) que pertence ao núcleo da aplicação. Ela define um contrato, um ponto de interação, sem se preocupar com a tecnologia.

Adapter: É a implementação concreta de uma porta. É o código que conecta uma tecnologia específica (um Controller REST, um repositório com JPA, um cliente de mensageria) à porta. Ele faz a "tradução" entre o mundo da tecnologia e o núcleo da aplicação.