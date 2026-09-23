package br.edu.ucsal.pokesal.model;

/**
 * Representa os itens consumíveis que podem ser mantidos na mochila e utilizados na batalha.
 */
public class Item {

  private final String nome;
  private final int pontosCura;
  private final String curaStatus;

  /**
   * Construtor da classe Item.
   *
   * @param nome Nome de exibição do item.
   * @param pontosCura Quantidade de pontos de HP restaurados pelo item.
   * @param curaStatus Tipo de status removido pelo item ("NENHUM" ou "TODOS").
   */
  public Item(String nome, int pontosCura, String curaStatus) {
    this.nome = nome;
    this.pontosCura = pontosCura;
    this.curaStatus = curaStatus;
  }

  /**
   * Obtém o nome formatado do item.
   *
   * @return O nome do item.
   */
  public String getNome() {
    return nome;
  }

  /**
   * Obtém a quantidade de pontos de cura fornecida pelo item.
   *
   * @return Os pontos de cura.
   */
  public int getPontosCura() {
    return pontosCura;
  }

  /**
   * Obtém o tipo de efeito de cura de status que o item possui.
   *
   * @return A regra de cura de status.
   */
  public String getCuraStatus() {
    return curaStatus;
  }
}
