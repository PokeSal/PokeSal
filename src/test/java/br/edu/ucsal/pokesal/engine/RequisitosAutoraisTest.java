package br.edu.ucsal.pokesal.engine;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.edu.ucsal.pokesal.model.PokeSal;
import br.edu.ucsal.pokesal.model.Terreno;
import br.edu.ucsal.pokesal.model.TipoPokesal;
import br.edu.ucsal.pokesal.repository.RepositorioResultados;
import br.edu.ucsal.pokesal.repository.RepositorioResultadosMemoria;
import br.edu.ucsal.pokesal.repository.ResultadoBatalha;
import br.edu.ucsal.pokesal.service.GeradorAleatorio;
import br.edu.ucsal.pokesal.service.RegistroBatalha;
import br.edu.ucsal.pokesal.service.ServicoTerreno;
import br.edu.ucsal.pokesal.util.ConstantesJogo;
import java.time.Clock;
import java.util.Scanner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Suíte de testes unitários para os Requisitos Autorais 04 e 05.
 */
@ExtendWith(MockitoExtension.class)
public class RequisitosAutoraisTest {

  @Mock
  private RepositorioResultados repositorioMock;

  @Mock
  private GeradorAleatorio geradorMock;

  @Mock
  private ServicoTerreno servicoTerrenoMock;

  @Mock
  private Clock clockMock;

  @Mock
  private RegistroBatalha registroBatalhaMock;

  @InjectMocks
  private GerenciadorTorneio gerenciadorTorneio;

  @Captor
  private ArgumentCaptor<ResultadoBatalha> captorResultado;

  @Test
  @DisplayName("Req 04: Validar compilação estatística e cálculo de MVP no repositório")
  void testCompilacaoEstatisticaMvpNoFimDoTorneio() {
    RepositorioResultadosMemoria repoMemoria = new RepositorioResultadosMemoria();

    ResultadoBatalha partida1 =
        new ResultadoBatalha("Ash", "Gary", "CharSal", "BulbaSal", 6, 120, 40, 1);
    ResultadoBatalha partida2 =
        new ResultadoBatalha("Misty", "Brock", "SquirtSal", "ChikoSal", 10, 80, 50, 2);

    repoMemoria.salvarResultado(partida1);
    repoMemoria.salvarResultado(partida2);

    assertEquals(2, repoMemoria.getResultados().size());

    ResultadoBatalha res1 = repoMemoria.getResultados().get(0);
    assertEquals("Ash", res1.treinadorVencedor());
    assertEquals("Gary", res1.treinadorPerdedor());
    assertEquals("CharSal", res1.pokesalVencedor());
    assertEquals("BulbaSal", res1.pokesalPerdedor());
    assertEquals(6, res1.quantidadeTurnos());
    assertEquals(120, res1.danoCausadoVencedor());
    assertEquals(40, res1.danoCausadoPerdedor());
    assertEquals(1, res1.itensUsadosPartida());

    ResultadoBatalha res2 = repoMemoria.getResultados().get(1);
    assertEquals("Misty", res2.treinadorVencedor());
    assertEquals("Brock", res2.treinadorPerdedor());
    assertEquals("SquirtSal", res2.pokesalVencedor());
    assertEquals("ChikoSal", res2.pokesalPerdedor());
    assertEquals(10, res2.quantidadeTurnos());
    assertEquals(80, res2.danoCausadoVencedor());
    assertEquals(50, res2.danoCausadoPerdedor());
    assertEquals(2, res2.itensUsadosPartida());

    assertDoesNotThrow(() -> repoMemoria.emitirRelatorioEstatistico());

    RepositorioResultadosMemoria repoVazio = new RepositorioResultadosMemoria();
    assertDoesNotThrow(() -> repoVazio.emitirRelatorioEstatistico());
  }

