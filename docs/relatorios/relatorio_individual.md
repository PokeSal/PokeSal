# Relatório de Contribuição Individual

*Elaborado de acordo com as atividades executadas na Fase 01 e Fase 02 do projeto PokeSal.*

---

## Arivaldo Teixeira Moraes Neto

### Fase 01
- **Análise e Modelagem:** Participação na análise estática do edital, identificando ambiguidades e lacunas nas regras de negócio. Implementação dos enums `TipoElemental` e `Terreno`, além do `TipoPokesal` com os atributos base dos seis Pokésais.
- **Domínio de Negócio:** Implementação das classes `PokeSal`, `Item` e `Mochila`, incluindo o controle de estado e o limite de itens por batalha.
- **Centralização de Constantes:** Criação da classe `ConstantesJogo.java` para padronizar multiplicadores elementais, bônus de terreno, chances e taxas do sistema.
- **Interface e Mensagens:** Implementação de nomes de exibição amigáveis para os enums e atualização das mensagens de console na `Main.java`.

### Fase 02
- **Gestão de Atas e Documentação:** Elaboração, organização e acompanhamento da coleta de assinaturas digitais da **Ata de Reunião Nº 02** (`Ata_de_Reuniao_02.pdf`) para acompanhamento e encerramento dos entregáveis do grupo.

---

## Cauã de Oliveira Lopes

### Fase 01
- **Estrutura e Arquitetura:** Configuração da estrutura inicial do repositório, `.gitignore`, documentação Javadoc e suporte à modelagem dos requisitos autorais.
- **Combate e Dano:** Implementação das vantagens/desvantagens elementais e bônus de terreno em `CalculadoraDano.java`.
- **Fluxo de Aplicação:** Desenvolvimento da seleção de Pokésais, definição de iniciativa (SPD), controle do fluxo de turnos, K.O., desistência e vitória por W.O. no terminal.
- **Itens e Status:** Integração da Mochila ao combate, recuperação de HP e suporte aos efeitos de status (QUEIMADO, ENVENENADO, PARALISADO).

### Fase 02
- **Qualidade e Refatoração:** Execução do **CheckList de Teste Estático (Revisão Manual)**, com foco na remoção de métodos obsoletos e código morto (`relatorio_teste_estatico.md`).
- **Análise SonarQube:** Execução do SonarScanner local e resolução dos **3 Bugs de Confiabilidade (Reliability)**, padronizando o reuso de geradores aleatórios (`Random`/`SecureRandom`) e adequação às regras do Checkstyle[cite: 7].
- **Rastreabilidade e IA:** Elaboração da **Matriz de Rastreabilidade, Documento de Testes e Registro de Bugs** (`relatorio_testes_matriz_rastreabilidade.md`), além dos relatórios de transparência do uso de Inteligência Artificial (`AI_DECLARATION.md` e `prompts_ia.txt`).

---

## Ryan Abade Oliveira

### Fase 01
- **Gerenciamento de Batalha:** Criação da classe `GerenciadorDeBatalha.java`, controle da iniciativa por SPD, gestão de turnos e encerramento do combate.
- **Mecânicas Autorais e Terreno:** Implementação do bônus defensivo com HP baixo, dano de recuo por ataques consecutivos, rotação dinâmica de terrenos e regeneração no Canteiro Central.
- **Tratamento de Exceções:** Implementação da validação e tratamento de entradas no terminal para prevenir erros de execução.

### Fase 02
- **Suíte de Testes Automatizados (JUnit 5):** Desenvolvimento e validação da suíte de testes unitários (`CalculadoraDanoTest.java`), cobrindo as mecânicas de combate, vantagens elementais, efeitos de terreno, limite de mochila com exceção (`IllegalStateException`) e requisitos autorais (100% de aprovação)[cite: 8].
- **Análise Estática Dinâmica:** Suporte na execução e validação das suítes automatizadas e verificação de comportamento dos testes no ambiente de análise contínua[cite: 8].

---

## Pedro Luiz de Barros Pereira

### Fase 01
- **Modelagem UML:** Elaboração do Diagrama de Casos de Uso (interações do treinador, combate, mochila e desistência) e do Diagrama de Classes (`PokeSal`, `Item`, `Mochila`, enums e camada engine).
- **Consistência de Arquitetura:** Mapeamento de divergências entre os diagramas UML e a implementação do código Java, garantindo conformidade entre a especificação visual e o modelo orientado a objetos.

### Fase 02
- **Acompanhamento e Revisão:** Acompanhamento da validação dos entregáveis de documentação da equipe e revisão da conformidade do projeto com o edital da Fase 02.