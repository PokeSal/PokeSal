package br.edu.ucsal.pokesal.model;

/**
 * Define os atributos base dos Pokesais e seus respectivos tipos.
 */

public enum TipoPokesal {
	BULBASAL("BulbaSal", 95, 57, 67, 45, TipoElemental.PLANTA), 
	CHARSAL("CharSal", 99, 56, 67, 65, TipoElemental.FOGO),
	SQUIRTSAL("SquirtSal", 94, 49, 65, 43, TipoElemental.AGUA),
	CHIKOSAL("ChikoSal", 95, 49, 65, 45, TipoElemental.PLANTA),
	CYNDASAL("CyndaSal", 89, 56, 67, 65, TipoElemental.FOGO), 
	TOTOSAL("TotoSal", 90, 55, 66, 43, TipoElemental.AGUA);

	private final String nome;
	private final int hpBase;
	private final int atkBase;
	private final int defBase;
	private final int spdBase;
	private final TipoElemental tipo;
	

	private TipoPokesal(String nome, int hpBase, int atkBase, int defBase, int spdBase, TipoElemental tipo) {
		this.nome = nome;
		this.hpBase = hpBase;
		this.atkBase = atkBase;
		this.defBase = defBase;
		this.spdBase = spdBase;
		this.tipo = tipo;
		
	}
public String getNome() {
		return nome;
	}
/**
 * Retorna o nome amígavel do usuário para a interface do usuário.
 * @return
 */
	public String getNomeFormatado() {
		return nome;
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
