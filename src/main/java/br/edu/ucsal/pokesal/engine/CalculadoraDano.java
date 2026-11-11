package br.edu.ucsal.pokesal.engine;

import br.edu.ucsal.pokesal.model.EfeitoStatus;
import br.edu.ucsal.pokesal.model.PokeSal;
import br.edu.ucsal.pokesal.model.Terreno;
import br.edu.ucsal.pokesal.model.TipoElemental;
import br.edu.ucsal.pokesal.service.GeradorAleatorio;
import br.edu.ucsal.pokesal.service.ServicoTerreno;
import br.edu.ucsal.pokesal.util.ConstantesJogo;
import java.time.Clock;

/** Componente responsável pelo cálculo de dano dos ataques e aplicação de efeitos de status. */
public class CalculadoraDano {

  private static final int DANO_MINIMO = 1;

  private final GeradorAleatorio gerador;
  private final ServicoTerreno servicoTerreno;

  /**
   * Construtor com injeção de dependências.
   *
   * @param gerador Provedor de aleatoriedade do combate.
   * @param servicoTerreno Provedor de condições do terreno.
   */
  public CalculadoraDano(GeradorAleatorio gerador, ServicoTerreno servicoTerreno) {
    this.gerador = gerador;
    this.servicoTerreno = servicoTerreno;
  }

  /**
   * Calcula o dano final do ataque considerando precisão, multiplicadores e crítico.
   *
   * @param atacante Pokésal realizador do ataque.
   * @param defensor Pokésal alvo.
   * @param clock Instância do relógio para consultar o terreno.
   * @return Valor do dano final causado.
   */
  public int calcularDano(PokeSal atacante, PokeSal defensor, Clock clock) {
    int sorteioPrecisao =
        gerador.gerarNumero(ConstantesJogo.FAIXA_MIN_SORTEIO, ConstantesJogo.FAIXA_MAX_SORTEIO);

    if (sorteioPrecisao > atacante.getPrecisao()) {
      return ConstantesJogo.DANO_ERRO;
    }

    Terreno terrenoAtual = servicoTerreno.obterTerrenoAtual(clock);

    double danoBase = ((double) atacante.getAtk() / defensor.getDef()) * ConstantesJogo.DANO_BASE;
    double efetividade = calcularEfetividade(atacante, defensor);
    double danoTerreno = calcularMultiplicadorTerreno(atacante, terrenoAtual);

    danoBase *= efetividade * danoTerreno;
    int danoArredondado = (int) Math.round(danoBase);

    if (gerador.testarChance(ConstantesJogo.CHANCE_CRITICO)) {
      danoArredondado = (int) Math.round(danoArredondado * ConstantesJogo.MULTIPLICADOR_CRITICO);
    }

    return Math.max(DANO_MINIMO, danoArredondado);
  }

  /**
   * Avalia e aplica status ao defensor se o sorteio de chance for bem-sucedido.
   *
   * @param atacante Pokésal realizador do ataque.
   * @param defensor Pokésal alvo.
   */
  public void aplicarEfeitoStatus(PokeSal atacante, PokeSal defensor) {
    if (defensor.getStatusAtual() != EfeitoStatus.NENHUM) {
      return;
    }

    if (gerador.testarChance(ConstantesJogo.CHANCE_EFEITO_STATUS)) {
      if (atacante.getTipo() == TipoElemental.FOGO && defensor.getTipo() != TipoElemental.FOGO) {
        defensor.setStatusAtual(EfeitoStatus.QUEIMADO);
      } else if (atacante.getTipo() == TipoElemental.AGUA
          && defensor.getTipo() != TipoElemental.AGUA) {
        defensor.setStatusAtual(EfeitoStatus.PARALISADO);
      } else if (atacante.getTipo() == TipoElemental.PLANTA
          && defensor.getTipo() != TipoElemental.PLANTA) {
        defensor.setStatusAtual(EfeitoStatus.ENVENENADO);
      }
    }
  }

  /**
   * Calcula o multiplicador de efetividade entre os tipos elementais.
   *
   * @param atacante Pokésal atacante.
   * @param defensor Pokésal defensor.
   * @return multiplicador super efetivo, pouco efetivo ou neutro.
   */
  public double calcularEfetividade(PokeSal atacante, PokeSal defensor) {
    boolean ehSuperEfetivo = (atacante.getTipo() == TipoElemental.FOGO
        && defensor.getTipo() == TipoElemental.PLANTA)
        || (atacante.getTipo() == TipoElemental.AGUA && defensor.getTipo() == TipoElemental.FOGO)
        || (atacante.getTipo() == TipoElemental.PLANTA && defensor.getTipo() == TipoElemental.AGUA);

    boolean ehPoucoEfetivo = (atacante.getTipo() == TipoElemental.FOGO
        && defensor.getTipo() == TipoElemental.AGUA)
        || (atacante.getTipo() == TipoElemental.AGUA && defensor.getTipo() == TipoElemental.PLANTA)
        || (atacante.getTipo() == TipoElemental.PLANTA && defensor.getTipo() == TipoElemental.FOGO);

    if (ehSuperEfetivo) {
      return ConstantesJogo.MULT_SUPER_EFETIVO;
    } else if (ehPoucoEfetivo) {
      return ConstantesJogo.MULT_POUCO_EFETIVO;
    } else {
      return ConstantesJogo.MULT_NEUTRO;
    }
  }

  /**
   * Calcula o multiplicador de dano concedido pelo terreno ao tipo do atacante.
   *
   * @param atacante Pokésal atacante.
   * @param terreno terreno ativo.
   * @return bônus do terreno ou multiplicador neutro.
   */
  public double calcularMultiplicadorTerreno(PokeSal atacante, Terreno terreno) {
    if (terreno == Terreno.ASFALTO_QUENTE && atacante.getTipo() == TipoElemental.FOGO) {
      return ConstantesJogo.BONUS_ASFALTO_QUENTE;
    } else if (terreno == Terreno.POCA_CHUVA && atacante.getTipo() == TipoElemental.AGUA) {
      return ConstantesJogo.BONUS_POCA_CHUVA;
    } else {
      return ConstantesJogo.MULT_NEUTRO;
    }
  }
}
