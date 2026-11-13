# Registro de Bugs e Defeitos Corrigidos (Fase 03)

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

## 1. Visão Geral

Durante a refatoração da Fase 03 e a execução das suítes de testes unitários com Mockito, foram mapeadas e corrigidas inconsistências de regras de negócio, contratos de log e pequenos problemas de execução no fluxo do jogo.

Todos os itens foram corrigidos e validados por meio de testes automatizados.

---

## 2. Bugs Identificados e Soluções Aplicadas

| ID | Componente | Descrição do Problema | Impacto | Correção Efetuada |
| --- | --- | --- | --- | --- |
| BUG-01 | GerenciadorDeBatalha | Duplicação de log de status no ataque (status impresso duas vezes no mesmo turno). | Mensagens redundantes no console. | Centralizada a verificação em executarTurnoAtaque, acionando o RegistroBatalha apenas quando há mudança real de estado. |
| BUG-02 | GerenciadorDeBatalha | Log confuso de dano de status no fim do turno (reaproveitava a mensagem de aplicação do efeito). | Parecia que o Pokésal contraía o status novamente a cada rodada. | Formatada mensagem explícita em aplicarDanoStatusFimDeTurno informando o dano sofrido por queimadura ou envenenamento. |
| BUG-03 | PokeSal / GerenciadorDeBatalha | Impressão direta com System.out na passiva defensiva, sem passar pelo auditor. | Mensagem saía fora da ordem cronológica dos eventos de combate. | Notificação da passiva defensiva redirecionada para o contrato RegistroBatalha. |
| BUG-04 | GerenciadorTorneio | Recompensa pós-semifinal (Requisito 05) sempre descartada quando a mochila já possuía 3 itens. | Vencedor da semifinal não conseguia receber o item mesmo após ter consumido itens na partida. | Ajustada verificação para permitir inclusão caso o treinador tenha consumido itens na semifinal. |
| BUG-05 | GerenciadorTorneio | Notificação de recompensa enviada via registrarEfeitoTerreno em vez de item. | Log classificado como efeito de terreno indevidamente. | Notificação do item recebido redirecionada para registrarUsoItem. |
| BUG-06 | Mochila | Limite de dois itens por batalha não era reiniciado para a Grande Final. | Treinadores que usavam dois itens na semifinal ficavam bloqueados de usar itens na final. | Criado o método resetarUsosBatalha na classe Mochila, chamado no início de cada partida pelo GerenciadorTorneio. |
| BUG-07 | Main | Exceção de índice fora do intervalo ao digitar zero ou valores inválidos no menu de Pokésais. | Encerrava o programa abruptamente se o usuário digitasse uma opção inexistente. | Adicionada validação de intervalo dentro do loop com tratamento de InputMismatchException. |
| BUG-08 | GerenciadorDeBatalha | Dano de recuo a cada 4 ataques (Requisito 01) era descontado no HP sem registro no log. | Jogador não recebia aviso visual do dano de esforço contínuo. | Adicionada chamada ao registroBatalha em processarRecuoPorAtaque informando o recuo sofrido. |
| BUG-09 | GerenciadorDeBatalha | Frase redundante no log de perda de turno por paralisia. | Mensagem ficava confusa no terminal. | Padronizada a mensagem enviada ao RegistroBatalha quando o Pokésal perde a vez por paralisia. |
