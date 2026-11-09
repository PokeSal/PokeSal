package br.edu.ucsal.pokesal.engine;

import br.edu.ucsal.pokesal.model.EfeitoStatus;
import br.edu.ucsal.pokesal.model.Item;
import br.edu.ucsal.pokesal.model.PokeSal;
import br.edu.ucsal.pokesal.model.Terreno;
import br.edu.ucsal.pokesal.model.TipoElemental;
import br.edu.ucsal.pokesal.repository.RepositorioResultados;
import br.edu.ucsal.pokesal.service.GeradorAleatorio;
import br.edu.ucsal.pokesal.service.RegistroBatalha;
import br.edu.ucsal.pokesal.service.ServicoTerreno;
import br.edu.ucsal.pokesal.util.ConstantesJogo;
import java.time.Clock;

public class GerenciadorDeBatalha {

	private final PokeSal pokesal1;
	private final PokeSal pokesal2;
	private final GeradorAleatorio gerador;
	private final ServicoTerreno servicoTerreno;
	private final Clock clock;
	private final RegistroBatalha registroBatalha;
	private final CalculadoraDano calculadoraDano;
	private final RepositorioResultados repositorio;

	private PokeSal primeiroAtacante;
	private PokeSal segundoAtacante;
	private int contadorTurno = 0;
	private int acoesRodadaAtual = 0;
	private PokeSal vencedor;

	private int danoCausadoPokesal1 = 0;
	private int danoCausadoPokesal2 = 0;
	private int totalItensUsadosPartida = 0;

	public GerenciadorDeBatalha(PokeSal pokesal1, PokeSal pokesal2, GeradorAleatorio gerador,
			ServicoTerreno servicoTerreno, Clock clock, RegistroBatalha registroBatalha, RepositorioResultados repositorio) {
		this.pokesal1 = pokesal1;
		this.pokesal2 = pokesal2;
		this.gerador = gerador;
		this.servicoTerreno = servicoTerreno;
		this.clock = clock;
		this.registroBatalha = registroBatalha;
		this.calculadoraDano = new CalculadoraDano(gerador, servicoTerreno);
		this.vencedor = null;
		this.repositorio = repositorio;
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

	public boolean executarTurnoAtaque(PokeSal atacante, PokeSal defensor) {
		if (verificarParalisiaEPerdaDeTurno(atacante)) {
			return true;
		}

		if (defensor.isPassivaDefensivaAtiva()) {
			registroBatalha.registrarEfeitoTerreno("Passiva de defesa ativada para " + defensor.getNome() + "!");
		}

		registroBatalha.registrarAtaque(atacante.getNome(), defensor.getNome());

		int dano = calculadoraDano.calcularDano(atacante, defensor, clock);
		boolean errou = (dano == ConstantesJogo.DANO_ERRO);
		boolean critico = !errou && (dano > (int) Math.round(((double) atacante.getAtk() / defensor.getDef())
				* ConstantesJogo.DANO_BASE * calculadoraDano.calcularEfetividade(atacante, defensor)
				* calculadoraDano.calcularMultiplicadorTerreno(atacante, servicoTerreno.obterTerrenoAtual(clock))));

		defensor.receberDano(dano);

		if (atacante == pokesal1) {
			this.danoCausadoPokesal1 += dano;
		} else {
			this.danoCausadoPokesal2 += dano;
		}

		registroBatalha.registrarDano(defensor.getNome(), dano, critico, errou);

		if (!errou) {
			EfeitoStatus statusAntes = defensor.getStatusAtual();
			calculadoraDano.aplicarEfeitoStatus(atacante, defensor);
			if (defensor.getStatusAtual() != statusAntes) {
				registroBatalha.registrarEfeitoStatus(defensor.getNome(), defensor.getStatusAtual());
			}
		}

		processarRecuoPorAtaque(atacante);
		verificarVencedor();
		avancarAcaoRodada();
		return true;
	}

	public boolean executarTurnoItem(PokeSal atacante, String nomeTreinador, int indiceItem) {
		if (verificarParalisiaEPerdaDeTurno(atacante)) {
			return true;
		}

		if (!atacante.getMochila().podeUsarItem()) {
			return false;
		}

		Item itemConsultado = atacante.getMochila().getItens().get(indiceItem);
		boolean usouComSucesso = atacante.usarItemEspecifico(indiceItem);

		if (usouComSucesso) {
			this.totalItensUsadosPartida++;
			registroBatalha.registrarUsoItem(nomeTreinador, itemConsultado.getNome());
			avancarAcaoRodada();
			return true;
		}
		return false;
	}

	private boolean verificarParalisiaEPerdaDeTurno(PokeSal atacante) {
		if (atacante.getStatusAtual() == EfeitoStatus.PARALISADO) {
			registroBatalha.registrarEfeitoTerreno(atacante.getNome() + " está PARALISADO e perdeu a vez!");
			atacante.setStatusAtual(EfeitoStatus.NENHUM);
			avancarAcaoRodada();
			return true;
		}
		return false;
	}

	private void avancarAcaoRodada() {
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
				registroBatalha.registrarEfeitoTerreno(
						atacante.getNome() + " sofreu " + danoRecuo + " de dano de recuo (Req 01)!");
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
						.registrarEfeitoTerreno("Canteiro Central restaurou " + cura + " HP de " + pokesal1.getNome());
			}
			if (pokesal2.isVivo() && pokesal2.getTipo() == TipoElemental.PLANTA) {
				int cura = (int) Math.round(pokesal2.getHpMaximo() * ConstantesJogo.BONUS_CANTEIRO_CENTRAL);
				pokesal2.curar(cura);
				registroBatalha
						.registrarEfeitoTerreno("Canteiro Central restaurou " + cura + " HP de " + pokesal2.getNome());
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
			registroBatalha.registrarEfeitoTerreno(
					pokesal1.getNome() + " sofreu " + danoStatus + " de dano por " + pokesal1.getStatusAtual());
		}
		if (this.pokesal2.getStatusAtual() == EfeitoStatus.QUEIMADO
				|| this.pokesal2.getStatusAtual() == EfeitoStatus.ENVENENADO) {
			int danoStatus = (int) Math.round(this.pokesal2.getHpMaximo() * ConstantesJogo.PERCENTUAL_DANO_STATUS);
			this.pokesal2.receberDano(danoStatus);
			registroBatalha.registrarEfeitoTerreno(
					pokesal2.getNome() + " sofreu " + danoStatus + " de dano por " + pokesal2.getStatusAtual());
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

	public int getContadorTurno() {
		return contadorTurno;
	}

	public int getDanoCausadoPokesal1() {
		return danoCausadoPokesal1;
	}

	public int getDanoCausadoPokesal2() {
		return danoCausadoPokesal2;
	}

	public int getTotalItensUsadosPartida() {
		return totalItensUsadosPartida;
	}
}