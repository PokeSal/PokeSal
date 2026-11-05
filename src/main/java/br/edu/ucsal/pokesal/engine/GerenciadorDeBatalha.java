package br.edu.ucsal.pokesal.engine;

import br.edu.ucsal.pokesal.model.EfeitoStatus;
import br.edu.ucsal.pokesal.model.PokeSal;
import br.edu.ucsal.pokesal.model.Terreno;
import br.edu.ucsal.pokesal.model.TipoElemental;
import br.edu.ucsal.pokesal.service.GeradorAleatorio;
import br.edu.ucsal.pokesal.service.RegistroBatalha;
import br.edu.ucsal.pokesal.service.ServicoTerreno;
import br.edu.ucsal.pokesal.util.ConstantesJogo;
import java.time.Clock;

/**
 * Gerencia o fluxo da batalha, ordens de turno, efeitos ambientais e registros.
 */
public class GerenciadorDeBatalha {

	private final PokeSal pokesal1;
	private final PokeSal pokesal2;
	private final GeradorAleatorio gerador;
	private final ServicoTerreno servicoTerreno;
	private final Clock clock;
	private final RegistroBatalha registroBatalha;
	private final CalculadoraDano calculadoraDano;

	private PokeSal primeiroAtacante;
	private PokeSal segundoAtacante;
	private int contadorTurno = 0;
	private int acoesRodadaAtual = 0;
	private PokeSal vencedor;

	/**
	 * Construtor completo com injeção de dependências.
	 *
	 * @param pokesal1        Pokésal do primeiro treinador.
	 * @param pokesal2        Pokésal do segundo treinador.
	 * @param gerador         Provedor de números aleatórios.
	 * @param servicoTerreno  Provedor do terreno ativo.
	 * @param clock           Provedor do horário do sistema.
	 * @param registroBatalha Auditor de eventos do combate.
	 */
	public GerenciadorDeBatalha(PokeSal pokesal1, PokeSal pokesal2, GeradorAleatorio gerador,
			ServicoTerreno servicoTerreno, Clock clock, RegistroBatalha registroBatalha) {
		this.pokesal1 = pokesal1;
		this.pokesal2 = pokesal2;
		this.gerador = gerador;
		this.servicoTerreno = servicoTerreno;
		this.clock = clock;
		this.registroBatalha = registroBatalha;
		this.calculadoraDano = new CalculadoraDano(gerador, servicoTerreno);
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
			if (this.gerador.testarChance(0.5)) {
				this.primeiroAtacante = pokesal1;
				this.segundoAtacante = pokesal2;
			} else {
				this.primeiroAtacante = pokesal2;
				this.segundoAtacante = pokesal1;
			}
		}
	}

	public void registrarAcao(PokeSal atacante) {
		this.acoesRodadaAtual++;

		if (this.acoesRodadaAtual >= 2) {
			this.acoesRodadaAtual = 0;
			this.contadorTurno++;

			aplicarDanoStatusFimDeTurno();
			aplicarCuraCanteiroCentral();
			verificarVencedor();
		}
	}

	public void processarRecuoPorAtaque(PokeSal atacante) {
		if (atacante != null && atacante.isVivo()) {
			int ataques = atacante.incrementarAtaquesConsecutivos();

			if (ataques > 0 && ataques % ConstantesJogo.INTERVALO_TURNO_RECUO == 0) {
				int danoRecuo = (int) Math.round(atacante.getHpMaximo() * ConstantesJogo.PERCENTUAL_DANO_RECUO);
				atacante.receberDano(danoRecuo);
			}
		}
	}

	public void aplicarCuraCanteiroCentral() {
		Terreno terrenoAtual = servicoTerreno.obterTerrenoAtual(clock);
		if (terrenoAtual == Terreno.CANTEIRO_CENTRAL) {
			if (pokesal1.isVivo() && pokesal1.getTipo() == TipoElemental.PLANTA) {
				int cura = (int) Math.round(pokesal1.getHpMaximo() * ConstantesJogo.BONUS_CANTEIRO_CENTRAL);
				pokesal1.curar(cura);
				registroBatalha
						.registrarEfeitoTerreno("Canteiro Central curou " + cura + " HP de " + pokesal1.getNome());
			}

			if (pokesal2.isVivo() && pokesal2.getTipo() == TipoElemental.PLANTA) {
				int cura = (int) Math.round(pokesal2.getHpMaximo() * ConstantesJogo.BONUS_CANTEIRO_CENTRAL);
				pokesal2.curar(cura);
				registroBatalha
						.registrarEfeitoTerreno("Canteiro Central curou " + cura + " HP de " + pokesal2.getNome());
			}
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
			registroBatalha.registrarEfeitoStatus(pokesal1.getNome(), pokesal1.getStatusAtual());
		}

		if (this.pokesal2.getStatusAtual() == EfeitoStatus.QUEIMADO
				|| this.pokesal2.getStatusAtual() == EfeitoStatus.ENVENENADO) {
			int danoStatus = (int) Math.round(this.pokesal2.getHpMaximo() * ConstantesJogo.PERCENTUAL_DANO_STATUS);
			this.pokesal2.receberDano(danoStatus);
			registroBatalha.registrarEfeitoStatus(pokesal2.getNome(), pokesal2.getStatusAtual());
		}
	}

	private void verificarVencedor() {
		if (!this.pokesal1.isVivo() || !this.pokesal2.isVivo()) {
			this.vencedor = this.pokesal1.isVivo() ? this.pokesal1 : this.pokesal2;
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
		return servicoTerreno.obterTerrenoAtual(clock);
	}

	public CalculadoraDano getCalculadoraDano() {
		return calculadoraDano;
	}

	public int getContadorTurno() {
		return contadorTurno;
	}
}