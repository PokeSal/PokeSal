# Relatório de Refatoração e Inversão de Dependência (Fase 03)

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

## 1. Visão Geral e Motivação da Refatoração

Na Fase 02 do projeto PokéSal, as classes de regra de negócio continham acoplamento direto com recursos de sistema. A aleatoriedade do combate dependia de instanciação direta com new Random(), os efeitos de terreno eram fixos, os eventos de batalha eram impressos diretamente no console com System.out e as regras de persistência não estavam isoladas. Esse cenário dificultava a escrita de testes unitários previsíveis e determinísticos, uma vez que não era possível isolar o combate dos efeitos colaterais de entrada e saída e do relógio da máquina.

Para a Fase 03, o sistema foi refatorado aplicando Inversão de Dependência (DIP) e interfaces de serviço, permitindo isolar cada classe testada por meio de dublês de teste com o Mockito.

---

## 2. Aplicação de Princípios SOLID e Padrões de Projeto

### 2.1 Princípio da Inversão de Dependência (DIP)
As classes de alto nível (GerenciadorDeBatalha e GerenciadorTorneio) deixaram de instanciar suas dependências concretas e passaram a depender de abstrações (interfaces):
- GeradorAleatorio: interface que abstrai geradores de números pseudoaleatórios e testes de probabilidade.
- ServicoTerreno: interface para obter as condições climáticas e de terreno a partir de um relógio.
- RegistroBatalha: interface responsável por receber os eventos do turno de batalha.
- RepositorioResultados: interface de contrato para salvar resultados e compilar estatísticas.
- java.time.Clock: abstração para controle temporal sem depender do horário real do sistema operacional.

Todas as dependências são fornecidas através dos construtores das classes, permitindo a substituição por mocks (@Mock) durante os testes automatizados.

### 2.2 Princípio da Responsabilidade Única (SRP)
As responsabilidades foram distribuídas para evitar classes sobrecarregadas:
- GerenciadorDeBatalha: responsável exclusivamente pelo fluxo de combate entre dois Pokésais, controle de iniciativa, aplicação de dano, status e passivas. A responsabilidade de persistir partidas foi retirada desta classe.
- GerenciadorTorneio: orquestra o chaveamento entre os treinadores, a coleta de entradas no terminal, a entrega de recompensas pós-semifinal e a chamada ao repositório de resultados.
- CalculadoraDano: responsável exclusivamente pelo cálculo matemático de dano, multiplicadores elementais e chances de infligir efeitos de status.
- RegistroBatalhaConsoleImpl: implementação concreta encarregada de exibir as mensagens no terminal.

### 2.3 Princípio Aberto/Fechado (OCP)
Com a definição das interfaces, o sistema pode receber novas implementações sem alterar o código das regras de negócio. Um novo repositório baseado em banco relacional ou em arquivo pode ser acoplado ao GerenciadorTorneio sem qualquer mudança em sua implementação interna.

---

## 3. Classes Refatoradas

| Classe | Alterações Realizadas | Motivação Técnica |
| --- | --- | --- |
| GerenciadorDeBatalha | Remoção de dependências estáticas; construtor passa a receber GeradorAleatorio, ServicoTerreno, Clock e RegistroBatalha. | Isola o combate 1v1 e permite simular turnos completos via mocks sem acoplamento a banco ou console. |
| GerenciadorTorneio | Construtor passa a receber RepositorioResultados, GeradorAleatorio, ServicoTerreno, Clock e RegistroBatalha; inclusão de tratamento de erro ao salvar partida. | Garante a regra de resiliência: falhas ao salvar não invalidam a partida nem interrompem o torneio. |
| CalculadoraDano | Recebe GeradorAleatorio e ServicoTerreno via construtor e Clock por parâmetro em calcularDano. | Permite mockar acertos críticos (6.25%) e falhas de precisão de forma determinística nos testes. |
| ServicoTerrenoImpl | Implementa ServicoTerreno utilizando faixas de horário do Clock para retornar Asfalto Quente (manhã), Canteiro Central (tarde) e Poça de Chuva (noite). | Permite testar a ativação de cada terreno de forma determinística injetando instâncias de Clock.fixed. |
| RepositorioResultadosMemoria | Implementação em memória da interface RepositorioResultados com geração de relatório estatístico (Requisito 04). | Separa a lógica de persistência do motor de jogo. |

---

## 4. Benefícios na Testabilidade com Mockito

A refatoração permitiu atingir os índices de cobertura exigidos pelo edital nas classes de negócio:
1. Controle de Aleatoriedade: com GeradorAleatorio mockado, é possível reproduzir golpes críticos, falhas de precisão e empates de velocidade de forma determinística.
2. Resiliência a Falhas de Persistência: o uso de RepositorioResultados com doThrow permitiu validar que o torneio continua normalmente mesmo se o repositório lançar exceção.
3. Validação de Ordem com InOrder: a interface RegistroBatalha possibilitou auditar a ordem exata de despacho dos eventos de cada turno: ataque -> dano -> status -> terreno -> item.
4. Inspeção de Dados com ArgumentCaptor: possibilitou inspecionar o objeto ResultadoBatalha gravado no repositório, validando nomes dos participantes, vencedor e contagem de turnos.
