package br.edu.ucsal.pokesal.model;

/**
 * Importa os itens da classe Item e define o contador para controle do uso de itens na batalha.
 */

import java.util.ArrayList;
import java.util.List;

import br.edu.ucsal.pokesal.util.ConstantesJogo;

public class Mochila {
	private List<Item> itens;
	private int itensUsados;

	public Mochila() {
		this.itens = new ArrayList<>();
		this.itensUsados = 0;
	}

	public void adicionarItem(Item item) {
		itens.add(item);
	}

	public boolean usarItem() {
		if (itensUsados < ConstantesJogo.LIMITE_USO_ITENS) {
			itensUsados++;
			return true;
		}
		return false;
	}

}
