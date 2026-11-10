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

1. **`@Mock` e `@InjectMocks`:** Utilizados para instanciar automaticamente as dependências abstratas no setup dos testes (`CalculadoraDanoTest`, `MecanicasBatalhaTest`, `TorneioRepositorioTest` e `RequisitosAutoraisTest`).
2. **`when(...).thenReturn(...)` com Retornos em Sequência:** Aplicado para simular comportamentos dinâmicos de aleatoriedade, como chamadas consecutivas ao gerador de números e variação de relógio.
3. **`verify(...)` (`times()`, `never()`, `atLeastOnce()`):** Aplicado para garantir que o repositório **nunca** seja chamado quando uma batalha não termina (`testRepositorioNaoChamadoSeBatalhaNaoTerminou`) e exatamente 3 vezes em um torneio completo (`testChaveamentoTorneioAvancaVencedores`).
4. **`InOrder` (Verificação de Ordem):** Utilizado em `testSequenciaDeEventosNoTurno` para garantir que o auditor registre os eventos exatamente na ordem padronizada: Ataque $\rightarrow$ Dano $\rightarrow$ Status $\rightarrow$ Terreno $\rightarrow$ Item.
5. **`ArgumentCaptor<ResultadoBatalha>`:** Utilizado para capturar a instância enviada ao `RepositorioResultados`, permitindo validar as asserções de nome do treinador vencedor, perdedor, Pokésais e quantidade de turnos decorridos.
6. **`doThrow(...)` (Simulação de Exceção):** Aplicado em `testFalhaNoRepositorioNaoInvalidaBatalha` para injetar uma `RuntimeException` durante o salvamento do resultado e validar se o torneio trata o erro sem abortar a partida.