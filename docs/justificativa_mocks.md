# 📄 Documento de Justificativa dos Mocks (Fase 03)

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

## 1. Visão Geral da Estratégia de Mocks

Na Fase 03 do projeto **PokéSal**, a aplicação da biblioteca **Mockito** permitiu isolar completamente as classes sob teste (*System Under Test - SUT*) de suas dependências externas, infraestrutura de I/O, fatores climáticos/temporais e geradores de aleatoriedade.

A escolha dos colaboradores a serem *mockados* versus aqueles mantidos com instâncias reais seguiu critérios estritos de arquitetura, focando no determinismo dos testes, velocidade de execução e alinhamento com o Princípio da Inversão de Dependência (DIP).

---

## 2. Justificativa das Dependências Mockadas (`@Mock`)

| Colaborador Mockado | Interface / Abstração | Razão Técnica e Justificativa do Isolamento |
| --- | --- | --- |
| **`GeradorAleatorio`** | `GeradorAleatorio` | **Determinismo e Reprodutibilidade:** Impede a variação de resultados em testes que dependem de sorteio de precisão (erro de golpe), acerto crítico ($6,25\%$), chance de aplicar efeito de status ($15\%$) e desempate de iniciativa por velocidade. Sem o mock, os testes unitários seriam frágeis e intermitentes (*flaky tests*). |
| **`ServicoTerreno`** | `ServicoTerreno` | **Isolamento de Estado Externo:** Permite injetar diretamente as condições de terreno (ex.: `ASFALTO_QUENTE`, `POCA_CHUVA` ou `CANTEIRO_CENTRAL`) sem depender da leitura real do relógio do sistema ou das regras de horário do ambiente de execução. |
| **`java.time.Clock`** | `Clock` | **Simulação Temporal Fictícia:** Permite congelar o horário do sistema em horários exatos diurnos (ex.: 12:00 UTC) ou noturnos (ex.: 22:00 UTC) via `Clock.fixed(...)`, testando a regra de negócio do terreno `ASFALTO_QUENTE` ativado apenas durante o dia sem depender do horário real em que a integração contínua (CI) é executada. |
| **`RegistroBatalha`** | `RegistroBatalha` | **Isolamento de Efeitos Colaterais de I/O:** Evita a poluição do console e permite auditar e verificar rigidamente a sequência exata de eventos disparados durante um turno de combate utilizando `InOrder` e `verify(...)`. |
| **`RepositorioResultados`** | `RepositorioResultados` | **Isolamento de Infraestrutura e Testes de Resiliência:** Permite capturar e inspecionar os objetos salvos via `ArgumentCaptor<ResultadoBatalha>` e simular falhas graves de persistência (como `doThrow(...)`), comprovando que o `GerenciadorTorneio` mantém a integridade do torneio mesmo com falhas na camada de dados. |

---

## 3. Justificativa das Classes Mantidas com Instâncias Reais (Não Mockadas)

Conforme recomendado nas boas práticas de teste unitário e explicitado na Seção 4.6 do edital, a equipe optou por **não mockar** entidades de domínio simples, modelos de dados e objetos de valor puro:

* **`PokeSal` (Entidade de Domínio):** É o objeto principal de estado no combate. Mockar o `PokeSal` exigiria simular individualmente métodos simples como `getHpAtual()`, `receberDano()`, `curar()` e `isVivo()`, o que tornaria os testes extremamente complexos sem ganhos reais de isolamento. Instâncias reais da classe garantem que o cálculo das passivas defensivas e estados sejam validados de forma integral.
* **`Mochila` e `Item` (Componentes de Modelo):** Representam estruturas de dados de armazenamento simples (listas encapsuladas). O uso da implementação real garante que os limites de uso (no máximo 2 itens por batalha) e limites de capacidade da mochila sejam validados contra a lógica real do código.
* **`TipoPokesal`, `TipoElemental`, `Terreno` e `EfeitoStatus` (Enums):** São enumerações imutáveis que definem constantes do sistema (atributos base, matrizes elementais e textos formatados). Mockar um `Enum` em Java é desnecessário e prejudicial à legibilidade.
* **`CalculadoraDano` (Classe Sob Teste / Utilitário Interno):** Nos testes do `GerenciadorDeBatalha`, a `CalculadoraDano` é exercitada como componente real para validar a integração matemática entre a injeção do gerador mockado e a aplicação final no HP dos Pokésais.

---

## 4. Recursos Avançados do Mockito Aplicados na Suíte

A suíte de testes da Fase 03 emprega os recursos avançados do ecossistema Mockito de forma intencional e justificada:

