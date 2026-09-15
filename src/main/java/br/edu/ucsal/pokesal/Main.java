package br.edu.ucsal.pokesal;

import br.edu.ucsal.pokesal.engine.CalculadoraDano;
import br.edu.ucsal.pokesal.engine.GerenciadorDeBatalha;
import br.edu.ucsal.pokesal.model.Item;
import br.edu.ucsal.pokesal.model.Mochila;
import br.edu.ucsal.pokesal.model.PokeSal;
import br.edu.ucsal.pokesal.model.Terreno;
import br.edu.ucsal.pokesal.model.TipoPokesal;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		Random rand = new Random();

		System.out.print("Digite o nome do Treinador 1: ");
		String nomeT1 = scan.next();
		PokeSal pokeSal1 = escolherPokeSal(scan, nomeT1);

		System.out.print("Digite o nome do Treinador 2: ");
		String nomeT2 = scan.next();
		PokeSal pokeSal2 = escolherPokeSal(scan, nomeT2);

		Terreno[] opcoesTerreno = Terreno.values();
		int sorteioTerreno = rand.nextInt(opcoesTerreno.length);
		Terreno terrenoSorteado = opcoesTerreno[sorteioTerreno];

		System.out.println("\n=== Terreno Sorteado para a Batalha ===");
		System.out.println("Local: " + terrenoSorteado);

		GerenciadorDeBatalha batalha = new GerenciadorDeBatalha(pokeSal1, pokeSal2, terrenoSorteado);

		batalha.definirOrdemAtuacao();
		System.out.println("\n>>> ORDEM DE INICIATIVA DA BATALHA <<<");
		System.out.println("1º a agir (Mais rápido): " + batalha.getPrimeiroAtacante().getNome());
		System.out.println("2º a agir: " + batalha.getSegundoAtacante().getNome());

		boolean vezDoTreinador1 = (batalha.getPrimeiroAtacante() == pokeSal1);
		PokeSal vencedorBatalha = null;

		while (pokeSal1.isVivo() && pokeSal2.isVivo()) {

			PokeSal atacante = vezDoTreinador1 ? pokeSal1 : pokeSal2;
			PokeSal defensor = vezDoTreinador1 ? pokeSal2 : pokeSal1;
			String nomeTreinadorDaVez = vezDoTreinador1 ? nomeT1 : nomeT2;

			System.out.println("\n--------------------------------------------------");
			System.out.println("STATUS DA BATALHA:");
			System.out.println(pokeSal1.getNome() + " -> HP: " + pokeSal1.getHpAtual() + "/" + pokeSal1.getHpMaximo());
			System.out.println(pokeSal2.getNome() + " -> HP: " + pokeSal2.getHpAtual() + "/" + pokeSal2.getHpMaximo());
			System.out.println("--------------------------------------------------");

			System.out.println(
					"\n>>> TURNO DE " + nomeTreinadorDaVez.toUpperCase() + " (" + atacante.getNome() + ") <<<");
			System.out.println("1 - Atacar");
			System.out.println("2 - Usar Mochila (Item de Cura)");
			System.out.println("3 - Desistir da Batalha");
			boolean acaoConcluida = false;
			int acaoEscolhida;
			try {
				System.out.print("Digite a opção desejada: ");
				acaoEscolhida = scan.nextInt();

				switch (acaoEscolhida) {
					case 1:
						int dano = CalculadoraDano.calcularDano(atacante, defensor, terrenoSorteado);
						defensor.receberDano(dano);
						System.out.println(
								"\n[AÇÃO] " + atacante.getNome() + " atacou " + defensor.getNome() + " causando "
										+ dano + " de dano!");

						if (!defensor.isVivo()) {
							vencedorBatalha = atacante;
						}
						acaoConcluida = true;
						break;

					case 2:
						Mochila mochilaAtacante = atacante.getMochila();
						if (!mochilaAtacante.podeUsarItem()) {
							System.out.println(
									"\n[MOCHILA] Você não possui itens disponíveis ou já atingiu o limite de uso!");
							break;
						}

						System.out.println("\n=== ITENS NA MOCHILA DE " + atacante.getNome() + " ===");
						List<Item> itens = mochilaAtacante.getItens();
						for (int i = 0; i < itens.size(); i++) {
							System.out.println((i + 1) + " - " + itens.get(i).getNome());
						}

						System.out.print("Escolha o item: ");
						int opcaoItem = scan.nextInt();

						if (atacante.usarItemEspecifico(opcaoItem - 1)) {
							System.out.println("\n[MOCHILA] Item utilizado com sucesso!");
							acaoConcluida = true;
						} else {
							System.out.println("\n[MOCHILA] Escolha inválida ou HP já está cheio!");
						}
						break;

					case 3:
						System.out.println("\nBatalha interrompida por " + nomeTreinadorDaVez + "!");
						vencedorBatalha = defensor; // O adversário vence por desistência
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

			if (acaoEscolhida == 3) {
				break;
			}

			if (vencedorBatalha != null) {
				break;
			}
			if (acaoConcluida) {
				batalha.aplicarCuraCanteiroCentral();
				vezDoTreinador1 = !vezDoTreinador1;
			}
		}

		System.out.println("\n==================================================");
		System.out.println(" FIM DE BATALHA NO " + terrenoSorteado + "!");
		System.out.println("==================================================");

		if (vencedorBatalha != null)

		{
			System.out.println(" O GRANDE VENCEDOR É: " + vencedorBatalha.getNome().toUpperCase() + "!");
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
					System.out.println((i + 1) + " - " + opcoes[i].getNome());
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