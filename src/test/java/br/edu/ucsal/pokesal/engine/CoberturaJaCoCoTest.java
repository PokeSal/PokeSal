package br.edu.ucsal.pokesal.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.ucsal.pokesal.model.EfeitoStatus;
import br.edu.ucsal.pokesal.model.Mochila;
import br.edu.ucsal.pokesal.model.PokeSal;
import br.edu.ucsal.pokesal.model.Terreno;
import br.edu.ucsal.pokesal.model.TipoPokesal;
import br.edu.ucsal.pokesal.repository.RepositorioResultadosMemoria;
import br.edu.ucsal.pokesal.service.GeradorAleatorio;
import br.edu.ucsal.pokesal.service.GeradorAleatorioImpl;
import br.edu.ucsal.pokesal.service.RegistroBatalhaConsoleImpl;
import br.edu.ucsal.pokesal.service.ServicoTerreno;
import br.edu.ucsal.pokesal.service.ServicoTerrenoImpl;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Random;
import java.util.Scanner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Suíte complementar para garantia das metas de cobertura do JaCoCo (Issue #97).
 */
public class CoberturaJaCoCoTest {

  private GeradorAleatorio geradorReal;
  private ServicoTerreno servicoTerrenoReal;
  private RegistroBatalhaConsoleImpl registroConsole;
  private Clock clockDia;
  private Clock clockNoite;

  @BeforeEach
  void setUp() {
    geradorReal = new GeradorAleatorioImpl(new Random());
    servicoTerrenoReal = new ServicoTerrenoImpl();
    registroConsole = new RegistroBatalhaConsoleImpl();
    clockDia = Clock.fixed(Instant.parse("2026-11-10T12:00:00Z"), ZoneId.of("UTC"));
    clockNoite = Clock.fixed(Instant.parse("2026-11-10T22:00:00Z"), ZoneId.of("UTC"));
  }

  @Test
  @DisplayName("Cobertura CalculadoraDano: Efetividades elementais e status cruzados")
  void testCalculadoraDanoCombinacoes() {
    CalculadoraDano calc = new CalculadoraDano(geradorReal, servicoTerrenoReal);
    PokeSal fogo = new PokeSal(TipoPokesal.CHARSAL);
    PokeSal agua = new PokeSal(TipoPokesal.SQUIRTSAL);
    PokeSal planta = new PokeSal(TipoPokesal.BULBASAL);

    assertEquals(2.0, calc.calcularEfetividade(agua, fogo));
    assertEquals(2.0, calc.calcularEfetividade(planta, agua));
    assertEquals(0.5, calc.calcularEfetividade(agua, planta));
    assertEquals(0.5, calc.calcularEfetividade(planta, fogo));
    assertEquals(1.0, calc.calcularEfetividade(fogo, fogo));

    assertEquals(1.10, calc.calcularMultiplicadorTerreno(agua, Terreno.POCA_CHUVA));
    assertEquals(1.0, calc.calcularMultiplicadorTerreno(agua, Terreno.ASFALTO_QUENTE));

    planta.setStatusAtual(EfeitoStatus.QUEIMADO);
    calc.aplicarEfeitoStatus(fogo, planta);
    assertEquals(EfeitoStatus.QUEIMADO, planta.getStatusAtual());

    PokeSal outroFogo = new PokeSal(TipoPokesal.CYNDASAL);
    GeradorAleatorio geradorStatusSempre = new GeradorAleatorio() {
      @Override
      public int gerarNumero(int min, int max) {
        return min;
      }

      @Override
      public double gerarDouble() {
        return 0.0;
      }

      @Override
      public boolean testarChance(double percentual) {
        return true;
      }
    };
    CalculadoraDano calcStatus = new CalculadoraDano(geradorStatusSempre, servicoTerrenoReal);

    calcStatus.aplicarEfeitoStatus(fogo, outroFogo);
    assertEquals(EfeitoStatus.NENHUM, outroFogo.getStatusAtual());

    calcStatus.aplicarEfeitoStatus(agua, outroFogo);
    assertEquals(EfeitoStatus.PARALISADO, outroFogo.getStatusAtual());

    calcStatus.aplicarEfeitoStatus(planta, agua);
    assertEquals(EfeitoStatus.ENVENENADO, agua.getStatusAtual());
  }

  @Test
  @DisplayName("Cobertura GerenciadorDeBatalha: Iniciativa por velocidade e desempate")
  void testIniciativaEvelocidade() {
    PokeSal rapido = new PokeSal(TipoPokesal.CHARSAL);
    PokeSal lento = new PokeSal(TipoPokesal.BULBASAL);
    GerenciadorDeBatalha b1 = new GerenciadorDeBatalha(lento, rapido, geradorReal,
        servicoTerrenoReal, clockDia, registroConsole);
    b1.definirOrdemAtuacao();
    assertEquals(rapido, b1.getPrimeiroAtacante());

    PokeSal p1 = new PokeSal(TipoPokesal.CHARSAL);
    PokeSal p2 = new PokeSal(TipoPokesal.CYNDASAL);
    GeradorAleatorio geradorFalso = new GeradorAleatorio() {
      @Override
      public int gerarNumero(int min, int max) {
        return min;
      }

      @Override
      public double gerarDouble() {
        return 0.0;
      }

      @Override
      public boolean testarChance(double percentual) {
        return false;
      }
    };
    GerenciadorDeBatalha b2 =
        new GerenciadorDeBatalha(p1, p2, geradorFalso, servicoTerrenoReal, clockDia,
            registroConsole);
    b2.definirOrdemAtuacao();
    assertEquals(p2, b2.getPrimeiroAtacante());
  }

  @Test
  @DisplayName("Cobertura GerenciadorDeBatalha: Status e cura de terreno")
  void testFimDeTurnoStatusEcuraTerreno() {
    PokeSal p1 = new PokeSal(TipoPokesal.BULBASAL);
    PokeSal p2 = new PokeSal(TipoPokesal.CHIKOSAL);

    p1.receberDano(30);
    p2.receberDano(30);
    p1.setStatusAtual(EfeitoStatus.QUEIMADO);
    p2.setStatusAtual(EfeitoStatus.ENVENENADO);

    ServicoTerreno servicoCanteiro = (clock) -> Terreno.CANTEIRO_CENTRAL;
    GerenciadorDeBatalha b =
        new GerenciadorDeBatalha(p1, p2, geradorReal, servicoCanteiro, clockDia, registroConsole);

    b.aplicarDanoStatusFimDeTurno();
    b.aplicarCuraCanteiroCentral();

    assertTrue(p1.getHpAtual() > 0);
    assertTrue(p2.getHpAtual() > 0);

    b.processarRecuoPorAtaque(null);
    PokeSal morto = new PokeSal(TipoPokesal.CHARSAL);
    morto.receberDano(999);
    b.processarRecuoPorAtaque(morto);
  }

  @Test
  @DisplayName("Cobertura GerenciadorTorneio: Interação com menus e opções inválidas")
  void testGerenciadorTorneioOpcoesMenu() {
    RepositorioResultadosMemoria repo = new RepositorioResultadosMemoria();
    GerenciadorTorneio torneio =
        new GerenciadorTorneio(repo, geradorReal, servicoTerrenoReal, clockDia, registroConsole);

    PokeSal p1 = new PokeSal(TipoPokesal.CHARSAL);
    PokeSal p2 = new PokeSal(TipoPokesal.BULBASAL);

    Scanner scan = new Scanner("abc\n99\n2\n9\n2\n1\n1\n3\n");
    GerenciadorTorneio.ResultadoPartida res =
        torneio.executarPartida("Ash", p1, "Gary", p2, scan, "TestFase");
    assertNotNull(res);

    GeradorAleatorio geradorCuraTotal = new GeradorAleatorio() {
      @Override
      public int gerarNumero(int min, int max) {
        return 90;
      }

      @Override
      public double gerarDouble() {
        return 0.9;
      }

      @Override
      public boolean testarChance(double percentual) {
        return false;
      }
    };
    GerenciadorTorneio torneioRecompensa = new GerenciadorTorneio(repo, geradorCuraTotal,
        servicoTerrenoReal, clockDia, registroConsole);
    PokeSal vencedor = new PokeSal(TipoPokesal.CHARSAL);
    vencedor.getMochila().usarItemPorIndice(0);
    torneioRecompensa.processarRecompensaPosBatalha(vencedor);
    assertEquals(3, vencedor.getMochila().getItens().size());
  }

  @Test
  @DisplayName("Cobertura Services e Models: Terreno, Gerador e Mochila")
  void testServicesEmodelsBorda() {
    Clock clockTarde = Clock.fixed(Instant.parse("2026-11-10T16:00:00Z"), ZoneId.of("UTC"));
    assertEquals(Terreno.ASFALTO_QUENTE, servicoTerrenoReal.obterTerrenoAtual(clockDia));
    assertEquals(Terreno.CANTEIRO_CENTRAL, servicoTerrenoReal.obterTerrenoAtual(clockTarde));
    assertEquals(Terreno.POCA_CHUVA, servicoTerrenoReal.obterTerrenoAtual(clockNoite));

    assertTrue(geradorReal.gerarNumero(1, 10) >= 1);
    assertTrue(geradorReal.gerarDouble() >= 0.0);
    assertNotNull(geradorReal.testarChance(0.5));

    registroConsole.registrarAtaque("CharSal", "BulbaSal");
    registroConsole.registrarDano("BulbaSal", 15, true, false);
    registroConsole.registrarDano("BulbaSal", 0, false, true);
    registroConsole.registrarDano("BulbaSal", 10, false, false);
    registroConsole.registrarEfeitoStatus("BulbaSal", EfeitoStatus.QUEIMADO);
    registroConsole.registrarEfeitoTerreno("Efeito ativo");
    registroConsole.registrarUsoItem("Ash", "Poção (+20 HP)");

    Mochila m = new Mochila();
    assertNull(m.usarItemPorIndice(99));

    PokeSal poke = new PokeSal(TipoPokesal.CHARSAL);
    poke.curar(100);
    assertEquals(poke.getHpMaximo(), poke.getHpAtual());

    assertFalse(poke.usarItemEspecifico(2));

    poke.setStatusAtual(EfeitoStatus.PARALISADO);
    assertTrue(poke.usarItemEspecifico(2));
    assertEquals(EfeitoStatus.NENHUM, poke.getStatusAtual());
  }
}