### 4.1 `@Mock` e `@InjectMocks`
* **`@Mock`:** Cria dublês de teste (*test doubles*) estritos para todas as dependências externas e interfaces (`GeradorAleatorio`, `ServicoTerreno`, `RegistroBatalha`, `RepositorioResultados`).
* **`@InjectMocks`:** Aplicado diretamente na classe sob teste (como `CalculadoraDano` em `MecanicasBatalhaTest`), delegando ao Mockito a tarefa de resolver e injetar automaticamente os mocks correspondentes via construtor. Isso elimina código repetitivo de instanciação manual (*boilerplate* nos `@BeforeEach`), garantindo aderência formal ao Princípio da Inversão de Dependência (DIP) e comprovando que a arquitetura não possui dependências ocultas.

### 4.2 Retornos em Sequência (`when(...).thenReturn(valor1, valor2, ...)`)
* Exigido formalmente para validar cenários onde sucessivas invocações ao mesmo colaborador produzem comportamentos encadeados diferentes.
* **Aplicação prática (`testRetornosEmSequenciaEAtLeastOnce`):** O método `geradorMock.testarChance(CHANCE_CRITICO)` foi configurado com `.thenReturn(false, true)`. Isso simula com exatidão dois turnos consecutivos de ataque: no primeiro, o ataque ocorre normalmente sem multiplicador de crítico; no segundo turno, o mesmo atacante obtém um golpe crítico, permitindo comparar determinística e matematicamente a progressão de dano ($1.5\times$) entre os dois turnos.

### 4.3 `verify(...)` com `times()`, `never()` e `atLeastOnce()`
* **`times(N)`:** Utilizado para assegurar quantidade exata de interações determinísticas, como exatamente 3 invocações a `salvarResultado(...)` nas 3 partidas de um torneio eliminatório de 4 treinadores (`testChaveamentoTorneioAvancaVencedores`).
* **`never()`:** Utilizado para certificar que métodos críticos **não** são executados sob certas pré-condições. Em `testRepositorioNaoChamadoSeBatalhaNaoTerminou`, comprova que o repositório não persiste dados enquanto a batalha ainda está em andamento (ambos os Pokésais com HP positivo).
* **`atLeastOnce()`:** Utilizado quando o foco da asserção é verificar que o canal de auditoria ou gerador foi acionado pelo menos uma vez durante o fluxo, independentemente da quantidade de turnos ou trocas de golpes necessárias para a conclusão da ação (ex.: `verify(registroBatalhaMock, atLeastOnce()).registrarDano(...)`).

### 4.4 Argument Matchers: Distinção entre `any(...)` e `eq(...)`
Conforme exigido pelo edital, a escolha entre correspondência flexível (*matchers*) e valores exatos é fundamentada no contexto de negócio de cada teste:
* **Quando utilizar `eq(valorExato)`:** Deve ser utilizado quando o valor passado ao colaborador for **relevante e determinante para a regra de negócio** sob validação. Exemplos:
  * Em `registrarAtaque(fogo.getNome(), planta.getNome())` e `registrarDano(eq(planta.getNome()), ...)`, é imprescindível assegurar que quem atacou e quem recebeu dano foi o alvo correto, e não o próprio atacante trocado.
  * Em `testSequenciaDeEventosNoTurno`, `eq("Ash")` garante que o uso de item foi registrado para o treinador correto.
* **Quando utilizar matchers genéricos (`any()`, `anyInt()`, `anyBoolean()`):** Deve ser utilizado quando o colaborador recebe dados que não afetam a asserção principal ou quando qualquer instância válida é aceitável pelo fluxo. Exemplos:
  * `any(ResultadoBatalha.class)` em `testFalhaNoRepositorioNaoInvalidaBatalha`: o teste valida a resiliência a falhas de conexão; o conteúdo específico do registro é irrelevante para provar que a exceção foi capturada e o torneio prosseguiu.
  * `anyInt()` e `anyBoolean()` na verificação da assinatura de `registrarDano`: o valor numérico exato do dano já foi matematicamente validado nos testes de cálculo; na verificação de log, basta comprovar que o evento foi despachado com a tipagem esperada.

### 4.5 `InOrder` (Verificação Estrita da Sequência de Turno)
* Aplicado em `testSequenciaDeEventosNoTurno` para garantir que o registrador de eventos receba as notificações rigorosamente na ordem do edital:  
  $$\text{Ataque} \longrightarrow \text{Dano} \longrightarrow \text{Efeito de Status} \longrightarrow \text{Efeito de Terreno / Recuo} \longrightarrow \text{Uso de Item}$$

### 4.6 `ArgumentCaptor` e Simulação de Falhas (`doThrow`)
* **`ArgumentCaptor<ResultadoBatalha>`:** Empregado em `testResultadoSalvoAoFimDaBatalha` para inspecionar em detalhes o payload final encaminhado à persistência (vencedor, perdedor, Pokésais e quantidade de turnos).
* **`doThrow(...)`:** Empregado em `testFalhaNoRepositorioNaoInvalidaBatalha` para simular uma pane de banco de dados (`RuntimeException`), validando que a aplicação não trava e que o torneio continua normalmente.