# Relatório de Contribuição Individual

*Elaborado de acordo com as atividades registradas no GitHub Projects.*

---

## Arivaldo Teixeira Moraes Neto

### 1. Análise e modelagem
- Participação na análise estática do edital, identificando ambiguidades e lacunas nas regras de negócio.
- Implementação dos enums `TipoElemental` e `Terreno`.
- Criação de `TipoPokesal` com os atributos base dos seis Pokésais.
- Implementação das classes `PokeSal`, `Item` e `Mochila`, incluindo o controle de estado e o limite de itens por batalha.

### 2. Organização do código
- Criação da classe `ConstantesJogo.java` para centralizar valores utilizados nas regras do jogo.
- Organização de multiplicadores elementais, bônus de terreno, chances e demais constantes do projeto.

### 3. Interface do terminal
- Implementação de nomes de exibição amigáveis para os enums `Terreno` e `TipoPokesal`.
- Atualização das mensagens da `Main.java`, tornando a interface do terminal mais clara e legível.

---

## Cauã de Oliveira Lopes

### 1. Estrutura e documentação
- Configuração da estrutura inicial do repositório e do `.gitignore`.
- Criação da estrutura de documentação, incluindo `docs/atas/`.
- Participação na análise estática do edital e na documentação dos requisitos autorais.
- Elaboração da documentação referente ao uso de Inteligência Artificial no projeto.

### 2. Cálculo de dano e combate
- Implementação das vantagens e desvantagens elementais e dos bônus de terreno.
- Inclusão e padronização de documentação Javadoc nas classes e métodos públicos.
- Refatoração do requisito 01 do dano de recuo.

### 3. Interface e fluxo de batalha
- Implementação da interação com os treinadores e seleção dos Pokésais pelo terminal.
- Implementação da definição de iniciativa com base no atributo SPD.
- Desenvolvimento do fluxo de turnos, incluindo K.O. e desistência.
- Implementação da vitória por W.O.

### 4. Itens e efeitos de status
- Integração da Mochila ao PokeSal e implementação do uso de itens durante a batalha.
- Validação do uso de itens e recuperação de HP.
- Participação na implementação dos efeitos de status QUEIMADO, ENVENENADO e PARALISADO.

---

## Ryan Abade Oliveira

### 1. Gerenciamento da batalha
- Criação da classe `GerenciadorDeBatalha`, responsável pelo controle do fluxo da batalha.
- Implementação da ordem de atuação com base no atributo SPD.
- Controle dos turnos e encerramento da batalha quando um Pokesal chega a 0 HP.

### 2. Requisitos autorais
- Implementação do aumento de 20% na DEF quando o HP fica abaixo de 20%.
- Implementação do dano de recuo de 10% a cada 3 turnos.
- Implementação da rotação dinâmica dos terrenos durante a batalha, com alteração do terreno após o intervalo de turnos definido.
- Criação e utilização das constantes relacionadas aos requisitos autorais.
- Correção e validação das mecânicas durante os testes.

### 3. Mecânicas de terreno e combate
- Implementação da cura do Canteiro Central ao final do turno.
- Adição de logs para ataques super efetivos e pouco efetivos.
- Exibição dos bônus de terreno durante o combate.
- Adição de mensagens para informar a ativação da passiva de defesa.

### 4. Tratamento de entradas e testes
- Implementação do tratamento de entradas inválidas no terminal, evitando o encerramento da aplicação por valores não numéricos.
- Realização de testes das mecânicas de batalha e correção de problemas identificados durante a execução.

---

## Pedro Luiz de Barros Pereira

### 1. Modelagem UML e diagramas
- Elaboração do Diagrama de Casos de Uso, mapeando as interações dos treinadores com o simulador (seleção de iniciais, ações de combate, uso de mochila e desistência).
- Criação do Diagrama de Classes, modelando as entidades do domínio (`PokeSal`, `Item`, `Mochila`), os enums (`TipoElemental`, `Terreno`, `TipoPokesal`, `EfeitoStatus`) e as classes da camada engine (`GerenciadorDeBatalha`, `CalculadoraDano`).
- Revisão das visibilidades, multiplicidades e relacionamentos entre as classes (associações e dependências) para garantir o alinhamento entre a modelagem e a implementação Java.

### 2. Análise estática e revisão de arquitetura
- Participação na análise estática de requisitos, auxiliando na identificação de lacunas e ambiguidades do edital.
- Mapeamento das divergências entre o código-fonte e a modelagem UML durante a revisão da Fase 01.