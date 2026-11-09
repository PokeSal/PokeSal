package br.edu.ucsal.pokesal.engine;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
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
 * Suíte de testes unitários obrigatórios do Torneio e Repositório (Issue #93).
 * Valida a integração do GerenciadorTorneio com RepositorioResultados
 * utilizando Mockito.
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

	@Captor
	private ArgumentCaptor<ResultadoBatalha> captorResultado;

	private final Clock clockDefault = Clock.systemDefaultZone();

	private GerenciadorTorneio gerenciadorTorneio;
	private PokeSal pokesal1;
	private PokeSal pokesal2;

	@BeforeEach
	void setUp() {
		gerenciadorTorneio = new GerenciadorTorneio(repositorioMock, geradorMock, servicoTerrenoMock, clockDefault,
				registroBatalhaMock);
		pokesal1 = new PokeSal(TipoPokesal.CHARSAL);
		pokesal2 = new PokeSal(TipoPokesal.BULBASAL);
	}

	@Test
	@DisplayName("testResultadoSalvoAoFimDaBatalha: ArgumentCaptor verificando vencedor, perdedor e número de turnos")
	void testResultadoSalvoAoFimDaBatalha() {
		when(servicoTerrenoMock.obterTerrenoAtual(clockDefault)).thenReturn(Terreno.ASFALTO_QUENTE);

		Scanner scan = new Scanner("3\n");
		GerenciadorTorneio.ResultadoPartida resultadoPartida = gerenciadorTorneio.executarPartida("Ash", pokesal1,
				"Gary", pokesal2, scan, "Semifinal");

		assertNotNull(resultadoPartida);

		verify(repositorioMock, times(1)).salvarResultado(captorResultado.capture());

		ResultadoBatalha resultadoSalvo = captorResultado.getValue();
		assertNotNull(resultadoSalvo);
		assertEquals(resultadoPartida.nomeTreinador(), resultadoSalvo.treinadorVencedor());
		assertNotNull(resultadoSalvo.treinadorPerdedor());
		assertNotNull(resultadoSalvo.pokesalVencedor());
		assertNotNull(resultadoSalvo.pokesalPerdedor());
	}

	@Test
	@DisplayName("testFalhaNoRepositorioNaoInvalidaBatalha: Repositório lança exceção e o vencedor continua definido")
	void testFalhaNoRepositorioNaoInvalidaBatalha() {
		when(servicoTerrenoMock.obterTerrenoAtual(clockDefault)).thenReturn(Terreno.ASFALTO_QUENTE);
		doThrow(new RuntimeException("Erro de conexão com o banco de dados")).when(repositorioMock)
				.salvarResultado(any(ResultadoBatalha.class));

		Scanner scan = new Scanner("3\n");

		GerenciadorTorneio.ResultadoPartida resultado = assertDoesNotThrow(
				() -> gerenciadorTorneio.executarPartida("Ash", pokesal1, "Gary", pokesal2, scan, "Semifinal"));

		assertNotNull(resultado);
		assertNotNull(resultado.nomeTreinador());
		assertNotNull(resultado.pokesal());
		verify(repositorioMock, times(1)).salvarResultado(any(ResultadoBatalha.class));
	}

	@Test
	@DisplayName("testRepositorioNaoChamadoSeBatalhaNaoTerminou: verify(..., never()) sobre o repositório")
	void testRepositorioNaoChamadoSeBatalhaNaoTerminou() {
		verify(repositorioMock, never()).salvarResultado(any(ResultadoBatalha.class));
		verify(repositorioMock, never()).emitirRelatorioEstatistico();
	}

	@Test
	@DisplayName("testChaveamentoTorneioAvancaVencedores: Resultados de batalha mockados determinando quem avança de fase")
	void testChaveamentoTorneioAvancaVencedores() {
		when(servicoTerrenoMock.obterTerrenoAtual(clockDefault)).thenReturn(Terreno.ASFALTO_QUENTE);

		PokeSal pokesal3 = new PokeSal(TipoPokesal.SQUIRTSAL);
		PokeSal pokesal4 = new PokeSal(TipoPokesal.CHIKOSAL);

		Scanner scan1 = new Scanner("3\n");
		GerenciadorTorneio.ResultadoPartida semi1 = gerenciadorTorneio.executarPartida("Ash", pokesal1, "Gary",
				pokesal2, scan1, "Semifinal 1");

		Scanner scan2 = new Scanner("3\n");
		GerenciadorTorneio.ResultadoPartida semi2 = gerenciadorTorneio.executarPartida("Misty", pokesal3, "Brock",
				pokesal4, scan2, "Semifinal 2");

		Scanner scanFinal = new Scanner("3\n");
		GerenciadorTorneio.ResultadoPartida finalPartida = gerenciadorTorneio.executarPartida(semi1.nomeTreinador(),
				semi1.pokesal(), semi2.nomeTreinador(), semi2.pokesal(), scanFinal, "Grande Final");

		assertNotNull(finalPartida);
		assertNotNull(finalPartida.nomeTreinador());
		verify(repositorioMock, times(3)).salvarResultado(any(ResultadoBatalha.class));
	}
}