# Documento de Testes, Matriz de Rastreabilidade e Análise de Qualidade — Fases 02 e 03

**Instituição:** Universidade Católica do Salvador – UCSal  
**Curso:** Análise e Desenvolvimento de Sistemas  
**Disciplina:** Testes e Qualidade de Software  
**Professor:** Pedro Arthur de Melo Nascimento  

**Equipe:**
- Cauã Lopes
- Arivaldo Teixeira
- Ryan Abade
- Pedro Luiz

---

## Sumário

- **PARTE I — FASE 02:** Testes JUnit 5, Matriz de Rastreabilidade e Análise Estática (SonarQube)
- **PARTE II — FASE 03:** Testes com Mockito, Matriz de Rastreabilidade Consolidada e Refatoração

---

# PARTE I — FASE 02

> Escopo da Fase 02: suíte JUnit 5 com instâncias concretas (sem mocks), cobrindo regras de negócio base, terreno, combate por turnos, limite de mochila e requisitos autorais 01 e 03, além da análise estática com SonarQube.

## 1. Visão Geral do Plano de Testes

O objetivo desta parte é evidenciar a estratégia de testes automatizados e a verificação estática/dinâmica de qualidade aplicadas ao sistema **PokeSal** na Fase 02. A suíte foi desenvolvida em **Java** utilizando **JUnit 5**, cobrindo todas as regras de negócio base, efeitos de terreno, mecânica de combate por turnos, limites de mochila e os requisitos autorais criados pela equipe.

---

## 2. Casos de Teste Automatizados (JUnit 5)

A suíte principal de testes está localizada em `src/test/java/br/edu/ucsal/pokesal/engine/CalculadoraDanoTest.java` e é composta pelos seguintes casos de teste:

### CT01: `testVantagemElemental`
* **Objetivo:** Validar os multiplicadores da Matriz de Efetividade Elemental (Super Efetivo, Pouco Efetivo e Neutro).
* **Entrada:** Combates entre Fogo vs. Planta, Fogo vs. Água e Fogo vs. Fogo.
* **Resultado Esperado:** Multiplicadores `2.0`, `0.5` e `1.0`.
* **Status:** **APROVADO (100% Sucesso)**

### CT02: `testEfeitoTerrenoEstacionamentoUcsal`
* **Objetivo:** Validar o impacto dos terrenos do Estacionamento da UCSal no dano e regeneração.
* **Entrada:** Asfalto Quente com tipo Fogo, Poça de Chuva com tipo Água e Canteiro Central com tipo Planta.
* **Resultado Esperado:** Bônus de $+15\%$ para Fogo, $+10\%$ para Água e cura de $5\%$ do HP máximo para Planta ao fim do turno.
* **Status:** **APROVADO (100% Sucesso)**

### CT03: `testOrdemDeAtaquePorVelocidade`
* **Objetivo:** Validar a prioridade do primeiro atacante baseada no atributo de Velocidade (SPD).
* **Entrada:** Pokésal com maior SPD vs. Pokésal com menor SPD.
* **Resultado Esperado:** O Pokésal de maior SPD é atribuído como `primeiroAtacante`.
* **Status:** **APROVADO (100% Sucesso)**

### CT04: `testUsoLimiteDeItensExcedido`
* **Objetivo:** Garantir a restrição do consumo de no máximo 2 itens por batalha e o disparo de exceção.
* **Entrada:** Consumo consecutivo de 3 itens na `Mochila`.
* **Resultado Esperado:** Sucesso nos 2 primeiros usos e disparo da exceção `IllegalStateException` no 3º uso.
* **Status:** **APROVADO (100% Sucesso)**

### CT05: `testCalculoDanoBoundaryValues`
* **Objetivo:** Validar os valores limite de ATK, DEF e HP no cálculo de dano.
* **Entrada:** Combinações de menor e maior ATK/DEF e aplicação de dano superior ao HP atual.
* **Resultado Esperado:** Dano respeitando os limites da fórmula e HP truncado em $0$ (nunca negativo).
* **Status:** **APROVADO (100% Sucesso)**

