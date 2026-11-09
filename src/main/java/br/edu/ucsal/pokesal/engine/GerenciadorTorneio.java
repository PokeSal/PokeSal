package br.edu.ucsal.pokesal.engine;

import br.edu.ucsal.pokesal.model.Item;
import br.edu.ucsal.pokesal.model.PokeSal;
import br.edu.ucsal.pokesal.repository.RepositorioResultados;
import br.edu.ucsal.pokesal.repository.ResultadoBatalha;
import br.edu.ucsal.pokesal.service.GeradorAleatorio;
import br.edu.ucsal.pokesal.service.RegistroBatalha;
import br.edu.ucsal.pokesal.service.ServicoTerreno;
import br.edu.ucsal.pokesal.util.ConstantesJogo;
import java.time.Clock;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class GerenciadorTorneio {

	private final RepositorioResultados repositorio;
	private final GeradorAleatorio gerador;
	private final ServicoTerreno servicoTerreno;
	private final Clock clock;
	private final RegistroBatalha registroBatalha;

	public GerenciadorTorneio(RepositorioResultados repositorio, GeradorAleatorio gerador,
			ServicoTerreno servicoTerreno, Clock clock, RegistroBatalha registroBatalha) {
		this.repositorio = repositorio;
		this.gerador = gerador;
		this.servicoTerreno = servicoTerreno;
		this.clock = clock;
		this.registroBatalha = registroBatalha;
	}

	public ResultadoPartida executarPartida(String nomeT1, PokeSal pokeSal1, String nomeT2, PokeSal pokeSal2,
			Scanner scan, String nomeFase) {
		System.out.println("\n==================================================");
		System.out.println("   INÍCIO DA PARTIDA - " + nomeFase.toUpperCase());
		System.out.println(
				"   " + nomeT1 + " (" + pokeSal1.getNome() + ") VS " + nomeT2 + " (" + pokeSal2.getNome() + ")");
		System.out.println("==================================================");

		// Reseta o limite de itens por confronto
		pokeSal1.getMochila().resetarUsosBatalha();
		pokeSal2.getMochila().resetarUsosBatalha();

		// Instanciação corrigida com 6 parâmetros
		GerenciadorDeBatalha batalha = new GerenciadorDeBatalha(pokeSal1, pokeSal2, gerador, servicoTerreno, clock,
				registroBatalha);

		batalha.definirOrdemAtuacao();
		boolean vezDoTreinador1 = (batalha.getPrimeiroAtacante() == pokeSal1);
		PokeSal vencedorBatalha = null;
		String treinadorVencedor = null;
		String treinadorPerdedor = null;
		PokeSal perdedorBatalha = null;

		while (pokeSal1.isVivo() && pokeSal2.isVivo()) {
			PokeSal atacante = vezDoTreinador1 ? pokeSal1 : pokeSal2;
			PokeSal defensor = vezDoTreinador1 ? pokeSal2 : pokeSal1;
			String nomeTreinadorDaVez = vezDoTreinador1 ? nomeT1 : nomeT2;
			String nomeTreinadorAdversario = vezDoTreinador1 ? nomeT2 : nomeT1;

			System.out.println("\n--------------------------------------------------");
			System.out.println("STATUS DA PARTIDA:");
			System.out.println(pokeSal1.getNome() + " [" + nomeT1 + "] -> HP: " + pokeSal1.getHpAtual() + "/"
					+ pokeSal1.getHpMaximo());
			System.out.println(pokeSal2.getNome() + " [" + nomeT2 + "] -> HP: " + pokeSal2.getHpAtual() + "/"
					+ pokeSal2.getHpMaximo());
			System.out.println("TERRENO ATIVO: " + batalha.getTerreno().getNomeFormatado());
			System.out.println("--------------------------------------------------");

			System.out.println(
					"\n>>> TURNO DE " + nomeTreinadorDaVez.toUpperCase() + " (" + atacante.getNome() + ") <<<");
			System.out.println("1 - Atacar");
			System.out.println("2 - Usar Mochila (Item de Cura)");
			System.out.println("3 - Desistir da Batalha");

			boolean acaoConcluida = false;
			int acaoEscolhida = 0;

			try {
				System.out.print("Digite a opção desejada: ");
				acaoEscolhida = scan.nextInt();

				switch (acaoEscolhida) {
				case 1:
					batalha.executarTurnoAtaque(atacante, defensor);
					if (!defensor.isVivo()) {
						vencedorBatalha = atacante;
						treinadorVencedor = nomeTreinadorDaVez;
						perdedorBatalha = defensor;
						treinadorPerdedor = nomeTreinadorAdversario;
					} else if (!atacante.isVivo()) {
						vencedorBatalha = defensor;
						treinadorVencedor = nomeTreinadorAdversario;
						perdedorBatalha = atacante;
						treinadorPerdedor = nomeTreinadorDaVez;
					}
					acaoConcluida = true;
					break;

				case 2:
					if (!atacante.getMochila().podeUsarItem()) {
						System.out.println(
								"\n[MOCHILA] Você não possui itens disponíveis ou já atingiu o limite de uso!");
						break;
					}

					System.out.println(
							"\n=== ITENS NA MOCHILA DE " + atacante.getNome() + " [" + nomeTreinadorDaVez + "] ===");
					List<Item> itens = atacante.getMochila().getItens();
					for (int i = 0; i < itens.size(); i++) {
						System.out.println((i + 1) + " - " + itens.get(i).getNome());
					}

					System.out.print("Escolha o item: ");
					int opcaoItem = scan.nextInt();

					if (opcaoItem >= 1 && opcaoItem <= itens.size()) {
						if (batalha.executarTurnoItem(atacante, nomeTreinadorDaVez, opcaoItem - 1)) {
							acaoConcluida = true;
						} else {
							System.out.println("\n[MOCHILA] HP já está cheio ou escolha inválida!");
						}
					} else {
						System.out.println("\n[MOCHILA] Opção inválida!");
					}
					break;

				case 3:
					System.out.println("\nBatalha interrompida por " + nomeTreinadorDaVez + "!");
					vencedorBatalha = defensor;
					treinadorVencedor = nomeTreinadorAdversario;
					perdedorBatalha = atacante;
					treinadorPerdedor = nomeTreinadorDaVez;
					break;

				default:
					System.out.println("\nOpção inválida! Tente novamente.");
					break;
				}
			} catch (InputMismatchException e) {
				System.out.println("Entrada inválida. Digite apenas números.");
				scan.nextLine();
				continue;
			}

			if (acaoEscolhida == 3 || vencedorBatalha != null) {
				break;
			}

			if (acaoConcluida) {
				if (batalha.getVencedor() != null) {
					vencedorBatalha = batalha.getVencedor();
					treinadorVencedor = (vencedorBatalha == pokeSal1) ? nomeT1 : nomeT2;
					perdedorBatalha = (vencedorBatalha == pokeSal1) ? pokeSal2 : pokeSal1;
					treinadorPerdedor = (vencedorBatalha == pokeSal1) ? nomeT2 : nomeT1;
					break;
				}
				vezDoTreinador1 = !vezDoTreinador1;
			}
		}

		int danoVencedor = (vencedorBatalha == pokeSal1) ? batalha.getDanoCausadoPokesal1()
				: batalha.getDanoCausadoPokesal2();
		int danoPerdedor = (vencedorBatalha == pokeSal1) ? batalha.getDanoCausadoPokesal2()
				: batalha.getDanoCausadoPokesal1();

		ResultadoBatalha resultado = new ResultadoBatalha(treinadorVencedor, treinadorPerdedor,
				vencedorBatalha.getNome(), perdedorBatalha.getNome(), batalha.getContadorTurno(), danoVencedor,
				danoPerdedor, batalha.getTotalItensUsadosPartida());

		try {
			repositorio.salvarResultado(resultado);
		} catch (Exception e) {
			System.out.println("\n[AVISO REPOSITÓRIO] Falha ao salvar resultado: " + e.getMessage());
		}

		return new ResultadoPartida(treinadorVencedor, vencedorBatalha);
	}

	public void processarRecompensaPosBatalha(PokeSal vencedor) {
		int sorteio = gerador.gerarNumero(ConstantesJogo.FAIXA_MIN_SORTEIO, ConstantesJogo.FAIXA_MAX_SORTEIO);
		Item itemRecompensa;

		if (sorteio <= 50) {
			itemRecompensa = new Item(ConstantesJogo.NOME_POCAO, ConstantesJogo.CURA_POCAO,
					ConstantesJogo.STATUS_NENHUM);
		} else if (sorteio <= 85) {
			itemRecompensa = new Item(ConstantesJogo.NOME_SUPER_POCAO, ConstantesJogo.CURA_SUPER_POCAO,
					ConstantesJogo.STATUS_NENHUM);
		} else {
			itemRecompensa = new Item(ConstantesJogo.NOME_CURA_TOTAL, ConstantesJogo.CURA_NENHUMA,
					ConstantesJogo.STATUS_TODOS);
		}

		boolean adicionou = vencedor.receberRecompensa(itemRecompensa);

		if (adicionou) {
			registroBatalha.registrarUsoItem("RECOMPENSA (REQ 05)",
					vencedor.getNome() + " ganhou " + itemRecompensa.getNome() + " para a Grande Final");
		} else {
			registroBatalha.registrarUsoItem("RECOMPENSA (REQ 05)", "Mochila de " + vencedor.getNome()
					+ " está cheia! O item " + itemRecompensa.getNome() + " foi descartado");
		}
	}

	public record ResultadoPartida(String nomeTreinador, PokeSal pokesal) {
	}
}