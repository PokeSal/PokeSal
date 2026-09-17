package br.edu.ucsal.pokesal.engine;

import java.util.Random;

import br.edu.ucsal.pokesal.model.EfeitoStatus;
import br.edu.ucsal.pokesal.model.PokeSal;
import br.edu.ucsal.pokesal.model.Terreno;
import br.edu.ucsal.pokesal.model.TipoElemental;
import br.edu.ucsal.pokesal.util.ConstantesJogo;

public class GerenciadorDeBatalha {
	private PokeSal pokesal1;
	private PokeSal pokesal2;
	private PokeSal primeiroAtacante;
	private PokeSal segundoAtacante;
	private Terreno terreno;
	private int contadorTurno = 0;
	private int acoesRodadaAtual = 0;
	private PokeSal ultimoAtacante;
	private PokeSal vencedor;

	public GerenciadorDeBatalha(PokeSal pokesal1, PokeSal pokesal2, Terreno terreno) {
		this.pokesal1 = pokesal1;
		this.pokesal2 = pokesal2;
		this.terreno = terreno;
		this.vencedor = null;
	}

	public void definirOrdemAtuacao() {
		if (pokesal1.getSpd() > pokesal2.getSpd()) {
			this.primeiroAtacante = pokesal1;
			this.segundoAtacante = pokesal2;
		} else if (pokesal2.getSpd() > pokesal1.getSpd()) {
			this.primeiroAtacante = pokesal2;
			this.segundoAtacante = pokesal1;
		} else {
			Random rand = new Random();
			if (rand.nextBoolean()) {
				this.primeiroAtacante = pokesal1;
				this.segundoAtacante = pokesal2;
			} else {
				this.primeiroAtacante = pokesal2;
				this.segundoAtacante = pokesal1;
			}
		}
	}

	public void registrarAcao(PokeSal atacante) {
		this.ultimoAtacante = atacante;
		this.acoesRodadaAtual++;

		if (this.acoesRodadaAtual >= 2) {
			this.acoesRodadaAtual = 0;
			this.contadorTurno++;

			aplicarDanoStatusFimDeTurno();
			aplicarCuraCanteiroCentral();
			mudarTerreno();
			verificarVencedor();
		}
	}

	public void processarRecuoPorAtaque(PokeSal atacante) {
		if (atacante != null && atacante.isVivo()) {
			int ataques = atacante.incrementarAtaquesConsecutivos();

			if (ataques > 0 && ataques % ConstantesJogo.INTERVALO_TURNO_RECUO == 0) {
				int danoRecuo = (int) Math.round(atacante.getHpMaximo() * ConstantesJogo.PERCENTUAL_DANO_RECUO);
				atacante.receberDano(danoRecuo);
				System.out.println("\n[RECUO] " + atacante.getNome() + " sofreu " + danoRecuo
						+ " de dano de recuo por realizar " + ataques + " ataques seguidos sem usar item!");
			}
		}
	}

	public void aplicarCuraCanteiroCentral() {

		if (this.terreno == Terreno.CANTEIRO_CENTRAL) {
			if (pokesal1.isVivo() && pokesal1.getTipo() == TipoElemental.PLANTA) {
				int curaCanteiro = (int) Math.round(pokesal1.getHpMaximo() * ConstantesJogo.BONUS_CANTEIRO_CENTRAL);
				pokesal1.curar(curaCanteiro);
				System.out.println("\n[TERRENO] O Canteiro Central restaurou " + curaCanteiro + " de HP de "
						+ pokesal1.getNome() + "!");
			}

			if (pokesal2.isVivo() && pokesal2.getTipo() == TipoElemental.PLANTA) {
				int curaCanteiro = (int) Math.round(pokesal2.getHpMaximo() * ConstantesJogo.BONUS_CANTEIRO_CENTRAL);
				pokesal2.curar(curaCanteiro);
				System.out.println("\n[TERRENO] O Canteiro Central restaurou " + curaCanteiro + " de HP de "
						+ pokesal2.getNome() + "!");
			}
		}
	}

	public void mudarTerreno() {
		if (this.contadorTurno > 0 && this.contadorTurno % ConstantesJogo.MUDANCA_TERRENO_TURNO == 0) {
			Random rand = new Random();
			Terreno[] opcoesTerreno = Terreno.values();
			int sorteioTerreno = rand.nextInt(opcoesTerreno.length);
			Terreno terrenoSorteado = opcoesTerreno[sorteioTerreno];
			this.terreno = terrenoSorteado;

			System.out.println(
					"\n[TERRENO] O ambiente da batalha mudou para: " + terrenoSorteado.getNomeFormatado() + "!");
		}

	}

	public void aplicarDanoStatusFimDeTurno() {
		if (!this.pokesal1.isVivo() || !this.pokesal2.isVivo()) {
			return;
		}

		if (this.pokesal1.getStatusAtual() == EfeitoStatus.QUEIMADO
				|| this.pokesal1.getStatusAtual() == EfeitoStatus.ENVENENADO) {
			int danoStatus = (int) Math.round(this.pokesal1.getHpMaximo() * ConstantesJogo.PERCENTUAL_DANO_STATUS);
			this.pokesal1.receberDano(danoStatus);
			System.out.println("\n[STATUS] " + this.pokesal1.getNome() + " sofreu " + danoStatus + " de dano por estar "
					+ this.pokesal1.getStatusAtual() + "!");
		}

		if (this.pokesal2.getStatusAtual() == EfeitoStatus.QUEIMADO
				|| this.pokesal2.getStatusAtual() == EfeitoStatus.ENVENENADO) {
			int danoStatus = (int) Math.round(this.pokesal2.getHpMaximo() * ConstantesJogo.PERCENTUAL_DANO_STATUS);
			this.pokesal2.receberDano(danoStatus);
			System.out.println("\n[STATUS] " + this.pokesal2.getNome() + " sofreu " + danoStatus + " de dano por estar "
					+ this.pokesal2.getStatusAtual() + "!");
		}
	}

	public void executarRodada() {
		definirOrdemAtuacao();

		int danoPrimeiro = CalculadoraDano.calcularDano(this.primeiroAtacante, this.segundoAtacante, this.terreno);

		this.segundoAtacante.receberDano(danoPrimeiro);

		this.ultimoAtacante = this.primeiroAtacante;

		if (!this.segundoAtacante.isVivo()) {
			verificarVencedor();
			return;
		}

		int danoSegundo = CalculadoraDano.calcularDano(this.segundoAtacante, this.primeiroAtacante, this.terreno);

		this.primeiroAtacante.receberDano(danoSegundo);

		this.ultimoAtacante = this.segundoAtacante;

		if (!this.primeiroAtacante.isVivo()) {
			verificarVencedor();
			return;
		}

		this.contadorTurno++;

		if (!this.ultimoAtacante.isVivo()) {
			verificarVencedor();
			return;
		}

	}

	private void verificarVencedor() {
		if (!this.pokesal1.isVivo() || !this.pokesal2.isVivo()) {
			if (this.pokesal1.isVivo()) {
				this.vencedor = this.pokesal1;
			} else
				this.vencedor = this.pokesal2;
		}
	}

	public boolean isVelocidadeIgual() {
		return pokesal1.getSpd() == pokesal2.getSpd();
	}

	public PokeSal getVencedor() {
		return this.vencedor;
	}

	public PokeSal getPrimeiroAtacante() {
		return primeiroAtacante;
	}

	public PokeSal getSegundoAtacante() {
		return segundoAtacante;
	}

	public Terreno getTerreno() {
		return this.terreno;
	}

}