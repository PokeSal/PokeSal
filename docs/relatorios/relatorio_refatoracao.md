# 📘 Relatório de Refatoração e Inversão de Dependência (Fase 03)

**Instituição:** Universidade Católica do Salvador – UCSal

**Curso:** Análise e Desenvolvimento de Sistemas

**Disciplina:** Testes e Qualidade de Software

**Professor:** Pedro Arthur de Melo Nascimento

**Projeto:** PokéSal — Simulador de Batalha (Fase 03)

**Integrantes da Equipe:**

* Cauã Lopes
* Arivaldo Teixeira
* Ryan Abade
* Pedro Luiz

---

## 1. Visão Geral e Motivação da Refatoração

Na Fase 02 do projeto **PokéSal**, as classes de regra de negócio apresentavam alto acoplamento concreto. A aleatoriedade do combate era gerada internamente via instanciação direta (`new Random()`), os dados de terreno eram estáticos, a auditoria de partidas dependia de chamadas diretas a `System.out.println` e as operações de torneio dependiam de dados fixos. Esse cenário tornava a execução de testes unitários determinísticos inviável, pois não era possível isolar as regras de combate das dependências externas e do relógio do sistema.

Para a Fase 03, a arquitetura do sistema foi integralmente refatorada visando testabilidade, modulação e alinhamento às boas práticas de Engenharia de Software. O objetivo central foi isolar cada componente por meio de **Injeção de Dependências** e **Interfaces de Serviço**, permitindo que a suíte de testes utilize **Mockito** para simular comportamentos externos de forma previsível e sem efeitos colaterais.

---

## 2. Aplicação dos Princípios SOLID e Padrões Arquiteturais

### 2.1 Princípio da Inversão de Dependência (DIP - Dependency Inversion Principle)

O motor do jogo e as regras de torneio deixaram de instanciar suas próprias dependências. Módulos de alto nível (`GerenciadorDeBatalha` e `GerenciadorTorneio`) passaram a depender exclusivamente de **abstrações (interfaces)** e não de implementações concretas:

* **`GeradorAleatorio`:** Interface que substitui o uso direto de `java.util.Random` e `Math.random()`.
* **`ServicoTerreno`:** Interface para consulta dinâmica das condições de terreno com base no relógio.
* **`RegistroBatalha`:** Interface encarregada da auditoria e log dos eventos do turno.
* **`RepositorioResultados`:** Interface de contrato para persistência e compilação estatística do torneio.
* **`java.time.Clock`:** Abstração nativa do Java para injeção e controle temporal de partidas.

Todas as dependências são recebidas estritamente através dos **construtores** das classes motoras, garantindo transparência e facilidade de substituição por instâncias *mockadas* (`@Mock`) durante os testes.

### 2.2 Princípio da Responsabilidade Única (SRP - Single Responsibility Principle)

A refatoração redistribuiu os papéis do sistema para evitar classes "Deus" (*God Classes*):

* **`GerenciadorDeBatalha`:** Responsável estritamente pelo fluxo de combate 1v1 entre dois Pokésais, controle de turnos, alteração de status e aplicação de passivas. A responsabilidade de salvar resultados de torneio foi removida desta classe.
* **`GerenciadorTorneio`:** Responsável exclusivamente pela orquestração do chaveamento entre treinadores, fluxo de menu interativo, concessão de recompensas pós-semifinal (Req Autoral 05) e integração resiliente com o repositório.
* **`CalculadoraDano`:** Componente especialista no cálculo matemático de dano, eficácia elemental e aplicação de efeitos de status.
* **`RegistroBatalhaConsoleImpl`:** Implementação concreta focada apenas em formatar e exibir as mensagens de auditoria no console.

### 2.3 Princípio Aberto/Fechado (OCP - Open/Closed Principle)

A introdução das interfaces permite que o sistema seja estendido com novos comportamentos sem alteração do código-fonte existente nas regras de negócio. Por exemplo, pode-se criar um `RepositorioResultadosBancoDadosImpl` ou `RegistroBatalhaArquivoImpl` e injetá-los no `GerenciadorTorneio` sem modificar uma única linha do motor de combate.

---

## 3. Mapeamento Detalhado das Classes Refatoradas

| Classe Refatorada | Mudanças Efetuadas | Motivação & Benefício Técnico |
| --- | --- | --- |
| **`GerenciadorDeBatalha`** | Removida dependência direta do repositório; adicionado construtor de 6 parâmetros recebendo `GeradorAleatorio`, `ServicoTerreno`, `Clock` e `RegistroBatalha`. | Isola o combate 1v1. Permite testar rodadas completas usando mocks sem necessidade de banco de dados ou entradas I/O reais. |
| **`GerenciadorTorneio`** | Construtor refatorado para receber `RepositorioResultados`, `GeradorAleatorio`, `ServicoTerreno`, `Clock` e `RegistroBatalha`. Adicionado bloco de tratamento de exceção no salvamento do resultado. | Garante a regra de resiliência (Seção 2.3 do edital): falhas de persistência lançam exceção capturada que não anula a partida nem interrompe o torneio. |
| **`CalculadoraDano`** | Passou a receber `GeradorAleatorio` e `ServicoTerreno` via construtor e `Clock` por parâmetro no método `calcularDano`. | Permite mockar acertos críticos (6,25%) e falhas de precisão (dano zero) de forma 100% determinística no JUnit. |
| **`ServicoTerrenoImpl`** | Implementa `ServicoTerreno` utilizando `LocalTime.now(clock)` para determinar se o horário corresponde ao dia (Asfalto Quente) ou noite (Poça de Chuva). | Permite simular mudanças de horário em testes injetando `Clock.fixed(...)`. |
| **`RepositorioResultadosMemoria`** | Criada implementação em memória da interface `RepositorioResultados`, encapsulando a emissão do relatório estatístico (Req Autoral 04). | Separa a lógica de dados do motor de combate e simplifica a verificação de estatísticas acumuladas do torneio. |

---

## 4. Impacto na Testabilidade e Uso do Mockito

A refatoração arquitetural foi o fator determinante para o alcance dos índices de cobertura exigidos pelo edital (85,2% de linhas e 71,7% de branches no pacote `engine`):

1. **Simulação Determinística de Aleatoriedade:** Com a interface `GeradorAleatorio`, os testes conseguem fixar retornos como acertos críticos (`testAcertoCriticoComGeradorMockado`), erros por precisão (`testGolpeErraQuandoPrecisaoFalha`) e sorteios de itens de recompensa pós-semifinal (`testDropRecompensaPosVitoriaComSucesso`).
2. **Isolamento de Falhas de Infraestrutura:** A injeção da interface `RepositorioResultados` permitiu utilizar `doThrow(...).when(repositorioMock).salvarResultado(...)`, comprovando que o `GerenciadorTorneio` é resiliente e mantém a integridade do vencedor em caso de erro na camada de dados.
3. **Auditoria de Sequência Estrita:** O desacoplamento do `RegistroBatalha` permitiu o uso de `InOrder` do Mockito (`testSequenciaDeEventosNoTurno`), garantindo que o ciclo de eventos do turno siga rigidamente a ordem: **Ataque $\rightarrow$ Dano $\rightarrow$ Efeito de Status $\rightarrow$ Efeito de Terreno $\rightarrow$ Uso de Item**.
4. **Inspeção de Objetos Consolidados:** O uso de `ArgumentCaptor<ResultadoBatalha>` permitiu validar minuciosamente se todos os dados consolidados da partida (vencedor, perdedor, número de turnos, dano causado) são transmitidos corretamente ao repositório ao final de cada confronto.