package br.edu.ucsal.pokesal.model;

import br.edu.ucsal.pokesal.util.ConstantesJogo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Representa a mochila de itens do Pokésal com limite de capacidade e uso por batalha. */
public class Mochila {

  private static final int CAPACIDADE_MAXIMA = 3;

  private final List<Item> itens;
  private int usosNaBatalha;

  /** Cria a mochila com os itens iniciais: poção, super poção e cura total. */
  public Mochila() {
    this.itens = new ArrayList<>();
    this.itens.add(
        new Item(
            ConstantesJogo.NOME_POCAO, ConstantesJogo.CURA_POCAO, ConstantesJogo.STATUS_NENHUM));
    this.itens.add(
        new Item(
            ConstantesJogo.NOME_SUPER_POCAO,
            ConstantesJogo.CURA_SUPER_POCAO,
            ConstantesJogo.STATUS_NENHUM));
    this.itens.add(
        new Item(
            ConstantesJogo.NOME_CURA_TOTAL,
            ConstantesJogo.CURA_NENHUMA,
            ConstantesJogo.STATUS_TODOS));
    this.usosNaBatalha = 0;
  }

  /**
   * Indica se ainda é possível usar um item nesta batalha.
   *
   * @return {@code true} se o limite de usos não foi atingido e há itens na mochila.
   */
  public boolean podeUsarItem() {
    return this.usosNaBatalha < ConstantesJogo.LIMITE_USO_ITENS && !this.itens.isEmpty();
  }

  /**
   * Usa e remove o item da posição informada.
   *
   * @param indice posição do item na mochila.
   * @return o item removido, ou {@code null} se o índice for inválido.
   * @throws IllegalStateException se o limite de uso de itens da batalha foi atingido.
   */
  public Item usarItemPorIndice(int indice) {
    if (!podeUsarItem()) {
      throw new IllegalStateException("Limite de uso de itens atingido nesta batalha!");
    }
    if (indice >= 0 && indice < this.itens.size()) {
      this.usosNaBatalha++;
      return this.itens.remove(indice);
    }
    return null;
  }

  /**
   * Adiciona um item à mochila, respeitando a capacidade máxima.
   *
   * @param item item a ser adicionado.
   * @return {@code true} se foi adicionado; {@code false} se a mochila está cheia.
   */
  public boolean adicionarItem(Item item) {
    if (this.itens.size() < CAPACIDADE_MAXIMA) {
      this.itens.add(item);
      return true;
    }
    return false;
  }

  /** Reseta o contador de usos para renovar a cota de 2 itens na próxima partida do torneio. */
  public void resetarUsosBatalha() {
    this.usosNaBatalha = 0;
  }

  public List<Item> getItens() {
    return Collections.unmodifiableList(itens);
  }

  public int getUsosNaBatalha() {
    return usosNaBatalha;
  }
}
