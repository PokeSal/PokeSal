# Relatório de Contribuição Individual

Universidade Católica do Salvador – UCSal  
Curso: Análise e Desenvolvimento de Sistemas  
Disciplina: Testes e Qualidade de Software  
Professor: Pedro Arthur de Melo Nascimento  
Projeto: PokéSal – Simulador de Batalha  

Integrantes:
- Arivaldo Teixeira Moraes Neto
- Cauã de Oliveira Lopes
- Pedro Luiz de Barros Pereira
- Ryan Abade Oliveira

---

## 1. Arivaldo Teixeira Moraes Neto

### Fase 01
- Análise de requisitos: identificação de ambiguidades no edital.
- Domínio: implementação dos enums TipoElemental, Terreno e TipoPokesal com os atributos base dos seis Pokésais.
- Modelagem de dados: implementação inicial das classes PokeSal, Item e Mochila.
- Criação da classe ConstantesJogo para centralizar valores fixos de combate e bônus.

### Fase 02
- Elaboração, condução e coleta de assinaturas da Ata de Reunião Nº 02.
- Apoio na revisão estática de código e checagem de regras de negócio.

### Fase 03 (Arivaldo Teixeira Moraes Neto)
- Refatoração da geração de aleatoriedade: criação da interface GeradorAleatorio e sua implementação GeradorAleatorioImpl, removendo instâncias estáticas e acoplamentos diretos com Random nas regras de negócio.
- Implementação das mecânicas de precisão de golpe (chance de erro com dano zero) e acerto crítico (multiplicador 1.5x com 6,25% de chance) na CalculadoraDano.
- Elaboração e revisão do documento de justificativa dos mocks (docs/justificativa_mocks.md), documentando a razão de cada colaborador mockado e o uso de argument matchers (any vs eq).
- Mapeamento e catalogação dos defeitos corrigidos durante os testes no documento de registro de bugs (docs/registro_bugs_fase03.md).
- Organização e registro das atas de reunião da Fase 03.

---

## 2. Cauã de Oliveira Lopes

### Fase 01
- Configuração do repositório Git, estrutura Maven, Checkstyle (`google_checks.xml`) e documentação Javadoc.
- Implementação da lógica de cálculo de dano elemental e modificadores de terreno na `CalculadoraDano`.
- Implementação do fluxo interativo de console, menus de seleção de treinadores/Pokésais e controle de turnos na classe `Main`.

### Fase 02
- Execução completa do checklist de teste estático manual (`relatorio_teste_estatico.md`).
- Configuração e execução do SonarScanner, com correção dos apontamentos iniciais de confiabilidade e manutenibilidade.
- Elaboração da matriz de rastreabilidade de testes e documentação de transparência de uso de IA (`AI_DECLARATION.md` e registro de prompts).

### Fase 03
- **Arquitetura e Desacoplamento:** Refatoração estrutural de `GerenciadorTorneio`, `GerenciadorDeBatalha` e `CalculadoraDano` para injeção de dependências via construtor (`Clock`, `GeradorAleatorio`, `ServicoTerreno`, `RegistroBatalha` e `RepositorioResultados`), eliminando dependências ocultas e acoplamentos estáticos.
- **Implementação dos Requisitos Autorais 04 e 05:** Desenvolvimento do `ResultadoBatalha` (record) e da lógica de compilação estatística e cálculo de MVP no `RepositorioResultadosMemoria` (Requisito 04), além do sistema de recompensa pós-semifinal com sorteio probabilístico e validação de capacidade da mochila em `GerenciadorTorneio` e `PokeSal` (Requisito 05).
- **Engenharia de Testes Unitários com Mockito (27 testes):**
  - Construção e refatoração das suítes `MecanicasBatalhaTest`, `TorneioRepositorioTest`, `RequisitosAutoraisTest` e `CoberturaJaCoCoTest`.
  - Aplicação de `@InjectMocks`, `@Mock`, `@Captor` (`ArgumentCaptor<ResultadoBatalha>`), retornos encadeados (`thenReturn`), simulação de falhas (`doThrow`), verificações negativas (`verify(..., never())`), `atLeastOnce()`, `times(3)` e auditoria estrita de ordem cronológica com `InOrder` (`ataque -> dano -> status -> terreno -> item`).
- **Depuração e Correção de Defeitos:** Identificação e correção de falhas críticas descobertas pelos testes e pela análise estática, incluindo o reset do limite de itens da `Mochila` entre semifinais e final, correção das faixas horárias de `ServicoTerrenoImpl` (habilitando o `Canteiro Central`), ajuste na ordem de log da passiva defensiva em `GerenciadorDeBatalha`, tratamento de nulidade em `GerenciadorTorneio` e eliminação de teste não determinístico em `CoberturaJaCoCoTest`.
- **Qualidade Estática, Cobertura e Documentação:** Execução do pipeline Maven + JaCoCo + SonarQube (atingindo Quality Gate `Passed`, `84,1%` de cobertura geral no SonarQube e `86%` de instruções / `81%` de branches no JaCoCo), atualização do relatório comparativo (`relatorio_comparativo_sonar.md`), matriz de rastreabilidade, registro de bugs e rastreabilidade de prompts de IA.

---

## 3. Ryan Abade Oliveira

### Fase 01
- Implementação da classe GerenciadorDeBatalha: controle de turnos, iniciativa por velocidade (SPD) e condições de vitória.
- Implementação dos requisitos autorais de combate: dano de recuo por esforço contínuo e passiva defensiva de baixo HP.
- Tratamento de exceções e validação de entradas no fluxo de batalha.

### Fase 02
- Implementação dos testes automatizados em JUnit 5 (CalculadoraDanoTest), cobrindo efetividade, terrenos, limites de mochila e requisitos autorais.

### Fase 03
- Implementação e validação da suíte de testes dos requisitos autorais com Mockito (RequisitosAutoraisTest.java), cobrindo o relatório estatístico e o drop de recompensas da mochila.
- Medição e análise comparativa da cobertura de código gerada pelo JaCoCo e métricas de qualidade de código.
- Apoio na refatoração da classe GerenciadorTorneio e persistência em RepositorioResultadosMemoria.

---

## 4. Pedro Luiz de Barros Pereira

### Fase 01
- Elaboração do Diagrama de Casos de Uso (interações do treinador, opções de combate e itens).
- Elaboração do Diagrama de Classes contemplando entidades de domínio, enums e motor de batalha.
- Revisão de consistência entre os diagramas UML e as classes implementadas no código Java.

### Fase 02
- Revisão dos entregáveis de documentação e validação do checklist estático em relação aos critérios do edital.

### Fase 03
- Atualização do Diagrama de Classes no UML, destacando as novas interfaces de serviço (GeradorAleatorio, ServicoTerreno, RegistroBatalha, RepositorioResultados) e suas implementações concretas.
- Elaboração dos Diagramas de Sequência exigidos pelo edital: turno completo de batalha e fluxo de registro de resultado no torneio.
- Revisão da conformidade da entrega com os critérios de avaliação da Fase 03.
