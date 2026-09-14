package br.edu.ucsal.pokesal.model;

/**
 * Define o construtor para a selecionar e nomear o PokeSal, criação dos metodos
 * de verificação de estado(Vivo ou Morto) e recebimento de dano.
 */

public class PokeSal {

	private String nome;
	private int hpMaximo;
	private int hpAtual;
	private int atk;
	private int def;
	private int spd;
	private TipoElemental tipo;

	public PokeSal(TipoPokesal tipo) {
		this.nome = tipo.getNome();
		this.hpMaximo = tipo.getHpBase();
		this.hpAtual = hpMaximo;
		this.atk = tipo.getAtkBase();
		this.def = tipo.getDefBase();
		this.spd = tipo.getSpdBase();
		this.tipo = tipo.getTipo();
	}

	/**
	 * 
	 * @return Verifica se a vida atual do pokesal é maior que zero para definir se
	 *         está vivo ou não
	 */
	public boolean isVivo() {
		if (hpAtual > 0) {
			return true;
		}
		return false;
	}

	/**
	 * Subtrai o dano recebido da vida atual do pokesal e garante que não terão
	 * valores negativos de vida.
	 * 
	 * @param quantidadeDano
	 */
	public void receberDano(int quantidadeDano) {
		this.hpAtual = this.hpAtual - quantidadeDano;

		if (this.hpAtual < 0) {
			this.hpAtual = 0;

		}

	}

	public String getNome() {
		return nome;
	}

	public int getHpMaximo() {
		return hpMaximo;
	}

	public int getHpAtual() {
		return hpAtual;
	}

	public int getAtk() {
		return atk;
	}

	public int getDef() {
		return def;
	}

	public int getSpd() {
		return spd;
	}

	public TipoElemental getTipo() {
		return tipo;
	}
}
