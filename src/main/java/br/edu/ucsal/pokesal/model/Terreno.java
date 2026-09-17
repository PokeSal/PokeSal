package br.edu.ucsal.pokesal.model;

/**
 * Enumeração dos terrenos disponíveis para o combate no Estacionamento da UCSal.
 */
public enum Terreno {

  /** Aumenta o dano de golpes do tipo Fogo em 15%. */
  ASFALTO_QUENTE("Asfalto Quente"),

  /** Aumenta o dano de golpes do tipo Água em 10%. */
  POCA_CHUVA("Poça de Chuva"),

  /** Regenera 5% do HP máximo de Pokésais do tipo Planta a cada turno. */
  CANTEIRO_CENTRAL("Canteiro Central");

  private final String nomeFormatado;

  private Terreno(String nomeFormatado) {
    this.nomeFormatado = nomeFormatado;
  }

  /**
   * Obtém o nome amigável do terreno para exibição na interface do console.
   *
   * @return O nome formatado do terreno.
   */
  public String getNomeFormatado() {
    return nomeFormatado;
  }
}