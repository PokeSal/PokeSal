package br.edu.ucsal.pokesal.engine;

import br.edu.ucsal.pokesal.model.EfeitoStatus;
import br.edu.ucsal.pokesal.model.PokeSal;
import br.edu.ucsal.pokesal.model.Terreno;
import br.edu.ucsal.pokesal.model.TipoElemental;
import br.edu.ucsal.pokesal.util.ConstantesJogo;
import java.util.Random;

/**
 * Gerencia a mecânica de combate, controle de turnos, alteração de terrenos e verificação de
 * condições de vitória entre dois Pokésais.
 */
public class GerenciadorDeBatalha {

  private PokeSal pokesal1;
  private PokeSal pokesal2;
  private PokeSal primeiroAtacante;
  private PokeSal segundoAtacante;
  private Terreno terreno;
  private int contadorTurno = 0;
  private int acoesRodadaAtual = 0;
  private PokeSal ultimoAtacante;
  private PokeSal vencedor;

  /**
   * Construtor que inicializa a batalha com dois combatentes e o terreno inicial.
   *
   * @param pokesal1 O Pokésal do primeiro treinador.
   * @param pokesal2 O Pokésal do segundo treinador.
   * @param terreno O terreno inicial sorteado para a batalha.
   */
  public GerenciadorDeBatalha(PokeSal pokesal1, PokeSal pokesal2, Terreno terreno) {
    this.pokesal1 = pokesal1;
    this.pokesal2 = pokesal2;
    this.terreno = terreno;
    this.vencedor = null;
  }

  /**
   * Define a ordem de atuação do turno com base na velocidade (SPD) dos Pokésais. Em caso de empate
   * de velocidade, realiza um sorteio aleatório para a prioridade.
   */
  public void definirOrdemAtuacao() {
    if (pokesal1.getSpd() > pokesal2.getSpd()) {
      this.primeiroAtacante = pokesal1;
      this.segundoAtacante = pokesal2;
    } else if (pokesal2.getSpd() > pokesal1.getSpd()) {
      this.primeiroAtacante = pokesal2;
      this.segundoAtacante = pokesal1;
    } else {
      Random rand = new Random();
      if (rand.nextBoolean()) {
        this.primeiroAtacante = pokesal1;
        this.segundoAtacante = pokesal2;
      } else {
        this.primeiroAtacante = pokesal2;
        this.segundoAtacante = pokesal1;
      }
    }
  }

  /**
   * Registra a ação realizada por um Pokésal e avança os turnos do combate quando ambos realizam
   * suas ações, acionando os efeitos de fim de turno.
   *
   * @param atacante O Pokésal que executou a ação no turno atual.
   */
  public void registrarAcao(PokeSal atacante) {
    this.ultimoAtacante = atacante;
    this.acoesRodadaAtual++;

    if (this.acoesRodadaAtual >= 2) {
      this.acoesRodadaAtual = 0;
      this.contadorTurno++;

      aplicarDanoStatusFimDeTurno();
      aplicarCuraCanteiroCentral();
      mudarTerreno();
      verificarVencedor();
    }
  }

  /**
   * Processa a penalidade de dano por recuo em Pokésais que realizaram sequências de ataques sem
   * utilizar itens para descanso.
   *
   * @param atacante O Pokésal que realizou o ataque.
   */
  public void processarRecuoPorAtaque(PokeSal atacante) {
    if (atacante != null && atacante.isVivo()) {
      int ataques = atacante.incrementarAtaquesConsecutivos();

      if (ataques > 0 && ataques % ConstantesJogo.INTERVALO_TURNO_RECUO == 0) {
        int danoRecuo =
            (int) Math.round(atacante.getHpMaximo() * ConstantesJogo.PERCENTUAL_DANO_RECUO);
        atacante.receberDano(danoRecuo);
        System.out.println("\n[RECUO] " + atacante.getNome() + " sofreu " + danoRecuo
            + " de dano de recuo por realizar " + ataques + " ataques seguidos sem usar item!");
      }
    }
  }

  /**
   * Aplica a regeneração de HP do terreno Canteiro Central para Pokésais do tipo Planta.
   */
  public void aplicarCuraCanteiroCentral() {
    if (this.terreno == Terreno.CANTEIRO_CENTRAL) {
      if (pokesal1.isVivo() && pokesal1.getTipo() == TipoElemental.PLANTA) {
        int curaCanteiro =
            (int) Math.round(pokesal1.getHpMaximo() * ConstantesJogo.BONUS_CANTEIRO_CENTRAL);
        pokesal1.curar(curaCanteiro);
        System.out.println("\n[TERRENO] O Canteiro Central restaurou " + curaCanteiro + " de HP de "
            + pokesal1.getNome() + "!");
      }

      if (pokesal2.isVivo() && pokesal2.getTipo() == TipoElemental.PLANTA) {
        int curaCanteiro =
            (int) Math.round(pokesal2.getHpMaximo() * ConstantesJogo.BONUS_CANTEIRO_CENTRAL);
        pokesal2.curar(curaCanteiro);
        System.out.println("\n[TERRENO] O Canteiro Central restaurou " + curaCanteiro + " de HP de "
            + pokesal2.getNome() + "!");
      }
    }
  }

