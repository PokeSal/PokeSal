# Documento de Testes e Matriz de Rastreabilidade

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

## 1. Visão Geral da Estratégia de Testes

O plano de testes do projeto PokéSal combina testes unitários e de integração com JUnit 5 e Mockito, cobrindo regras de negócio base, efeitos de terreno, combate por turnos, limites de mochila, requisitos autorais e resiliência no chaveamento de torneio.

---

## 2. Casos de Teste da Fase 02 (Testes de Domínio e Regras Base)

Implementados em src/test/java/br/edu/ucsal/pokesal/engine/CalculadoraDanoTest.java:

### CT01: testVantagemElemental
- Objetivo: validar os multiplicadores elementais (2.0x super efetivo, 0.5x pouco efetivo e 1.0x neutro).
- Entrada: combinações Fogo vs Planta, Fogo vs Água e Fogo vs Fogo.
- Resultado esperado: multiplicadores 2.0, 0.5 e 1.0 aplicados.
- Status: Aprovado.

### CT02: testEfeitoTerrenoEstacionamentoUcsal
- Objetivo: validar o impacto dos terrenos no dano e regeneração de HP.
- Entrada: Fogo no Asfalto Quente, Água na Poça de Chuva e Planta no Canteiro Central.
- Resultado esperado: bônus de 15% para Fogo, 10% para Água e cura de 5% de HP para Planta.
- Status: Aprovado.

### CT03: testOrdemDeAtaquePorVelocidade
- Objetivo: validar a definição de iniciativa baseada no atributo SPD.
- Entrada: Pokésais com atributos de velocidade distintos.
- Resultado esperado: o Pokésal mais rápido é definido como primeiro atacante.
- Status: Aprovado.

### CT04: testUsoLimiteDeItensExcedido
- Objetivo: validar a restrição de no máximo 2 itens por batalha e o disparo de exceção.
- Entrada: tentativa de consumir um 3º item na mesma batalha.
- Resultado esperado: lançamento de IllegalStateException.
- Status: Aprovado.

### CT05: testCalculoDanoBoundaryValues
- Objetivo: validar valores limite de ATK, DEF e HP no cálculo de dano.
- Entrada: valores extremos e aplicação de dano superior ao HP restante.
- Resultado esperado: dano não negativo e HP limitado em zero (sem valores negativos).
- Status: Aprovado.

### CT06: testDanoRecuo (Requisito Autoral 01)
- Objetivo: validar penalidade de recuo por esforço contínuo.
- Entrada: 4 ataques seguidos sem usar itens da mochila.
- Resultado esperado: desconto de 10% do HP máximo no 4º ataque.
- Status: Aprovado.

### CT07: testPassivaDefensivaBaixoHp (Requisito Autoral 03)
- Objetivo: validar a ativação da passiva de defesa em estado crítico.
- Entrada: redução de HP para 20% ou menos do HP máximo.
- Resultado esperado: bônus permanente de +20% na DEF até o fim do combate.
- Status: Aprovado.

---

## 3. Casos de Teste da Fase 03 (Isolamento com Mockito)

Implementados em MecanicasBatalhaTest, TorneioRepositorioTest e RequisitosAutoraisTest:

### CT08: testAcertoCriticoComGeradorMockado
- Objetivo: validar multiplicador crítico (1.5x) quando o gerador retorna acerto crítico.
- Mocks: GeradorAleatorio (chance de crítico verdadeira) e ServicoTerreno.
- Resultado esperado: dano multiplicado por 1.5.
- Status: Aprovado.

### CT09: testGolpeErraQuandoPrecisaoFalha
- Objetivo: validar que falha de precisão resulta em dano zero e mantém HP do alvo inalterado.
- Mocks: GeradorAleatorio retornando sorteio acima da precisão do atacante.
- Resultado esperado: dano igual a zero e HP inalterado.
- Status: Aprovado.

### CT10: testAsfaltoQuenteApenasDuranteDia
- Objetivo: validar bônus de Asfalto Quente condicionado ao horário diurno.
- Mocks: ServicoTerreno retornando Asfalto Quente de dia e Canteiro Central à noite.
- Resultado esperado: dano diurno superior ao dano noturno.
- Status: Aprovado.

### CT11: testRetornosEmSequenciaComAtLeastOnce
- Objetivo: validar retornos em sequência via thenReturn e verificação com atLeastOnce.
- Mocks: GeradorAleatorio retornando false e true em sequência para a chance de crítico; RegistroBatalha verificado com atLeastOnce.
- Resultado esperado: 1º ataque sem crítico e 2º ataque com crítico (dano 1.5x); auditor chamado ao menos uma vez.
- Status: Aprovado.

### CT12: testSequenciaDeEventosNoTurno
- Objetivo: validar ordem das notificações enviadas ao RegistroBatalha via InOrder.
- Mocks: RegistroBatalha com InOrder.
- Resultado esperado: sequência estrita ataque -> dano -> status -> terreno -> item.
- Status: Aprovado.

### CT13: testResultadoSalvoAoFimDaBatalha
- Objetivo: validar payload de persistência via ArgumentCaptor.
- Mocks: RepositorioResultados e ArgumentCaptor de ResultadoBatalha.
- Resultado esperado: captura com sucesso de vencedor, perdedor, Pokésais e quantidade de turnos.
- Status: Aprovado.

### CT14: testFalhaNoRepositorioNaoInvalidaBatalha
- Objetivo: validar resiliência do torneio perante erro no repositório.
- Mocks: RepositorioResultados configurado com doThrow.
- Resultado esperado: exceção tratada, vencedor mantido e fluxo continuado.
- Status: Aprovado.

