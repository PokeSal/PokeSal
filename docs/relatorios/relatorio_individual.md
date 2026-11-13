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

### Fase 03
- Elaboração e revisão do documento de justificativa dos mocks (docs/justificativa_mocks.md), documentando a razão de cada colaborador mockado e o uso de argument matchers (any vs eq).
- Mapeamento e catalogação dos defeitos corrigidos durante os testes no documento de registro de bugs (docs/registro_bugs_fase03).
- Organização e registro das atas de reunião da Fase 03.

---

## 2. Cauã de Oliveira Lopes

### Fase 01
- Configuração do repositório Git, estrutura de pastas Maven, checkstyle e documentação Javadoc.
- Implementação da lógica de cálculo de dano elemental e terreno na CalculadoraDano.
- Implementação do fluxo de console, menus de seleção e turnos na classe Main.

### Fase 02
- Execução do checklist de teste estático manual (relatorio_teste_estatico.md).
- Execução do SonarScanner e correção dos bugs de confiabilidade apontados pelo SonarQube.
- Elaboração da matriz de rastreabilidade de testes e documentação de transparência de IA (AI_DECLARATION.md).

### Fase 03
- Refatoração da suíte de testes com Mockito: aplicação de @InjectMocks na CalculadoraDano, configuração de retornos em sequência (thenReturn), verificação com atLeastOnce() e validação da ordem completa de eventos com InOrder.
- Correção do teste de chaveamento do torneio com combates e iniciativa orientados por mocks e teste de batalha não finalizada com verify never.
- Ajuste no ServicoTerrenoImpl para cobrir os três terrenos (Asfalto Quente, Canteiro Central e Poça de Chuva) por faixas de horário do Clock.
- Desenvolvimento de testes para elevação da cobertura de branches do GerenciadorDeBatalha acima de 70%.

---

## 3. Ryan Abade Oliveira

### Fase 01
- Implementação da classe GerenciadorDeBatalha: controle de turnos, iniciativa por velocidade (SPD) e condições de vitória.
- Implementação dos requisitos autorais de combate: dano de recuo por esforço contínuo e passiva defensiva de baixo HP.
- Tratamento de exceções e validação de entradas no fluxo de batalha.

### Fase 02
- Implementação dos testes automatizados em JUnit 5 (CalculadoraDanoTest), cobrindo efetividade, terrenos, limites de mochila e requisitos autorais.
- Acompanhamento das métricas de cobertura no JaCoCo.

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
