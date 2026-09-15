package br.edu.ucsal.pokesal;

import java.util.Scanner;
import java.util.Random;

import br.edu.ucsal.pokesal.model.PokeSal;
import br.edu.ucsal.pokesal.model.Terreno;
import br.edu.ucsal.pokesal.model.TipoPokesal;
import br.edu.ucsal.pokesal.engine.GerenciadorDeBatalha;

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

		while (pokeSal1.isVivo() && pokeSal2.isVivo()) {

			System.out.println("\n--------------------------------------------------");
			System.out.println("STATUS DA BATALHA:");
			System.out.println(pokeSal1.getNome() + " -> HP: " + pokeSal1.getHpAtual() + "/" + pokeSal1.getHpMaximo());
			System.out.println(pokeSal2.getNome() + " -> HP: " + pokeSal2.getHpAtual() + "/" + pokeSal2.getHpMaximo());
			System.out.println("--------------------------------------------------");

			batalha.definirOrdemAtuacao();
			System.out.println("\n PRIMEIRO ATACANTE: " + batalha.getPrimeiroAtacante().getNome());
			System.out.println("\n SEGUNDO ATACANTE: " + batalha.getSegundoAtacante().getNome());

			System.out.println("\nESCOLHA SUA AÇÃO:");
			System.out.println("1 - Atacar");
			System.out.println("2 - Usar Mochila (Item de Cura)");
			System.out.println("3 - Desistir da Batalha");
			System.out.print("Digite a opção desejada: ");
			int acaoEscolhida = scan.nextInt();

			switch (acaoEscolhida) {
			case 1:
				batalha.executarRodada();
				break;
			case 2:
				System.out.println("\n[Mochila em breve na próxima Issue!]");
				break;
			case 3:
				System.out.println("\nBatalha interrompida pelo jogador!");
				break;
			default:
				System.out.println("\nOpção inválida! Tente novamente.");
				break;
			}

			if (acaoEscolhida == 3) {
				break;
			}

			if (batalha.getVencedor() != null) {
				System.out.println("\n==================================================");
				System.out.println("              FIM DE BATALHA NO" + terrenoSorteado + "!");
				System.out.println("==================================================");
				System.out.println(" O GRANDE VENCEDOR É: " + batalha.getVencedor().getNome().toUpperCase() + "!");
			} else {
				System.out.println(" A batalha foi interrompida/empata!");
			}

			System.out.println("==================================================");
		}

	}

	private static PokeSal escolherPokeSal(Scanner scan, String nomeTreinador) {
		System.out.println("\n=== Treinador " + nomeTreinador + ", escolha o seu PokeSal ===");
		TipoPokesal[] opcoes = TipoPokesal.values();

		for (int i = 0; i < opcoes.length; i++) {
			System.out.println((i + 1) + " - " + opcoes[i].getNome());
		}

		System.out.print("Digite o número da sua escolha: ");
		int opcaoEscolhida = scan.nextInt();

		TipoPokesal tipoEscolhido = opcoes[opcaoEscolhida - 1];
		return new PokeSal(tipoEscolhido);
	}
}