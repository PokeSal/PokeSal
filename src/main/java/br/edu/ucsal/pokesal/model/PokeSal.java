package br.edu.ucsal.pokesal.model;

import br.edu.ucsal.pokesal.util.ConstantesJogo;

public class PokeSal {

	private String nome;
	private int hpMaximo;
	private int hpAtual;
	private int atk;
	private int def;
	private int spd;
	private TipoElemental tipo;
	private boolean passivaDefesaAtivada;
	private Mochila mochila;
	private EfeitoStatus statusAtual = EfeitoStatus.NENHUM;

	public PokeSal(TipoPokesal tipo) {
		this.nome = tipo.getNomeFormatado();
		this.hpMaximo = tipo.getHpBase();
		this.hpAtual = hpMaximo;
		this.atk = tipo.getAtkBase();
		this.def = tipo.getDefBase();
		this.spd = tipo.getSpdBase();
		this.tipo = tipo.getTipo();
		this.passivaDefesaAtivada = false;
		this.mochila = new Mochila();
	}

	public boolean isVivo() {
		return this.hpAtual > 0;
	}

	public void receberDano(int quantidadeDano) {
		this.hpAtual -= quantidadeDano;

		if (this.hpAtual < 0) {
			this.hpAtual = 0;
		}

		if (this.hpAtual > 0 && !passivaDefesaAtivada
				&& this.hpAtual <= (hpMaximo * ConstantesJogo.LIMITAR_PASSIVA_DEFESA)) {
			this.def = (int) Math.round(this.def * ConstantesJogo.MULTIPLICADOR_PASSIVA_DEFESA);
			this.passivaDefesaAtivada = true;
			System.out.println("\n[PASSIVA] Passiva de defesa ativada!");
		}
	}

	public void curar(int quantidadeCura) {
		this.hpAtual += quantidadeCura;
		if (this.hpAtual > this.hpMaximo) {
			this.hpAtual = this.hpMaximo;
		}
	}

	public boolean usarItemEspecifico(int indice) {

		if (indice < 0 || indice >= this.mochila.getItens().size()) {
			return false;
		}

		Item itemConsultado = this.mochila.getItens().get(indice);

		boolean precisaCuraHp = (itemConsultado.getPontosCura() > 0 && this.hpAtual < this.hpMaximo);
		boolean precisaCuraStatus = (itemConsultado.getCuraStatus().equalsIgnoreCase("TODOS")
				&& this.statusAtual != EfeitoStatus.NENHUM);

		if (!precisaCuraHp && !precisaCuraStatus) {
			return false;
		}

		Item itemUsado = this.mochila.usarItemPorIndice(indice);
		if (itemUsado != null) {
			if (itemUsado.getPontosCura() > 0) {
				curar(itemUsado.getPontosCura());
			}
			if (itemUsado.getCuraStatus().equalsIgnoreCase("TODOS")) {
				this.statusAtual = EfeitoStatus.NENHUM;
			}
			return true;
		}

		return false;
	}

	public Mochila getMochila() {
		return mochila;
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

	public EfeitoStatus getStatusAtual() {
		return statusAtual;
	}

	public void setStatusAtual(EfeitoStatus statusAtual) {
		this.statusAtual = statusAtual;
	}

}