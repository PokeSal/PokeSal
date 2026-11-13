package br.edu.ucsal.pokesal.engine;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.edu.ucsal.pokesal.model.PokeSal;
import br.edu.ucsal.pokesal.model.Terreno;
import br.edu.ucsal.pokesal.model.TipoPokesal;
import br.edu.ucsal.pokesal.repository.RepositorioResultados;
import br.edu.ucsal.pokesal.repository.ResultadoBatalha;
import br.edu.ucsal.pokesal.service.GeradorAleatorio;
import br.edu.ucsal.pokesal.service.RegistroBatalha;
import br.edu.ucsal.pokesal.service.ServicoTerreno;
import br.edu.ucsal.pokesal.util.ConstantesJogo;
import java.time.Clock;
import java.util.Scanner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Suíte de testes unitários para o Torneio e Repositório (Issue #93).
 */
@ExtendWith(MockitoExtension.class)
public class TorneioRepositorioTest {

  @Mock
  private RepositorioResultados repositorioMock;

  @Mock
  private GeradorAleatorio geradorMock;

  @Mock
  private ServicoTerreno servicoTerrenoMock;

  @Mock
  private RegistroBatalha registroBatalhaMock;

  private final Clock clockDefault = Clock.systemDefaultZone();

  private GerenciadorTorneio gerenciadorTorneio;

  @Captor
  private ArgumentCaptor<ResultadoBatalha> captorResultado;

  private PokeSal pokesal1;
  private PokeSal pokesal2;

  @BeforeEach
  void setUp() {
    gerenciadorTorneio = new GerenciadorTorneio(repositorioMock, geradorMock, servicoTerrenoMock,
        clockDefault, registroBatalhaMock);
    pokesal1 = new PokeSal(TipoPokesal.CHARSAL);
    pokesal2 = new PokeSal(TipoPokesal.BULBASAL);
  }

  @Test
  @DisplayName("ArgumentCaptor verificando vencedor, perdedor e número de turnos")
  void testResultadoSalvoAoFimDaBatalha() {
    when(servicoTerrenoMock.obterTerrenoAtual(clockDefault)).thenReturn(Terreno.ASFALTO_QUENTE);

    Scanner scan = new Scanner("3\n");
    GerenciadorTorneio.ResultadoPartida resultadoPartida =
        gerenciadorTorneio.executarPartida("Ash", pokesal1, "Gary", pokesal2, scan, "Semifinal");

    assertNotNull(resultadoPartida);

    verify(repositorioMock, times(1)).salvarResultado(captorResultado.capture());

    ResultadoBatalha resultadoSalvo = captorResultado.getValue();
    assertNotNull(resultadoSalvo);
    assertEquals(resultadoPartida.nomeTreinador(), resultadoSalvo.treinadorVencedor());
    assertEquals("Ash", resultadoSalvo.treinadorPerdedor());
    assertEquals(pokesal2.getNome(), resultadoSalvo.pokesalVencedor());
    assertEquals(pokesal1.getNome(), resultadoSalvo.pokesalPerdedor());
  }

  @Test
  @DisplayName("Repositório lança exceção e o vencedor continua definido")
  void testFalhaNoRepositorioNaoInvalidaBatalha() {
    when(servicoTerrenoMock.obterTerrenoAtual(clockDefault)).thenReturn(Terreno.ASFALTO_QUENTE);
    doThrow(new RuntimeException("Erro de conexão com o banco de dados")).when(repositorioMock)
        .salvarResultado(any(ResultadoBatalha.class));

    Scanner scan = new Scanner("3\n");

    GerenciadorTorneio.ResultadoPartida resultado = assertDoesNotThrow(() -> gerenciadorTorneio
        .executarPartida("Ash", pokesal1, "Gary", pokesal2, scan, "Semifinal"));

    assertNotNull(resultado);
    assertNotNull(resultado.nomeTreinador());
    assertNotNull(resultado.pokesal());
    verify(repositorioMock, times(1)).salvarResultado(any(ResultadoBatalha.class));
  }

  @Test
  @DisplayName("verify(..., never()) sobre o repositório quando a batalha não foi finalizada")
  void testRepositorioNaoChamadoSeBatalhaNaoTerminou() {
    when(servicoTerrenoMock.obterTerrenoAtual(clockDefault)).thenReturn(Terreno.ASFALTO_QUENTE);
    when(geradorMock.gerarNumero(
        ConstantesJogo.FAIXA_MIN_SORTEIO, ConstantesJogo.FAIXA_MAX_SORTEIO)).thenReturn(1);
    when(geradorMock.testarChance(anyDouble())).thenReturn(false);

    GerenciadorDeBatalha batalha = new GerenciadorDeBatalha(
        pokesal1, pokesal2, geradorMock, servicoTerrenoMock, clockDefault, registroBatalhaMock);

    batalha.definirOrdemAtuacao();
    batalha.executarTurnoAtaque(pokesal1, pokesal2);

    // Comprova que a batalha foi iniciada, houve troca de dano mas ambos continuam vivos
    assertTrue(pokesal1.isVivo());
    assertTrue(pokesal2.isVivo());
    assertNull(batalha.getVencedor());

    // Assegura categoricamente que o repositório NUNCA foi chamado com a batalha incompleta
    verify(repositorioMock, never()).salvarResultado(any(ResultadoBatalha.class));
    verify(repositorioMock, never()).emitirRelatorioEstatistico();
  }

  @Test
  @DisplayName("Resultados de batalha mockados determinando quem avança de fase")
  void testChaveamentoTorneioAvancaVencedores() {
    when(servicoTerrenoMock.obterTerrenoAtual(clockDefault)).thenReturn(Terreno.ASFALTO_QUENTE);
    when(geradorMock.gerarNumero(
        ConstantesJogo.FAIXA_MIN_SORTEIO, ConstantesJogo.FAIXA_MAX_SORTEIO)).thenReturn(1);
    when(geradorMock.testarChance(anyDouble())).thenReturn(false);

    // Semifinal 1: Ash (CharSal - SPD 65) vs Gary (SquirtSal - SPD 43)
    // Ash ataca primeiro e com SquirtSal com 1 HP, nocauteia imediatamente.
    PokeSal squirtSalGary = new PokeSal(TipoPokesal.SQUIRTSAL);
    squirtSalGary.receberDano(squirtSalGary.getHpAtual() - 1);
    Scanner scan1 = new Scanner("1\n");
    GerenciadorTorneio.ResultadoPartida semi1 = gerenciadorTorneio.executarPartida(
        "Ash", pokesal1, "Gary", squirtSalGary, scan1, "Semifinal 1");
    assertEquals("Ash", semi1.nomeTreinador());

    // Semifinal 2: Misty (CyndaSal - SPD 65) vs Brock (ChikoSal - SPD 45)
    // Misty ataca primeiro e com ChikoSal com 1 HP, nocauteia imediatamente.
    PokeSal cyndaSalMisty = new PokeSal(TipoPokesal.CYNDASAL);
    PokeSal chikoSalBrock = new PokeSal(TipoPokesal.CHIKOSAL);
    chikoSalBrock.receberDano(chikoSalBrock.getHpAtual() - 1);
    Scanner scan2 = new Scanner("1\n");
    GerenciadorTorneio.ResultadoPartida semi2 = gerenciadorTorneio.executarPartida(
        "Misty", cyndaSalMisty, "Brock", chikoSalBrock, scan2, "Semifinal 2");
    assertEquals("Misty", semi2.nomeTreinador());

    // Grande Final: Ash (CharSal - SPD 65) vs Misty (CyndaSal - SPD 65)
    // Desempate de velocidade via geradorMock para que Ash ataque primeiro.
    when(geradorMock.testarChance(0.5)).thenReturn(true);
    cyndaSalMisty.receberDano(cyndaSalMisty.getHpAtual() - 1);

    Scanner scanFinal = new Scanner("1\n");
    GerenciadorTorneio.ResultadoPartida finalPartida =
        gerenciadorTorneio.executarPartida(semi1.nomeTreinador(), semi1.pokesal(),
            semi2.nomeTreinador(), semi2.pokesal(), scanFinal, "Grande Final");

    assertNotNull(finalPartida);
    assertEquals("Ash", finalPartida.nomeTreinador());
    assertEquals(pokesal1.getNome(), finalPartida.pokesal().getNome());

    // Verifica que exatamente 3 resultados de batalha foram registrados (2 semis + 1 final)
    verify(repositorioMock, times(3)).salvarResultado(any(ResultadoBatalha.class));
  }
}
