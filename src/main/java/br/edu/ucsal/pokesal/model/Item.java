package br.edu.ucsal.pokesal.model;

/**
 * Representa os consumíveis que podem ser guardados na mochila.
 */
public class Item {

	private final String nome;
	private final int pontosCura;
	private final String curaStatus;

	public Item(String nome, int pontosCura, String curaStatus) {
		this.nome = nome;
		this.pontosCura = pontosCura;
		this.curaStatus = curaStatus;
	}

	public String getNome() {
		return nome;
	}

	public int getPontosCura() {
		return pontosCura;
	}

	public String getCuraStatus() {
		return curaStatus;
	}

}
