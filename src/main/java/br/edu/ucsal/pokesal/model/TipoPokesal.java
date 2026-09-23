package br.edu.ucsal.pokesal.model;

/**
 * Define os atributos base e inicializadores de cada espécie de Pokésal disponível.
 */
public enum TipoPokesal {

  BULBASAL("BulbaSal", 95, 57, 67, 45, TipoElemental.PLANTA), CHARSAL("CharSal", 99, 56, 67, 65,
      TipoElemental.FOGO), SQUIRTSAL("SquirtSal", 94, 49, 65, 43, TipoElemental.AGUA), CHIKOSAL(
          "ChikoSal", 95, 49, 65, 45, TipoElemental.PLANTA), CYNDASAL("CyndaSal", 89, 56, 67, 65,
              TipoElemental.FOGO), TOTOSAL("TotoSal", 90, 55, 66, 43, TipoElemental.AGUA);

  private final String nome;
  private final int hpBase;
  private final int atkBase;
  private final int defBase;
  private final int spdBase;
  private final TipoElemental tipo;

  private TipoPokesal(String nome, int hpBase, int atkBase, int defBase, int spdBase,
      TipoElemental tipo) {
    this.nome = nome;
    this.hpBase = hpBase;
    this.atkBase = atkBase;
    this.defBase = defBase;
    this.spdBase = spdBase;
    this.tipo = tipo;
  }

  /**
   * Obtém o nome original do Pokésal.
   *
   * @return O nome do Pokésal.
   */
  public String getNome() {
    return nome;
  }

  /**
   * Retorna o nome amigável do Pokésal para a interface do usuário.
   *
   * @return O nome formatado do Pokésal.
   */
  public String getNomeFormatado() {
    return nome;
  }

  /**
   * Obtém o valor base de pontos de vida (HP) da espécie.
   *
   * @return O HP base.
   */
  public int getHpBase() {
    return hpBase;
  }

  /**
   * Obtém o valor base de ataque (ATK) da espécie.
   *
   * @return O ataque base.
   */
  public int getAtkBase() {
    return atkBase;
  }

  /**
   * Obtém o valor base de defesa (DEF) da espécie.
   *
   * @return A defesa base.
   */
  public int getDefBase() {
    return defBase;
  }

  /**
   * Obtém o valor base de velocidade (SPD) da espécie.
   *
   * @return A velocidade base.
   */
  public int getSpdBase() {
    return spdBase;
  }

  /**
   * Obtém o tipo elemental associado à espécie.
   *
   * @return O enum TipoElemental.
   */
  public TipoElemental getTipo() {
    return tipo;
  }
}
