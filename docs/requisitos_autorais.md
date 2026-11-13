# Requisitos Autorais - Projeto PokeSal

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

## 1. Visão Geral

Este documento descreve as regras e justificativas dos requisitos autorais desenvolvidos pela equipe para o simulador de batalhas PokeSal, com o intuito de tornar o combate estratégico e aumentar o tempo de sobrevivência dos Pokésais.

---

## 2. Requisito Autoral 01: Dano de Recuo por Desgaste Contínuo

### 2.1 Descrição e Regra de Negócio
A cada 4 ataques consecutivos desferidos sem utilizar itens da mochila, o Pokésal sofre um dano direto equivalente a 10% do seu HP Máximo como penalidade de recuo por esforço contínuo. Caso o treinador utilize um item da mochila, a contagem de ataques consecutivos é reiniciada.

### 2.2 Justificativa
A regra foi pensada para evitar que a batalha se resuma a ataques repetidos e ininterruptos sem gestão dos recursos da mochila. O recuo incentiva o jogador a alternar turnos ofensivos com turnos de suporte.

---

## 3. Requisito Autoral 02: Rotação Dinâmica de Terreno

### 3.1 Descrição e Regra de Negócio
A cada 3 turnos completos, o terreno do Estacionamento da UCSal pode alternar entre os três ambientes possíveis: Asfalto Quente, Poça de Chuva e Canteiro Central.

### 3.2 Justificativa
O objetivo é evitar que um Pokésal mantenha vantagem elemental de terreno estática durante toda a luta, exigindo que os jogadores se adaptem às variações ambientais da partida.

---

## 4. Requisito Autoral 03: Passiva Defensiva de Baixo HP

### 4.1 Descrição e Regra de Negócio
Quando o HP atual do Pokésal atinge 20% ou menos do seu HP Máximo, uma passiva de defesa é ativada uma única vez por batalha, concedendo um bônus de +20% no atributo de Defesa (DEF) de forma permanente até o fim daquele confronto.

### 4.2 Justificativa
A passiva funciona como um mecanismo de sobrevida para evitar que Pokésais sejam eliminados muito rapidamente por golpes super efetivos, dando oportunidade de reação e recuperação no final do combate.

---

## 5. Requisito Autoral 04 (Fase 03): Relatório Estatístico e MVP do Torneio

### 5.1 Descrição e Regra de Negócio
Ao término do torneio, o RepositorioResultados compila os dados de todas as partidas disputadas e emite um relatório estatístico apresentando:
- O MVP do torneio (Pokésal que causou o maior dano acumulado somando todas as partidas).
- A partida mais duradoura (confronto com maior número de turnos).
- O consumo total de suprimentos (soma de todos os itens utilizados ao longo do torneio).

### 5.2 Justificativa e Dependência Externa Mockável
Depende da interface RepositorioResultados e de sua persistência em memória, permitindo verificar e auditar o cálculo das métricas acumuladas via testes unitários isolados com Mockito.

---

## 6. Requisito Autoral 05 (Fase 03): Sistema de Recompensa Pós-Semifinal

### 6.1 Descrição e Regra de Negócio
Ao vencer uma partida semifinal, o Pokésal vencedor recebe um item de cura sorteado para uso na Grande Final:
- Sorteio de 1 a 50: Poção (+20 HP)
- Sorteio de 51 a 85: Super Poção (+40 HP)
- Sorteio de 86 a 100: Cura Total (Cura Status)

Caso a mochila já esteja em sua capacidade máxima de 3 itens, o item sorteado é descartado e um aviso é registrado no log.

### 6.2 Justificativa e Dependência Externa Mockável
Depende da interface GeradorAleatorio para determinar o item recebido e da interface RegistroBatalha para o log da premiação, permitindo testar via Mockito todos os ramos de probabilidade e descarte com determinismo.