### CT06: `testDanoRecuo` (Requisito Autoral 01)
* **Objetivo:** Validar a penalidade de dano de recuo em sequências de ataques sem pausa para itens.
* **Entrada:** Execução de 4 ataques consecutivos pelo mesmo Pokésal.
* **Resultado Esperado:** Nenhum dano nos 3 primeiros ataques e aplicação de $10\%$ do HP máximo em dano no 4º ataque.
* **Status:** **APROVADO (100% Sucesso)**

### CT07: `testPassivaDefensivaBaixoHp` (Requisito Autoral 03)
* **Objetivo:** Validar a ativação do bônus de defesa quando o HP atinge estado crítico.
* **Entrada:** Redução do HP do Pokésal para valor $\le 20\%$ do HP máximo.
* **Resultado Esperado:** Incremento permanente de $+20\%$ no atributo DEF, ativado apenas uma vez.
* **Status:** **APROVADO (100% Sucesso)**

---

## 3. Matriz de Rastreabilidade de Requisitos (Fase 02)

A matriz a seguir relaciona os Requisitos Funcionais do edital e Autorais aos seus respectivos Casos de Teste Automatizados e métodos no código-fonte:

| ID Requisito | Descrição do Requisito | Caso de Teste JUnit | Método / Classe Testada |
| :--- | :--- | :--- | :--- |
| **RF01** | Seleção e Atributos de Pokésal | `testCalculoDanoBoundaryValues` | `PokeSal.java` / `TipoPokesal.java` |
| **RF02** | Matriz Elementar (Fogo, Água, Planta) | `testVantagemElemental` | `CalculadoraDano.calcularEfetividade()` |
| **RF03** | Efeitos de Terreno da UCSal | `testEfeitoTerrenoEstacionamentoUcsal` | `CalculadoraDano.calcularMultiplicadorTerreno()` |
| **RF04** | Sistema de Iniciativa por SPD | `testOrdemDeAtaquePorVelocidade` | `GerenciadorDeBatalha.definirOrdemAtuacao()` |
| **RF05** | Limite de 2 Itens por Batalha e Exceção | `testUsoLimiteDeItensExcedido` | `Mochila.usarItemPorIndice()` |
| **REQ-AUT-01** | Dano de Recuo a cada 4 Ataques | `testDanoRecuo` | `GerenciadorDeBatalha.processarRecuoPorAtaque()` |
| **REQ-AUT-03** | Passiva Defensiva com HP $\le 20\%$ | `testPassivaDefensivaBaixoHp` | `PokeSal.receberDano()` |

---

## 4. Análise Estática de Código e Métricas do SonarQube (Fase 02)

O projeto foi submetido à análise estática contínua através do **SonarQube Community Server** (executado localmente). As métricas colhidas e evidenciadas na pasta `docs/sonar/` mostram a evolução do código antes e depois dos ajustes de engenharia:

### Comparativo de Métricas do SonarQube (Antes x Depois)

| Métrica | Análise Inicial (Antes) | Análise Final (Depois) | Status / Classificação |
| :--- | :--- | :--- | :--- |
| **Bugs** | 3 Bugs (Nota D) | **0 Bugs (Nota A)** | **Resolvido / Reuso de Gerador Random** |
| **Vulnerabilities** | 0 Vulnerabilidades (Nota A) | **0 Vulnerabilidades (Nota A)** | **Aprovado** |
| **Security Hotspots** | 4 Hotspots (Nota E) | **0 / Marcados como Safe (Nota A)** | **Revisado** |
| **Code Smells** | 60 Code Smells (Nota A) | **60 Code Smells (Nota A)** | **Aprovado (Nível de Manutenibilidade A)** |
| **Duplications** | 0.0% | **0.0%** | **Aprovado** |

### Registro de Bugs Identificados e Solucionados