  @Test
  @DisplayName("Req 04: Validar que relatório estatístico não é emitido em torneio incompleto")
  void testEstatisticaNaoGeradaSeTorneioIncompleto() {
    when(servicoTerrenoMock.obterTerrenoAtual(clockMock)).thenReturn(Terreno.ASFALTO_QUENTE);

    PokeSal p1 = new PokeSal(TipoPokesal.CHARSAL);
    PokeSal p2 = new PokeSal(TipoPokesal.BULBASAL);
    Scanner scan = new Scanner("3\n");

    gerenciadorTorneio.executarPartida("Ash", p1, "Gary", p2, scan, "Semifinal");

    verify(repositorioMock, times(1)).salvarResultado(any(ResultadoBatalha.class));
    verify(repositorioMock, never()).emitirRelatorioEstatistico();
  }

  @Test
  @DisplayName("Req 04: Validar envio de métricas estatísticas MVP com Mockito")
  void testEnvioMetricasEstatisticasMvpComMockito() {
    when(servicoTerrenoMock.obterTerrenoAtual(clockMock)).thenReturn(Terreno.ASFALTO_QUENTE);
    when(geradorMock.gerarNumero(
        ConstantesJogo.FAIXA_MIN_SORTEIO, ConstantesJogo.FAIXA_MAX_SORTEIO)).thenReturn(1);
    when(geradorMock.testarChance(anyDouble())).thenReturn(false);

    PokeSal p1 = new PokeSal(TipoPokesal.CHARSAL);
    PokeSal p2 = new PokeSal(TipoPokesal.BULBASAL);
    Scanner scan = new Scanner("1\n1\n2\n1\n3\n");

    gerenciadorTorneio.executarPartida("Ash", p1, "Gary", p2, scan, "Semifinal");

    verify(repositorioMock, times(1)).salvarResultado(captorResultado.capture());
    ResultadoBatalha resultadoSalvo = captorResultado.getValue();
    assertNotNull(resultadoSalvo);
    assertTrue(resultadoSalvo.danoCausadoVencedor() > 0);
    assertTrue(resultadoSalvo.danoCausadoPerdedor() > 0);
    assertEquals(1, resultadoSalvo.itensUsadosPartida());
    verify(repositorioMock, never()).emitirRelatorioEstatistico();
  }

  @Test
  @DisplayName("Req 05: Validar adição de item de recompensa com sucesso na mochila")
  void testDropRecompensaPosVitoriaComSucesso() {
    PokeSal vencedor = new PokeSal(TipoPokesal.CHARSAL);

    vencedor.getMochila().usarItemPorIndice(0);
    assertEquals(2, vencedor.getMochila().getItens().size());

    when(geradorMock.gerarNumero(
        ConstantesJogo.FAIXA_MIN_SORTEIO, ConstantesJogo.FAIXA_MAX_SORTEIO)).thenReturn(25);

    gerenciadorTorneio.processarRecompensaPosBatalha(vencedor);

    assertEquals(3, vencedor.getMochila().getItens().size());
    verify(registroBatalhaMock, times(1)).registrarUsoItem(
        eq("RECOMPENSA (REQ 05)"), contains("ganhou " + ConstantesJogo.NOME_POCAO));
  }

  @Test
  @DisplayName("Req 05: Validar descarte do item de recompensa quando a mochila está lotada")
  void testDropRecompensaIgnoradoMochilaCheia() {
    PokeSal vencedor = new PokeSal(TipoPokesal.CHARSAL);

    assertEquals(3, vencedor.getMochila().getItens().size());

    when(geradorMock.gerarNumero(
        ConstantesJogo.FAIXA_MIN_SORTEIO, ConstantesJogo.FAIXA_MAX_SORTEIO)).thenReturn(70);

    gerenciadorTorneio.processarRecompensaPosBatalha(vencedor);

    assertEquals(3, vencedor.getMochila().getItens().size());
    verify(registroBatalhaMock, times(1)).registrarUsoItem(
        eq("RECOMPENSA (REQ 05)"), contains("está cheia! O item "
            + ConstantesJogo.NOME_SUPER_POCAO + " foi descartado"));
  }
}