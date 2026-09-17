package br.edu.ucsal.pokesal.engine;

import br.edu.ucsal.pokesal.model.EfeitoStatus;
import br.edu.ucsal.pokesal.model.PokeSal;
import br.edu.ucsal.pokesal.model.Terreno;
import br.edu.ucsal.pokesal.model.TipoElemental;
import br.edu.ucsal.pokesal.util.ConstantesJogo;
import java.util.Random;

/**
 * Utilitário responsável pelo cálculo de dano dos ataques e aplicação de efeitos de status em
 * combate.
 */
public class CalculadoraDano {

  /** Construtor privado para evitar a instanciação de uma classe utilitária. */
  private CalculadoraDano() {}

  /**
   * Calcula o dano final de um ataque considerando os atributos do atacante e defensor,
   * efetividade elemental e modificadores do terreno.
   *
   * @param atacante O Pokésal que está realizando o ataque.
   * @param defensor O Pokésal que está recebendo o ataque.
   * @param terreno O ambiente onde a batalha está ocorrendo.
   * @return O valor inteiro do dano final causado (mínimo de 1).
   */
  public static int calcularDano(PokeSal atacante, PokeSal defensor, Terreno terreno) {
    double danoBase =
        ((double) atacante.getAtk() / defensor.getDef()) * ConstantesJogo.DANO_BASE;

    double efetividade = calcularEfetividade(atacante, defensor);
    double danoTerreno = calcularMultiplicadorTerreno(atacante, terreno);

    danoBase *= efetividade * danoTerreno;

    int danoArredondado = (int) Math.round(danoBase);

    return Math.max(1, danoArredondado);
  }

  /**
   * Calcula o multiplicador de efetividade elemental com base no tipo do atacante e do defensor.
   *
   * @param atacante O Pokésal atacante.
   * @param defensor O Pokésal defensor.
   * @return O multiplicador de vantagem elemental (Super Efetivo, Pouco Efetivo ou Neutro).
   */
  public static double calcularEfetividade(PokeSal atacante, PokeSal defensor) {
    boolean ehSuperEfetivo =
        (atacante.getTipo() == TipoElemental.FOGO && defensor.getTipo() == TipoElemental.PLANTA)
            || (atacante.getTipo() == TipoElemental.AGUA
                && defensor.getTipo() == TipoElemental.FOGO)
            || (atacante.getTipo() == TipoElemental.PLANTA
                && defensor.getTipo() == TipoElemental.AGUA);

    boolean ehPoucoEfetivo =
        (atacante.getTipo() == TipoElemental.FOGO && defensor.getTipo() == TipoElemental.AGUA)
            || (atacante.getTipo() == TipoElemental.AGUA
                && defensor.getTipo() == TipoElemental.PLANTA)
            || (atacante.getTipo() == TipoElemental.PLANTA
                && defensor.getTipo() == TipoElemental.FOGO);

    if (ehSuperEfetivo) {
      System.out.println("\n[ELEMENTAL] Ataque SUPER EFETIVO!");
      return ConstantesJogo.MULT_SUPER_EFETIVO;
    } else if (ehPoucoEfetivo) {
      System.out.println("\n[ELEMENTAL] Ataque pouco efetivo...");
      return ConstantesJogo.MULT_POUCO_EFETIVO;
    } else {
      return ConstantesJogo.MULT_NEUTRO;
    }
  }

  /**
   * Calcula o multiplicador de bônus concedido pelo terreno atual ao Pokésal atacante.
   *
   * @param atacante O Pokésal atacante.
   * @param terreno O terreno atual da batalha.
   * @return O multiplicador de bônus do terreno ou o multiplicador neutro.
   */
  public static double calcularMultiplicadorTerreno(PokeSal atacante, Terreno terreno) {
    if (terreno == Terreno.ASFALTO_QUENTE && atacante.getTipo() == TipoElemental.FOGO) {
      System.out.println(
          "\n[TERRENO] O terreno ASFALTO QUENTE potencializou o ataque de fogo!");
      return ConstantesJogo.BONUS_ASFALTO_QUENTE;
    } else if (terreno == Terreno.POCA_CHUVA && atacante.getTipo() == TipoElemental.AGUA) {
      System.out.println(
          "\n[TERRENO] O terreno POÇA DE CHUVA potencializou o ataque de água!");
      return ConstantesJogo.BONUS_POCA_CHUVA;
    } else {
      return ConstantesJogo.MULT_NEUTRO;
    }
  }

  /**
   * Avalia a probabilidade e aplica um efeito de status ao defensor caso ele ainda não possua um.
   *
   * @param atacante O Pokésal que executou o ataque.
   * @param defensor O Pokésal que pode receber o efeito de status.
   */
  public static void aplicarEfeitoStatus(PokeSal atacante, PokeSal defensor) {
    if (defensor.getStatusAtual() != EfeitoStatus.NENHUM) {
      return;
    }

    Random rand = new Random();
    double chanceAplicarEfeito = rand.nextDouble();

    if (chanceAplicarEfeito < ConstantesJogo.CHANCE_EFEITO_STATUS) {
      if (atacante.getTipo() == TipoElemental.FOGO
          && defensor.getTipo() != TipoElemental.FOGO) {
        defensor.setStatusAtual(EfeitoStatus.QUEIMADO);
        System.out.println(
            "\n[STATUS] " + defensor.getNome() + " ficou QUEIMADO pelo ataque!");
      } else if (atacante.getTipo() == TipoElemental.AGUA
          && defensor.getTipo() != TipoElemental.AGUA) {
        defensor.setStatusAtual(EfeitoStatus.PARALISADO);
        System.out.println(
            "\n[STATUS] " + defensor.getNome() + " ficou PARALISADO pelo ataque!");
      } else if (atacante.getTipo() == TipoElemental.PLANTA
          && defensor.getTipo() != TipoElemental.PLANTA) {
        defensor.setStatusAtual(EfeitoStatus.ENVENENADO);
        System.out.println(
            "\n[STATUS] " + defensor.getNome() + " ficou ENVENENADO pelo ataque!");
      }
    }
  }
}