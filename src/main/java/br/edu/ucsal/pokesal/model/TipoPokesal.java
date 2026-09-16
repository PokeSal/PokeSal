package br.edu.ucsal.pokesal.model;

/**
 * Define os atributos base dos Pokesais e seus respectivos tipos.
 */

public enum TipoPokesal {
	BULBASAL(45, 57, 57, 45, TipoElemental.PLANTA,"BulbaSal"), 
	CHARSAL(39, 56, 47, 65, TipoElemental.FOGO,"CharSal"),
	SQUIRTSAL(44, 49, 65, 43, TipoElemental.AGUA,"SquirtSal"),
	CHIKOSAL(45, 49, 65, 45, TipoElemental.PLANTA,"ChikoSal"),
	CYNDASAL(39, 56, 47, 65, TipoElemental.FOGO,"CyndaSal"), 
	TOTOSAL(50, 55, 56, 43, TipoElemental.AGUA,"TotoSal");

	private final int hpBase;
	private final int atkBase;
	private final int defBase;
	private final int spdBase;
	private final TipoElemental tipo;
	private final String nomeFormatado;

	private TipoPokesal(int hpBase, int atkBase, int defBase, int spdBase, TipoElemental tipo, String nomeFormatado) {
		this.hpBase = hpBase;
		this.atkBase = atkBase;
		this.defBase = defBase;
		this.spdBase = spdBase;
		this.tipo = tipo;
		this.nomeFormatado = nomeFormatado;
	}
/**
 * Retorna o nome amígavel do usuário para a interface do usuário.
 * @return
 */
	public String getNomeFormatado() {
		return nomeFormatado;
	}

	public int getHpBase() {
		return hpBase;
	}

	public int getAtkBase() {
		return atkBase;
	}

	public int getDefBase() {
		return defBase;
	}

	public int getSpdBase() {
		return spdBase;
	}

	public TipoElemental getTipo() {
		return tipo;
	}
	

}
