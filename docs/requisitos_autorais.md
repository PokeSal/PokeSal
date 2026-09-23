# 📜 Requisitos Autorais - Projeto PokeSal

**Instituição:** Universidade Católica do Salvador – UCSal  
**Curso:** Análise e Desenvolvimento de Sistemas  
**Disciplina:** Testes e Qualidade de Software  
**Professor:** Pedro Arthur de Melo Nascimento  

**Equipe**
- Cauã Lopes
- Arivaldo Teixeira
- Ryan Abade
- Pedro Luiz

---

## 📌 Visão Geral

Este documento formaliza a especificação, justificativa e regras de negócio dos 3 Requisitos Autorais criados pela equipe para o simulador de batalhas **PokeSal**. A concepção destas regras visa tornar o combate mais dinâmico, estratégico e principalmente duradouro, integrando mecânicas de desgaste, adaptação ambiental e sobrevivência.

---

## 🎯 Requisito Autoral 01: Dano de Recuo por Desgaste Contínuo

### 1.1. Descrição e Regra de Negócio
A cada **4 ataques consecutivos** realizados sem o uso de itens da mochila, o Pokésal sofre um dano direto de **10% do seu HP Máximo** como penalidade de recuo por esforço físico contínuo. Caso o treinador utilize um item da mochila, a contagem de ataques do Pokésal é zerada imediatamente.

### 1.2. Justificativa e Motivação
A mecânica de recuo foi pensada para impedir que um jogador fique apenas atacando sem parar para vencer a luta facilmente sem gerenciar os recursos da mochila. Esse desgaste obriga o treinador a pausar a sequência ofensiva e usar itens de cura estrategicamente para compensar o dano acumulado.

---

## 🎯 Requisito Autoral 02: Rotação Dinâmica de Terreno

### 1.1. Descrição e Regra de Negócio
A cada **3 turnos completos**, as condições climáticas do Estacionamento da UCSal mudam aleatoriamente entre os três ambientes possíveis: `ASFALTO_QUENTE`, `POCA_CHUVA` e `CANTEIRO_CENTRAL`.

### 1.2. Justificativa e Motivação
O objetivo principal desse requisito é o balanceamento da partida, evitando que um Pokésal seja beneficiado unilateralmente por um terreno estático durante todo o combate. A variação constante força ambos os jogadores a adaptarem suas escolhas de acordo com o ambiente no momento.

---

## 🎯 Requisito Autoral 03: Passiva Defensiva de Baixo HP

### 1.1. Descrição e Regra de Negócio
Quando o HP do Pokésal cai para **20% ou menos do seu HP Máximo**, a sua Passiva de Defesa é ativada automaticamente uma única vez por batalha, concedendo um **bônus permanente de +20% no atributo de Defesa (`DEF`)**.

### 1.2. Justificativa e Motivação
Criado para fazer a batalha durar mais tempo, esse requisito atua como um "segundo fôlego" ao aumentar a resistência do Pokésal em momento crítico. Isso evita eliminações precoces e abre margem para viradas e jogadas de recuperação no final da partida.