1. **BUG-01, BUG-02 e BUG-03 (Reliability / Reuso de Instância `Random`):**
   * *Diagnóstico:* O SonarQube identificou a instanciação contínua de `new Random()` dentro de métodos frequentemente chamados em `CalculadoraDano.java` e `GerenciadorDeBatalha.java`.
   * *Resolução:* Refatorado para declarar instâncias reutilizáveis (`private static final Random RANDOM` / `private final Random random`), otimizando o uso de memória e zerando os alertas de confiabilidade.
   * *Evidência:* Imagens arquivadas em `docs/sonar/sonar_overview_depois.png` e `docs/sonar/sonar_issues_bugs_safe.png`.

---
---

# PARTE II — FASE 03

> Escopo da Fase 03: refatoração para Inversão de Dependência (detalhada em `docs/relatorio_refatoracao.md`) e expansão da suíte com **Mockito**, isolando aleatoriedade, terreno/relógio, auditoria e repositório de resultados. Os casos CT01–CT07 da Fase 02 foram mantidos; esta parte acrescenta os casos **CT08 a CT19**.

## 5. Suíte de Testes Obrigatórios com Mockito (Fase 03)

A suíte da Fase 03 foi expandida com o isolamento de dependências via Mockito, dividida nas classes `MecanicasBatalhaTest`, `TorneioRepositorioTest`, `RequisitosAutoraisTest` e `CoberturaJaCoCoTest`:

### CT08: `testAcertoCriticoComGeradorMockado`
* **Objetivo:** Validar o multiplicador de dano crítico (x1.5) quando o gerador de aleatoriedade retorna acerto crítico.
* **Mocks:** `GeradorAleatorio` (retornando chance de crítico verdadeira) e `ServicoTerreno`.
* **Resultado Esperado:** Dano final igual a $DanoBase \times Efetividade \times Terreno \times 1.5$.
* **Status:** **APROVADO (100% Sucesso)**

### CT09: `testGolpeErraQuandoPrecisaoFalha`
* **Objetivo:** Validar que um golpe com falha de precisão resulta em dano zero e mantém o HP do alvo intacto.
* **Mocks:** `GeradorAleatorio` (retornando sorteio de precisão 99 > precisão do Pokésal).
* **Resultado Esperado:** Dano retornado igual a `0` e HP do alvo inalterado.
* **Status:** **APROVADO (100% Sucesso)**

### CT10: `testAsfaltoQuenteApenasDuranteDia`
* **Objetivo:** Validar a variação dinâmica de dano no terreno Asfalto Quente conforme o horário mockado.
* **Mocks:** `ServicoTerreno` e `Clock` (alternando entre turno diurno e noturno).
* **Resultado Esperado:** Dano aplicado durante o dia maior que o dano no mesmo terreno à noite.
* **Status:** **APROVADO (100% Sucesso)**

### CT11: `testSequenciaDeEventosNoTurno`
* **Objetivo:** Garantir que o auditor receba as notificações de turno na ordem estrita do edital.
* **Mocks:** `RegistroBatalha` e `InOrder` do Mockito.
* **Resultado Esperado:** Ordem exata de chamadas: Ataque $\rightarrow$ Dano $\rightarrow$ Efeito de Status $\rightarrow$ Efeito de Terreno $\rightarrow$ Uso de Item.
* **Status:** **APROVADO (100% Sucesso)**

### CT12: `testResultadoSalvoAoFimDaBatalha`
* **Objetivo:** Inspecionar os dados salvos no repositório de estatísticas ao encerrar uma partida.
* **Mocks:** `RepositorioResultados` e `ArgumentCaptor<ResultadoBatalha>`.
* **Resultado Esperado:** `ArgumentCaptor` captura com sucesso os nomes do vencedor, perdedor, Pokésais e turnos decorridos.
* **Status:** **APROVADO (100% Sucesso)**

