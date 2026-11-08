package br.edu.ucsal.pokesal.model;

import br.edu.ucsal.pokesal.util.ConstantesJogo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa a mochila de itens do Pokésal com limite de capacidade e uso por
 * batalha.
 */
public class Mochila {

	private final List<Item> itens;
	private int usosNaBatalha;

	public Mochila() {
		this.itens = new ArrayList<>();
		this.itens.add(new Item(ConstantesJogo.NOME_POCAO, ConstantesJogo.CURA_POCAO, ConstantesJogo.STATUS_NENHUM));
		this.itens.add(new Item(ConstantesJogo.NOME_SUPER_POCAO, ConstantesJogo.CURA_SUPER_POCAO,
				ConstantesJogo.STATUS_NENHUM));
		this.itens.add(
				new Item(ConstantesJogo.NOME_CURA_TOTAL, ConstantesJogo.CURA_NENHUMA, ConstantesJogo.STATUS_TODOS));
		this.usosNaBatalha = 0;
	}

	public boolean podeUsarItem() {
		return this.usosNaBatalha < ConstantesJogo.LIMITE_USO_ITENS && !this.itens.isEmpty();
	}

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

	public boolean adicionarItem(Item item) {
		if (this.itens.size() < 3) {
			this.itens.add(item);
			return true;
		}
		return false;
	}

	/**
	 * Reseta o contador de usos para renovar a cota de 2 itens na próxima partida
	 * do torneio.
	 */
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