  /**
   * Realiza a rotação dinâmica de terreno a cada intervalo configurado de turnos.
   */
  public void mudarTerreno() {
    if (this.contadorTurno > 0 && this.contadorTurno % ConstantesJogo.MUDANCA_TERRENO_TURNO == 0) {
      Random rand = new Random();
      Terreno[] opcoesTerreno = Terreno.values();
      int sorteioTerreno = rand.nextInt(opcoesTerreno.length);
      Terreno terrenoSorteado = opcoesTerreno[sorteioTerreno];
      this.terreno = terrenoSorteado;

      System.out.println("\n[TERRENO] O ambiente da batalha mudou para: "
          + terrenoSorteado.getNomeFormatado() + "!");
    }
  }

  /**
   * Aplica o dano residual dos efeitos de status (Queimado ou Envenenado) ao final do turno.
   */
  public void aplicarDanoStatusFimDeTurno() {
    if (!this.pokesal1.isVivo() || !this.pokesal2.isVivo()) {
      return;
    }

    if (this.pokesal1.getStatusAtual() == EfeitoStatus.QUEIMADO
        || this.pokesal1.getStatusAtual() == EfeitoStatus.ENVENENADO) {
      int danoStatus =
          (int) Math.round(this.pokesal1.getHpMaximo() * ConstantesJogo.PERCENTUAL_DANO_STATUS);
      this.pokesal1.receberDano(danoStatus);
      System.out.println("\n[STATUS] " + this.pokesal1.getNome() + " sofreu " + danoStatus
          + " de dano por estar " + this.pokesal1.getStatusAtual() + "!");
    }

    if (this.pokesal2.getStatusAtual() == EfeitoStatus.QUEIMADO
        || this.pokesal2.getStatusAtual() == EfeitoStatus.ENVENENADO) {
      int danoStatus =
          (int) Math.round(this.pokesal2.getHpMaximo() * ConstantesJogo.PERCENTUAL_DANO_STATUS);
      this.pokesal2.receberDano(danoStatus);
      System.out.println("\n[STATUS] " + this.pokesal2.getNome() + " sofreu " + danoStatus
          + " de dano por estar " + this.pokesal2.getStatusAtual() + "!");
    }
  }

  /**
   * Executa a sequência padrão de uma rodada completa de combate com troca de ataques.
   */
  public void executarRodada() {
    definirOrdemAtuacao();

    int danoPrimeiro =
        CalculadoraDano.calcularDano(this.primeiroAtacante, this.segundoAtacante, this.terreno);

    this.segundoAtacante.receberDano(danoPrimeiro);

    this.ultimoAtacante = this.primeiroAtacante;

    if (!this.segundoAtacante.isVivo()) {
      verificarVencedor();
      return;
    }

    int danoSegundo =
        CalculadoraDano.calcularDano(this.segundoAtacante, this.primeiroAtacante, this.terreno);

    this.primeiroAtacante.receberDano(danoSegundo);

    this.ultimoAtacante = this.segundoAtacante;

    if (!this.primeiroAtacante.isVivo()) {
      verificarVencedor();
      return;
    }

    this.contadorTurno++;

    if (!this.ultimoAtacante.isVivo()) {
      verificarVencedor();
      return;
    }
  }

  private void verificarVencedor() {
    if (!this.pokesal1.isVivo() || !this.pokesal2.isVivo()) {
      if (this.pokesal1.isVivo()) {
        this.vencedor = this.pokesal1;
      } else {
        this.vencedor = this.pokesal2;
      }
    }
  }

  /**
   * Verifica se ambos os Pokésais possuem o mesmo valor no atributo de velocidade (SPD).
   *
   * @return Verdadeiro se as velocidades forem idênticas, falso caso contrário.
   */
  public boolean isVelocidadeIgual() {
    return pokesal1.getSpd() == pokesal2.getSpd();
  }

  /**
   * Obtém o Pokésal declarado vencedor da batalha.
   *
   * @return O Pokésal vencedor ou null caso a batalha ainda esteja em andamento.
   */
  public PokeSal getVencedor() {
    return this.vencedor;
  }

  /**
   * Obtém o Pokésal definido para agir em primeiro lugar no turno.
   *
   * @return O Pokésal com maior velocidade ou sorteado em primeiro.
   */
  public PokeSal getPrimeiroAtacante() {
    return primeiroAtacante;
  }

  /**
   * Obtém o Pokésal definido para agir em segundo lugar no turno.
   *
   * @return O Pokésal com menor velocidade ou sorteado em segundo.
   */
  public PokeSal getSegundoAtacante() {
    return segundoAtacante;
  }

  /**
   * Obtém o terreno atual onde a batalha está ocorrendo.
   *
   * @return O objeto Terreno ativo.
   */
  public Terreno getTerreno() {
    return this.terreno;
  }
}
