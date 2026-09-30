# Documento de Testes, Matriz de Rastreabilidade e Análise de Qualidade (SonarQube) — Fase 02

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

## 1. Visão Geral do Plano de Testes

O objetivo deste documento é evidenciar a estratégia de testes automatizados e a verificação estática/dinâmica de qualidade aplicadas ao sistema **PokeSal** na Fase 02. A suíte foi desenvolvida em **Java** utilizando **JUnit 5**, cobrindo todas as regras de negócio base, efeitos de terreno, mecânica de combate por turnos, limites de mochila e os requisitos autorais criados pela equipe.

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

## 3. Matriz de Rastreabilidade de Requisitos

A matriz a seguir relaciona os Requisitos Funcionais do edital e Autorais aos seus respectivos Casos de Teste Automatizados e métodos no código-fonte:

| ID Requisito | Descrição do Requisito | Caso de Teste JUnit | Método / Classe Testada |
| :--- | :--- | :--- | :--- |
| **RF01** | Seleção e Atributos de Pokésal | `testCalculoDanoBoundaryValues` | `PokeSal.java` / `TipoPokesal.java` |
| **RF02** | Matriz Elementar (Fogo, Água, Planta) | `testVantagemElemental` | `CalculadoraDano.calcularEfetividade()` |
| **RF03** | Efeitos de Terreno da UCSal | `testEfeitoTerrenoEstacionamentoUcsal` | `CalculadoraDano.calcularMultiplicadorTerreno()` |
| **RF04** | Sistema de Iniciativa por SPD | `testOrdemDeAtaquePorVelocidade` | `GerenciadorDeBatalha.definirOrdemAtuacao()` |
| **RF05** | Limite de 2 Itens por Batalha e Exceção | `testUsoLimiteDeItensExcedido` | `Mochila.usarItemPorIndice()` |
| **REQ-AUT-01**| Dano de Recuo a cada 4 Ataques | `testDanoRecuo` | `GerenciadorDeBatalha.processarRecuoPorAtaque()` |
| **REQ-AUT-03**| Passiva Defensiva com HP $\le 20\%$ | `testPassivaDefensivaBaixoHp` | `PokeSal.receberDano()` |

---

## 4. Análise Estática de Código e Métricas do SonarQube

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