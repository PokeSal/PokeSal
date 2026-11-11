package br.edu.ucsal.pokesal;

import br.edu.ucsal.pokesal.engine.GerenciadorTorneio;
import br.edu.ucsal.pokesal.engine.GerenciadorTorneio.ResultadoPartida;
import br.edu.ucsal.pokesal.model.PokeSal;
import br.edu.ucsal.pokesal.model.TipoPokesal;
import br.edu.ucsal.pokesal.repository.RepositorioResultadosMemoria;
import br.edu.ucsal.pokesal.service.GeradorAleatorio;
import br.edu.ucsal.pokesal.service.GeradorAleatorioImpl;
import br.edu.ucsal.pokesal.service.RegistroBatalha;
import br.edu.ucsal.pokesal.service.RegistroBatalhaConsoleImpl;
import br.edu.ucsal.pokesal.service.ServicoTerreno;
import br.edu.ucsal.pokesal.service.ServicoTerrenoImpl;
import java.time.Clock;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

/** Ponto de entrada do torneio Pokésal da UCSAL. */
public class Main {

  /**
   * Inicia o torneio: coleta treinadores, executa semifinais e final e emite o relatório.
   *
   * @param args argumentos de linha de comando (não utilizados).
   */
  public static void main(String[] args) {
    Scanner scan = new Scanner(System.in);

    GeradorAleatorio gerador = new GeradorAleatorioImpl(new Random());
    Clock clock = Clock.systemDefaultZone();
    ServicoTerreno servicoTerreno = new ServicoTerrenoImpl();
    RegistroBatalha registroBatalha = new RegistroBatalhaConsoleImpl();
    RepositorioResultadosMemoria repositorio = new RepositorioResultadosMemoria();

    GerenciadorTorneio torneio =
        new GerenciadorTorneio(repositorio, gerador, servicoTerreno, clock, registroBatalha);

    System.out.println("==================================================");
    System.out.println("   BEM-VINDO AO TORNEIO POKÉSAL DA UCSAL PITUAÇU  ");
    System.out.println("==================================================");

    System.out.print("\nDigite o nome do Treinador 1 (Semifinal 1): ");
    String nomeT1 = scan.next();
    PokeSal pokeSal1 = escolherPokeSal(scan, nomeT1);

    System.out.print("\nDigite o nome do Treinador 2 (Semifinal 1): ");
    String nomeT2 = scan.next();
    PokeSal pokeSal2 = escolherPokeSal(scan, nomeT2);

    System.out.print("\nDigite o nome do Treinador 3 (Semifinal 2): ");
    String nomeT3 = scan.next();
    PokeSal pokeSal3 = escolherPokeSal(scan, nomeT3);

    System.out.print("\nDigite o nome do Treinador 4 (Semifinal 2): ");
    String nomeT4 = scan.next();
    PokeSal pokeSal4 = escolherPokeSal(scan, nomeT4);

    // SEMIFINAL 1
    ResultadoPartida resSemifinal1 =
        torneio.executarPartida(nomeT1, pokeSal1, nomeT2, pokeSal2, scan, "Semifinal 1");
    torneio.processarRecompensaPosBatalha(resSemifinal1.pokesal());

    // SEMIFINAL 2
    ResultadoPartida resSemifinal2 =
        torneio.executarPartida(nomeT3, pokeSal3, nomeT4, pokeSal4, scan, "Semifinal 2");
    torneio.processarRecompensaPosBatalha(resSemifinal2.pokesal());

    System.out.println("\n==================================================");
    System.out.println("   CLASSIFICADOS PARA A GRANDE FINAL!");
    System.out.println(
        "   Finalista 1: "
            + resSemifinal1.nomeTreinador()
            + " com "
            + resSemifinal1.pokesal().getNome()
            + " [HP Restante: "
            + resSemifinal1.pokesal().getHpAtual()
            + "]");
    System.out.println(
        "   Finalista 2: "
            + resSemifinal2.nomeTreinador()
            + " com "
            + resSemifinal2.pokesal().getNome()
            + " [HP Restante: "
            + resSemifinal2.pokesal().getHpAtual()
            + "]");
    System.out.println("==================================================");

    // GRANDE FINAL
    ResultadoPartida resFinal =
        torneio.executarPartida(
            resSemifinal1.nomeTreinador(),
            resSemifinal1.pokesal(),
            resSemifinal2.nomeTreinador(),
            resSemifinal2.pokesal(),
            scan,
            "Grande Final");

    // DECLARAÇÃO DO CAMPEÃO GERAL
    System.out.println("\n🏆🏆🏆🏆🏆🏆🏆🏆🏆🏆🏆🏆🏆🏆🏆🏆🏆🏆🏆🏆🏆🏆🏆🏆🏆");
    System.out.println(" O GRANDE CAMPEÃO GERAL DO ESTACIONAMENTO DA UCSAL É: ");
    System.out.println(" TREINADOR: " + resFinal.nomeTreinador().toUpperCase());
    System.out.println(" COM SEU VALENTE: " + resFinal.pokesal().getNome().toUpperCase());
    System.out.println("🏆🏆🏆🏆🏆🏆🏆🏆🏆🏆🏆🏆🏆🏆🏆🏆🏆🏆🏆🏆🏆🏆🏆🏆🏆\n");

    // RELATÓRIO ESTATÍSTICO DO TORNEIO (Req Autoral 04)
    repositorio.emitirRelatorioEstatistico();
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

        if (opcaoEscolhida >= 1 && opcaoEscolhida <= opcoes.length) {
          TipoPokesal tipoEscolhido = opcoes[opcaoEscolhida - 1];
          return new PokeSal(tipoEscolhido);
        } else {
          System.out.println("Opção inválida! Escolha um número entre 1 e " + opcoes.length + ".");
        }
      } catch (InputMismatchException e) {
        System.out.println("Entrada inválida. Digite apenas números.");
        scan.nextLine();
      }
    }
  }
}
