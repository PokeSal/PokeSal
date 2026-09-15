package br.edu.ucsal.pokesal.model;

import br.edu.ucsal.pokesal.util.ConstantesJogo;
import java.util.ArrayList;
import java.util.List;

public class Mochila {
	private List<Item> itens;
	private int itensUsados;

	public Mochila() {
		this.itens = new ArrayList<>();
		this.itensUsados = 0;

		this.itens.add(new Item("Poção (+20 HP)", 20));
		this.itens.add(new Item("Super Poção (+40 HP)", 40));
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