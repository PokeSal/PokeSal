# 📋 Relatório de Análise Estática de Requisitos

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

## 1. Visão Geral

Este documento registra a **Análise Estática** realizada sobre o documento de requisitos do projeto **PokeSal**. A revisão buscou identificar omissões, ambiguidades e contradições na especificação original.

A fundamentação desta análise baseia-se diretamente no princípio enfatizado pelo professor em sala de aula: **requisitos de qualidade devem ser claros, inequívocos e estritamente TESTÁVEIS**.

---

## 2. Inspeção de Requisitos e Resoluções da Equipe

### 2.1. Omissão: Fórmula de Dano Base
- **Problema Encontrado:** O documento não especifica a fórmula de cálculo de dano, permitindo abordagens (ex: `ATK - DEF`) que poderiam resultar em dano negativo ou nulo se a defesa do alvo for maior que o ataque.
- **Resolução Adotada:** A equipe definiu a fórmula proporcional `((ATK / DEF) * DANO_BASE) * Multiplicadores`, garantindo o valor mínimo de 1 de dano.

### 2.2. Omissão: Desempate de Velocidade (Iniciativa)
- **Problema Encontrado:** O documento especifica que o atributo `SPD` define a iniciativa, mas não informa como deveria ser o comportamento em caso de empate numérico exato.
- **Resolução Adotada:** A equipe implementou um sorteio aleatório em caso de empate, exibindo a condição de desempate diretamente no console.

### 2.3. Omissão: Origem e Probabilidade dos Efeitos de Status
- **Problema Encontrado:** O documento cita a existência dos status (Queimado, Envenenado, Paralisado) e do item *Antidote*, mas não fala a probabilidade do efeito pegar, não fala como seria a aplicação, o dano e a duração desses efeitos durante a batalha.
- **Resolução Adotada:** A equipe definiu a probabilidade fixa de 15% para que ataques elementais apliquem status referentes ao seu tipo (Fogo $\rightarrow$ Queimado, Água $\rightarrow$ Paralisado, Planta $\rightarrow$ Envenenado).

### 2.4. Omissão: Multiplicador de Tipos Iguais
- **Problema Encontrado:** O documento define a matriz de vantagem/desvantagem entre tipos opostos, mas não informa como deveria ser o comportamento para confrontos de mesmo tipo (ex: Planta x Planta).
- **Resolução Adotada:** A equipe padronizou o multiplicador neutro (1.0x) para confrontos entre Pokésais do mesmo tipo elemental.

### 2.5. Contradição/Ambiguidade: Atributo de Precisão e Efeito do Terreno "Poça de Chuva"
- **Problema Encontrado:** O documento afirma que a *Poça de Chuva* dá "10% de precisão ou dano" aos golpes de Água, porém o atributo de *Precisão* não existe na especificação do Pokésal.
- **Resolução Adotada:** A equipe eliminou a ambiguidade da precisão, fixando a regra em bônus direto de +10% de dano para o tipo Água no terreno Poça de Chuva.

### 2.6. Ambiguidade: Mecânica e Duração da Paralisia
- **Problema Encontrado:** O documento cita vagamente que a Paralisia "reduz SPD", sem definir a porcentagem da redução, a duração do efeito ou se o Pokésal é impedido de agir.
- **Resolução Adotada:** A equipe padronizou que a Paralisia faz o Pokésal perder exatamente 1 turno de ação, retornando ao estado normal no turno seguinte.

### 2.7. Ambiguidade: Regra de Consumo da Mochila
- **Problema Encontrado:** O documento limita o uso a 2 itens por batalha e indica que o uso gasta o turno, mas não informa como deveria ser o comportamento de uso quando o Pokésal está com HP máximo e sem status.
- **Resolução Adotada:** A equipe implementou uma trava lógica na classe `PokeSal` para rejeitar o uso do item e impedir o desperdício do turno quando não houver necessidade de cura.