### CT15: testRepositorioNaoChamadoSeBatalhaNaoTerminou
- Objetivo: comprovar que o repositório não persiste dados durante batalha em andamento.
- Mocks: RepositorioResultados verificado com never.
- Resultado esperado: zero invocações a salvarResultado e emitirRelatorioEstatistico enquanto ambos Pokésais estiverem vivos.
- Status: Aprovado.

### CT16: testChaveamentoTorneioAvancaVencedores
- Objetivo: validar chaveamento de 4 competidores com combates e iniciativa orientados por mocks.
- Mocks: RepositorioResultados verificado com times(3) após 2 semifinais e 1 final.
- Resultado esperado: 3 partidas processadas e vencedores avançando corretamente para a final.
- Status: Aprovado.

### CT17: testCompilacaoEstatisticaMVPNoFimDoTorneio (Requisito Autoral 04)
- Objetivo: validar cálculo do MVP, partida mais longa e consumo de suprimentos.
- Componente: RepositorioResultadosMemoria.
- Resultado esperado: relatório compilando o Pokésal MVP e totais acumulados.
- Status: Aprovado.

### CT18: testEstatisticaNaoGeradaSeTorneioIncompleto (Requisito Autoral 04)
- Objetivo: garantir via verify never que estatísticas não são geradas antes de partidas serem registradas.
- Mocks: RepositorioResultados.
- Resultado esperado: nenhuma invocação ao relatório estatístico.
- Status: Aprovado.

### CT19: testDropRecompensaPosVitoriaComSucesso (Requisito Autoral 05)
- Objetivo: validar entrega de item sorteado ao vencedor da semifinal.
- Mocks: GeradorAleatorio (sorteio <= 50) e RegistroBatalha.
- Resultado esperado: item Poção adicionado à mochila do vencedor.
- Status: Aprovado.

### CT20: testDropRecompensaIgnoradoMochilaCheia (Requisito Autoral 05)
- Objetivo: validar descarte do prêmio quando a mochila do vencedor já possui 3 itens.
- Mocks: GeradorAleatorio e RegistroBatalha.
- Resultado esperado: mochila mantém 3 itens e o descarte é registrado.
- Status: Aprovado.

---

## 4. Matriz de Rastreabilidade Consolidada (Fase 03)

| ID | Requisito | Caso de Teste | Colaboradores Mockados | Resultado Obtido |
| --- | --- | --- | --- | --- |
| RF01 | Atributos e Limites de HP | testCalculoDanoBoundaryValues | Instâncias reais | Dano válido e HP mínimo em zero. Aprovado. |
| RF02 | Matriz Elemental (2.0x, 0.5x, 1.0x) | testVantagemElemental | Instâncias reais | Multiplicadores corretos aplicados. Aprovado. |
| RF03 | Efeitos de Terreno Base | testEfeitoTerrenoEstacionamentoUcsal | Instâncias reais | Bônus aplicados conforme especificação. Aprovado. |
| RF04 | Iniciativa por Velocidade (SPD) | testOrdemDeAtaquePorVelocidade | Instâncias reais | Maior SPD ataca primeiro. Aprovado. |
| RF05 | Limite de 2 Itens por Batalha | testUsoLimiteDeItensExcedido | Instâncias reais | Lança exceção ao exceder limite. Aprovado. |
| RF06 | Acerto Crítico (1.5x, chance 6.25%) | testAcertoCriticoComGeradorMockado | GeradorAleatorio, ServicoTerreno | Dano multiplicado por 1.5. Aprovado. |
| RF07 | Erro de Golpe por Precisão | testGolpeErraQuandoPrecisaoFalha | GeradorAleatorio | Dano zero e HP alvo mantido. Aprovado. |
| RF08 | Terreno Dinâmico via Clock | testAsfaltoQuenteApenasDuranteDia | ServicoTerreno, Clock | Bônus ativo apenas no dia mockado. Aprovado. |
| RF09 | Resiliência do Repositório | testFalhaNoRepositorioNaoInvalidaBatalha | RepositorioResultados | Exceção tratada, torneio continua. Aprovado. |
| RF10 | Salvamento com ArgumentCaptor | testResultadoSalvoAoFimDaBatalha | RepositorioResultados, ArgumentCaptor | Dados capturados com precisão. Aprovado. |
| RF11 | Ordem Estrita de Eventos do Turno | testSequenciaDeEventosNoTurno | RegistroBatalha, InOrder | Eventos registrados na ordem correta. Aprovado. |
| RF12 | Chaveamento Eliminatório | testChaveamentoTorneioAvancaVencedores | RepositorioResultados, GeradorAleatorio | Três partidas registradas com sucesso. Aprovado. |
| REQ01 | Recuo a cada 4 Ataques | testDanoRecuo | Instâncias reais | Desconto de 10% no 4º ataque. Aprovado. |
| REQ02 | Rotação Dinâmica de Terrenos | testEfeitoTerrenoEstacionamentoUcsal | ServicoTerreno | Alternância de condições ambientais. Aprovado. |
| REQ03 | Passiva Defensiva (HP <= 20%) | testPassivaDefensivaBaixoHp | Instâncias reais | Bônus de +20% na defesa. Aprovado. |
| REQ04 | Relatório Estatístico e MVP | testCompilacaoEstatisticaMVPNoFimDoTorneio | RepositorioResultadosMemoria | Estatísticas compiladas com precisão. Aprovado. |
| REQ05 | Recompensa Pós-Semifinal | testDropRecompensaPosVitoriaComSucesso | GeradorAleatorio, RegistroBatalha | Item entregue ao vencedor. Aprovado. |
