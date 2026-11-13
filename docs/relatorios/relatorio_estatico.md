# Relatório de Análise Estática de Requisitos

Universidade Católica do Salvador – UCSal  
Curso: Análise e Desenvolvimento de Sistemas  
Disciplina: Testes e Qualidade de Software  
Professor: Pedro Arthur de Melo Nascimento  

Equipe:
- Cauã Lopes
- Arivaldo Teixeira
- Ryan Abade
- Pedro Luiz

---

## 1. Visão Geral

Este documento registra a análise estática realizada sobre o documento de requisitos do projeto PokeSal. A revisão buscou identificar omissões, ambiguidades e contradições na especificação original.

A fundamentação desta análise baseia-se no princípio de que requisitos de qualidade devem ser claros, inequívocos e testáveis.

---

## 2. Inspeção de Requisitos e Resoluções da Equipe

### 2.1 Omissão: Fórmula de Dano Base
- Problema: o documento não especifica a fórmula de cálculo de dano, permitindo abordagens (ex: ATK - DEF) que poderiam resultar em dano negativo ou nulo se a defesa do alvo for maior que o ataque.
- Resolução: a equipe adotou a fórmula proporcional ((ATK / DEF) * DANO_BASE) * Multiplicadores, garantindo valor mínimo de 1 de dano.

### 2.2 Omissão: Desempate de Velocidade (Iniciativa)
- Problema: o documento especifica que o atributo SPD define a iniciativa, mas não informa o comportamento em caso de empate numérico exato.
- Resolução: a equipe implementou um sorteio de desempate exibindo a condição diretamente no console.

### 2.3 Omissão: Origem e Probabilidade dos Efeitos de Status
- Problema: o documento cita a existência dos status (Queimado, Envenenado, Paralisado), mas não estabelece a probabilidade de aplicação nem a duração desses efeitos.
- Resolução: a equipe definiu a probabilidade de 15% para que ataques elementais apliquem status referentes ao seu tipo (Fogo -> Queimado, Água -> Paralisado, Planta -> Envenenado).

### 2.4 Omissão: Multiplicador de Tipos Iguais
- Problema: o documento define a matriz entre tipos opostos, mas não informa o comportamento para confrontos de mesmo tipo (ex: Planta x Planta).
- Resolução: a equipe padronizou o multiplicador neutro (1.0x) para confrontos entre Pokésais do mesmo tipo elemental.

### 2.5 Contradição/Ambiguidade: Atributo de Precisão e Efeito do Terreno "Poça de Chuva"
- Problema: o documento afirmava que a Poça de Chuva dá "10% de precisão ou dano" aos golpes de Água, porém o atributo de precisão não existia na especificação inicial da Fase 01.
- Resolução: a equipe fixou a regra em bônus direto de +10% de dano para o tipo Água no terreno Poça de Chuva na Fase 01. Na Fase 03, a precisão foi incorporada como atributo formal (0 a 100).

### 2.6 Ambiguidade: Mecânica e Duração da Paralisia
- Problema: o documento citava que a Paralisia "reduz SPD", sem definir a porcentagem da redução ou a duração do efeito.
- Resolução: a equipe padronizou que a Paralisia faz o Pokésal perder exatamente 1 turno de ação, retornando ao estado normal no turno seguinte.

### 2.7 Ambiguidade: Regra de Consumo da Mochila
- Problema: o documento cita o limite de 2 itens por batalha, mas não especificava se itens de cura consumiam o turno do jogador.
- Resolução: o sistema consome o turno do jogador ao utilizar qualquer item de suporte.