### CT13: `testFalhaNoRepositorioNaoInvalidaBatalha`
* **Objetivo:** Garantir a resiliência do torneio caso a camada de persistência lance uma exceção.
* **Mocks:** `RepositorioResultados` (configurado com `doThrow(...)`).
* **Resultado Esperado:** A exceção do repositório é capturada/tratada e o vencedor da partida permanece validado.
* **Status:** **APROVADO (100% Sucesso)**

### CT14: `testRepositorioNaoChamadoSeBatalhaNaoTerminou`
* **Objetivo:** Garantir que partidas em andamento ou não finalizadas não salvem resultados precocemente.
* **Mocks:** `RepositorioResultados` (validado com `verify(..., never())`).
* **Resultado Esperado:** Zero chamadas ao método `salvarResultado()`.
* **Status:** **APROVADO (100% Sucesso)**

### CT15: `testChaveamentoTorneioAvancaVencedores`
* **Objetivo:** Validar o avanço de vencedores em um chaveamento eliminatório de 3 confrontos encadeados.
* **Mocks:** `RepositorioResultados` (validado com `verify(..., times(3))`).
* **Resultado Esperado:** Processamento de 2 Semifinais e 1 Grande Final, registrando 3 resultados consolidados.
* **Status:** **APROVADO (100% Sucesso)**

### CT16: `testCompilacaoEstatisticaMVPNoFimDoTorneio` (Req Autoral 04)
* **Objetivo:** Validar o cálculo de MVP (maior dano acumulado), partida mais longa e consumo de suprimentos.
* **Mocks / Componentes:** `RepositorioResultadosMemoria`.
* **Resultado Esperado:** Emissão do relatório compilando o Pokésal MVP e partidas acumuladas sem erros.
* **Status:** **APROVADO (100% Sucesso)**

### CT17: `testEstatisticaNaoGeradaSeTorneioIncompleto` (Req Autoral 04)
* **Objetivo:** Garantir via `verify(..., never())` que a emissão de estatísticas não ocorre em torneios interrompidos.
* **Mocks:** `RepositorioResultados`.
* **Resultado Esperado:** Nenhuma invocação ao relatório estatístico.
* **Status:** **APROVADO (100% Sucesso)**

### CT18: `testDropRecompensaPosVitoriaComSucesso` (Req Autoral 05)
* **Objetivo:** Validar a concessão de item de recompensa pós-semifinal quando a mochila do vencedor possui espaço.
* **Mocks:** `GeradorAleatorio` (retornando sorteio <= 50) e `RegistroBatalha`.
* **Resultado Esperado:** Item (Poção) adicionado à mochila do Pokésal e notificação registrada no auditor.
* **Status:** **APROVADO (100% Sucesso)**

### CT19: `testDropRecompensaIgnoradoMochilaCheia` (Req Autoral 05)
* **Objetivo:** Validar o descarte controlado do item de recompensa quando a mochila do vencedor está com capacidade lotada (3 itens).
* **Mocks:** `GeradorAleatorio` e `RegistroBatalha`.
* **Resultado Esperado:** Mochila mantém o tamanho máximo de 3 itens e o descarte é notificado no auditor.
* **Status:** **APROVADO (100% Sucesso)**

---

## 6. Matriz de Rastreabilidade de Requisitos (Fase 03 — Consolidada)

A matriz da Fase 03 estende a da Fase 02 com as colunas exigidas no edital (Seção 4.6): **Colaboradores Mockados** e **Resultado Esperado / Obtido**. Os requisitos já existentes na Fase 02 (RF01–RF05, REQ-AUT-01 e REQ-AUT-03) são mantidos; os novos itens da Fase 03 são **RF06 a RF12, REQ-AUT-04 e REQ-AUT-05**.

