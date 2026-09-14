package br.edu.ucsal.pokesal.model;

/**
 * Representa os consumíveis que podem ser guardados na mochila.
 */
public class Item {

	private final String nome;
	private final int pontosCura;

	public Item(String nome, int pontosCura) {
		this.nome = nome;
		this.pontosCura = pontosCura;
	}

	public String getNome() {
		return nome;
	}

	public int getPontosCura() {
		return pontosCura;
	}

}
