package br.edu.ucsal.pokesal.model;

import br.edu.ucsal.pokesal.util.ConstantesJogo;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa a mochila de itens consumíveis pertencente a um Pokésal.
 */
public class Mochila {

  private List<Item> itens;
  private int itensUsados;

  /**
   * Construtor padrão da Mochila que inicializa os itens consumíveis disponíveis.
   */
  public Mochila() {
    this.itens = new ArrayList<>();
    this.itensUsados = 0;

    this.itens.add(new Item(ConstantesJogo.NOME_POCAO, ConstantesJogo.CURA_POCAO,
        ConstantesJogo.STATUS_NENHUM));
    this.itens.add(new Item(ConstantesJogo.NOME_SUPER_POCAO, ConstantesJogo.CURA_SUPER_POCAO,
        ConstantesJogo.STATUS_NENHUM));
    this.itens.add(new Item(ConstantesJogo.NOME_CURA_TOTAL, ConstantesJogo.CURA_NENHUMA,
        ConstantesJogo.STATUS_TODOS));
  }

  /**
   * Obtém a lista de itens atualmente presentes na mochila.
   *
   * @return A lista de itens.
   */
  public List<Item> getItens() {
    return itens;
  }

  /**
   * Verifica se o treinador ainda pode utilizar itens respeitando o limite da batalha.
   *
   * @return Verdadeiro se ainda puder usar item, falso caso contrário.
   */
  public boolean podeUsarItem() {
    return itensUsados < ConstantesJogo.LIMITE_USO_ITENS && !itens.isEmpty();
  }

  /**
   * Utiliza e remove um item da mochila pelo seu índice na lista.
   *
   * @param indice Posição do item a ser utilizado na lista.
   * @return O objeto Item utilizado ou null caso a ação seja inválida.
   */
  public Item usarItemPorIndice(int indice) {
    if (podeUsarItem() && indice >= 0 && indice < itens.size()) {
      itensUsados++;
      return itens.remove(indice);
    }
    return null;
  }

  /**
   * Obtém a quantidade de itens que já foram consumidos durante a batalha.
   *
   * @return A quantidade de itens usados.
   */
  public int getItensUsados() {
    return itensUsados;
  }
}
