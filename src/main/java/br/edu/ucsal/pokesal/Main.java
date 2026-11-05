package br.edu.ucsal.pokesal;

import br.edu.ucsal.pokesal.engine.GerenciadorDeBatalha;
import br.edu.ucsal.pokesal.model.EfeitoStatus;
import br.edu.ucsal.pokesal.model.Item;
import br.edu.ucsal.pokesal.model.PokeSal;
import br.edu.ucsal.pokesal.model.TipoPokesal;
import br.edu.ucsal.pokesal.service.GeradorAleatorio;
import br.edu.ucsal.pokesal.service.GeradorAleatorioImpl;
import br.edu.ucsal.pokesal.service.RegistroBatalha;
import br.edu.ucsal.pokesal.service.RegistroBatalhaConsoleImpl;
import br.edu.ucsal.pokesal.service.ServicoTerreno;
import br.edu.ucsal.pokesal.service.ServicoTerrenoImpl;
import java.time.Clock;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Ponto de entrada do sistema PokeSal no console com Inversão de Dependência.
 */
public class Main {

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);

		GeradorAleatorio gerador = new GeradorAleatorioImpl(new Random());
		Clock clock = Clock.systemDefaultZone();
		ServicoTerreno servicoTerreno = new ServicoTerrenoImpl();
		RegistroBatalha registroBatalha = new RegistroBatalhaConsoleImpl();

		System.out.print("Digite o nome do Treinador 1: ");
		String nomeT1 = scan.next();
		PokeSal pokeSal1 = escolherPokeSal(scan, nomeT1);

		System.out.print("Digite o nome do Treinador 2: ");
		String nomeT2 = scan.next();
		PokeSal pokeSal2 = escolherPokeSal(scan, nomeT2);

		GerenciadorDeBatalha batalha = new GerenciadorDeBatalha(pokeSal1, pokeSal2, gerador, servicoTerreno, clock,
				registroBatalha);

		batalha.definirOrdemAtuacao();
		System.out.println("\n>>> ORDEM DE INICIATIVA DA BATALHA <<<");

		String treinadorPrimeiro = (batalha.getPrimeiroAtacante() == pokeSal1) ? nomeT1 : nomeT2;
		String treinadorSegundo = (batalha.getSegundoAtacante() == pokeSal1) ? nomeT1 : nomeT2;

		if (batalha.isVelocidadeIgual()) {
			System.out.println("1º a agir (Empate de SPD - Sorteado): " + batalha.getPrimeiroAtacante().getNome() + " ["
					+ treinadorPrimeiro + "]");
		} else {
			System.out.println("1º a agir (Mais rápido): " + batalha.getPrimeiroAtacante().getNome() + " ["
					+ treinadorPrimeiro + "]");
		}

		System.out.println("2º a agir: " + batalha.getSegundoAtacante().getNome() + " [" + treinadorSegundo + "]");

		boolean vezDoTreinador1 = (batalha.getPrimeiroAtacante() == pokeSal1);
		PokeSal vencedorBatalha = null;
		String treinadorVencedor = null;

		while (pokeSal1.isVivo() && pokeSal2.isVivo()) {
			PokeSal atacante = vezDoTreinador1 ? pokeSal1 : pokeSal2;
			PokeSal defensor = vezDoTreinador1 ? pokeSal2 : pokeSal1;
			String nomeTreinadorDaVez = vezDoTreinador1 ? nomeT1 : nomeT2;
			String nomeTreinadorAdversario = vezDoTreinador1 ? nomeT2 : nomeT1;

			if (atacante.getStatusAtual() == EfeitoStatus.PARALISADO) {
				System.out.println("\n[STATUS] " + atacante.getNome() + " [" + nomeTreinadorDaVez
						+ "] está PARALISADO e perdeu a vez!");
				atacante.setStatusAtual(EfeitoStatus.NENHUM);
				batalha.registrarAcao(atacante);
				if (batalha.getVencedor() != null) {
					vencedorBatalha = batalha.getVencedor();
					treinadorVencedor = (vencedorBatalha == pokeSal1) ? nomeT1 : nomeT2;
					break;
				}
				vezDoTreinador1 = !vezDoTreinador1;
				continue;
			}

			System.out.println("\n--------------------------------------------------");
			System.out.println("STATUS DA BATALHA:");
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
					} else if (!atacante.isVivo()) {
						vencedorBatalha = defensor;
						treinadorVencedor = nomeTreinadorAdversario;
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
					break;

				default:
					System.out.println("\nOpção inválida! Tente novamente.");
					break;
				}
			} catch (InputMismatchException e) {
				System.out.println("Entrada inválida. Digite apenas números");
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
					break;
				}
				vezDoTreinador1 = !vezDoTreinador1;
			}
		}

		System.out.println("\n==================================================");
		System.out.println(" FIM DE BATALHA NO " + batalha.getTerreno().getNomeFormatado() + "!");
		System.out.println("==================================================");

		if (vencedorBatalha != null) {
			System.out.println(" O GRANDE VENCEDOR É O TREINADOR: " + treinadorVencedor.toUpperCase() + " COM SEU "
					+ vencedorBatalha.getNome().toUpperCase() + "!");
		} else {
			System.out.println(" A batalha foi interrompida ou empatou!");
		}

		System.out.println("==================================================");
	}

	private static PokeSal escolherPokeSal(Scanner scan, String nomeTreinador) {
		while (true) {
			try {
				System.out.println("\n=== Treinador " + nomeTreinador + ", escolha o seu PokeSal ===");
				TipoPokesal[] opcoes = TipoPokesal.values();

				for (int i = 0; i < opcoes.length; i++) {
					System.out.println((i + 1) + " - " + opcoes[i].getNomeFormatado());
				}

				System.out.print("Digite o número da sua escolha: ");
				int opcaoEscolhida = scan.nextInt();

				TipoPokesal tipoEscolhido = opcoes[opcaoEscolhida - 1];
				return new PokeSal(tipoEscolhido);
			} catch (InputMismatchException e) {
				System.out.println("Entrada inválida. Digite apenas números");
				scan.nextLine();
			}
		}
	}
}