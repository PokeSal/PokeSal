package br.edu.ucsal.pokesal.model;

/**
 * Define os atributos base dos Pokesais e seus respectivos tipos.
 */

public enum TipoPokesal {
	BULBASAL(45, 57, 57, 45, TipoElemental.PLANTA), CHARSAL(39, 56, 47, 65, TipoElemental.FOGO),
	SQUIRTSAL(44, 49, 65, 43, TipoElemental.AGUA), CHIKOSAL(45, 49, 65, 45, TipoElemental.PLANTA),
	CYNDASAL(39, 56, 47, 65, TipoElemental.FOGO), TOTOSAL(50, 55, 56, 43, TipoElemental.AGUA);

	private final int hpBase;
	private final int atkBase;
	private final int defBase;
	private final int spdBase;
	private final TipoElemental tipo;

	private TipoPokesal(int hpBase, int atkBase, int defBase, int spdBase, TipoElemental tipo) {
		this.hpBase = hpBase;
		this.atkBase = atkBase;
		this.defBase = defBase;
		this.spdBase = spdBase;
		this.tipo = tipo;
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
