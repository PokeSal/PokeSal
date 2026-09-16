package br.edu.ucsal.pokesal.model;

/**
 * Define os terrenos fixos.
 */
public enum Terreno {
	ASFALTO_QUENTE("Asfalto Quente"), POCA_CHUVA("Poça Chuva"), CANTEIRO_CENTRAL("Canteiro Central");
	private final String nomeFormatado;

	private Terreno(String nomeFormatado) {
		this.nomeFormatado = nomeFormatado;
	}

	public String getNomeFormatado() {
		return nomeFormatado;
	}
	
	
}
