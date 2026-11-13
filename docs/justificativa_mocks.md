# Documento de Justificativa dos Mocks (Fase 03)

Universidade Católica do Salvador – UCSal  
Curso: Análise e Desenvolvimento de Sistemas  
Disciplina: Testes e Qualidade de Software  
Professor: Pedro Arthur de Melo Nascimento  
Projeto: PokéSal – Simulador de Batalha (Fase 03)  

Integrantes:
- Arivaldo Teixeira Moraes Neto
- Cauã de Oliveira Lopes
- Pedro Luiz de Barros Pereira
- Ryan Abade Oliveira

---

## 1. Visão Geral da Estratégia de Mocks

Na Fase 03 do projeto PokéSal, a utilização do Mockito teve como finalidade isolar as classes sob teste (SUT) de suas dependências externas, infraestrutura, fatores temporais e fontes de aleatoriedade.

A definição das dependências que seriam mockadas e daquelas mantidas com instâncias reais seguiu critérios de determinismo, velocidade de execução dos testes e aderência ao Princípio da Inversão de Dependência (DIP).

---

## 2. Justificativa das Dependências Mockadas (@Mock)

| Colaborador Mockado | Interface / Tipo | Justificativa do Isolamento |
| --- | --- | --- |
| GeradorAleatorio | GeradorAleatorio | Permite controlar os sorteios de precisão (erro de golpe), chance de acerto crítico (6.25%), chance de infligir efeito de status (15%) e desempate de iniciativa por velocidade. Sem o mock, os testes seriam intermitentes e não determinísticos. |
| ServicoTerreno | ServicoTerreno | Permite injetar condições específicas de terreno (Asfalto Quente, Canteiro Central ou Poça de Chuva) sem depender da leitura de relógio ou de regras externas durante o teste unitário. |
| java.time.Clock | Clock | Permite fixar o instante do sistema em horários diurnos ou noturnos via Clock.fixed, testando regras como a ativação do Asfalto Quente durante o dia de maneira previsível em qualquer ambiente de execução. |
| RegistroBatalha | RegistroBatalha | Isola o fluxo de saída no terminal e permite auditar a sequência exata de eventos disparados durante os turnos de combate através de InOrder e verify. |
| RepositorioResultados | RepositorioResultados | Permite validar os dados persistidos por meio de ArgumentCaptor e simular falhas de banco de dados (doThrow), comprovando que o GerenciadorTorneio mantém a partida e o torneio ativos mesmo em caso de erro de persistência. |

---

## 3. Justificativa das Classes Mantidas com Instâncias Reais (Não Mockadas)

Conforme as recomendações de boas práticas de teste unitário e o edital da Fase 03, a equipe optou por não mockar entidades de domínio simples, modelos e estruturas de dados:

- PokeSal (Entidade de Domínio): É o elemento central de estado do combate. Mockar o PokeSal exigiria simular individualmente métodos simples como getHpAtual, receberDano, curar e isVivo, o que tornaria o teste prolixo sem ganho real de isolamento. O uso da classe real assegura a correta validação das passivas defensivas e estados.
- Mochila e Item (Componentes de Modelo): Representam estruturas de dados de armazenamento simples. O uso das instâncias reais assegura que regras como o limite de uso de até dois itens por batalha e a capacidade da mochila sejam validadas diretamente.
- TipoPokesal, TipoElemental, Terreno e EfeitoStatus (Enums): São constantes e enumerações imutáveis que definem atributos base, matrizes elementais e descrições. Mockar enums em Java não é recomendado e prejudicaria a clareza dos testes.
- CalculadoraDano: Quando testada pelo GerenciadorDeBatalha, atua como colaboradora matemática real para verificar a integração entre os valores numéricos gerados e o dano aplicado no combate.

---

## 4. Recursos Avançados do Mockito Aplicados na Suíte

### 4.1 @Mock e @InjectMocks
- @Mock: Define os dublês de teste para as interfaces de serviço (GeradorAleatorio, ServicoTerreno, RegistroBatalha e RepositorioResultados).
- @InjectMocks: Utilizado na CalculadoraDano em MecanicasBatalhaTest, deixando a cargo do Mockito a resolução e injeção automática dos mocks declarados via construtor, eliminando instanciações manuais repetitivas e demonstrando a inversão de controle.

### 4.2 Retornos em Sequência com thenReturn
Exigido para simular chamadas sucessivas ao mesmo método mockado que retornam valores distintos:
- Em testRetornosEmSequenciaComAtLeastOnce, o método geradorMock.testarChance(CHANCE_CRITICO) foi configurado com thenReturn(false, true). Isso simula dois ataques seguidos: o primeiro sem crítico e o segundo com crítico, permitindo comparar diretamente o multiplicador de dano (1.5x) entre os dois turnos.

### 4.3 verify com times, never e atLeastOnce
- times(N): Garante uma quantidade exata de chamadas, como as três persistências em um torneio eliminatório de quatro participantes (testChaveamentoTorneioAvancaVencedores).
- never(): Garante que métodos de escrita ou relatórios não sejam acionados indevidamente, como a ausência de persistência enquanto a batalha ainda está em andamento (testRepositorioNaoChamadoSeBatalhaNaoTerminou).
- atLeastOnce(): Valida interações recorrentes onde importa que o colaborador tenha sido acionado pelo menos uma vez durante o combate (como registrarAtaque e registrarDano).

### 4.4 Argument Matchers: any vs eq
A escolha entre matchers flexíveis e valores exatos seguiu o objetivo de validação de cada caso de teste:
- Quando usar eq(valor): Deve ser utilizado quando o valor passado ao colaborador é parte fundamental da regra que está sendo testada. Por exemplo, em registrarAtaque(fogo.getNome(), planta.getNome()) e registrarDano(eq(planta.getNome()), ...), é necessário assegurar que o alvo registrado foi o defensor correto. Da mesma forma, eq("Ash") garante que o uso de item foi creditado ao treinador correto.
- Quando usar any(): Deve ser utilizado quando qualquer valor do tipo esperado é aceitável para o propósito daquele teste. Por exemplo, em testFalhaNoRepositorioNaoInvalidaBatalha, o que se testa é o tratamento de exceção ao chamar salvarResultado(any(ResultadoBatalha.class)); os campos internos daquele registro específico não interferem na validação da resiliência. O mesmo vale para anyInt() e anyBoolean() na assinatura de métodos auxiliares de log.

### 4.5 InOrder
Aplicado em testSequenciaDeEventosNoTurno para verificar que os registros no RegistroBatalha ocorrem rigorosamente na ordem exigida pelo edital:
ataque -> dano -> status -> terreno -> item.

### 4.6 ArgumentCaptor e Simulação de Falhas com doThrow
- ArgumentCaptor: Em testResultadoSalvoAoFimDaBatalha, captura a instância de ResultadoBatalha repassada ao repositório para validar vencedor, perdedor, Pokésais e quantidade de turnos.
- doThrow: Em testFalhaNoRepositorioNaoInvalidaBatalha, simula o lançamento de uma RuntimeException durante salvarResultado para verificar que o GerenciadorTorneio captura o erro sem encerrar a partida ou o torneio.
