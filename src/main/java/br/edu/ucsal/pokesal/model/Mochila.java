package br.edu.ucsal.pokesal.model;

import java.util.ArrayList;
import java.util.List;
import br.edu.ucsal.pokesal.util.ConstantesJogo;

public class Mochila {
	private List<Item> itens;
	private int itensUsados;

	public Mochila() {
		this.itens = new ArrayList<>();
		this.itensUsados = 0;

		this.itens.add(new Item("Água do Bebedouro da UCSal (+20 HP)", 20));
		this.itens.add(new Item("Água da Torneira da UCSal (+40 HP)", 40));
	}

	public List<Item> getItens() {
		return itens;
	}

	public boolean podeUsarItem() {
		return itensUsados < ConstantesJogo.LIMITE_USO_ITENS && !itens.isEmpty();
	}

	public Item usarItemPorIndice(int indice) {
		if (podeUsarItem() && indice >= 0 && indice < itens.size()) {
			itensUsados++;
			return itens.remove(indice);
		}
		return null;
	}

	public int getItensUsados() {
		return itensUsados;
	}
}