| ID Requisito | Descrição do Requisito | Caso de Teste JUnit | Colaboradores Mockados | Resultado Esperado / Obtido |
| :--- | :--- | :--- | :--- | :--- |
| **RF01** | Seleção e Atributos de Pokésal | `testCalculoDanoBoundaryValues` | Instâncias Concretas | Dano truncado em limites válidos e HP $\ge 0$. (Obtido: Sucesso) |
| **RF02** | Matriz Elementar | `testVantagemElemental` | Instâncias Concretas | Multiplicadores 2.0x, 0.5x e 1.0x aplicados. (Obtido: Sucesso) |
| **RF03 / RF08** | Serviço de Terreno Dinâmico (Dia/Noite) | `testAsfaltoQuenteApenasDuranteDia` | `ServicoTerreno`, `Clock` | Bônus ativo apenas no horário diurno mockado. (Obtido: Sucesso) |
| **RF04** | Sistema de Iniciativa por SPD | `testOrdemDeAtaquePorVelocidade` | Instâncias Concretas | Prioridade atribuída ao de maior SPD. (Obtido: Sucesso) |
| **RF05** | Limite de 2 Itens por Batalha | `testUsoLimiteDeItensExcedido` | Instâncias Concretas | Lança `IllegalStateException` no 3º uso. (Obtido: Sucesso) |
| **RF06** | Golpe Crítico (6,25% chance) | `testAcertoCriticoComGeradorMockado` | `GeradorAleatorio`, `ServicoTerreno` | Aplicação exata do multiplicador 1.5x no dano. (Obtido: Sucesso) |
| **RF07** | Golpe Erra (Precisão Falha) | `testGolpeErraQuandoPrecisaoFalha` | `GeradorAleatorio` | Dano retornado igual a 0 e HP do alvo mantido. (Obtido: Sucesso) |
| **RF09** | Resiliência do Repositório de Torneio | `testFalhaNoRepositorioNaoInvalidaBatalha` | `RepositorioResultados` | Exceção tratada e torneio prossegue normalmente. (Obtido: Sucesso) |
| **RF10** | Salvamento de Resultado e Inspeção | `testResultadoSalvoAoFimDaBatalha` | `RepositorioResultados`, `ArgumentCaptor` | Captura exata do vencedor, perdedor e turnos. (Obtido: Sucesso) |
| **RF11** | Sequência Estrita de Eventos do Turno | `testSequenciaDeEventosNoTurno` | `RegistroBatalha`, `InOrder` | Auditoria chamada na ordem exata do edital. (Obtido: Sucesso) |
| **RF12** | Progressão do Torneio Eliminatório | `testChaveamentoTorneioAvancaVencedores` | `RepositorioResultados` | 3 partidas processadas e registradas no repositório. (Obtido: Sucesso) |
| **REQ-AUT-01** | Dano de Recuo a cada 4 Ataques | `testDanoRecuo` | Instâncias Concretas | Aplicação de 10% do HP em dano no 4º ataque. (Obtido: Sucesso) |
| **REQ-AUT-03** | Passiva Defensiva (HP $\le 20\%$) | `testPassivaDefensivaBaixoHp` | Instâncias Concretas | Incremento de +20% no atributo DEF. (Obtido: Sucesso) |
| **REQ-AUT-04** | Relatório Estatístico do Torneio | `testCompilacaoEstatisticaMVPNoFimDoTorneio` | `RepositorioResultadosMemoria` | Compilação de MVP e totais acumulados. (Obtido: Sucesso) |
| **REQ-AUT-05** | Drop de Recompensa Pós-Semifinal | `testDropRecompensaPosVitoriaComSucesso` | `GeradorAleatorio`, `RegistroBatalha` | Item inserido na mochila (ou descartado se cheia). (Obtido: Sucesso) |

---

## 7. Cobertura de Código (Fase 03)

A refatoração arquitetural da Fase 03 permitiu atingir os índices de cobertura exigidos pelo edital no pacote `engine`: **85,2% de cobertura de linhas** e **71,7% de cobertura de branches**. O detalhamento das mudanças que viabilizaram esses índices está em `docs/relatorio_refatoracao.md`.